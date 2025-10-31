package y2015.day9;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Combinations;
import common.Input;
import common.Pair;

public class Day9 {

	public static void main(String[] args) {
		Map<Pair<String>, Integer> distances = new HashMap<>();
		Set<String> places = new HashSet<>();
		Input.parseLines("y2015/day9/day9.txt", Function.identity(), entry -> addToDistances(entry, distances, places));

		Combinations.permutations(places).mapToInt(order -> orderToDistance(order, distances)).max()
				.ifPresent(System.out::println);
	}

	private static int orderToDistance(Deque<String> order, Map<Pair<String>, Integer> distances) {
		int distance = 0;
		String current = order.remove();
		for (String next : order) {
			distance += distances.get(Pair.of(current, next));
			current = next;
		}
		return distance;
	}

	private static void addToDistances(String entry, Map<Pair<String>, Integer> distances, Set<String> places) {
		Pattern distPattern = Pattern.compile("([a-zA-Z]+) to ([a-zA-Z]+) = (\\d+)");
		Matcher distMatcher = distPattern.matcher(entry);

		if (!distMatcher.matches()) {
			throw new IllegalArgumentException(entry);
		}

		places.add(distMatcher.group(1));
		places.add(distMatcher.group(2));
		distances.put(Pair.of(distMatcher.group(1), distMatcher.group(2)), Integer.parseInt(distMatcher.group(3)));
	}
}
