import com.ginsberg.gatherers4j.Gatherers4j
import groovy.stream.Stream

var abc = 'A'..'C'
var nums = 1..3

// crossWith
assert Stream.from(x:abc, y:nums).map{ x + y }.collect().toString()
    == '[A1, A2, A3, B1, B2, B3, C1, C2, C3]'

// foldIndexed => X

// interleaveWith
assert Stream.from(abc.indices)
    .flatMap { [abc[it], nums[it]] }
    .collect() == ['A', 1, 'B', 2, 'C', 3]
assert Stream.from(abc).zip(nums) { x, y -> [x, y] }
    .flatMap { x, y -> [x, y] }
    .collect() == ['A', 1, 'B', 2, 'C', 3]

// mapIndexed => mapWithIndex
assert Stream.from(abc)
    .mapWithIndex { s, i -> s + i }
    .toList() == ['A0', 'B1', 'C2']

// orderByFrequency => X

// peekIndexed => tapWithIndex
assert Stream.from(abc)
    .tapWithIndex { s, i -> println "Element $s at index $i" }
    .toList() == abc

// repeat
assert Stream.from(abc)
    .repeat(3)
    .toList() == ['A', 'B', 'C', 'A', 'B', 'C', 'A', 'B', 'C']

// repeatInfinitely
assert Stream.from(abc)
    .repeat()
    .take(5)
    .toList() == ['A', 'B', 'C', 'A', 'B']

// reverse => X

// rotate => X
var abcde = ['A', 'B', 'C', 'D', 'E']

// scanIndexed => X

// shuffle X

// throttle X

// withIndex X

// zipWith => zip
assert Stream.from(abc).zip(nums){ s, i -> s + i }.collect() == ['A1', 'B2', 'C3']

// zipWithNext X
// dedupeConsecutive X
// dedupeConsecutiveBy X
// distinctBy X

// dropEveryNth/takeEveryNth
var result = []
Stream.from('A'..'G').tapEvery(3) { result << it }.collect()
assert result == ['C', 'F']
result = []
assert Stream.from('A'..'G')
    .filterWithIndex { next, idx -> idx % 3 == 0 }
    .toList() == ['A', 'D', 'G']
assert Stream.from('A'..'G')
    .filterWithIndex { next, idx -> idx % 3 }
    .toList() == ['B', 'C', 'E', 'F']

// dropLast/dropRight X

// filterIndexed
assert Stream.from(abcde).filterWithIndex{ s, n -> n % 2 == 0 }.toList() == ['A', 'C', 'E']
assert Stream.from(abcde).filterWithIndex{ s, n -> n < 2 || s == 'E' }.toList() == ['A', 'B', 'E']

// filterInstanceOf X

// filterOrdered X
// filterOrderedBy X
// sampleFixedSize X
// samplePercentage X

// takeLast X

// takeUntil/until
assert Stream.from(abcde).until { it == 'D' }.toList() == abc
/* */
// window/collate
//assert Stream.from(abcde).collate(3).collect() == [['A', 'B', 'C'], ['D', 'E']]
//assert Stream.from('A'..'G').collate(2, 2, false).collect()
//    == [['A', 'B'], ['C', 'D'], ['E', 'F']]
/*
assert Stream.from('A'..'G').collate(2, 3, true).collect()
    == [['A', 'B'], ['D', 'E'], ['G']]
assert Stream.from('A'..'G').collate(3, 2, true).collect()
    == [['A', 'B', 'C'], ['C', 'D', 'E'], ['E', 'F', 'G'], ['G']]

// take
assert Stream.from(abcde).take(4).collect()
  == ['A', 'B', 'C', 'D']

// skip
assert Stream.from(abcde).skip(1).collect()
  == ['B', 'C', 'D', 'E']
/* */
