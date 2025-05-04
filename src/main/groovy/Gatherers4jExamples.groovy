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


import com.ginsberg.gatherers4j.enums.Order
import com.ginsberg.gatherers4j.enums.Rotate

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Gatherers
import java.util.stream.Stream
import com.ginsberg.gatherers4j.Gatherers4j
import com.ginsberg.gatherers4j.enums.Frequency

import static java.lang.String.CASE_INSENSITIVE_ORDER

var abc = 'A'..'C'
var nums = 1..3

// crossWith/combinations
assert abc.stream()
    .gather(Gatherers4j.crossWith(nums.stream()))
    .map(pair -> pair.first + pair.second)
    .toList() == ['A1', 'A2', 'A3', 'B1', 'B2', 'B3', 'C1', 'C2', 'C3']

// foldIndexed
assert abc.stream()
    .gather(Gatherers4j.foldIndexed(
        () -> '', // initialValue
        (index, carry, next) -> carry + next + index
    ))
    .findFirst().get() == 'A0B1C2'

// interleaveWith
assert abc.stream()
    .gather(Gatherers4j.interleaveWith(nums.stream()))
    .toList() == ['A', 1, 'B', 2, 'C', 3]

// mapIndexed
assert abc.stream()
    .gather(Gatherers4j.mapIndexed (
        (i, s) -> s + i
    ))
    .toList() == ['A0', 'B1', 'C2']

// orderByFrequency
var letters = ['A', 'A', 'A', 'B', 'B', 'B', 'B', 'C', 'C']
assert letters.stream()
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
    .toString() == '[A:3, B:4, C:2]'
assert letters.stream()
    .gather(Gatherers4j.orderByFrequency(Frequency.Ascending))
    .map(withCount -> [withCount.value, withCount.count])
    .toList()
    .collectEntries()
    .toString() == '[C:2, A:3, B:4]'
assert letters.stream()
    .gather(Gatherers4j.orderByFrequency(Frequency.Descending))
    .map(withCount -> [withCount.value, withCount.count])
    .toList()
    .collectEntries()
    .toString() == '[B:4, A:3, C:2]'

// peekIndexed
assert abc.stream()
    .gather(Gatherers4j.peekIndexed(
        (index, element) -> println "Element $element at index $index"
    ))
    .toList() == abc

// repeat
assert abc.stream()
    .gather(Gatherers4j.repeat(3))
    .toList() == ['A', 'B', 'C', 'A', 'B', 'C', 'A', 'B', 'C']

// repeatInfinitely
assert abc.stream()
    .gather(Gatherers4j.repeatInfinitely())
    .limit(5)
    .toList() == ['A', 'B', 'C', 'A', 'B']

// reverse
assert abc.stream()
    .gather(Gatherers4j.reverse())
    .toList() == 'C'..'A'

// rotate
var abcde = 'A'..'E'
var shift = 2
assert abcde.stream()
    .gather(Gatherers4j.rotate(Rotate.Left, shift))
    .toList() == ['C', 'D', 'E', 'A', 'B']
assert abcde.stream()
    .gather(Gatherers4j.rotate(Rotate.Right, shift))
    .toList() == ['D', 'E', 'A', 'B', 'C']

// scanIndexed
assert abc.stream()
    .gather(
        Gatherers4j.scanIndexed(
            () -> '',
            (index, carry, next) -> carry + next + index
        )
    )
    .toList() == [ 'A0', 'A0B1', 'A0B1C2' ]

// shuffle
int seed = 42
assert Stream.of(*'A'..'G')
    .gather(Gatherers4j.shuffle(new Random(seed)))
    .toList() == [ 'B', 'D', 'F', 'A', 'E', 'G', 'C' ]

// throttle X

// withIndex
assert abc.stream()
    .gather(Gatherers4j.withIndex())
    .map(withIndex -> "$withIndex.value$withIndex.index")
    .toList() == ['A0', 'B1', 'C2']

// zipWith
assert abc.stream()
    .gather(Gatherers4j.zipWith(nums.stream()))
    .map(pair -> "$pair.first$pair.second")
    .toList() == ['A1', 'B2', 'C3']

// zipWithNext X
// dedupeConsecutive X
// dedupeConsecutiveBy X

// distinctBy
assert Stream.of('A', 'BB', 'CC', 'D')
    .gather(Gatherers4j.distinctBy(String::size))
    .toList() == ['A', 'BB']

// dropEveryNth
assert ('A'..'G').stream()
    .gather(Gatherers4j.dropEveryNth(3))
    .toList() == ['B', 'C', 'E', 'F']
// takeEveryNth
assert ('A'..'G').stream()
    .gather(Gatherers4j.takeEveryNth(3))
    .toList() == ['A', 'D', 'G']

// dropLast/dropRight
assert abcde.stream()
    .gather(Gatherers4j.dropLast(2))
    .toList() == abc

// filterIndexed
assert abcde.stream()
    .gather(Gatherers4j.filterIndexed{ n, s -> n % 2 == 0 })
    .toList() == ['A', 'C', 'E']
assert abcde.stream()
    .gather(Gatherers4j.filterIndexed{ n, s -> n < 2 || s == 'E' })
    .toList() == ['A', 'B', 'E']

// filterInstanceOf
var mixed = [(byte)1, (short)2, 3, (long)4, 5.0, 6.0d, '7', '42']
assert mixed.stream()
    .gather(Gatherers4j.filterInstanceOf(Integer))
    .toList() == [3]
assert mixed.stream()
    .gather(Gatherers4j.filterInstanceOf(Number))
    .toList() == [1, 2, 3, 4, 5.0, 6.0]
assert mixed.stream()
    .gather(Gatherers4j.filterInstanceOf(Integer, Short))
    .toList() == [2, 3]

// filterOrdered X
// filterOrderedBy X
// sampleFixedSize X
// samplePercentage X

// takeLast
assert abcde.stream()
    .gather(Gatherers4j.takeLast(3))
    .toList() == ['C', 'D', 'E']

// takeUntil
assert abcde.stream()
    .gather(Gatherers4j.takeUntil{ it == 'C' })
    .toList() == abc

// uniquelyOccurring
assert Stream.of('A', 'B', 'C', 'A')
    .gather(Gatherers4j.uniquelyOccurring())
    .toList() == ['B', 'C']

// group "adjacent"
assert Stream.of('A', 'A', 'BB', 'BB', 'CCC', 'A')
    .gather(Gatherers4j.group())
    .toList() == [ ['A', 'A'], ['BB', 'BB'], ['CCC'], ['A'] ]

// groupBy "adjacent"
assert Stream.of('A', 'B', 'C', 'BB', 'BBB', 'C', 'DD', 'DD')
    .gather(Gatherers4j.groupBy(String::length))
    .toList() == [ ['A', 'B', 'C'], ['BB'], ['BBB'], ['C'], ['DD', 'DD'] ]

// groupOrdered X
// groupOrderedBy X

// collate/window
assert abcde.stream()
    .gather(Gatherers.windowFixed(3)) // vanilla streams
    .toList()
    .toString() == '[[A, B, C], [D, E]]'
assert abcde.stream()
    .gather(Gatherers4j.window(3, 3, true))
    .toList()
    .toString() == '[[A, B, C], [D, E]]'
assert ('A'..'G').stream()
    .gather(Gatherers4j.window(2, 2, false))
    .toList() == [['A', 'B'], ['C', 'D'], ['E', 'F']]
assert ('A'..'G').stream()
    .gather(Gatherers4j.window(2, 3, true))
    .toList() == [['A', 'B'], ['D', 'E'], ['G']]
assert ('A'..'G').stream()
    .gather(Gatherers4j.window(3, 2, true))
    .toList() == [['A', 'B', 'C'], ['C', 'D', 'E'], ['E', 'F', 'G'], ['G']]

assert (1..5).stream().gather(Gatherers4j.window(3, 1, true)).toList() ==
    [[1, 2, 3], [2, 3, 4], [3, 4, 5], [4, 5]]
assert (1..8).stream().gather(Gatherers4j.window(3, 2, true)).toList() ==
    [[1, 2, 3], [3, 4, 5], [5, 6, 7], [7, 8]]
assert (1..8).stream().gather(Gatherers4j.window(3, 2, false)).toList() ==
    [[1, 2, 3], [3, 4, 5], [5, 6, 7]]
assert (1..8).stream().gather(Gatherers4j.window(3, 4, false)).toList() ==
    [[1, 2, 3], [5, 6, 7]]
assert (1..8).stream().gather(Gatherers4j.window(3, 3, true)).toList() ==
    [[1, 2, 3], [4, 5, 6], [7, 8]]

/*
"" -> []
"aaa" -> [['a', 'a', 'a']]
"abc" -> [['a'], ['b'], ['c']]
"aabbaa" -> [['a', 'a'], ['b', 'b'], ['a', 'a']]
"abbccdc" -> [['a'], ['b','b'], ['c', 'c'], ['d'], ['c']]
 */
assert ['', 'aaa', 'abc', 'aabbaa', 'abbccdc'].collect {
    it.toList().stream().gather(Gatherers4j.group()).collect()
} == [
    [],
    [['a', 'a', 'a']],
    [['a'], ['b'], ['c']],
    [['a', 'a'], ['b', 'b'], ['a', 'a']],
    [['a'], ['b', 'b'], ['c', 'c'], ['d'], ['c']],
]

assert Stream.iterate(0, n -> n + 1)
    .gather(Gatherers.windowFixed(3))
    .limit(3)
    .toList() == [[0, 1, 2], [3, 4, 5], [6, 7, 8]]

//assert Iterators.iterate(0, n -> n + 1)
//    .collate(3)
//    .take(3)
//    .toList() == [[0, 1, 2], [3, 4, 5], [6, 7, 8]]

def items = ['a', 'a', 'a', 'a', 'b', 'c', 'c', 'a', 'a', 'd', 'e', 'e', 'e', 'e', 'E']

// name variants: compress/dedupe/dedupeConsecutive/dedupeAdjacent
// see also: https://aperiodic.net/pip/scala/s-99/#p08
// gatherer4j variants:
assert items.stream().gather(Gatherers4j.dedupeConsecutive()).toList() == ['a', 'b', 'c', 'a', 'd', 'e', 'E']
assert items.stream().gather(Gatherers4j.dedupeConsecutiveBy(String::toLowerCase)).toList() == ['a', 'b', 'c', 'a', 'd', 'e']
// potential Groovy DGM method
//assert items.dedupe() == ['a', 'b', 'c', 'a', 'd', 'e', 'E']

// name variants: pack/adjacent/group/chop/run/chopAdjacent/chopRun
// see also: https://aperiodic.net/pip/scala/s-99/#p09
// gatherer4j variants:
assert items.stream().gather(Gatherers4j.group()).toList() ==
    [['a', 'a', 'a', 'a'], ['b'], ['c', 'c'], ['a', 'a'], ['d'], ['e', 'e', 'e', 'e'], ['E']]
assert items.stream().gather(Gatherers4j.groupBy(String::toLowerCase)).toList()
  == [['a', 'a', 'a', 'a'], ['b'], ['c', 'c'], ['a', 'a'], ['d'], ['e', 'e', 'e', 'e', 'E']]
assert items.stream().gather(Gatherers4j.groupOrdered(Order.Equal)).toList() ==
    [['a', 'a', 'a', 'a'], ['b'], ['c', 'c'], ['a', 'a'], ['d'], ['e', 'e', 'e', 'e'], ['E']]
assert items.stream().gather(Gatherers4j.groupOrdered(Order.AscendingOrEqual)).toList() ==
    [['a', 'a', 'a', 'a', 'b', 'c', 'c'], ['a', 'a', 'd', 'e', 'e', 'e', 'e'], ['E']]
assert items.stream().gather(Gatherers4j.groupOrderedBy(Order.AscendingOrEqual, CASE_INSENSITIVE_ORDER)).toList() ==
    [['a', 'a', 'a', 'a', 'b', 'c', 'c'], ['a', 'a', 'd', 'e', 'e', 'e', 'e', 'E']]
// potential Groovy DGM method:
//assert items.chop() == [['a', 'a', 'a', 'a'], ['b'], ['c', 'c'], ['a', 'a'], ['d'], ['e', 'e', 'e', 'e'], ['E']]
