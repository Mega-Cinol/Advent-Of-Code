package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class PathFinding {
	public static <T> Map<T, Integer> countSteps(T initial, BiPredicate<T, Integer> isDone,
			Function<T, Set<T>> getPossibleMoves) {
		Map<T, Integer> visited = new HashMap<>();
		int depth = 0;
		Set<T> nextStep = new HashSet<>();
		nextStep.add(initial);
		do {
			Set<T> toVisit = new HashSet<>();
			for (T current : nextStep) {
				if (isDone.test(current, depth)) {
					visited.put(current, depth);
					return visited;
				}
				toVisit.addAll(getPossibleMoves.apply(current));
				visited.put(current, depth);
			}
			depth++;
			nextStep = toVisit;
			nextStep.removeAll(visited.keySet());
		} while (!nextStep.isEmpty());
		return visited;
	}

	public static <T> Map<T, Integer> countSteps(T initial, Predicate<Map<T, Integer>> isDone,
			Function<T, Set<T>> getPossibleMoves) {
		Map<T, Integer> visited = new HashMap<>();
		int depth = 0;
		Set<T> nextStep = new HashSet<>();
		nextStep.add(initial);
		do {
			if (isDone.test(visited)) {
				return visited;
			}
			Set<T> toVisit = new HashSet<>();
			for (T current : nextStep) {
				toVisit.addAll(getPossibleMoves.apply(current));
				visited.put(current, depth);
			}
			depth++;
			nextStep = toVisit;
			nextStep.removeAll(visited.keySet());
		} while (!nextStep.isEmpty());
		return visited;
	}

	public static <T> Map<T, Integer> pathWithWeights(T initial, Predicate<Map<T, Integer>> isDone,
			Function<T, Map<T, Integer>> getPossibleMoves) {
		Map<T, Integer> visited = new HashMap<>();
		Map<T, Integer> toVisit = new HashMap<>();
		toVisit.put(initial, 0);
		while (!toVisit.isEmpty() && !isDone.test(visited)) {
			T current = toVisit.entrySet().stream().min((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
					.map(Map.Entry::getKey).get();
			for (Map.Entry<T, Integer> neighbour : getPossibleMoves.apply(current).entrySet()) {
				if (visited.containsKey(neighbour.getKey())) {
					continue;
				}
				toVisit.merge(neighbour.getKey(), toVisit.get(current) + neighbour.getValue(), (o, n) -> o > n ? n : o);
			}
			visited.put(current, toVisit.get(current));
			toVisit.remove(current);
		}
		return visited;
	}

	public static record DistanceAndPrevious<T> (T previous, long distance)
			implements Comparable<DistanceAndPrevious<T>> {

		@Override
		public int compareTo(DistanceAndPrevious<T> o) {
			return Long.compare(distance, o.distance);
		}
	}

	public static <T> Map<T, DistanceAndPrevious<T>> pathWithWeightsAndTree(T initial,
			Predicate<Map<T, DistanceAndPrevious<T>>> isDone, Function<T, Map<T, Integer>> getPossibleMoves) {
		Map<T, DistanceAndPrevious<T>> visited = new HashMap<>();
		Map<T, DistanceAndPrevious<T>> toVisit = new HashMap<>();
		toVisit.put(initial, new DistanceAndPrevious<T>(null, 0));
		while (!toVisit.isEmpty() && !isDone.test(visited)) {
			T current = toVisit.entrySet().stream().min((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
					.map(Map.Entry::getKey).get();
			for (Map.Entry<T, Integer> neighbour : getPossibleMoves.apply(current).entrySet()) {
				if (visited.containsKey(neighbour.getKey())) {
					continue;
				}
				toVisit.merge(neighbour.getKey(),
						new DistanceAndPrevious<T>(current, toVisit.get(current).distance + neighbour.getValue()),
						(o, n) -> o.distance > n.distance ? n : o);
			}
			visited.put(current, toVisit.get(current));
			toVisit.remove(current);
		}
		return visited;
	}

	public static <T> Map<T, Set<List<T>>> getShortestPaths(T initial, Predicate<Map<T, Set<List<T>>>> isDone,
			Function<T, Set<T>> getPossibleMoves) {
		Map<T, Set<List<T>>> paths = new HashMap<>();
		Map<T, Set<T>> nextStepAndFrom = new HashMap<>();
		nextStepAndFrom.put(initial, null);
		do {
			Map<T, Set<T>> toVisitAndFrom = new HashMap<>();
			if (isDone.test(paths)) {
				return paths;
			}
			for (Map.Entry<T, Set<T>> current : nextStepAndFrom.entrySet()) {
				T nextStep = current.getKey();
				Set<T> from = current.getValue();
				Set<List<T>> shortestPaths = new HashSet<>();
				if (from != null) {
					for (T fromElement : from) {
						for (List<T> path : paths.get(fromElement)) {
							List<T> newPath = new ArrayList<>(path);
							newPath.add(nextStep);
							shortestPaths.add(newPath);
						}
					}
				} else {
					List<T> path = new ArrayList<>();
					path.add(nextStep);
					shortestPaths.add(path);
				}
				paths.merge(nextStep, shortestPaths, (o, n) -> {
					Set<List<T>> merged = new HashSet<>(o);
					merged.addAll(n);
					return merged;
				});
				for (T possibleMove : getPossibleMoves.apply(nextStep)) {
					Set<T> nextStepSet = new HashSet<>();
					nextStepSet.add(nextStep);
					toVisitAndFrom.merge(possibleMove, nextStepSet, (o, n) -> {
						Set<T> merged = new HashSet<>(o);
						merged.addAll(n);
						return merged;
					});
				}
			}
			nextStepAndFrom = toVisitAndFrom;
			paths.keySet().stream().forEach(nextStepAndFrom::remove);
		} while (!nextStepAndFrom.isEmpty());
		return paths;
	}
}
