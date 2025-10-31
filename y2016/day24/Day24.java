package y2016.day24;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import common.Combinations;
import common.Input;
import common.PathFinding;
import common.Point;

public class Day24 {

	private static Set<Point> points = new HashSet<>();
	private static Map<Integer, Point> keys = new HashMap<>();
	private static Map<Point, Integer> revKeys = new HashMap<>();
	public static void main(String[] args) {
		List<String> input = Input.parseLines("y2016/day24/day24.txt").collect(Collectors.toList());
		parseInput(input);
		Map<Integer, Map<Integer, Integer>> distances = new HashMap<>();
		for (Map.Entry<Integer, Point> key : keys.entrySet())
		{
			PathFinding.countSteps(key.getValue(), (a, b) -> false, p -> p.getNonDiagonalNeighbours().stream()
					.filter(pt -> points.contains(pt)).collect(Collectors.toSet())).entrySet().stream()
			.filter(e -> revKeys.containsKey(e.getKey()))
			.forEach(e -> {
				int sourceKey = key.getKey();
				int targetKey = revKeys.get(e.getKey());
				int distance = e.getValue();
				distances.computeIfAbsent(sourceKey, i -> new HashMap<>()).put(targetKey, distance);
				distances.computeIfAbsent(targetKey, i -> new HashMap<>()).put(sourceKey, distance);
			});
		}
		System.out.println(distances);
		
		Combinations.permutations(keys.keySet().stream().filter(k -> k != 0).collect(Collectors.toSet())).mapToInt(q -> pathToDistance(distances, q)).min().ifPresent(System.out::println);
	}
	private static void parseInput(List<String> input)
	{
		for (int row = 0 ; row < input.size() ; row++)
		{
			for (int col = 0 ; col < input.get(0).length() ; col++)
			{
				if (input.get(row).charAt(col) == '#')
				{
					continue;
				}
				points.add(new Point(row, col));
				if (input.get(row).charAt(col) != '.')
				{
					keys.put(Integer.valueOf("" + input.get(row).charAt(col)), new Point(row, col));
					revKeys.put(new Point(row, col), Integer.valueOf("" + input.get(row).charAt(col)));
				}
			}
		}
	}
	private static int pathToDistance(Map<Integer, Map<Integer, Integer>> distances, Deque<Integer> path)
	{
		int currentKey = 0;
		int distance = 0;
		while (!path.isEmpty())
		{
			int nextKey = path.remove();
			distance += distances.get(currentKey).get(nextKey);
			currentKey = nextKey;
		}
		distance += distances.get(currentKey).get(0);
		return distance;
	}
}
