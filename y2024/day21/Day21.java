package y2024.day21;

import common.AdventSolution;
import common.Direction;
import common.Point;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day21 extends AdventSolution {
    @Override
    public Object part1Solution() {
        var numPad = Map.ofEntries(Map.entry('A', new Point(2, 3)),
                Map.entry('0', new Point(1, 3)),
                Map.entry('1', new Point(0, 2)),
                Map.entry('2', new Point(1, 2)),
                Map.entry('3', new Point(2, 2)),
                Map.entry('4', new Point(0, 1)),
                Map.entry('5', new Point(1, 1)),
                Map.entry('6', new Point(2, 1)),
                Map.entry('7', new Point(0, 0)),
                Map.entry('8', new Point(1, 0)),
                Map.entry('9', new Point(2, 0)));
        var arrowPad = Map.ofEntries(Map.entry('A', new Point(2, 0)),
                Map.entry('^', new Point(1, 0)),
                Map.entry('<', new Point(0, 1)),
                Map.entry('v', new Point(1, 1)),
                Map.entry('>', new Point(2, 1)));
        return getInput()
                .mapToLong(sequence -> evaluateSequence(sequence, numPad, arrowPad, 3))
                .sum();
    }

    @Override
    public Object part2Solution() {
        var numPad = Map.ofEntries(Map.entry('A', new Point(2, 3)),
                Map.entry('0', new Point(1, 3)),
                Map.entry('1', new Point(0, 2)),
                Map.entry('2', new Point(1, 2)),
                Map.entry('3', new Point(2, 2)),
                Map.entry('4', new Point(0, 1)),
                Map.entry('5', new Point(1, 1)),
                Map.entry('6', new Point(2, 1)),
                Map.entry('7', new Point(0, 0)),
                Map.entry('8', new Point(1, 0)),
                Map.entry('9', new Point(2, 0)));
        var arrowPad = Map.ofEntries(Map.entry('A', new Point(2, 0)),
                Map.entry('^', new Point(1, 0)),
                Map.entry('<', new Point(0, 1)),
                Map.entry('v', new Point(1, 1)),
                Map.entry('>', new Point(2, 1)));
        return getInput()
                .mapToLong(sequence -> evaluateSequence(sequence, numPad, arrowPad, 26))
                .sum();
    }

    private long evaluateSequence(String sequence, Map<Character, Point> numPad, Map<Character, Point> arrowPad, int numberOfSteps) {
        long shortest = Long.MAX_VALUE;
        var count = 0;
        for (var topLeft = 0 ; topLeft < MOVERS.size() ; topLeft++) {
            for (var bottomLeft = 0; bottomLeft < MOVERS.size() ; bottomLeft++) {
                for (var topRight = 0; topRight < MOVERS.size() ; topRight++) {
                    for (var bottomRight = 0; bottomRight < MOVERS.size() ; bottomRight++) {
                        System.out.printf("Find path for algorithms: %d/16\n", count++);
                        var algorithms = new HashMap<Set<Direction>, List<Mover>>();
                        algorithms.put(Set.of(), MOVERS);
                        algorithms.put(Set.of(Direction.UP), List.of(moveFromToVerticalFirst));
                        algorithms.put(Set.of(Direction.DOWN), List.of(moveFromToVerticalFirst));
                        algorithms.put(Set.of(Direction.LEFT), List.of(moveFromToHorizontalFirst));
                        algorithms.put(Set.of(Direction.RIGHT), List.of(moveFromToHorizontalFirst));
                        algorithms.put(Set.of(Direction.UP, Direction.LEFT), List.of(MOVERS.get(topLeft), MOVERS.get(1 - topLeft)));
                        algorithms.put(Set.of(Direction.DOWN, Direction.LEFT), List.of(MOVERS.get(bottomLeft), MOVERS.get(1 - bottomLeft)));
                        algorithms.put(Set.of(Direction.UP, Direction.RIGHT), List.of(MOVERS.get(topRight), MOVERS.get(1 - topRight)));
                        algorithms.put(Set.of(Direction.DOWN, Direction.RIGHT), List.of(MOVERS.get(bottomRight), MOVERS.get(1 - bottomRight)));

                        var pathLength = getPathLengthWithCache(sequence, numPad, arrowPad, numberOfSteps, algorithms);
                        if (shortest > pathLength) {
                            shortest = pathLength;
                        }
                    }
                }
            }
        }
        var codeNumericValue = Long.parseLong(sequence.replaceAll("A", ""));
        return shortest * codeNumericValue;
    }

    private long getPathLengthWithCache(String sequence, Map<Character, Point> numPad, Map<Character, Point> arrowPad, int numberOfSteps, Map<Set<Direction>, List<Mover>> algorithms) {
        var path = enterSequence(sequence, numPad, numPad.get('A'), algorithms);
        path = getPath(path, arrowPad, (numberOfSteps - 1) / 2, algorithms);
        var subPaths = new HashMap<String, Long>();
        return Arrays.stream(path.split("A"))
                .map(seq -> seq + "A")
                .mapToLong(seq -> subPaths.computeIfAbsent(seq, s -> (long) getPath(s, arrowPad, numberOfSteps / 2, algorithms).length()))
                .sum();
    }

    private String getPath(String sequence, Map<Character, Point> arrowPad, int numberOfSteps, Map<Set<Direction>, List<Mover>> algorithms) {
        var path = sequence;
        for (var step = 0; step < numberOfSteps ; step++) {
            path = enterSequence(path, arrowPad, arrowPad.get('A'), algorithms);
        }
        return path;
    }

    private String enterSequence(String sequence, Map<Character, Point> pad, Point initial, Map<Set<Direction>, List<Mover>> directionAlgorithms) {
        var current = initial;
        var path = new StringBuilder();
        for (var i = 0; i < sequence.length(); i++) {
            var target = pad.get(sequence.charAt(i));
            var directions = getMoveDirection(current, target);
            var nextStep = moveFromTo(current, target, Set.copyOf(pad.values()), directionAlgorithms.get(directions));
            path.append(nextStep);
            path.append("A");
            current = target;
        }
        return path.toString();
    }

    private Set<Direction> getMoveDirection(Point from, Point to) {
        var directions = new HashSet<Direction>();
        if (from.getX() < to.getX()) {
            directions.add(Direction.RIGHT);
        } else if (from.getX() > to.getX()) {
            directions.add(Direction.LEFT);
        }
        if (from.getY() < to.getY()) {
            directions.add(Direction.UP);
        } else if (from.getY() > to.getY()) {
            directions.add(Direction.DOWN);
        }
        return directions;
    }

    private String moveFromTo(Point from, Point to, Set<Point> allowed, List<Mover> movers) {
        if (from.equals(to)) {
            return "";
        }
        var horizontalFirst = movers.get(0).move(from, to, allowed);
        if (horizontalFirst != null) {
            return horizontalFirst;
        }
        return movers.get(1).move(from, to, allowed);
    }

    @FunctionalInterface
    private interface Mover {
        String move(Point from, Point to, Set<Point> allowed);
    }

    private static final Mover moveFromToHorizontalFirst = (Point from, Point to, Set<Point> allowed) -> {
        var current = from;
        var horizontalFirst = new StringBuilder();
        while (current.getX() < to.getX()) {
            horizontalFirst.append('>');
            current = current.move(Direction.RIGHT);
            if (!allowed.contains(current)) {
                return null;
            }
        }
        while (current.getX() > to.getX()) {
            horizontalFirst.append('<');
            current = current.move(Direction.LEFT);
            if (!allowed.contains(current)) {
                return null;
            }
        }
        while (current.getY() < to.getY()) {
            horizontalFirst.append('v');
            current = current.move(Direction.DOWN);
            if (!allowed.contains(current)) {
                return null;
            }
        }
        while (current.getY() > to.getY()) {
            horizontalFirst.append('^');
            current = current.move(Direction.UP);
            if (!allowed.contains(current)) {
                return null;
            }
        }
        return horizontalFirst.toString();
    };

    private static final Mover moveFromToVerticalFirst = (Point from, Point to, Set<Point> allowed) -> {
        var current = from;
        var verticalFirst = new StringBuilder();
        while (current.getY() < to.getY()) {
            verticalFirst.append('v');
            current = current.move(Direction.DOWN);
            if (!allowed.contains(current)) {
                return null;
            }
        }
        while (current.getY() > to.getY()) {
            verticalFirst.append('^');
            current = current.move(Direction.UP);
            if (!allowed.contains(current)) {
                return null;
            }
        }
        while (current.getX() < to.getX()) {
            verticalFirst.append('>');
            current = current.move(Direction.RIGHT);
            if (!allowed.contains(current)) {
                return null;
            }
        }
        while (current.getX() > to.getX()) {
            verticalFirst.append('<');
            current = current.move(Direction.LEFT);
            if (!allowed.contains(current)) {
                return null;
            }
        }
        return verticalFirst.toString();
    };

    private static final List<Mover> MOVERS = List.of(moveFromToVerticalFirst, moveFromToHorizontalFirst);

    public static void main(String[] args) {
        new Day21().solve();
    }
}
