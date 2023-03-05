package y2021.day7;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day7 {

	public static void main(String[] args) {
		List<Integer> crabs = Input.parseLines("y2021/day7/day7.txt").map(s -> s.split(","))
				.flatMap(Stream::of).map(Integer::valueOf)
				.collect(Collectors.toList());
		int min = crabs.stream().min(Integer::compare).get();
		int max = crabs.stream().max(Integer::compare).get();
		// part1
		int minCost = Integer.MAX_VALUE;
		for (int pos = min ; pos <= max ; pos++)
		{
			int posCpy = pos;
			int cost = crabs.stream().mapToInt(current -> Math.abs(posCpy - current)).sum();
			if (cost < minCost)
			{
				minCost = cost;
			}
		}
		System.out.println(minCost);
		// part2
		minCost = Integer.MAX_VALUE;
		for (int pos = min ; pos <= max ; pos++)
		{
			int posCpy = pos;
			int cost = crabs.stream().mapToInt(current -> Math.abs(posCpy - current)).map(Day7::seriesSum).sum();
			if (cost < minCost)
			{
				minCost = cost;
			}
		}
		System.out.println(minCost);
	}
	private static int seriesSum(int amount)
	{
		return (amount * amount + amount) / 2;
	}
}
