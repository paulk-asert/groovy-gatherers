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

var words = 'the quick brown fox jumped over the lazy dog'.split().toList()
var search = 'brown fox'.split().toList()

assert search.stream().gather(tails()).toList() ==
    [['brown', 'fox'], ['fox'], []]

static <T> Gatherer<T, ?, List<T>> tails() {
    Gatherer.ofSequential(
        () -> [],
        Gatherer.Integrator.ofGreedy { state, element, downstream ->
            state << element
            return true
        },
        (state, downstream) -> {
            state.tails().each(downstream::push)
        }
    )
}

static <T> Gatherer<T, ?, List<T>> initsOfTails() {
    Gatherer.ofSequential(
        () -> [],
        Gatherer.Integrator.ofGreedy { state, element, downstream ->
            state << element
            return true
        },
        (state, downstream) -> {
            state.tails()*.inits().sum().each(downstream::push)
        }
    )
}

assert words.stream().gather(initsOfTails()).anyMatch { it == search }
