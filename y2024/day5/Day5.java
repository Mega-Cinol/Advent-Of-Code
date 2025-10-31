package y2024.day5;

import common.AdventSolution;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class Day5 extends AdventSolution {

    private record Node(int value, Set<Node> before, Set<Node> after) {
        public Node(int value) {
            this(value, new HashSet<>(), new HashSet<>());
        }
        @Override
        public boolean equals(Object o) {
            if (o instanceof Node n) {
                return value == n.value;
            }
            return false;
        }

        public Node copy() {
            return new Node(value, new HashSet<>(before), new HashSet<>(after));
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(value);
        }
        @Override
        public String toString() {
            var beforeStr = before.stream()
                    .map(Node::value)
                    .map(String::valueOf)
                    .collect(joining(", "));
            var afterStr = after.stream()
                    .map(Node::value)
                    .map(String::valueOf)
                    .collect(joining(", "));
            return "Node{" + value + "} Before: " + beforeStr + " After: " + afterStr;
        }
    }

    private static class NodeOrderer {
        private Map<Integer, Node> ndoesMap = new HashMap<>();

        public void add(int first, int second) {
            var firstNode = ndoesMap.computeIfAbsent(first, Node::new);
            var secondNode = ndoesMap.computeIfAbsent(second, Node::new);
            firstNode.before.add(secondNode);
            secondNode.after.add(firstNode);
        }
        @Override
        public String toString() {
            return ndoesMap.values().stream()
                    .map(Node::toString)
                    .collect(joining("\n"));
        }
        public Map<Integer, Integer> buildOrder(Set<Integer> items) {
            var nodes = ndoesMap.values().stream()
                    .filter(node -> items.contains(node.value))
                    .map(Node::copy)
                    .collect(Collectors.toSet());
            var orderedValues = new HashMap<Integer, Integer>();
            var idx = 0;
            while (!nodes.isEmpty()) {
                var first = nodes
                        .stream()
                        .filter(node -> items.contains(node.value))
                        .filter(node -> node.after.stream().map(Node::value).noneMatch(items::contains))
                        .findFirst()
                        .orElseThrow(IllegalStateException::new);
                orderedValues.put(first.value, idx++);
                nodes.remove(first);
                nodes.stream().map(Node::after).forEach(after -> after.remove(first));
            }
            return orderedValues;
        }
    }

    @Override
    public Object part1Solution() {
        var nodeOrderer = new NodeOrderer();
        var inputIterator = getInput().toList().iterator();
        while (inputIterator.hasNext()) {
            var line = inputIterator.next();
            if (line.isBlank()) {
                break;
            }
            var splitLine = line.trim().split("\\|");
            var first = Integer.parseInt(splitLine[0]);
            var second = Integer.parseInt(splitLine[1]);
            nodeOrderer.add(first, second);
        }

        var updates = new ArrayList<List<Integer>>();
        while (inputIterator.hasNext()) {
            var line = inputIterator.next();
            updates.add(Arrays.stream(line.split(",")).map(Integer::parseInt).toList());
        }

        return updates.stream()
                .filter(list -> {
                    var order = nodeOrderer.buildOrder(Set.copyOf(list));
                    return isSorted(list, Comparator.comparingInt(order::get));
                })
                .mapToLong(this::getMiddleElement)
                .sum();
    }

    private <T> boolean isSorted(List<T> list, Comparator<T> comparator) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (comparator.compare(list.get(i), list.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

    private long getMiddleElement(List<Integer> list) {
        return list.get(list.size() / 2);
    }

    @Override
    public Object part2Solution() {
        var nodeOrderer = new NodeOrderer();
        var inputIterator = getInput().toList().iterator();
        while (inputIterator.hasNext()) {
            var line = inputIterator.next();
            if (line.isBlank()) {
                break;
            }
            var splitLine = line.trim().split("\\|");
            var first = Integer.parseInt(splitLine[0]);
            var second = Integer.parseInt(splitLine[1]);
            nodeOrderer.add(first, second);
        }

        var updates = new ArrayList<List<Integer>>();
        while (inputIterator.hasNext()) {
            var line = inputIterator.next();
            updates.add(Arrays.stream(line.split(",")).map(Integer::parseInt).toList());
        }

        return updates.stream()
                .filter(list -> {
                    var order = nodeOrderer.buildOrder(Set.copyOf(list));
                    return !isSorted(list, Comparator.comparingInt(order::get));
                })
                .map(list -> {
                    var order = nodeOrderer.buildOrder(Set.copyOf(list));
                    return list.stream()
                            .sorted(Comparator.comparingInt(order::get))
                            .toList();
                })
                .mapToLong(this::getMiddleElement)
                .sum();
    }

    public static void main(String[] args) {
        new Day5().solve();
    }
}
