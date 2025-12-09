package y2025.day4;

import common.AdventSolution;
import common.Point;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day4 extends AdventSolution {
  @Override
  public Object part1Solution() {
    var map = parseGrid(chr -> switch (chr) {
      case '@' -> true;
      case '.' -> false;
      default -> throw new IllegalArgumentException("Unexpected character " + chr);
    });
    return findRemovable(map)
            .size();
  }

  @Override
  public Object part2Solution() {
    var mapInput = parseGrid(chr -> switch (chr) {
      case '@' -> true;
      case '.' -> false;
      default -> throw new IllegalArgumentException("Unexpected character " + chr);
    });
    var map = new HashMap<>(mapInput);
    var toRemove = findRemovable(map);
    var removedCount = 0;
    while (!toRemove.isEmpty()) {
      removedCount+=toRemove.size();
      toRemove.forEach(map::remove);
      toRemove = findRemovable(map);
    }
    return removedCount;
  }

  private Set<Point> findRemovable(Map<Point, Boolean> map) {
    return map.entrySet()
            .stream()
            .filter(Map.Entry::getValue)
            .filter(e -> e.getKey()
                    .getNeighbours()
                    .stream()
                    .filter(pos -> map.getOrDefault(pos, false)).count() < 4)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
  }
  public static void main(String[] args) {
    new Day4().solve();
  }
}
