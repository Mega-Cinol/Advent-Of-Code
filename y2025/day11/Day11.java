package y2025.day11;

import common.AdventSolution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day11 extends AdventSolution {
  @Override
  public Object part1Solution() {
    var graph = getInput().collect(Collectors.toMap(this::extractKey, this::extractConnections));
    var knownPaths = new HashMap<String, Long>();
    return countPaths("you", "out", graph, knownPaths, Set.of());
  }

  private long countPaths(String from, String to, Map<String, Set<String>> graph, Map<String, Long> knownPaths, Set<String> nodesToSkip) {
    if (knownPaths.containsKey(from)) {
      return knownPaths.get(from);
    }
    if (nodesToSkip.contains(from)) {
      return 0;
    }
    var pathsCount = 0L;
    if (!graph.containsKey(from)) {
      return 0;
    }
    for (var direct: graph.get(from)) {
      if (to.equals(direct)) {
        pathsCount++;
      } else {
        pathsCount += countPaths(direct, to, graph, knownPaths, nodesToSkip);
      }
    }
    knownPaths.put(from, pathsCount);
    return pathsCount;
  }

  private Set<String> extractConnections(String line) {
    var connectionsArray = line.split(":")[1].split(" ");
    return Arrays.stream(connectionsArray)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
  }

  private String extractKey(String line) {
    return line.split(":")[0];
  }

  @Override
  public Object part2Solution() {
    var graph = getInput().collect(Collectors.toMap(this::extractKey, this::extractConnections));
    var knownPaths = new HashMap<String, Long>();
    var dacFirst = countPaths("svr", "dac", graph, knownPaths, Set.of("fft"));
    knownPaths.clear();
    dacFirst *= countPaths("dac", "fft", graph, knownPaths, Set.of());
    knownPaths.clear();
    dacFirst *= countPaths("fft", "out", graph, knownPaths, Set.of());
    knownPaths.clear();
    var fftFirst = countPaths("svr", "fft", graph, knownPaths, Set.of("dac"));
    knownPaths.clear();
    fftFirst *= countPaths("fft", "dac", graph, knownPaths, Set.of());
    knownPaths.clear();
    fftFirst *= countPaths("dac", "out", graph, knownPaths, Set.of());
    return dacFirst + fftFirst;
  }

  public static void main(String[] args) {
    new Day11().solve();
  }
}
