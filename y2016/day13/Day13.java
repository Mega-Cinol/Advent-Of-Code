package y2016.day13;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import common.PathFinding;
import common.Point;

public class Day13 {

	public static void main(String[] args) {
		Point initial = new Point(1,1);
		Map<Point, Boolean> walls = new HashMap<>();
		for (int x = 0 ; x <= 50 ; x++)
		{
			for (int y = 0 ; y <= 50 ; y++)
			{
				if (isWall(new Point(x,y)))
				{
					System.out.print("#");
				}
				else
				{
					System.out.print(" ");
				}
			}
			System.out.println();
		}
		System.out.println(PathFinding.countSteps(initial, (point, depth) -> point.equals(new Point(31,39)), p -> getNeighbours(p, walls)).get(new Point(31,39)));
		System.out.println(PathFinding.countSteps(initial, (point, depth) -> depth == 50, p -> getNeighbours(p, walls)).size());
	}
	private static Set<Point> getNeighbours(Point point, Map<Point, Boolean> walls)
	{
		return point.getNeighbours().stream()
		.filter(p -> p.getX() >= 0 && p.getY() >= 0)
		.filter(p -> p.getX() == point.getX() || p.getY() == point.getY())
		.filter(p -> !isWall(p, walls))
		.collect(Collectors.toSet());
	}
	private static boolean isWall(Point point, Map<Point, Boolean> walls)
	{
		return walls.computeIfAbsent(point, Day13::isWall);
	}
	private static boolean isWall(Point point)
	{
		long number = point.getX() * point.getX() + 3 * point.getX() + 2 * point.getX() * point.getY() + point.getY() + point.getY() * point.getY();
		number += 1364;
		int ones = 0;
		long mask = 1;
		while (mask <= number)
		{
			if ((mask & number) > 0)
			{
				ones++;
			}
			mask *= 2;
		}
		return ones % 2 == 1;
	}
}
