package y2024.day15;

import common.AdventSolution;
import common.Direction;
import common.Input;
import common.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day15 extends AdventSolution {
    private enum Item {
        WALL('#'),
        ROBOT('@'),
        BOX('O'),
        BOX_LEFT('['),
        BOX_RIGHT(']'),
        EMPTY('.');
        private final char symbol;
        private Item(char symbol) {
            this.symbol = symbol;
        }
        public char getSymbol() {
            return symbol;
        }
        public static Item fromSymbol(char symbol) {
            return Arrays.stream(values())
                    .filter(e -> e.getSymbol() == symbol)
                    .findFirst()
                    .get();
        }
    }
    @Override
    public Object part1Solution() {
        var input = getInput().toList();
        var emptyLineIdx = input.indexOf("");
        var grid = Input.parseGrid(input.subList(0, emptyLineIdx), Item::fromSymbol);
        var orders = new ArrayList<Direction>();
        input.subList(emptyLineIdx + 1, input.size())
                .stream()
                .forEach(line -> {
                    for (var i = 0; i < line.length(); i++) {
                        var direction = Direction.fromCharacter(line.charAt(i));
                        orders.add(direction);
                    }
                });
        grid.entrySet().removeIf(e -> e.getValue() == Item.EMPTY);
        var robotLocation = grid.entrySet().stream().filter(e -> e.getValue() == Item.ROBOT).map(Map.Entry::getKey).findFirst().get();
        grid.put(robotLocation, Item.EMPTY);
        for (var order : orders) {
            var target = robotLocation.move(order);
            while (grid.get(target) == Item.BOX) {
                target = target.move(order);
            }
            if (grid.get(target) == Item.WALL) {
                continue;
            }
            if (!robotLocation.move(order).equals(target)) {
                grid.put(robotLocation.move(order), Item.EMPTY);
                grid.put(target, Item.BOX);
            }
            robotLocation = robotLocation.move(order);
        }
        return grid.entrySet().stream()
                .filter(e -> e.getValue() == Item.BOX)
                .map(Map.Entry::getKey)
                .mapToLong(p -> p.getY() * 100 + p.getX())
                .sum();
    }

    @Override
    public Object part2Solution() {
        var input = getInput().toList();
        var emptyLineIdx = input.indexOf("");
        var grid = Input.parseGrid(input.subList(0, emptyLineIdx), Item::fromSymbol);
        var wideGrid = new HashMap<Point, Item>();
        for (var gridItem : grid.entrySet()) {
            var leftPoint = new Point(gridItem.getKey().getX() * 2, gridItem.getKey().getY());
            var rightPoint = new Point(gridItem.getKey().getX() * 2 + 1, gridItem.getKey().getY());
            switch (gridItem.getValue()) {
                case WALL:
                    wideGrid.put(leftPoint, Item.WALL);
                    wideGrid.put(rightPoint, Item.WALL);
                    break;
                case ROBOT:
                    wideGrid.put(leftPoint, Item.ROBOT);
                    break;
                case BOX:
                    wideGrid.put(leftPoint, Item.BOX_LEFT);
                    wideGrid.put(rightPoint, Item.BOX_RIGHT);
            }
        }
        var orders = new ArrayList<Direction>();
        input.subList(emptyLineIdx + 1, input.size())
                .stream()
                .forEach(line -> {
                    for (var i = 0; i < line.length(); i++) {
                        var direction = Direction.fromCharacter(line.charAt(i));
                        orders.add(direction);
                    }
                });
        var robotLocation = wideGrid.entrySet().stream().filter(e -> e.getValue() == Item.ROBOT).map(Map.Entry::getKey).findFirst().get();
        wideGrid.remove(robotLocation);

        for (var order : orders) {
            var targetType = wideGrid.getOrDefault(robotLocation.move(order), Item.EMPTY);
            switch (targetType) {
                case ROBOT:
                case BOX:
                    throw new IllegalStateException();
                case EMPTY:
                    robotLocation = robotLocation.move(order);
                    break;
                case BOX_LEFT:
                case BOX_RIGHT:
                    var boxes = findAllBoxes(robotLocation.move(order), wideGrid, order);
                    var canMove = boxes.stream()
                            .allMatch(box -> wideGrid.getOrDefault(box.move(order), Item.EMPTY) != Item.WALL);
                    if (canMove) {
                        var newBoxes = boxes.stream()
                                .map(box -> Map.entry(box.move(order), wideGrid.get(box)))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                        boxes.forEach(wideGrid::remove);
                        wideGrid.putAll(newBoxes);
                        robotLocation = robotLocation.move(order);
                    }
            }
        }
        return wideGrid.entrySet().stream()
                .filter(e -> e.getValue() == Item.BOX_LEFT)
                .map(Map.Entry::getKey)
                .mapToLong(p -> p.getY() * 100 + p.getX())
                .sum();
    }

    private Set<Point> findAllBoxes(Point start, Map<Point, Item> grid, Direction direction) {
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            var boxes = new HashSet<Point>();
            var current = start;
            while (grid.get(current) == Item.BOX_LEFT || grid.get(current) == Item.BOX_RIGHT) {
                boxes.add(current);
                current = current.move(direction);
            }
            return boxes;
        }
        var other = grid.get(start) == Item.BOX_LEFT ? start.move(Direction.RIGHT) : start.move(Direction.LEFT);
        var boxes = new HashSet<Point>();
        boxes.add(start);
        boxes.add(other);
        Set<Point> toVisit = new HashSet<Point>();
        if ((grid.get(start.move(direction)) == Item.BOX_LEFT) || grid.get((start.move(direction))) == Item.BOX_RIGHT) {
            toVisit.add(start.move(direction));
        }
        if ((grid.get(other.move(direction)) == Item.BOX_LEFT) || grid.get((other.move(direction))) == Item.BOX_RIGHT) {
            toVisit.add(other.move(direction));
        }
        toVisit = toVisit.stream()
                .map(location -> grid.get(location) == Item.BOX_RIGHT ? location.move(Direction.LEFT) : location)
                .collect(Collectors.toSet());
        toVisit.stream()
                .map(location -> findAllBoxes(location, grid, direction))
                .forEach(boxes::addAll);
        return boxes;
    }

    public static void main(String[] args) {
        new Day15().solve();
    }
}
