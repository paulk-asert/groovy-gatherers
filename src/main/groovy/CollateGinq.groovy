assert GQL {
    from n in 1..8
    limit 3
    select n
} == [1, 2, 3]

assert GQL {
    from n in 1..8
    limit 2, 3
    select n
} == [3, 4, 5]

assert GQL {
    from ns in (
        from n in 1..8
        select n, (lead(n) over(orderby n)), (lead(n, 2) over(orderby n))
    )
    limit 3
    select ns
}*.toList() == [[1, 2, 3], [2, 3, 4], [3, 4, 5]]

