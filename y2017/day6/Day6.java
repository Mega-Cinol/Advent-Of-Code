package y2017.day6;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day6 {

	public static void main(String[] args) {
		List<Integer> memory = Stream.of(Input.parseLines("y2017/day6/day6.txt").findAny().get().split(","))
				.map(Integer::valueOf).collect(Collectors.toList());
		Map<List<Integer>, Integer> seen = new HashMap<>();
		int count = 0;
		while (seen.get(memory) == null)
		{
			seen.put(memory, count);
			count++;
			int from = 15;
			for (int i = 15 ; i >= 0 ; i--)
			{
				if (memory.get(i) >= memory.get(from))
				{
					from = i;
				}
			}
			int amount = memory.get(from);
			memory.set(from, 0);
			int curr = from;
			while (amount > 0)
			{
				curr++;
				curr %= 16;
				memory.set(curr, memory.get(curr) + 1);
				amount--;
			}
		}
		System.out.println(count - seen.get(memory));
	}
}
