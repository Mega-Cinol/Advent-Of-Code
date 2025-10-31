package y2017.day7;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day7 {

	public static void main(String[] args) {
		Set<String> names = Input.parseLines("y2017/day7/day7.txt", Day7::extractName).collect(Collectors.toSet());
		Input.parseLines("y2017/day7/day7.txt", Day7::extractChildren, names::removeAll);
		String root = names.stream().findAny().get();

		Map<String, Integer> weight = new HashMap<>();
		Map<String, Set<String>> children = new HashMap<>();
		Input.parseLines("y2017/day7/day7.txt").forEach(line -> populateMaps(line, weight, children));
		System.out.println(weight);
		nodeWeight(root, weight, children);
	}

	private static String extractName(String text) {
		return text.split(" ")[0];
	}

	private static Set<String> extractChildren(String text) {
		String[] split = text.split("-");
		if (split.length == 1) {
			return new HashSet<>();
		}
		split = split[1].substring(2).split(", ");
		return Stream.of(split).collect(Collectors.toSet());
	}

	private static void populateMaps(String line, Map<String, Integer> weight, Map<String, Set<String>> children) {
		String name = extractName(line);
		Set<String> nodeChildren = extractChildren(line);
		int nodeWeight = Integer.parseInt(line.substring(line.indexOf('(') + 1, line.indexOf(')')));
		weight.put(name, nodeWeight);
		children.put(name, nodeChildren);
	}

	private static int nodeWeight(String node, Map<String, Integer> weight, Map<String, Set<String>> children) {
		if (children.containsKey(node) && !children.get(node).isEmpty()) {
			List<Integer> childWeight = children.get(node).stream().map(c -> nodeWeight(c, weight, children))
					.collect(Collectors.toList());
			int expected = childWeight.get(0);
			if (childWeight.stream().anyMatch(w -> w != expected)) {
				children.get(node).stream().forEach(child -> {
					System.out.println("Node: " + node + " " + child + ": " + nodeWeight(child, weight, children));
				});
			}
			return weight.get(node) + childWeight.stream().mapToInt(Integer::intValue).sum();
		} else {
			return weight.get(node);
		}
	}
}
