package y2024.day10;

import common.AdventSolution;
import common.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day10 extends AdventSolution {
    @Override
    public Object part1Solution() {
        var map = parseGrid(c -> (int) (c - '0'));
        return map.entrySet().stream()
                .filter(e -> e.getValue() == 0)
                .map(Map.Entry::getKey)
                .mapToInt(head -> countHikingTrails(head, map))
                .sum();
    }

    public int countHikingTrails(Point head, Map<Point, Integer> map) {
        var heads = new HashSet<Point>();
        heads.add(head);
        for (var step = 0 ; step < 9 ; step++) {
            var nextStep = step + 1;
            var nextHead = heads.stream()
                    .map(Point::getNonDiagonalNeighbours)
                    .flatMap(Set::stream)
                    .filter(point -> map.getOrDefault(point, -1) == nextStep)
                    .collect(Collectors.toSet());
            heads.clear();
            heads.addAll(nextHead);
        }
        return heads.size();
    }
    @Override
    public Object part2Solution() {
        var map = parseGrid(c -> (int) (c - '0'));
        return map.entrySet().stream()
                .filter(e -> e.getValue() == 0)
                .map(Map.Entry::getKey)
                .mapToInt(head -> countHikingTrails2(head, map))
                .sum();

    }
    public int countHikingTrails2(Point head, Map<Point, Integer> map) {
        var heads = new ArrayList<Point>();
        heads.add(head);
        for (var step = 0 ; step < 9 ; step++) {
            var nextStep = step + 1;
            var nextHead = heads.stream()
                    .map(Point::getNonDiagonalNeighbours)
                    .flatMap(Set::stream)
                    .filter(point -> map.getOrDefault(point, -1) == nextStep)
                    .toList();
            heads.clear();
            heads.addAll(nextHead);
        }
        return heads.size();
    }
    public static void main(String[] args) {
        new Day10().solve();
    }
}
