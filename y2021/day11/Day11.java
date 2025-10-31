package y2021.day11;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day11 {

	public static void main(String[] args) {
		// part1
		Map<Point, Integer> octopuses = new HashMap<>();
		List<String> input = Input.parseLines("y2021/day11/day11.txt").collect(Collectors.toList());
		for (int row = 0 ; row < input.size() ; row++)
		{
			for (int col = 0 ; col < input.get(row).length() ; col++)
			{
				octopuses.put(new Point(col, row), input.get(row).charAt(col) - '0');
			}
		}
		int flashes = 0;
		for (int i = 0 ; i < 100 ; i++)
		{
			Map<Point, Integer> newOctopuses = new HashMap<>();
			octopuses.entrySet().forEach(e -> newOctopuses.put(e.getKey(), e.getValue() + 1));
			Set<Point> toFlash = newOctopuses.entrySet().stream().filter(e -> e.getValue() == 10).map(Map.Entry::getKey).collect(Collectors.toSet());
			Set<Point> flashed = new HashSet<>();
			toFlash.stream().forEach(p -> flash(p, newOctopuses, flashed));
			flashed.stream().forEach(p -> newOctopuses.put(p, 0));
			flashes += flashed.size();
			octopuses = newOctopuses;
		}
		System.out.println(flashes);
		octopuses = new HashMap<>();
		for (int row = 0 ; row < input.size() ; row++)
		{
			for (int col = 0 ; col < input.get(row).length() ; col++)
			{
				octopuses.put(new Point(col, row), input.get(row).charAt(col) - '0');
			}
		}
		int step = 0;
		while (true)
		{
			step++;
			Map<Point, Integer> newOctopuses = new HashMap<>();
			octopuses.entrySet().forEach(e -> newOctopuses.put(e.getKey(), e.getValue() + 1));
			Set<Point> toFlash = newOctopuses.entrySet().stream().filter(e -> e.getValue() == 10).map(Map.Entry::getKey).collect(Collectors.toSet());
			Set<Point> flashed = new HashSet<>();
			toFlash.stream().forEach(p -> flash(p, newOctopuses, flashed));
			if (flashed.size() == octopuses.size())
			{
				break;
			}
			flashed.stream().forEach(p -> newOctopuses.put(p, 0));
			octopuses = newOctopuses;
		}
		System.out.println(step);
	}
	private static void flash(Point start, Map<Point, Integer> octopuses, Set<Point> flashed)
	{
		if (octopuses.get(start) < 10 || flashed.contains(start))
		{
			return;
		}
		flashed.add(start);
		start.getNeighbours().stream().filter(octopuses::containsKey).forEach(p -> octopuses.merge(p, 1, (o, n) -> o + n));
		Set<Point> toFlash = start.getNeighbours().stream().filter(octopuses::containsKey).filter(p -> octopuses.get(p) > 9).collect(Collectors.toSet());
		for (Point p : toFlash)
		{
			flash(p, octopuses, flashed);
		}
	}
}
