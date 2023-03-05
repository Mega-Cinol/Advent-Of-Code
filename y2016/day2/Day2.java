package y2016.day2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Direction;
import common.Input;
import common.Point;

public class Day2 {

	private static final Map<Point, String> NUMBERS = new HashMap<>();
	static {
		NUMBERS.put(new Point(2, 2), "1");
		NUMBERS.put(new Point(1, 1), "2");
		NUMBERS.put(new Point(2, 1), "3");
		NUMBERS.put(new Point(3, 1), "4");
		NUMBERS.put(new Point(0, 0), "5");
		NUMBERS.put(new Point(1, 0), "6");
		NUMBERS.put(new Point(2, 0), "7");
		NUMBERS.put(new Point(3, 0), "8");
		NUMBERS.put(new Point(4, 0), "9");
		NUMBERS.put(new Point(2, -2), "D");
		NUMBERS.put(new Point(1, -1), "A");
		NUMBERS.put(new Point(2, -1), "B");
		NUMBERS.put(new Point(3, -1), "C");
	}
	public static void main(String[] args) {
		List<String> commands = Input.parseLines("y2016/day2/day2.txt", Function.identity()).collect(Collectors.toList());
		Point current = new Point(0, 0);
		for (String cmd : commands)
		{
			current = toDigit(cmd, current);
		}
	}
	private static Point toDigit(String instructions, Point current)
	{
		for (int i = 0 ; i < instructions.length() ; i++)
		{
			Point old = current;
			current = current.move(charToDirection(instructions.charAt(i)));
			if (!isValid(current))
			{
				current = old;
			}
		}
		System.out.println(NUMBERS.get(current));
		return current;
	}

	private static boolean isValid(Point point)
	{
		return NUMBERS.containsKey(point);
    }
	private static Direction charToDirection(char symbol)
	{
		switch (symbol) {
		case 'L':
			return Direction.WEST;
		case 'R':
			return Direction.EAST;
		case 'U':
			return Direction.NORTH;
		case 'D':
			return Direction.SOUTH;
		default:
			return null;
		}
	}
}
