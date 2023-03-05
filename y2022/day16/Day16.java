package y2022.day16;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;
import common.PathFinding;

public class Day16 {

	public static void main(String[] args) {
		var nodes = Input.parseLines("y2022/day16/day16.txt", Node::fromString)
				.collect(Collectors.groupingBy(Node::getId, Collectors.reducing(null, (o, n) -> o != null ? o : n)));
		var significantNodes = nodes.values().stream().filter(n -> n.getFlow() > 0).collect(Collectors.toList());
		significantNodes.add(nodes.get("AA"));
		var distances = new HashMap<Node, Map<Node, Integer>>();
		significantNodes.stream().forEach(node -> {
			var paths = PathFinding.pathWithWeights(node, result -> result.keySet().containsAll(significantNodes),
					nod -> nod.getConnected().stream().map(nodes::get)
							.collect(Collectors.toMap(Function.identity(), f -> Integer.valueOf(1))));
			significantNodes.forEach(otherNode -> distances.computeIfAbsent(node, k -> new HashMap<>()).put(otherNode,
					paths.get(otherNode)));
		});
		System.out.println(maxFlow(distances, nodes.get("AA"), 0, 0, 0));
		System.out.println(maxFlow2(distances));
		System.out.println(maxFlow2Part2(distances));
	}

	private static long maxFlow(Map<Node, Map<Node, Integer>> distances, Node current, int minute, long currentFlow,
			long score) {
		long max = score;
		if (!current.isOpened() && current.getFlow() > 0) {
			current.open();
			max = Math.max(max, maxFlow(distances, current, minute + 1, currentFlow + current.getFlow(),
					score + currentFlow + current.getFlow()));
			current.close();
		} else {
			for (var neight : distances.get(current).entrySet()) {
				if (neight == current) {
					continue;
				}
				if (neight.getKey().isOpened()) {
					continue;
				}
				if (neight.getKey().getFlow() == 0) {
					continue;
				}
				if (minute + neight.getValue() >= 29) {
					continue;
				}
				max = Math.max(max, maxFlow(distances, neight.getKey(), minute + neight.getValue(), currentFlow,
						score + currentFlow * neight.getValue()));
			}
			max = Math.max(max, score + (29 - minute) * currentFlow);
		}
		return max;
	}

	private static long maxFlow2(Map<Node, Map<Node, Integer>> distances) {
		return permutations(distances, 29).stream().mapToLong(p -> evaluateFlow(distances, p, 29)).max().getAsLong();
	}

	private static long maxFlow2Part2(Map<Node, Map<Node, Integer>> distances) {
		var permutations = permutations(distances, 25);
		var max = 0L;
		for (var perm : permutations) {
			for (var perm2 : permutations) {
				if (noCommonElements(perm, perm2)) {
					var result = evaluateFlow(distances, perm, 25) + evaluateFlow(distances, perm2, 25);
					max = Math.max(max, result);
				}
			}
		}
		return max;
	}

	private static <T> boolean noCommonElements(Collection<T> c1, Collection<T> c2) {
		return c1.stream().noneMatch(c2::contains);
	}

	private static long evaluateFlow(Map<Node, Map<Node, Integer>> distances, List<Node> permutation, int timeLimit) {
		var score = permutation.get(0).getFlow();
		var flow = permutation.get(0).getFlow();
		var start = distances.keySet().stream().filter(n -> n.getId().equals("AA")).findAny().get();
		var time = distances.get(start).get(permutation.get(0)) + 1;
		for (int i = 1; i < permutation.size(); i++) {
			var dist = distances.get(permutation.get(i - 1)).get(permutation.get(i));
			score += flow * dist + flow + permutation.get(i).getFlow();
			time += dist + 1;
			flow += permutation.get(i).getFlow();
		}
		return score + (timeLimit - time) * flow;
	}

	private static Set<List<Node>> permutations(Map<Node, Map<Node, Integer>> distances, int limit) {
		var allPermutations = new HashMap<Integer, Set<List<Node>>>();
		allPermutations.put(1, distances.keySet().stream().filter(n -> !n.getId().equals("AA")).map(List::of)
				.collect(Collectors.toSet()));
		int level = 2;
		boolean added = false;
		do {
			Set<List<Node>> nextLevelPerm = allPermutations.get(level - 1).stream().map(perm -> distances.keySet()
					.stream().filter(n -> !n.getId().equals("AA")).filter(n -> !perm.contains(n)).map(n -> {
						var newPerm = new ArrayList<Node>();
						newPerm.addAll(perm);
						newPerm.add(n);
						return newPerm;
					}).filter(newPerm -> evaluateTime(newPerm, distances) < limit).collect(Collectors.toSet()))
					.flatMap(Set::stream).collect(Collectors.toSet());
			added = !nextLevelPerm.isEmpty();
			allPermutations.put(level, nextLevelPerm);
			level++;
		} while (added);
		return allPermutations.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
	}

	private static int evaluateTime(List<Node> perm, Map<Node, Map<Node, Integer>> distances) {
		var fromStart = distances.entrySet().stream().filter(e -> e.getKey().getId().equals("AA"))
				.map(Map.Entry::getValue).findAny().get();
		var time = fromStart.get(perm.get(0)) + 1;
		for (int i = 1; i < perm.size(); i++) {
			time += distances.get(perm.get(i)).get(perm.get(i - 1)) + 1;
		}
		return time;
	}

	private static class Node {
		private final String id;
		private final Set<String> connectedNodes = new HashSet<>();
		private final long flow;
		private boolean opened = false;

		private Node(String id, long flow, List<String> connectedNodes) {
			this.id = id;
			this.flow = flow;
			this.connectedNodes.addAll(connectedNodes);
		}

		public static Node fromString(String str) {
			Pattern p = Pattern.compile(
					"Valve ([A-Z][A-Z]) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z][A-Z](, [A-Z][A-Z])*)");
			var m = p.matcher(str);
			if (!m.matches()) {
				throw new IllegalArgumentException(str);
			}
			var id = m.group(1);
			var flow = Long.parseLong(m.group(2));
			var connected = Stream.of(m.group(3).split(", ")).toList();
			return new Node(id, flow, connected);
		}

		public String getId() {
			return id;
		}

		public void open() {
			opened = true;
		}

		public void close() {
			opened = false;
		}

		public boolean isOpened() {
			return opened;
		}

		public long getFlow() {
			return flow;
		}

		public Set<String> getConnected() {
			return connectedNodes;
		}

		@Override
		public String toString() {
			return id;
		}
	}
}
