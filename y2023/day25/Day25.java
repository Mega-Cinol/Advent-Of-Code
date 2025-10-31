package y2023.day25;

import common.AdventSolution;

import java.util.*;
import java.util.regex.Pattern;

public class Day25 extends AdventSolution {

    private static final Pattern NODE_PATTERN = Pattern.compile("[a-z]{3}");

    @Override
    public Object part1Solution() {
        var graph = new HashMap<String, Set<String>>();
        getInput()
                .forEach(line -> {
                    var matcher = NODE_PATTERN.matcher(line);
                    matcher.find();
                    var fromNode = matcher.group();
                    while (matcher.find()) {
                        var toNode = matcher.group();
                        graph.computeIfAbsent(fromNode, k -> new HashSet<>()).add(toNode);
                        graph.computeIfAbsent(toNode, k -> new HashSet<>()).add(fromNode);
                    }
                });
        var firstNode = graph.entrySet().stream()
                .sorted((e1, e2) -> e1.getValue().size() - e2.getValue().size())
                .map(Map.Entry::getKey)
                .findFirst();
        var growingGraph = new GrowingGraph();
        growingGraph.add(firstNode.get(), graph);
        var value = growGraph(growingGraph, graph);

        return value * (graph.size() - value);
    }

    // I'm not sure if this could work for every input and every starting position, but it worked for mine
    private int growGraph(GrowingGraph growingGraph, Map<String, Set<String>> fullGraph) {
        var candidates = growingGraph.getBestExpandCandidatesSorted();
        for (var candidate : candidates) {
            growingGraph.add(candidate, fullGraph);
            if (growingGraph.getConnectionsCount() == 3) {
                return growingGraph.getSize();
            }
            var grownValue = growGraph(growingGraph, fullGraph);
            if (grownValue > 0) {
                return grownValue;
            }
            growingGraph.remove(candidate, fullGraph);
        }
        return -1;
    }

    private static class GrowingGraph {
        private Set<String> graph = new HashSet<>();
        private Map<String, Integer> neighbours = new HashMap<>();

        public void add(String node, Map<String, Set<String>> fullGraph) {
            neighbours.remove(node);
            graph.add(node);
            fullGraph.get(node).stream()
                    .filter(neight -> !graph.contains(neight))
                    .forEach(newNeight -> neighbours.merge(newNeight, 1, Integer::sum));
        }

        public void remove(String node, Map<String, Set<String>> fullGraph) {
            fullGraph.get(node).forEach(neightToRemove -> neighbours.merge(neightToRemove, -1, Integer::sum));
            neighbours.entrySet().removeIf(e -> e.getValue() < 1);
            neighbours.put(node, (int) fullGraph.get(node).stream().filter(graph::contains).count());
            graph.remove(node);
        }

        public List<String> getBestExpandCandidatesSorted() {
            return neighbours.entrySet()
                    .stream()
                    .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                    .map(Map.Entry::getKey)
                    .toList();
        }

        public int getConnectionsCount() {
            return neighbours.size();
        }

        public int getSize() {
            return graph.size();
        }
    }

    @Override
    public Object part2Solution() {
        return null;
    }

    public static void main(String[] args) {
        new Day25().solve();
    }
}
