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

import static java.util.stream.Gatherers.windowFixed
import static java.util.stream.Gatherers.windowSliding
import java.util.stream.Gatherer

assert (1..8).stream().gather(windowFixed(3)).toList() ==
    [[1, 2, 3], [4, 5, 6], [7, 8]]
assert (1..8).stream().gather(windowFixedTruncating(3)).toList() ==
    [[1, 2, 3], [4, 5, 6]]
assert (1..5).stream().gather(windowSliding(3)).toList() ==
    [[1, 2, 3], [2, 3, 4], [3, 4, 5]]
assert (1..8).stream().gather(windowSlidingByStep(3, 2)).toList() ==
    [[1, 2, 3], [3, 4, 5], [5, 6, 7], [7, 8]]
assert (1..8).stream().gather(windowSlidingByStep(3, 2, false)).toList() ==
    [[1, 2, 3], [3, 4, 5], [5, 6, 7]]
assert (1..8).stream().gather(windowSlidingByStep(3, 4, false)).toList() ==
    [[1, 2, 3], [5, 6, 7]]
assert (1..8).stream().gather(windowSlidingByStep(3, 3)).toList() ==
    [[1, 2, 3], [4, 5, 6], [7, 8]]

static <T> Gatherer<T, ?, List<T>> windowFixedTruncating(int windowSize) {
    Gatherer.ofSequential(
        () -> [],
        Gatherer.Integrator.ofGreedy { window, element, downstream ->
            window << element
            if (window.size() < windowSize) return true
            var result = List.copyOf(window)
            window.clear()
            downstream.push(result)
        }
    )
}

static <T> Gatherer<T, ?, List<T>> windowSlidingByStep(int windowSize, int stepSize, boolean keepRemaining = true) {
    int skip = 0
    Gatherer.ofSequential(
        () -> [],
        Gatherer.Integrator.ofGreedy { window, element, downstream ->
            if (skip) {
                skip--
                return true
            }
            window << element
            if (window.size() < windowSize) return true
            var result = List.copyOf(window)
            skip = stepSize > windowSize ? stepSize - windowSize : 0
            [stepSize, windowSize].min().times {
                window.removeAt(0)
            }
            downstream.push(result)
        },
        (window, downstream) -> {
            if (keepRemaining) {
                var result = List.copyOf(window)
                downstream.push(result)
            }
        }
    )
}
