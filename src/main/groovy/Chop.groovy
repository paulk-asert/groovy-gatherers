assert (1..8).chop(3) == [[1, 2, 3]]
assert (1..8).chop(3, 2, 1) == [[1, 2, 3], [4, 5], [6]]
assert (1..8).chop(3, -1) == [[1, 2, 3], [4, 5, 6, 7, 8]]
