/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.stream.Gatherer

assert (1..8).chop(3) == [[1, 2, 3]]
assert (1..8).chop(3, 2, 1) == [[1, 2, 3], [4, 5], [6]]
assert (1..8).chop(3, -1) == [[1, 2, 3], [4, 5, 6, 7, 8]]

assert (1..8).stream().gather(windowMultiple(3)).toList() ==
    [[1, 2, 3]]
assert (1..8).stream().gather(windowMultiple(3, 2, 1)).toList() ==
    [[1, 2, 3], [4, 5], [6]]
assert (1..8).stream().gather(windowMultiple(3, -1)).toList() ==
    [[1, 2, 3], [4, 5, 6, 7, 8]]

static <TR> Gatherer<TR, ?, List<TR>> windowMultiple(int... steps) {
    var remaining = steps.toList()
    Gatherer.ofSequential(
        () -> [],
        Gatherer.Integrator.ofGreedy { window, element, downstream ->
            if (!remaining) {
                return true
            }
            window << element
            if (remaining[0] != -1) remaining[0]--
            if (remaining[0]) return true
            remaining.removeFirst()
            var result = List.copyOf(window)
            window.clear()
            downstream.push(result)
        },
        (window, downstream) -> {
            if (window) {
                var result = List.copyOf(window)
                downstream.push(result)
            }
        }
    )
}
