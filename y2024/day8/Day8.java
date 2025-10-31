package y2024.day8;

import common.AdventSolution;
import common.Point;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Day8 extends AdventSolution {
    @Override
    public Object part1Solution() {
        var map = parseGrid(Function.identity());

        return map.values()
                .stream()
                .distinct()
                .filter(c -> c != '.')
                .map(character -> findAntinodes(map, character))
                .flatMap(Set::stream)
                .distinct()
                .count();
    }

    private Set<Point> findAntinodes(Map<Point, Character> map, Character node) {
        var locations = map.entrySet()
                .stream()
                .filter(e -> e.getValue() == node)
                .map(Map.Entry::getKey)
                .toList();
        var antinodes = new HashSet<Point>();
        for (var i = 1 ; i < locations.size() ; i++) {
            for (var j = 0 ; j < i ; j++) {
                var moveVector = locations.get(i).distance(locations.get(j));
                var antinode1 = locations.get(i).move(moveVector);
                var antinode2 = locations.get(j).move(moveVector.negate());
                if (map.containsKey(antinode1)) {
                    antinodes.add(antinode1);
                }
                if (map.containsKey(antinode2)) {
                    antinodes.add(antinode2);
                }
            }
        }
        return antinodes;
    }

    @Override
    public Object part2Solution() {
        var map = parseGrid(Function.identity());

        return map.values()
                .stream()
                .distinct()
                .filter(c -> c != '.')
                .map(character -> findAntinodes2(map, character))
                .flatMap(Set::stream)
                .distinct()
                .count();

    }
    private Set<Point> findAntinodes2(Map<Point, Character> map, Character node) {
        var locations = map.entrySet()
                .stream()
                .filter(e -> e.getValue() == node)
                .map(Map.Entry::getKey)
                .toList();
        var antinodes = new HashSet<>(locations);
        for (var i = 1 ; i < locations.size() ; i++) {
            for (var j = 0 ; j < i ; j++) {
                var moveVector = locations.get(i).distance(locations.get(j));
                var antinode = locations.get(i).move(moveVector);
                while (map.containsKey(antinode)) {
                    antinodes.add(antinode);
                    antinode = antinode.move(moveVector);
                }
                antinode = locations.get(j).move(moveVector.negate());
                while (map.containsKey(antinode)) {
                    antinodes.add(antinode);
                    antinode = antinode.move(moveVector.negate());
                }
            }
        }
        return antinodes;
    }
    public static void main(String[] args) {
        new Day8().solve();
    }
}
