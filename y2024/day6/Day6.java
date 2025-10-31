package y2024.day6;

import common.AdventSolution;
import common.Direction;
import common.Point;

import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day6 extends AdventSolution {
    @Override
    public Object part1Solution() {
        var map = parseGrid(Function.identity());
        var guardPosition = map.entrySet()
                .stream()
                .filter(e -> e.getValue().equals('^'))
                .map(Map.Entry::getKey)
                .findAny()
                .orElseThrow();
        var obstacles = map.entrySet().stream()
                .filter(e -> e.getValue().equals('#'))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        var guardDirection = Direction.UP;
        var visited = new HashSet<Point>();
        visited.add(guardPosition);
        while (map.containsKey(guardPosition)) {
            while (obstacles.contains(guardPosition.move(guardDirection))) {
                guardDirection = guardDirection.right();
            }
            guardPosition = guardPosition.move(guardDirection);
            visited.add(guardPosition);
        }
        return visited.size() - 1;
    }

    @Override
    public Object part2Solution() {
        record Visited(Point point, Direction direction) {}

        var map = parseGrid(Function.identity());
        var minX = Point.minX(map.keySet());
        var maxX = Point.maxX(map.keySet());
        var minY = Point.minY(map.keySet());
        var maxY = Point.maxY(map.keySet());

        var initGuardPosition = map.entrySet()
                .stream()
                .filter(e -> e.getValue().equals('^'))
                .map(Map.Entry::getKey)
                .findAny()
                .orElseThrow();
        var obstacles = map.entrySet().stream()
                .filter(e -> e.getValue().equals('#'))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        var loopedCount = 0l;
        for (var x = minX; x <= maxX; x++) {
            for (var y = minY; y <= maxY; y++) {
                if (obstacles.contains(new Point(x, y))) {
                    continue;
                }
                var newObstacles = new HashSet<Point>();
                newObstacles.addAll(obstacles);
                newObstacles.add(new Point(x, y));
                var guardPosition = initGuardPosition;
                var guardDirection = Direction.UP;
                var visited = new HashSet<Visited>();
                visited.add(new Visited(guardPosition, guardDirection));
                while (map.containsKey(guardPosition)) {
                    while (newObstacles.contains(guardPosition.move(guardDirection))) {
                        guardDirection = guardDirection.right();
                    }
                    guardPosition = guardPosition.move(guardDirection);
                    if (!visited.add(new Visited(guardPosition, guardDirection))) {
                        loopedCount++;
                        break;
                    }
                }
            }
        }
        return loopedCount;
    }

    public static void main(String[] args) {
        new Day6().solve();
    }
}
