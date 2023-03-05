package y2020.day15;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Day15 {

	public static void main(String[] args) {
		List<Integer> numbers = new ArrayList<>();
		Map<Integer, Integer> lastSpokenMap = new HashMap<>();

		initialize(numbers, lastSpokenMap, 0, 13, 1, 16, 6);
		numbers.add(17);

		while (numbers.size() < 30000000)
		{
			int previous = numbers.get(numbers.size() - 1);
			int lastSpoken = lastSpokenMap.getOrDefault(previous, -1);
			lastSpokenMap.put(previous, numbers.size());
			if (lastSpoken == -1)
			{
				numbers.add(0);
			}
			else
			{
				numbers.add(numbers.size() - lastSpoken);
			}
		}
		System.out.println(numbers.get(30000000 - 1));
	}

	private static void initialize(List<Integer> numbers, Map<Integer, Integer> lastSpokenMap, int... initialNumbers)
	{
		numbers.clear();
		lastSpokenMap.clear();
		IntStream.of(initialNumbers).forEach(num -> addNumber(numbers, lastSpokenMap, num));
	}

	private static void addNumber(List<Integer> numbers, Map<Integer, Integer> lastSpokenMap, int number)
	{
		numbers.add(number);
		lastSpokenMap.put(number, numbers.size());
	}
}
