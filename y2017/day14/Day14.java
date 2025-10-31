package y2017.day14;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import common.Point;
import y2017.day10.Day10;

public class Day14 {

	public static void main(String[] args) {
		String input = "wenycdww";
		int result = IntStream.range(0, 128).mapToObj(i -> input + "-" + i).map(Day10::hashInputAsNums).mapToInt(Day14::countUsed).sum();
		System.out.println(result);
		List<List<Integer>> rowHashes = IntStream.range(0, 128).mapToObj(i -> input + "-" + i).map(Day10::hashInputAsNums).collect(Collectors.toList());
		Set<Point> points = new HashSet<>();
		for (int row = 0 ; row < rowHashes.size() ; row++)
		{
			points.addAll(getUsedLocation(rowHashes.get(row), row));
		}
		int groupCount = 0;
		while (!points.isEmpty())
		{
			Point start = points.stream().findAny().get();
			removeWithNeights(start, points);
			groupCount++;
		}
		System.out.println(groupCount);
	}

	private static void removeWithNeights(Point start, Set<Point> points)
	{
		points.remove(start);
		start.getNonDiagonalNeighbours().stream().filter(points::contains).forEach(p -> removeWithNeights(p, points));
	}

	private static int countUsed(List<Integer> nums)
	{
		return nums.stream()
				.mapToInt(num -> (num & 1) + ((num & 2) / 2) + ((num & 4) / 4) + ((num & 8) / 8))
				.sum();
	}

	private static Set<Point> getUsedLocation(List<Integer> nums, int row)
	{
		Set<Point> rowPoints = new HashSet<>();
		for (int i = 0 ; i < nums.size() ; i++)
		{
			if ((nums.get(i) & 8) > 0)
			{
				rowPoints.add(new Point(row, i * 4));
			}
			if ((nums.get(i) & 4) > 0)
			{
				rowPoints.add(new Point(row, i * 4 + 1));
			}
			if ((nums.get(i) & 2) > 0)
			{
				rowPoints.add(new Point(row, i * 4 + 2));
			}
			if ((nums.get(i) & 1) > 0)
			{
				rowPoints.add(new Point(row, i * 4 + 3));
			}
		}
		return rowPoints;
	}
}
