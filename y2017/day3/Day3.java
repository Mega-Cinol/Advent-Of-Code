package y2017.day3;

import java.util.HashMap;
import java.util.Map;

import common.Direction;
import common.Point;

public class Day3 {
	public static void main(String[] args)
	{
		Map<Point, Integer> values = new HashMap<>();
		Point current = new Point(0,0);
		int currentValue = 1;
		Direction currentDirection = Direction.EAST;
		while (currentValue <= 325489)
		{
			values.put(current, currentValue);
			current = current.move(currentDirection);
			currentDirection = checkDirection(values, currentDirection, current);
			currentValue = current.getNeighbours().stream().filter(values::containsKey).mapToInt(values::get).sum();
		}
		System.out.println(currentValue);
		for (int i = -4 ; i <= 4 ; i++)
		{
			for (int j = -4 ; j <= 4 ; j++)
			{
				int v = values.computeIfAbsent(new Point(j, i), p -> 0);
				if (v < 1000000)
				{
					System.out.print(' ');
				}
				if (v < 100000)
				{
					System.out.print(' ');
				}
				if (v < 10000)
				{
					System.out.print(' ');
				}
				if (v < 1000)
				{
					System.out.print(' ');
				}
				if (v < 100)
				{
					System.out.print(' ');
				}
				if (v < 10)
				{
					System.out.print(' ');
				}
				System.out.print(v);
				System.out.print(' ');
			}
			System.out.println();
		}
	}
	private static Direction checkDirection(Map<Point, Integer> values, Direction currentDirection, Point current)
	{
		switch (currentDirection) {
		case NORTH:
		case DOWN:
			if (!values.containsKey(current.move(Direction.WEST)))
			{
				return Direction.WEST;
			}
			break;
		case WEST:
		case LEFT:
			if (!values.containsKey(current.move(Direction.SOUTH)))
			{
				return Direction.SOUTH;
			}
			break;
		case SOUTH:
		case UP:
			if (!values.containsKey(current.move(Direction.EAST)))
			{
				return Direction.EAST;
			}
			break;
		case EAST:
		case RIGHT:
			if (!values.containsKey(current.move(Direction.NORTH)))
			{
				return Direction.NORTH;
			}
			break;
		}
		return currentDirection;
	}
}
