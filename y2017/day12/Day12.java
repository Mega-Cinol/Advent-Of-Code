package y2017.day12;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day12 {

	public static void main(String[] args) {
		Map<Integer, Set<Integer>> pipes = new HashMap<>();
		Input.parseLines("y2017/day12/day12.txt").forEach(i -> addToMap(i, pipes));

		Set<Integer> visited = new HashSet<>();
		countConected(0, pipes, visited);
		System.out.println(visited.size());

		int groupCount = 0;
		while (!pipes.isEmpty())
		{
			groupCount++;
			visited.clear();
			int start = pipes.keySet().stream().findAny().get();
			countConected(start, pipes, visited);
			visited.forEach(pipes::remove);
		}
		System.out.println(groupCount);
	}

	public static void countConected(int from, Map<Integer, Set<Integer>> pipes, Set<Integer> visited)
	{
		if (!visited.add(from))
		{
			return;
		}
		pipes.get(from).forEach(n -> countConected(n, pipes, visited));
	}
	private static void addToMap(String item, Map<Integer, Set<Integer>> pipes)
	{
		int firstSpaceIndex = item.indexOf(' ');
		int key = Integer.parseInt(item.substring(0, firstSpaceIndex));
		Set<Integer> values = Stream.of(item.substring(item.indexOf(' ', firstSpaceIndex + 1) + 1).split(", "))
				.map(Integer::valueOf)
				.collect(Collectors.toSet());
		pipes.put(key, values);
	}
}
