package y2021.day12;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import common.Input;

public class Day12 {

	public static void main(String[] args) {
		Map<String, Set<String>> map = new HashMap<>();
		Input.parseLines("y2021/day12/day12.txt").forEach(edge -> {
			String left = edge.split("-")[0];
			String right = edge.split("-")[1];
			map.computeIfAbsent(left, k -> new HashSet<>()).add(right);
			map.computeIfAbsent(right, k -> new HashSet<>()).add(left);
		});
		System.out.println(getAllPaths("start", "end", map, new HashSet<>()).size());
		System.out.println(getAllPathsPart2("start", "end", map, new HashSet<>(), true).size());
	}

	private static Set<Deque<String>> getAllPaths(String from, String to, Map<String, Set<String>> map,
			Set<String> visited) {
		Set<Deque<String>> result = new HashSet<>();
		if (from.equals(to)) {
			return result;
		}
		Set<String> newVisited = new HashSet<String>(visited);
		newVisited.add(from);
		for (String nextStep : map.get(from)) {
			if (nextStep.toLowerCase().equals(nextStep) && visited.contains(nextStep)) {
				continue;
			}
			if (nextStep.equals(to)) {
				Deque<String> path = new ArrayDeque<>();
				path.add(to);
				result.add(path);
				continue;
			}
			for (Deque<String> path : getAllPaths(nextStep, to, map, newVisited)) {
				path.addFirst(nextStep);
				result.add(path);
			}
		}
		return result;
	}

	private static Set<Deque<String>> getAllPathsPart2(String from, String to, Map<String, Set<String>> map,
			Set<String> visited, boolean extraVisit) {
		Set<Deque<String>> result = new HashSet<>();
		if (from.equals(to)) {
			return result;
		}
		Set<String> newVisited = new HashSet<String>(visited);
		newVisited.add(from);
		boolean oldExtraVisit = extraVisit;
		for (String nextStep : map.get(from)) {
			if (nextStep.toLowerCase().equals(nextStep) && visited.contains(nextStep)) {
				if (extraVisit && !"start".equals(nextStep) && !"end".equals(nextStep)) {
					extraVisit = false;
				} else {
					continue;
				}
			}
			if (nextStep.equals(to)) {
				Deque<String> path = new ArrayDeque<>();
				path.add(to);
				result.add(path);
				continue;
			}
			for (Deque<String> path : getAllPathsPart2(nextStep, to, map, newVisited, extraVisit)) {
				path.addFirst(nextStep);
				result.add(path);
			}
			extraVisit = oldExtraVisit;
		}
		return result;
	}
}
