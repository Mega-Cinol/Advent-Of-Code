package y2020.day10;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import common.Input;

public class Day10 {

	public static void main(String[] args) {
		NavigableSet<Integer> numbers = new TreeSet<>();
		Input.parseLines("y2020/day10/day10.txt", Integer::parseInt, numbers::add);
		long diff1 = 0;
		long diff3 = 0;
		for (int number : numbers) {
			Integer lower = numbers.lower(number);
			if (lower == null) {
				lower = 0;
			}
			if (number - lower == 1) {
				diff1++;
			} else if (number - lower == 3) {
				diff3++;
			}
		}
		System.out.println(diff3 * diff1 + diff1);
		System.out.println(countWays(numbers, 0, new HashMap<>()));
	}

	private static long countWays(NavigableSet<Integer> numbers, int current, Map<Integer, Long> known) {
		if (numbers.higher(current) == null) {
			return 1;
		}
		if (known.containsKey(current))
		{
			return known.get(current);
		}
		long result = 0;
		if (numbers.contains(current + 1)) {
			result += countWays(numbers, current + 1, known);
		}
		if (numbers.contains(current + 2)) {
			result += countWays(numbers, current + 2, known);
		}
		if (numbers.contains(current + 3)) {
			result += countWays(numbers, current + 3, known);
		}
		known.put(current, result);
		return result;
	}
}
