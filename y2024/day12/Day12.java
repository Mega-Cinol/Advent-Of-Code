package y2024.day12;

import common.AdventSolution;
import common.Direction;
import common.Point;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

public class Day12 extends AdventSolution {
    @Override
    public Object part1Solution() {
        var map = parseGrid(Function.identity());
        var cost = 0L;
        while (!map.isEmpty()) {
            var element = map.entrySet().iterator().next();
            var region = new HashSet<Point>();
            Set<Point> toAdd = new HashSet<>();
            toAdd.add(element.getKey());
            while (!toAdd.isEmpty()) {
                region.addAll(toAdd);
                Set<Point> finalToAdd = toAdd;
                toAdd = toAdd.stream()
                        .map(Point::getNonDiagonalNeighbours)
                        .flatMap(Set::stream)
                        .filter(point -> !region.contains(point))
                        .filter(point -> !finalToAdd.contains(point))
                        .filter(map::containsKey)
                        .filter(point -> map.get(point).equals(element.getValue()))
                        .collect(Collectors.toSet());
            }
            var area = region.size();
            var perimeter = region.stream()
                            .mapToLong(point -> point.getNonDiagonalNeighbours().stream().filter(neighbour -> !region.contains(neighbour)).count())
                            .sum();
            cost += area * perimeter;
            region.forEach(map::remove);
        }
        return cost;
    }

    @Override
    public Object part2Solution() {
        var map = parseGrid(Function.identity());
        var cost = 0L;
        while (!map.isEmpty()) {
            var element = map.entrySet().iterator().next();
            var region = new HashSet<Point>();
            Set<Point> toAdd = new HashSet<>();
            toAdd.add(element.getKey());
            while (!toAdd.isEmpty()) {
                region.addAll(toAdd);
                Set<Point> finalToAdd = toAdd;
                toAdd = toAdd.stream()
                        .map(Point::getNonDiagonalNeighbours)
                        .flatMap(Set::stream)
                        .filter(point -> !region.contains(point))
                        .filter(point -> !finalToAdd.contains(point))
                        .filter(map::containsKey)
                        .filter(point -> map.get(point).equals(element.getValue()))
                        .collect(Collectors.toSet());
            }
            var area = region.size();
            var perimeter = countSides(region);
            cost += area * perimeter;
            region.forEach(map::remove);
        }
        return cost;
    }

    record Boundary(Point in, Point out) {}

    private long countSides(Set<Point> region) {
        var boundaries = new HashSet<Boundary>();
        region.forEach(point -> {
            point.getNonDiagonalNeighbours().stream()
                    .filter(neighbour -> !region.contains(neighbour))
                    .map(neighbour -> new Boundary(point, neighbour))
                    .forEach(boundaries::add);
        });
        var vertical = boundaries.stream()
                .sorted((b1, b2) -> (int)(b1.in().getY() - b2.in().getY()))
                .filter(b -> b.in().getY() == b.out().getY())
                .collect(Collectors.groupingBy(boundary -> boundary.in().getX()));
        var sides = vertical.values().stream()
                .mapToLong(b -> countSidesOnAxis(b, Point::getY, Point::getX))
                .sum();
        var horizontal = boundaries.stream()
                .sorted((b1, b2) -> (int)(b1.in().getX() - b2.in().getX()))
                .filter(b -> b.in().getX() == b.out().getX())
                .collect(Collectors.groupingBy(boundary -> boundary.in().getY()));
        sides += horizontal.values().stream()
                .mapToLong(b -> countSidesOnAxis(b, Point::getX, Point::getY))
                .sum();
        return sides;
    }

    private long countSidesOnAxis(List<Boundary> boundaries, ToLongFunction<Point> edgeExrtactor, ToLongFunction<Point> otherEdgeExrtactor) {
        var beforeAndAfter = boundaries.stream()
                .collect(Collectors.groupingBy(boundary -> otherEdgeExrtactor.applyAsLong(boundary.out())));
        return beforeAndAfter.values().stream()
                .mapToLong(b -> countConsecutive(b, edgeExrtactor))
                .sum();
    }

    private long countConsecutive(List<Boundary> boundaries, ToLongFunction<Point> edgeExtractor) {
        if (boundaries.isEmpty()) {
            return 0;
        }
        if (boundaries.size() == 1) {
            return 1;
        }
        var sides = 1L;
        var locations = boundaries.stream().map(Boundary::out).mapToLong(edgeExtractor).boxed().sorted().toList();
        for (var i = 1; i < locations.size(); i++) {
            if (locations.get(i) - 1 > locations.get(i - 1)) {
                sides++;
            }
        }
        return sides;
    }

    public static void main(String[] args) {
        new Day12().solve();
    }
}
