assert (1..8)[0..2] == [1, 2, 3]
assert (1..8)[0..2,3..4,5] == [1, 2, 3, 4, 5, 6]
assert (1..8)[0..2,3..-1] == 1..8
assert (1..8).take(3) == [1, 2, 3]
