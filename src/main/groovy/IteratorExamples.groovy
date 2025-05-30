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

var abc = 'A'..'C'
var nums = 1..3

// crossWith/combinations
assert [abc, nums].combinations()
    .collect(list -> list.first + list.last)
    .toString() == '[A1, B1, C1, A2, B2, C2, A3, B3, C3]'
assert [nums, abc].combinations()
    .collect(list -> list.last + list.first)
    .toString() == '[A1, A2, A3, B1, B2, B3, C1, C2, C3]'
assert Iterators.combine(letter: abc.iterator(), number: nums.iterator())
    .collecting(map -> map.letter + map.number)
    .toList() == ['A1', 'A2', 'A3', 'B1', 'B2', 'B3', 'C1', 'C2', 'C3']

// foldIndexed => inject + indexed/withIndex
assert abc.withIndex().inject('') { carry, next ->
    carry + next.first + next.last
} == 'A0B1C2'

// interleaveWith
//assert abc.iterator()
//    .interleave(nums.iterator())
//    .toList() == ['A', 1, 'B', 2, 'C', 3]
assert [abc, nums].transpose().sum() == ['A', 1, 'B', 2, 'C', 3]
assert abc.indices.collectMany{ [abc[it], nums[it]] } == ['A', 1, 'B', 2, 'C', 3]
assert abc.zip(nums).sum([]) == ['A', 1, 'B', 2, 'C', 3]

// mapIndexed => collect + indexed/withIndex
assert abc.iterator().withIndex().collect { s, i -> s + i } == ['A0', 'B1', 'C2']
assert abc.iterator().withIndex().collecting { s, i -> s + i }.toList() == ['A0', 'B1', 'C2']

var letters = ['A', 'A', 'A', 'B', 'B', 'B', 'B', 'C', 'C']

// orderByFrequency
assert letters.countBy().toString() == '[A:3, B:4, C:2]'
assert letters.countBy()
    .sort{ e -> e.value }
    .toString() == '[C:2, A:3, B:4]'
assert letters
    .countBy()
    .sort{ e -> -e.value }
    .toString() == '[B:4, A:3, C:2]'

// peekIndexed
// Eager
assert abc.eachWithIndex { String entry, int i ->
    println "Element $entry at index $i"
} == abc

assert !abc.iterator().eachWithIndex { String entry, int i ->
    println "Element $entry at index $i"
}.hasNext()

assert abc.iterator().withIndex().tapEvery { tuple ->
    println "Element $tuple.first at index $tuple.last"
}*.first == abc

// repeat
assert abc * 3 == ['A', 'B', 'C', 'A', 'B', 'C', 'A', 'B', 'C']

// repeatInfinitely
assert abc.repeat().take(5).toList() == ['A', 'B', 'C', 'A', 'B']

// reverse
assert abc.iterator()
    .reverse()
    .toList() == 'C'..'A'

// rotate
var abcde = ['A', 'B', 'C', 'D', 'E']
var temp = abcde.clone()
var shift = 2
Collections.rotate(temp, -shift) // -ve for left
assert temp == ['C', 'D', 'E', 'A', 'B']

temp = abcde.clone()
Collections.rotate(temp, shift) // +ve for right
assert temp == ['D', 'E', 'A', 'B', 'C']
assert abcde[shift..-1] + abcde[0..<shift] == ['C', 'D', 'E', 'A', 'B']
assert abcde[shift<..-1] + abcde[0..shift] == ['D', 'E', 'A', 'B', 'C']

// scanIndexed => withIndex/inject
assert abc.withIndex().inject([]) { sum, next ->
    [*sum, "${sum.takeRight(1)[0] ?: ''}$next.v1$next.v2"]
} == [ 'A0', 'A0B1', 'A0B1C2' ]

// shuffle
int seed = 42
assert ('A'..'G').shuffled(new Random(seed))
    == ['C', 'G', 'E', 'A', 'F', 'D', 'B']

// throttle X

// withIndex
assert abc.iterator().withIndex()
    .collect { s, i -> s + i } == ['A0', 'B1', 'C2']

assert abc.iterator().withIndex()
    .collect(tuple -> "$tuple.v1$tuple.v2") == ['A0', 'B1', 'C2'] // Eager

assert abc.iterator().indexed().collectEntries() == [0:'A', 1:'B', 2:'C']

// zipWith
assert [abc, nums].transpose().collect{ s, n -> s + n }
    == ['A1', 'B2', 'C3']
assert abc.iterator()
    .zip(nums.iterator())
    .collect { s, n -> s + n } == ['A1', 'B2', 'C3']

// zipWithNext X

// dedupeConsecutive X
// dedupeConsecutiveBy X

// distinctBy
assert ['A', 'BB', 'CC', 'D'].iterator()
    .toUnique(String::size)
    .toList() == ['A', 'BB']

// dropEveryNth/takeEveryNth

// drop every 3rd
/* Waiting on GROOVY-11606
assert ('A'..'G').iterator().withIndex()
    .findAllLazy { next, i -> i % 3 }
    .toList()*.first == ['B', 'C', 'E', 'F']
*/
assert ('A'..'G').iterator().withIndex()
    .findAll { next, i -> i % 3 } // Eager
    *.first == ['B', 'C', 'E', 'F']
// take every 3rd
/* Waiting on GROOVY-11606
assert ('A'..'G').iterator().withIndex()
    .findAllLazy { next, i -> i % 3 == 0 }
    .toList()*.first == ['A', 'D', 'G']
*/
assert ('A'..'G').iterator().withIndex()
    .findAll { next, i -> i % 3 == 0 } // Eager
    *.first == ['A', 'D', 'G']

// dropLast/dropRight
assert abcde.dropRight(2) == abc

// filterIndexed
assert abcde[0, 2, 4] == ['A', 'C', 'E']
assert abcde.iterator().withIndex()
    .findingAll { s, n -> n % 2 == 0 }*.first == ['A', 'C', 'E']
assert abcde.iterator().withIndex()
    .findingAll { s, n -> n < 2 || s == 'E' }*.first == ['A', 'B', 'E']

assert abcde.iterator().withIndex()
    .findAll { s, n -> n % 2 == 0 }*.first == ['A', 'C', 'E'] // Eager
assert abcde.iterator().withIndex()
    .findAll { s, n -> n < 2 || s == 'E' }*.first == ['A', 'B', 'E'] // Eager

// filterInstanceOf
var mixed = [(byte)1, (short)2, 3, (long)4, 5.0, 6.0d, '7', '42']
assert mixed.grep(Integer) == [3]
assert mixed.grep(Number) == [1, 2, 3, 4, 5.0, 6.0]
assert mixed.grep(~/\d/).toString() == '[1, 2, 3, 4, 7]'
assert mixed.findAll{ it.getClass() in [Integer, Short] } == [2, 3]

// filterOrdered X
// filterOrderedBy X
// sampleFixedSize X
// samplePercentage X

// takeRight
assert abcde.takeRight(3) == 'C'..'E'

// takeUntil/takeWhile X
assert abcde.iterator()
    .takeWhile { it != 'D' }
    .toList() == abc

// uniquelyOccurring
assert ['A', 'B', 'C', 'A'].countBy().findAll{ it.value == 1 }*.key
    == ['B', 'C']

// groupBy
assert ['A', 'A', 'BB', 'BB', 'CCC', 'A'].groupBy()*.value
    == [ ['A', 'A', 'A'], ['BB', 'BB'], ['CCC'] ]

// groupBy
assert ['A', 'B', 'C', 'BB', 'BBB', 'C', 'DD', 'DD'].groupBy(String::size)*.value
    == [['A', 'B', 'C', 'C'], ['BB', 'DD', 'DD'], ['BBB']]

// window/collate
assert abcde.collate(3) == [['A', 'B', 'C'], ['D', 'E']]
assert ('A'..'G').collate(2, 2, false) == [['A', 'B'], ['C', 'D'], ['E', 'F']]
assert ('A'..'G').collate(2, 3, true) == [['A', 'B'], ['D', 'E'], ['G']]
assert ('A'..'G').collate(3, 2, true) == [['A', 'B', 'C'], ['C', 'D', 'E'], ['E', 'F', 'G'], ['G']]
assert ('A'..'G').collate(5, 2, true) == [['A', 'B', 'C', 'D', 'E'], ['C', 'D', 'E', 'F', 'G'], ['E', 'F', 'G'], ['G']]

// takeLast
assert abcde.takeRight(3) == ['C', 'D', 'E']

// take/limit
assert abcde.take(4) == ['A', 'B', 'C', 'D']
assert abcde.stream().limit(4).toList() == ['A', 'B', 'C', 'D']
// drop/skip
assert abcde.drop(1) == ['B', 'C', 'D', 'E']
assert abcde.stream().skip(1).toList() == ['B', 'C', 'D', 'E']
