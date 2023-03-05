package y2021.day6;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day6 {

	public static void main(String[] args) {
		// part1
		countAfterDays(80);
		// part2
		countAfterDays(256);
	}

	private static void countAfterDays(int nDays)
	{
		Map<Integer, Long> daysToCount = Input.parseLines("y2021/day6/day6.txt").map(s -> s.split(","))
				.flatMap(Stream::of).map(Integer::valueOf)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		for (int day = 0 ; day < nDays ; day++)
		{
			long newFishes = daysToCount.getOrDefault(0, 0L);
			for (int i = 0 ; i < 8 ; i++)
			{
				daysToCount.put(i, daysToCount.getOrDefault(i+1, 0L));
			}
			daysToCount.put(8, newFishes);
			daysToCount.merge(6, newFishes, (f1, f2) -> f1+f2);
		}
		System.out.println(daysToCount.values().stream().mapToLong(Long::longValue).sum());
	}
}
