package y2015.day13;

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

public class Day13 {
	public static void main(String[] args) {
		Map<String, Integer> happinessMatrix = new HashMap<>();
		Set<String> people = new HashSet<>();
		Input.parseLines("y2015/day13/day13.txt", Function.identity(), entry -> addToHappiness(entry, happinessMatrix, people));
		people.add("me");

		Combinations.permutations(people).mapToInt(order -> orderToHappiness(order, happinessMatrix)).max()
				.ifPresent(System.out::println);
	}

	private static int orderToHappiness(Deque<String> order, Map<String, Integer> happinessMatrix) {
//		System.out.println(order);
		int happiness = 0;
		String current = order.remove();
		String first = current;
		for (String next : order) {
//			System.out.println(happinessMatrix.get(current + next));
//			System.out.println(happinessMatrix.get(next + current));
			happiness += happinessMatrix.getOrDefault(current + next, 0) + happinessMatrix.getOrDefault(next + current, 0);
			current = next;
		}
//		System.out.println(happinessMatrix.get(current + first));
//		System.out.println(happinessMatrix.get(first + current));
		happiness += happinessMatrix.getOrDefault(current + first, 0) + happinessMatrix.getOrDefault(first + current, 0);
//		System.out.println(happiness);
		return happiness;
	}

	private static void addToHappiness(String entry, Map<String, Integer> happinessMatrix, Set<String> people) {
		Pattern happpyPattern = Pattern.compile("([a-zA-Z]+) would gain (\\d+) happiness units by sitting next to ([a-zA-Z]+)\\.");
		Pattern unhapppyPattern = Pattern.compile("([a-zA-Z]+) would lose (\\d+) happiness units by sitting next to ([a-zA-Z]+)\\.");
		Matcher happyMatcher = happpyPattern.matcher(entry);
		Matcher unhappyMatcher = unhapppyPattern.matcher(entry);

		if (!happyMatcher.matches() && !unhappyMatcher.matches()) {
			throw new IllegalArgumentException(entry);
		}

		if (happyMatcher.matches())
		{
			people.add(happyMatcher.group(1));
			people.add(happyMatcher.group(3));
			happinessMatrix.put(happyMatcher.group(1) + happyMatcher.group(3), Integer.parseInt(happyMatcher.group(2)));
		}
		else if (unhappyMatcher.matches())
		{
			people.add(unhappyMatcher.group(1));
			people.add(unhappyMatcher.group(3));
			happinessMatrix.put(unhappyMatcher.group(1) + unhappyMatcher.group(3), -1 * Integer.parseInt(unhappyMatcher.group(2)));
		}
	}
}
