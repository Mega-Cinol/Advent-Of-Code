package y2024.day14;

import common.AdventSolution;
import common.Point;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day14 extends AdventSolution {
    private record Robot(Point p, Point v) {
        public static Robot fromString(String description) {
            var robotPattern = Pattern.compile("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)");
            var descriptionMatcher = robotPattern.matcher(description);
            if (!descriptionMatcher.matches()) {
                throw new IllegalArgumentException("Invalid description: " + description);
            }
            var p = new Point(Long.parseLong(descriptionMatcher.group(1)), Long.parseLong(descriptionMatcher.group(2)));
            var v = new Point(Long.parseLong(descriptionMatcher.group(3)), Long.parseLong(descriptionMatcher.group(4)));
            return new Robot(p, v);
        }
        public Robot move(long times) {
            return new Robot(p.move(v.multiply(times)), v);
        }
        public Robot inBounds(long xBounds, long yBounds) {
            var newX = p.getX() % xBounds;
            newX = newX < 0 ? newX + xBounds : newX;
            var newY = p.getY() % yBounds;
            newY = newY < 0 ? newY + yBounds : newY;
            return new Robot(new Point(newX, newY), v);
        }
        public int quadrant(long xBounds, long yBounds) {
            var middleX = xBounds / 2;
            var middleY = yBounds / 2;
            if ((p.getX() > middleX) && (p.getY() > middleY)) {
                return 1;
            }
            if ((p.getX() > middleX) && (p.getY() < middleY)) {
                return 2;
            }
            if ((p.getX() < middleX) && (p.getY() < middleY)) {
                return 3;
            }
            if ((p.getX() < middleX) && (p.getY() > middleY)) {
                return 4;
            }
            return -1;
        }
        public List<Point> findCycle(long xBounds, long yBounds) {
            var cycle = new HashSet<Point>();
            var orderedCycle = new ArrayList<Point>();
            var position = p;
            while (cycle.add(position)) {
                orderedCycle.add(position);
                position = position.move(v);
                var newX = position.getX() % xBounds;
                newX = newX < 0 ? newX + xBounds : newX;
                var newY = position.getY() % yBounds;
                newY = newY < 0 ? newY + yBounds : newY;
                position = new Point(newX, newY);
            }
            return orderedCycle;
        }
    }
    @Override
    public Object part1Solution() {
        var xBounds = 101L;
        var yBounds = 103L;
        return getInput()
                .map(Robot::fromString)
                .map(r -> r.move(100))
                .map(r -> r.inBounds(xBounds, yBounds))
                .collect(Collectors.groupingBy(r -> r.quadrant(xBounds, yBounds), Collectors.counting()))
                .entrySet()
                .stream()
                .filter(e -> e.getKey() > 0)
                .map(Map.Entry::getValue)
                .reduce(1L, (a, b) -> a * b);
    }

    @Override
    public Object part2Solution() {
        var xBounds = 101L;
        var yBounds = 103L;
        var robots = getInput()
                .map(Robot::fromString)
                .toList();
        var cycles = robots.stream()
                .map(r -> r.findCycle(xBounds, yBounds))
                .toList();
        var bestCycle = 0L;
        var shortestDistance = Long.MAX_VALUE;
        for (var cycle = 0L ; cycle < cycles.get(0).size(); cycle++) {
            System.out.println(cycle + " " + shortestDistance);
            long finalCycle = cycle;
            var movedRobots = robots.stream().map(r -> r.move(finalCycle))
                    .map(r -> r.inBounds(xBounds, yBounds))
                    .toList();
            var neighbourDistances = getNeighbourDistances(movedRobots.stream().map(Robot::p).collect(Collectors.toSet()));
            if (neighbourDistances < shortestDistance) {
                shortestDistance = neighbourDistances;
                bestCycle = finalCycle;
            }
        }
        long finalBestCycle = bestCycle;
        var movedRobots = robots.stream().map(r -> r.move(finalBestCycle))
                .map(r -> r.inBounds(xBounds, yBounds))
                .toList();
        System.out.println("===========================" + bestCycle + "======================================");
        printRobots(movedRobots, xBounds, yBounds);

        return 0;
    }

    private long getNeighbourDistances(Set<Point> points) {
        return points.stream()
                .mapToLong(p -> getNearestNeighbourDistance(p, points))
                .sum();
    }
    private long getNearestNeighbourDistance(Point p, Set<Point> points) {
        return points.stream()
                .filter(point -> !point.equals(p))
                .mapToLong(point -> point.getManhattanDistance(p))
                .min()
                .getAsLong();
    }

    private void printRobots(List<Robot> robots, long xBounds, long yBounds) {
        for (var y = 0L ; y <= yBounds ; y++) {
            for (var x = 0L ; x <= xBounds ; x++) {
                long finalX = x;
                long finalY = y;
                if (robots.stream().anyMatch(r -> r.p().equals(new Point(finalX, finalY)))) {
                    System.out.print("x");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
    private <T> List<Integer> allIndexes(List<T> list, T item) {
        var indexes = new ArrayList<Integer>();
        for (var i = 0 ; i < list.size() ; i++) {
            if (list.get(i).equals(item)) {
                indexes.add(i);
            }
        }
        return indexes;
    }
    public static void main(String[] args) {
        new Day14().solve();

    }
}
