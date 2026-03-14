package y2025.day8;

import common.AdventSolution;
import common.Point;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day8 extends AdventSolution {
  private Point strToPoint(String str) {
    var coords = str.split(",");
    var x = Long.parseLong(coords[0]);
    var y = Long.parseLong(coords[1]);
    var z = Long.parseLong(coords[2]);
    return new Point(x, y, z);
  }

  private long distance(Point p1, Point p2) {
    var dx = p1.getX() - p2.getX();
    var dy = p1.getY() - p2.getY();
    var dz = p1.getZ() - p2.getZ();
    return dx * dx + dy * dy + dz * dz;
  }

  @Override
  public Object part1Solution() {
    var points = getInput().map(this::strToPoint).toList();
    var distances = new HashMap<Set<Point>, Long>();
    for (var i = 0; i < points.size() - 1; i++) {
      for (int j = i + 1; j < points.size(); j++) {
        distances.put(Set.of(points.get(i), points.get(j)), distance(points.get(i), points.get(j)));
      }
    }
    var pairsToConnect = distances.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .collect(Collectors.toCollection(ArrayDeque::new));
    var circuits = new HashSet<Set<Point>>();
    pairsToConnect.stream().limit(1000).forEach(pair -> addToCircuits(pair, circuits));

    return circuits.stream().map(Set::size).sorted(Collections.reverseOrder()).limit(3).reduce(1, (a, b) -> a * b);
  }

  private void addToCircuits(Set<Point> pair, Set<Set<Point>> circuits) {
    var affectedCircuits = circuits.stream().filter(c -> c.stream().anyMatch(pair::contains)).collect(Collectors.toCollection(HashSet::new));
    if (affectedCircuits.isEmpty()) {
      circuits.add(pair);
    }
    circuits.removeAll(affectedCircuits);
    var newCircuit = affectedCircuits.stream().reduce(pair, (a, b) -> {
      var merged = new HashSet<>(a);
      merged.addAll(b);
      return merged;
    });
    circuits.add(newCircuit);
  }

  @Override
  public Object part2Solution() {
    var points = getInput().map(this::strToPoint).toList();
    var distances = new HashMap<Set<Point>, Long>();
    for (var i = 0; i < points.size() - 1; i++) {
      for (int j = i + 1; j < points.size(); j++) {
        distances.put(Set.of(points.get(i), points.get(j)), distance(points.get(i), points.get(j)));
      }
    }
    var pairsToConnect = distances.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .collect(Collectors.toCollection(ArrayDeque::new));
    var circuits = new HashSet<Set<Point>>();
    Set<Point> pairToConnect = null;
    do {
      pairToConnect = pairsToConnect.removeFirst();
      addToCircuits(pairToConnect, circuits);
    } while(circuits.size() != 1 || circuits.iterator().next().size() != points.size());

    return pairToConnect.stream().mapToLong(Point::getX).reduce(1, (a, b) -> a * b);
  }

  public static void main(String[] args) {
    new Day8().solve();
  }
}
