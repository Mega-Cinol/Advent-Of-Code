package y2024.day4;

import common.AdventSolution;
import common.Point;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4 extends AdventSolution {
    @Override
    public Object part1Solution() {
        var input = parseGrid(Function.identity());
        return input.entrySet().stream()
                .filter(e -> e.getValue() == 'X')
                .map(Map.Entry::getKey)
                .mapToLong(xLocation -> countMas(input, xLocation))
                .sum();
    }

    private long countMas(Map<Point, Character> input, Point xLocation) {
        return new Point(0, 0).getNeighbours().stream().filter(direction -> hasMas(input, xLocation, direction)).count();
    }

    private boolean hasMas(Map<Point, Character> input, Point xLocation, Point direction) {
        var mLocation = xLocation.move(direction);
        if (input.getOrDefault(mLocation, '?') != 'M') {
            return false;
        }
        var aLocation = mLocation.move(direction);
        if (input.getOrDefault(aLocation, '?') != 'A') {
            return false;
        }
        var sLocation = aLocation.move(direction);
        return input.getOrDefault(sLocation, '?') == 'S';
    }

    @Override
    public Object part2Solution() {
        var input = parseGrid(Function.identity());
        return input.entrySet().stream()
                .filter(e -> e.getValue() == 'A')
                .map(Map.Entry::getKey)
                .filter(aLocation -> isXMas(input, aLocation))
                .count();
    }

    private boolean isXMas(Map<Point, Character> input, Point aLocation) {
        var topLeftMas = input.getOrDefault(aLocation.move(-1, 1), '?') == 'M' && input.getOrDefault(aLocation.move(1, -1), '?') == 'S';
        var topLeftSam = input.getOrDefault(aLocation.move(-1, 1), '?') == 'S' && input.getOrDefault(aLocation.move(1, -1), '?') == 'M';
        var topRightMas = input.getOrDefault(aLocation.move(1, 1), '?') == 'M' && input.getOrDefault(aLocation.move(-1, -1), '?') == 'S';
        var topRightSam = input.getOrDefault(aLocation.move(1, 1), '?') == 'S' && input.getOrDefault(aLocation.move(-1, -1), '?') == 'M';
        return (topLeftMas || topLeftSam) && (topRightMas || topRightSam);
    }
    public static void main(String[] args) {
        new Day4().solve();
    }
}
