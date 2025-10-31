package y2016.day6;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Input;

public class Day6 {

	public static void main(String[] args) {
		List<String> input = Input.parseLines("y2016/day6/day6.txt", Function.identity()).collect(Collectors.toList());
		for (int i = 0 ; i < 8 ; i++)
		{
			int idx = i;
			Map<Character, Long> charCount = input.stream()
					.collect(Collectors.groupingBy(s -> s.charAt(idx), Collectors.counting()));
			long mostFrequent = charCount.values().stream().min(Comparator.naturalOrder()).get();
			charCount.entrySet().stream().filter(e -> e.getValue() == mostFrequent).map(Map.Entry::getKey).forEach(System.out::print);
			System.out.println();
		}
	}

}
