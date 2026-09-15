<!--
SPDX-License-Identifier: Apache-2.0

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
## Using Groovy with Gatherers

![Gatherers - produced by Dall-E 3](./img/gatherers.png)

Examples for:
https://groovy.apache.org/blog/groovy-gatherers

## JDK requirements

Groovy 6 needs JDK 17, but individual examples need newer APIs, so each is compiled and run
only where it can work (`jdkFloor` in `build.gradle`, and the CI matrix is 17, 21 and 25):

| example | needs | why |
| --- | --- | --- |
| `IteratorExamples` | 21 | `list.first` / `*.last` resolve to `SequencedCollection` getters |
| `*Gatherer` | 24 | `java.util.stream.Gatherer` is final in JDK 24 |
| `Gatherers4jExamples` | 25 | gatherers4j 0.14.0 declares a JVM 25 floor |

## Native images (GraalVM)

`native-build.sh` builds every example script into its own GraalVM native image and runs it,
comparing the output with the JVM run:

```bash
GRAALVM_HOME=/path/to/graalvm-25 ./native-build.sh              # JAVA_HOME: a JDK 17+ for Gradle
GRAALVM_HOME=/path/to/graalvm-25 ./native-build.sh --only Chop,CollateGinq --skip-build
```

Fourteen of the fifteen scripts build and produce output identical to the JVM
(`Gatherers4jExamples` fails on the JVM too: a newer gatherers4j emits a trailing partial
window the example did not expect). Groovy's own reflective needs come from the metadata the
groovy, groovy-ginq, groovy-macro and groovy-nio jars ship; each script is recorded with the
agent for what it does itself, under `-Dgroovy.indy.aot.link=true` so the JVM links call sites
the way the image will.

What needed a hand: a closure or method pointer passed where a dynamic call expects a
functional interface is coerced through a method handle the agent does not instrument, so the
proxy entry for that interface is not recorded. `Gatherer.ofSequential` and `Gatherer.of` take
up to four of them (`Supplier`, `Integrator` or `Integrator.Greedy`, `BinaryOperator`,
`BiConsumer`), and `Arrays.parallelPrefix(nums, Integer::sum)` needs a `BinaryOperator`. The
`native/<Script>.proxies` files list the interfaces per script and the build adds them to the
recording. Everything else, including the GINQ queries and the groovy-stream library, works
from the shipped metadata and the recording alone.

Two build notes: groovy-stream 0.9.1 drags in Groovy 2.3.8, which is excluded so Groovy 6
supplies the runtime, and the recording is trimmed only of entries the groovy jars' metadata
covers, never by package name, since groovy-stream's extension class lives in `groovy.stream`.

