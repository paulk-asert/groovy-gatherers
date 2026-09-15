#!/usr/bin/env bash
#
# Builds a GraalVM native image for each example script and runs it.
#
#   ./native-build.sh [--exact] [--only <Name>[,<Name>...]] [--skip-build]
#
#   --exact        build with --exact-reachability-metadata: any reflective access
#                  not registered fails instead of returning "not found"
#   --only         limit to the named scripts (base names, comma-separated)
#   --skip-build   reuse build/classes and build/lib from the previous run
#
# Everything this writes goes under build/ (images, logs and the agent recordings in
# build/conf); the only hand-maintained inputs are src/native/<Script>.proxies, the
# functional interfaces each script coerces closures to (see below).
#
# Needs GRAALVM_HOME (or JAVA_HOME) pointing at a GraalVM 25.0.4+ with native-image,
# a JDK 17+ for Gradle in JAVA_HOME when GRAALVM_HOME is set separately, and python3.
# Groovy's own reflective needs come from the metadata the groovy jars ship; each
# script is recorded with the agent for what the script itself does, and entries the
# jars already cover are dropped from that recording so the jars stay the source.
# (Dropping by package name instead would lose third-party classes in groovy.*
# packages such as groovy-stream's extension, and Groovy API classes the script
# itself reaches reflectively, which are the script's to record.)
set -euo pipefail
cd "$(dirname "$0")"

EXACT=false; ONLY=""; SKIP_BUILD=false
while [ $# -gt 0 ]; do
    case "$1" in
        --exact) EXACT=true ;;
        --only) ONLY="$2"; shift ;;
        --skip-build) SKIP_BUILD=true ;;
        *) echo "unknown option: $1" >&2; exit 2 ;;
    esac
    shift
done
GRAALVM="${GRAALVM_HOME:-${JAVA_HOME:-}}"
if [ -z "$GRAALVM" ] || [ ! -x "$GRAALVM/bin/native-image" ]; then
    echo "native-image not found: set GRAALVM_HOME (or JAVA_HOME) to a GraalVM with native-image" >&2
    exit 1
fi
JAVA="$GRAALVM/bin/java"
NATIVE_IMAGE="$GRAALVM/bin/native-image"
step() { printf '\n== %s\n' "$*"; }

if [ "$SKIP_BUILD" = false ]; then
    step "compile the scripts and collect the runtime jars"
    ./gradlew --quiet compileGroovy copyDependenciesToLib
fi
CLASSES=build/classes/groovy/main
CP="$CLASSES:build/lib/*"
mkdir -p build/native build/conf

if [ -n "$ONLY" ]; then
    SCRIPTS=$(echo "$ONLY" | tr ',' ' ')
else
    SCRIPTS=$(ls src/main/groovy/*.groovy | xargs -n1 basename | sed 's/\.groovy$//' | sort)
fi

PASSED=(); FAILED=()
for NAME in $SCRIPTS; do
    step "$NAME: record with the agent (JVM linked as the image will, -Dgroovy.indy.aot.link=true)"
    rm -rf "build/conf/$NAME" && mkdir -p "build/conf/$NAME"
    if ! "$JAVA" -agentlib:native-image-agent=config-output-dir="build/conf/$NAME" -Dgroovy.indy.aot.link=true \
            -cp "$CP" "$NAME" > "build/native/$NAME.jvm.out" 2>&1; then
        echo "  the script fails on the JVM; skipping"; tail -5 "build/native/$NAME.jvm.out"; FAILED+=("$NAME (jvm)"); continue
    fi
    CONF="build/conf/$NAME/reachability-metadata.json" LIB=build/lib
    LIB="$LIB" python3 - "$CONF" <<'PY'
import glob, json, os, sys, zipfile
path = sys.argv[1]
# what the groovy jars ship, merged by type across their conditions
shipped = {}
for jar in glob.glob(os.environ["LIB"] + "/groovy-*.jar"):
    with zipfile.ZipFile(jar) as z:
        for name in z.namelist():
            if name.startswith("META-INF/native-image/") and name.endswith("reachability-metadata.json"):
                for e in json.loads(z.read(name)).get("reflection", []):
                    if isinstance(e.get("type"), str):
                        merged = shipped.setdefault(e["type"], {})
                        for k, v in e.items():
                            if k in ("type", "condition"):
                                continue
                            merged[k] = merged.get(k, []) + v if k == "methods" and k in merged else v
def covered(e):
    flags = shipped.get(e["type"])
    if flags is None:
        return False
    for need, value in e.items():
        if need in ("type", "condition"):
            continue
        if need == "methods":
            listed = [(m["name"], m.get("parameterTypes", [])) for m in flags.get("methods", [])]
            if not (flags.get("allDeclaredMethods") or flags.get("allPublicMethods")
                    or all((m["name"], m.get("parameterTypes", [])) in listed for m in value)):
                return False
        elif need == "fields":
            if not (flags.get("allDeclaredFields") or all(f["name"] in [x["name"] for x in flags.get("fields", [])] for f in value)):
                return False
        elif not flags.get(need):
            return False
    return True
data = json.load(open(path))
before = len(data.get("reflection", []))
data["reflection"] = [e for e in data.get("reflection", []) if not (isinstance(e.get("type"), str) and covered(e))]
json.dump(data, open(path, "w"), indent=2)
print(f"  kept {len(data['reflection'])} of {before} reflection entries ({len(shipped)} types shipped by the groovy jars)")
PY
    # closures coerced to a functional interface at a dynamic call site are proxied through a
    # method handle the agent does not instrument; those interfaces are listed by hand per script
    if [ -f "src/native/$NAME.proxies" ]; then
        python3 - "build/conf/$NAME/reachability-metadata.json" "src/native/$NAME.proxies" <<'PY'
import json, sys
data = json.load(open(sys.argv[1]))
proxies = [l.strip() for l in open(sys.argv[2]) if l.strip() and not l.startswith('#')]
data['reflection'] += [{'type': {'proxy': [i]}} for i in proxies]
json.dump(data, open(sys.argv[1], 'w'), indent=2)
print(f"  added {len(proxies)} hand-listed proxy interface(s): {', '.join(proxies)}")
PY
    fi
    step "$NAME: build the native image$([ "$EXACT" = true ] && echo ' (--exact-reachability-metadata)')"
    ARGS=(-H:ConfigurationFileDirectories="build/conf/$NAME/" -cp "$CP" -o "build/native/$NAME" "$NAME")
    if [ "$EXACT" = true ]; then ARGS=(--exact-reachability-metadata "${ARGS[@]}"); fi
    if ! "$NATIVE_IMAGE" "${ARGS[@]}" > "build/native/$NAME.build.log" 2>&1; then
        tail -20 "build/native/$NAME.build.log"; FAILED+=("$NAME (build)"); continue
    fi
    grep -E "Finished generating" "build/native/$NAME.build.log" || true
    step "$NAME: run the image"
    if "build/native/$NAME" > "build/native/$NAME.native.out" 2>&1; then
        if diff -q "build/native/$NAME.jvm.out" "build/native/$NAME.native.out" > /dev/null; then
            echo "  OK, output identical to the JVM run"
        else
            echo "  OK, output differs from the JVM run:"; { diff "build/native/$NAME.jvm.out" "build/native/$NAME.native.out" || true; } | head -10
        fi
        PASSED+=("$NAME")
    else
        tail -15 "build/native/$NAME.native.out"; FAILED+=("$NAME (run)")
    fi
done

step "summary: ${#PASSED[@]} passed, ${#FAILED[@]} failed"
[ ${#PASSED[@]} -gt 0 ] && echo "  passed: ${PASSED[*]}"
[ ${#FAILED[@]} -gt 0 ] && { echo "  failed: ${FAILED[*]}"; exit 1; }
exit 0
