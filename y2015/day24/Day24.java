package y2015.day24;

import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import common.Input;

public class Day24 {
	private static int maxDepth = Integer.MAX_VALUE;
	public static void main(String[] args)
	{
		NavigableSet<Integer> numbers = Input.parseLines("y2015/day24/day24.txt", Integer::parseInt)
				.collect(Collectors.toCollection(TreeSet::new));
		int partValue = numbers.stream().mapToInt(Integer::intValue).sum() / 4;
		System.out.println(partValue);
		subGroups(numbers, partValue, 0).stream().filter(s -> s.size() == 4).mapToLong(s -> s.stream().reduce(1, (a, b) -> a*b)).sorted().forEach(System.out::println);
	}
	private static Set<Set<Integer>> subGroups(NavigableSet<Integer> input, int sum, int depth)
	{
		Set<Set<Integer>> results = new HashSet<Set<Integer>>();
		if (depth > maxDepth)
		{
			return results;
		}
		Integer next = sum;
		while (next != null)
		{
			next = input.floor(next);
			if (next != null && next == sum)
			{
				results.clear();
				Set<Integer> sr = new TreeSet<>();
				sr.add(next);
				results.add(sr);
				maxDepth = depth;
				return results;
			}
			if (next == null)
			{
				break;
			}
			Integer n = next;
			input.remove(next);
			Set<Set<Integer>> subResult = subGroups(input, sum - next, depth + 1);
			input.add(next);
			subResult.forEach(sr -> sr.add(n));
			results.addAll(subResult);
			next--;
		}
		return results;
	}
}
