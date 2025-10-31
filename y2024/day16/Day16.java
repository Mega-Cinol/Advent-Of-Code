package y2024.day16;

import common.AdventSolution;
import common.Direction;
import common.PathFinding;
import common.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day16 extends AdventSolution {
    private enum Type {
        WALL('#'),
        EMPTY('.'),
        START('S'),
        END('E');
        private final char symbol;
        Type(char symbol) {
            this.symbol = symbol;
        }
        public char getSymbol() {
            return symbol;
        }
        public static Type fromSymbol(char symbol) {
            return Arrays.stream(Type.values())
                    .filter(t -> t.symbol == symbol)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid symbol: " + symbol));
        }
    }
    private record Reindeer(Point location, Direction direction) {}
    @Override
    public Object part1Solution() {
        var track = parseGrid(Type::fromSymbol);
        var start = track.entrySet().stream().filter(e -> e.getValue() == Type.START).findFirst().get().getKey();
        var end = track.entrySet().stream().filter(e -> e.getValue() == Type.END).findFirst().get().getKey();
        var points = track.entrySet().stream().filter(e -> e.getValue() != Type.WALL).map(Map.Entry::getKey).collect(Collectors.toSet());

        var graph = new HashMap<Point, Map<Point, Long>>();
        var toAdd = Set.of(start);
        while (!graph.keySet().containsAll(toAdd)) {
            var edges = toAdd.stream()
                    .filter(e -> !graph.containsKey(e))
                    .map(point -> {
                        var pointEdges = findEdges(point, points, start, end);
                        return Map.entry(point, pointEdges);
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            graph.putAll(edges);
            toAdd = edges.values().stream().flatMap(m -> m.keySet().stream())
                    .filter(e -> !graph.containsKey(e))
                    .collect(Collectors.toSet());
        }
        var distances = PathFinding.pathWithWeights(new Reindeer(start, Direction.RIGHT), visited -> visited.containsKey(end),
                reindeer -> {
                    var nextNodes = new HashMap<Reindeer, Integer>();
                    for (var neight : graph.get(reindeer.location).entrySet()) {
                        var newDirection = Direction.betweenPoints(reindeer.location, neight.getKey());
                        nextNodes.put(new Reindeer(neight.getKey(), newDirection), neight.getValue().intValue() + turnCost(reindeer.direction, newDirection));
                    }
                    return nextNodes;
                });
        return distances.entrySet().stream()
                .filter(e -> end.equals(e.getKey().location))
                .mapToLong(Map.Entry::getValue)
                .min();
    }

    private int turnCost(Direction from, Direction to) {
        if (from == to) {
            return 0;
        }
        if (from.left() == to || from.right() == to) {
            return 1000;
        }
        return 2000;
    }
    private Map<Point, Long> findEdges(Point point, Set<Point> track, Point start, Point end) {
        var neights = point.getNonDiagonalNeighbours();
        var edges = new HashMap<Point, Long>();
        Stream.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
                .filter(d -> neights.contains(point.move(d)))
                .filter(d -> track.contains(point.move(d)))
                .forEach(d -> {
                    var current = point.move(d);
                    var canTurn = track.contains(current.move(d.left())) || track.contains(current.move(d.right()));
                    while (!canTurn) {
                        if (current.equals(end) || current.equals(start) || !track.contains(current.move(d))) {
                            break;
                        }
                        current = current.move(d);
                        canTurn = track.contains(current.move(d.left())) || track.contains(current.move(d.right()));
                    }
                    edges.put(current, current.getManhattanDistance(point));
                });
        return edges;
    }
    @Override
    public Object part2Solution() {
        var track = parseGrid(Type::fromSymbol);
        var start = track.entrySet().stream().filter(e -> e.getValue() == Type.START).findFirst().get().getKey();
        var end = track.entrySet().stream().filter(e -> e.getValue() == Type.END).findFirst().get().getKey();
        var points = track.entrySet().stream().filter(e -> e.getValue() != Type.WALL).map(Map.Entry::getKey).collect(Collectors.toSet());

        var graph = new HashMap<Point, Map<Point, Long>>();
        var toAdd = Set.of(start);
        while (!graph.keySet().containsAll(toAdd)) {
            var edges = toAdd.stream()
                    .filter(e -> !graph.containsKey(e))
                    .map(point -> {
                        var pointEdges = findEdges(point, points, start, end);
                        return Map.entry(point, pointEdges);
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            graph.putAll(edges);
            toAdd = edges.values().stream().flatMap(m -> m.keySet().stream())
                    .filter(e -> !graph.containsKey(e))
                    .collect(Collectors.toSet());
        }
        var distances = pathWithWeights(new Reindeer(start, Direction.RIGHT),
                reindeer -> {
                    var nextNodes = new HashMap<Reindeer, Integer>();
                    for (var neight : graph.get(reindeer.location).entrySet()) {
                        var newDirection = Direction.betweenPoints(reindeer.location, neight.getKey());
                        nextNodes.put(new Reindeer(neight.getKey(), newDirection), neight.getValue().intValue() + turnCost(reindeer.direction, newDirection));
                    }
                    return nextNodes;
                });
        var bestPaths = distances.entrySet().stream()
                .filter(e -> end.equals(e.getKey().location))
                .map(Map.Entry::getValue)
                .flatMap(Set::stream)
                .collect(Collectors.groupingBy(this::pathPrice, Collectors.toList()))
                .entrySet().stream()
                .min(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .get();
        return bestPaths.stream()
                .map(this::visitedPoints)
                .flatMap(Set::stream)
                .distinct()
                .count();
    }

    private Set<Point> visitedPoints(List<Reindeer> path) {
        if (path.isEmpty()) {
            return Set.of();
        }
        if (path.size() == 1) {
            return Set.of(path.get(0).location);
        }
        var visited = new HashSet<Point>();
        for (int i = 1; i < path.size(); i++) {
            Point.range(path.get(i - 1).location(), path.get(i).location()).forEach(visited::add);
        }
        return visited;
    }
    private  Map<Reindeer, Set<List<Reindeer>>> pathWithWeights(Reindeer initial,
                                                Function<Reindeer, Map<Reindeer, Integer>> getPossibleMoves) {
        var visited = new HashMap<Reindeer, Set<List<Reindeer>>>();
        var toVisit = new HashMap<Reindeer, Set<List<Reindeer>>>();
        toVisit.put(initial, Set.of(List.of(initial)));
        while (!toVisit.isEmpty()) {
            var current = toVisit.entrySet().stream()
                    .map(e -> Map.entry(e.getKey(), pathPrice(e.getValue().iterator().next())))
                    .min((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                    .map(Map.Entry::getKey).get();
            for (var neighbour : getPossibleMoves.apply(current).entrySet()) {
                if (visited.containsKey(neighbour.getKey())) {
                    continue;
                }
                var neighbourPaths = toVisit.get(current).stream().map(ArrayList::new)
                        .peek(path -> path.add(neighbour.getKey()))
                        .map(arrayList -> (List<Reindeer>) arrayList)
                        .collect(Collectors.toSet());
                toVisit.merge(neighbour.getKey(), neighbourPaths, this::mergePaths);
            }
            visited.put(current, toVisit.get(current));
            toVisit.remove(current);
        }
        return visited;
    }

    private Set<List<Reindeer>> mergePaths(Set<List<Reindeer>> oldPaths, Set<List<Reindeer>> newPaths) {
        var oldPrice = pathPrice(oldPaths.iterator().next());
        var newPrice = pathPrice(newPaths.iterator().next());
        if (oldPrice < newPrice) {
            return oldPaths;
        } else if (oldPrice > newPrice) {
            return newPaths;
        } else {
            var mergedSet = new HashSet<>(oldPaths);
            mergedSet.addAll(newPaths);
            return mergedSet;
        }
    }
    private int pathPrice(List<Reindeer> path) {
        var pathPrice = 0;
        for (int i = 1 ; i < path.size() ; i++) {
            var newDirection = Direction.betweenPoints(path.get(i - 1).location, path.get(i).location);
            pathPrice += turnCost(path.get(i - 1).direction, newDirection);
            pathPrice += path.get(i).location.getManhattanDistance(path.get(i - 1).location);
        }
        return pathPrice;
    }

    public static void main(String[] args) {
        new Day16().solve();
    }
}
