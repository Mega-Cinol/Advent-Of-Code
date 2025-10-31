package y2024.day23;

import common.AdventSolution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Day23 extends AdventSolution {
    @Override
    public Object part1Solution() {
        var graph = new Graph(new HashMap<>());
        getInput().forEach(line -> {
            var nodes = line.split("-");
            graph.addConnection(nodes[0], nodes[1]);
        });
        var triplets = new HashSet<Set<String>>();
        graph.forEach((node, connections) -> {
            var connectionsList = List.copyOf(connections);
            for (var i = 0 ; i < connectionsList.size() - 1 ; i++) {
                for (var j = i + 1 ; j < connectionsList.size() ; j++) {
                    if (graph.areConnected(connectionsList.get(i), connectionsList.get(j))) {
                        triplets.add(Set.of(connectionsList.get(i), connectionsList.get(j), node));
                    }
                }
            }
        });

        return triplets.stream()
                .filter(triplet -> triplet.stream().anyMatch(node -> node.startsWith("t")))
                .count();
    }

    @Override
    public Object part2Solution() {
        var graph = new Graph(new HashMap<>());
        getInput().forEach(line -> {
            var nodes = line.split("-");
            graph.addConnection(nodes[0], nodes[1]);
        });
        var subsets = new HashSet<Set<String>>();
        graph.forEach((node, connections) -> {
            var largest = new HashSet<>(largestSubset(connections, graph));
            largest.add(node);
            subsets.add(largest);
        });

        return subsets.stream()
                .sorted((s1, s2) -> s2.size() - s1.size())
                .limit(1)
                .flatMap(Set::stream)
                .sorted(String::compareTo)
                .collect(Collectors.joining(","));
    }

    private Set<String> largestSubset(Set<String> elements, Graph graph) {
        var subsets = new HashSet<Set<String>>();
        elements.forEach(element -> subsets.add(Set.of(element)));
        var updated = false;
        do {
            updated = false;
            var newSubsets = new HashSet<Set<String>>();
            for (var element : elements) {
                for (var subset : subsets) {
                    if (subset.stream().allMatch(e -> graph.areConnected(e, element))) {
                        updated = true;
                        var newSubset = new HashSet<>(subset);
                        newSubset.add(element);
                        newSubsets.add(newSubset);
                    }
                }
            }
            if (updated) {
                subsets.clear();
                subsets.addAll(newSubsets);
            }
        } while (updated);
        return subsets.iterator().next();
    }

    private record Graph(Map<String, Set<String>> graph) {
        public void addConnection(String node1, String node2) {
            graph.computeIfAbsent(node1, k -> new HashSet<>()).add(node2);
            graph.computeIfAbsent(node2, k -> new HashSet<>()).add(node1);
        }
        public boolean areConnected(String node1, String node2) {
            return graph.get(node1).contains(node2);
        }
        public void forEach(BiConsumer<String, Set<String>> consumer) {
            graph.forEach(consumer);
        }
    }
    public static void main(String[] args) {
        new Day23().solve();
    }
}
