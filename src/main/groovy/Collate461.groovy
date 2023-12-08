import static java.util.stream.Gatherers.fixedWindow
import static java.util.stream.Gatherers.slidingWindow
import java.util.stream.Gatherer

assert (1..8).stream().gather(fixedWindow(3)) == [[1, 2, 3], [4, 5, 6], [7, 8]]
assert (1..8).stream().gather(fixedTruncatingWindow(3)) == [[1, 2, 3], [4, 5, 6]]
assert (1..5).stream().gather(slidingWindow(3)) == [[1, 2, 3], [2, 3, 4], [3, 4, 5]]
//assert (1..8).collate(3, 1, false).take(3) == [[1, 2, 3], [2, 3, 4], [3, 4, 5]]
//assert (1..8).collate(3, 2, false) == [[1, 2, 3], [3, 4, 5], [5, 6, 7]]

def <TR> Gatherer<TR, ?, List<TR>> fixedTruncatingWindow(int windowSize) {
    if (windowSize < 1)
        throw new IllegalArgumentException("window size must be non-zero")

    return Gatherer.ofSequential(
            () -> new ArrayList<TR>(windowSize),
            Gatherer.Integrator.ofGreedy((window, element, downstream) -> {
                window.add(element)
                if (window.size() < windowSize)
                    return true

                var result = new ArrayList<TR>(window)
                window.clear()
                return downstream.push(result)
            })
    )
}