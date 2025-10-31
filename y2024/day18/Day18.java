package y2024.day18;

import common.AdventSolution;
import common.PathFinding;
import common.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day18 extends AdventSolution {
    @Override
    public Object part1Solution() {
        var damagedBytes = getInput()
                .map(Point::new)
                .limit(1024)
                .collect(Collectors.toSet());
        var path = PathFinding.pathWithWeights(new Point(0, 0), result -> result.containsKey(new Point(70, 70)), current -> current.getNonDiagonalNeighbours()
                .stream()
                .filter(p -> p.getX() >= 0 && p.getX() <= 70 && p.getY() >= 0 && p.getY() <= 70)
                .filter(p -> !damagedBytes.contains(p))
                .collect(Collectors.toMap(Function.identity(), p -> 1)));
        return path.get(new Point(70, 70));
    }

    @Override
    public Object part2Solution() {
        var damagedBytes = getInput()
                .map(Point::new)
                .toList();
        var good = 1023;
        var bad = damagedBytes.size() - 1;
        var next = (bad + good) / 2;
        while (bad > good + 1) {
            var damagedBytesAtStep = new HashSet<>(damagedBytes.subList(0, next + 1));
            if (isBlocked(damagedBytesAtStep)) {
                bad = next;
            } else {
                good = next;
            }
            next = (bad + good) / 2;
        }
        return damagedBytes.get(bad);
    }

    private boolean isBlocked(Set<Point> damagedBytes) {
        var path = PathFinding.pathWithWeights(new Point(0, 0), result -> result.containsKey(new Point(70, 70)), current -> current.getNonDiagonalNeighbours()
                .stream()
                .filter(p -> p.getX() >= 0 && p.getX() <= 70 && p.getY() >= 0 && p.getY() <= 70)
                .filter(p -> !damagedBytes.contains(p))
                .collect(Collectors.toMap(Function.identity(), p -> 1)));
        return !path.containsKey(new Point(70, 70));
    }
    public static void main(String[] args) {
        new Day18().solve();
    }
}
