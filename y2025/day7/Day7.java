package y2025.day7;

import common.AdventSolution;
import common.Direction;
import common.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day7 extends AdventSolution {
  private enum Type {
    NONE('.'),
    SPLITTER('^'),
    START('S');
    private final char symbol;

    Type(char symbol) {
      this.symbol = symbol;
    }
    public static Type valueOf(char symbol) {
      return Arrays.stream(values())
              .filter(t -> t.symbol == symbol)
              .findFirst()
              .orElseThrow();
    }
  }
  private static class SplitCounter {
    long count = 0l;
    public void inc() {
      count++;
    }
    public long getCount() {
      return count;
    }
  }
  @Override
  public Object part1Solution() {
    var grid = parseGrid(Type::valueOf);
    var startPos = grid.entrySet().stream().filter(e -> e.getValue() == Type.START).findFirst().orElseThrow();
    var rayPositions = new HashSet<Point>();
    rayPositions.add(startPos.getKey());
    var splits = new SplitCounter();
    while (!rayPositions.isEmpty()) {
      var nextLevel = new HashSet<Point>();
      rayPositions.forEach(rayPos -> {
        var newPos = rayPos.move(Direction.DOWN);
        if (!grid.containsKey(newPos)) return;
        switch(grid.get(newPos)) {
          case NONE ->  nextLevel.add(newPos);
          case SPLITTER -> {
            nextLevel.add(newPos.move(Direction.LEFT));
            nextLevel.add(newPos.move(Direction.RIGHT));
            splits.inc();
          }
        }
      });
      rayPositions.clear();
      rayPositions.addAll(nextLevel);
    }
    return splits.getCount();
  }

  @Override
  public Object part2Solution() {
    var grid = parseGrid(Type::valueOf);
    var splitterPaths = new HashMap<Point, Long>();
    grid.entrySet().stream().filter(e -> e.getValue() == Type.SPLITTER)
            .map(Map.Entry::getKey)
            .sorted(Comparator.comparing(Point::getY))
            .forEach(splitter -> {
              splitterPaths.put(splitter, allPossiblePaths(splitter, grid, splitterPaths));
            });
    var maxY = Point.maxY(grid.keySet());
    return grid.keySet().stream().filter(p -> p.getY() == maxY)
            .mapToLong(p -> allPossiblePaths(p, grid, splitterPaths))
            .sum();
  }

  private long allPossiblePaths(Point splitter, Map<Point, Type>grid, Map<Point, Long> knownSplittersPath) {
    var paths = 0L;
    var current = splitter.move(Direction.UP);
    while (grid.get(current) == Type.NONE) {
      if (grid.get(current.move(Direction.LEFT)) == Type.SPLITTER) {
        var sourceSplitterPaths = knownSplittersPath.get(current.move(Direction.LEFT));
        if (sourceSplitterPaths == null) {
          throw new IllegalStateException("No splitter path found for " + current);
        }
        paths += sourceSplitterPaths;
      }
      if (grid.get(current.move(Direction.RIGHT)) == Type.SPLITTER) {
        var sourceSplitterPaths = knownSplittersPath.get(current.move(Direction.RIGHT));
        if (sourceSplitterPaths == null) {
          throw new IllegalStateException("No splitter path found for " + current);
        }
        paths += sourceSplitterPaths;
      }
      current = current.move(Direction.UP);
    }
    if (grid.get(current) == Type.START) {
      paths++;
    }
    return paths;
  }
  public static void main(String[] args) {
    new Day7().solve();
  }
}
