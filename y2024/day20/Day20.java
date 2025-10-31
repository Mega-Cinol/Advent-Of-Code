package y2024.day20;

import common.AdventSolution;
import common.PathFinding;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day20 extends AdventSolution {

    private enum BlockType {
        EMPTY('.'),
        WALL('#'),
        START('S'),
        END('E');
        private char symbol;

        BlockType(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }

        public static BlockType fromSymbol(char symbol) {
            for (BlockType type : BlockType.values()) {
                if (type.getSymbol() == symbol) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown symbol: " + symbol);
        }
    }

    @Override
    public Object part1Solution() {
        var map = parseGrid(BlockType::fromSymbol);
        var start = map.entrySet().stream()
                .filter(e -> e.getValue() == BlockType.START)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown start"));
        var end = map.entrySet().stream()
                .filter(e -> e.getValue() == BlockType.END)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown end"));
        var path = map.entrySet().stream()
                .filter(e -> e.getValue() != BlockType.WALL)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        var shortestPath = PathFinding.pathWithWeights(start,
                paths -> paths.containsKey(end),
                step -> step.getNonDiagonalNeighbours()
                        .stream()
                        .filter(path::contains)
                        .collect(Collectors.toMap(Function.identity(), p -> 1)));
        var goodCheatsCount = 0L;
        for (var step : path) {
            if (!shortestPath.containsKey(step)) {
                continue;
            }
            var goodCheats = step.getNonDiagonalNeighbours().stream()
                    .filter(p -> !path.contains(p))
                    .flatMap(walls -> walls.getNonDiagonalNeighbours().stream())
                    .filter(path::contains)
                    .filter(cheat -> Math.abs(shortestPath.get(step) - shortestPath.get(cheat)) >= 102)
                    .count();
            goodCheatsCount += goodCheats;
        }
        return goodCheatsCount / 2;
    }

    @Override
    public Object part2Solution() {
        var map = parseGrid(BlockType::fromSymbol);
        var start = map.entrySet().stream()
                .filter(e -> e.getValue() == BlockType.START)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown start"));
        var end = map.entrySet().stream()
                .filter(e -> e.getValue() == BlockType.END)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown end"));
        var path = map.entrySet().stream()
                .filter(e -> e.getValue() != BlockType.WALL)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        var shortestPath = PathFinding.pathWithWeights(start,
                paths -> paths.containsKey(end),
                step -> step.getNonDiagonalNeighbours()
                        .stream()
                        .filter(path::contains)
                        .collect(Collectors.toMap(Function.identity(), p -> 1)));
        var goodCheatsCount = 0L;
        for (var step : path) {
            if (!shortestPath.containsKey(step)) {
                continue;
            }
            var goodCheats = shortestPath.keySet().stream()
                    .filter(pathStep -> pathStep.getManhattanDistance(step) <= 20)
                    .filter(cheat -> Math.abs(shortestPath.get(step) - shortestPath.get(cheat)) - step.getManhattanDistance(cheat) >= 100)
                    .count();
            goodCheatsCount += goodCheats;
        }
        return goodCheatsCount / 2;
    }

    public static void main(String[] args) {
        new Day20().solve();
    }
}
