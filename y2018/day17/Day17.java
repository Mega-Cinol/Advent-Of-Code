package y2018.day17;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Direction;
import common.Input;
import common.Point;

public class Day17 {
	enum BlockType
	{
		EPMTY(' '),
		CLAY('#'),
		RUNNING_WATER('|'),
		STILL_WATER('~');
		private final char symbol;
		BlockType(char symbol)
		{
			this.symbol = symbol;
		}
		public char getSymbol()
		{
			return symbol;
		}
	}
	public static void main(String[] args) {
		Map<Point, BlockType> blocked = new HashMap<>();
		Input.parseLines("y2018/day17/day17.txt").forEach(line -> addClay(line, blocked));
		long minY = Point.minY(blocked.keySet());
		spring(new Point(500, minY), blocked);
		printMap(blocked);
		System.out.println(blocked.values().stream().filter(v -> v == BlockType.STILL_WATER || v ==BlockType.RUNNING_WATER).count());
		System.out.println(blocked.values().stream().filter(v -> v == BlockType.STILL_WATER).count());
	}

	private static void addClay(String desc, Map<Point, BlockType> blocked)
	{
		Pattern p = Pattern.compile("[xy]=(\\d+), [xy]=(\\d+)\\.\\.(\\d+)");
		Matcher m = p.matcher(desc);
		if (!m.matches())
		{
			throw new IllegalArgumentException(desc);
		}
		Point start;
		Point end;
		if (desc.startsWith("x"))
		{
			start = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
			end = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(3)));
		} else {
			start = new Point(Integer.parseInt(m.group(2)), Integer.parseInt(m.group(1)));
			end = new Point(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(1)));
		}
		Point.range(start, end).forEach(pt -> blocked.put(pt, BlockType.CLAY));
	}

	private static void spring(Point location, Map<Point, BlockType> blocked)
	{
		long maxY = Point.maxY(blocked.keySet());
		blocked.remove(location);
		while (!blocked.containsKey(location))
		{
			if (location.getY() > maxY)
			{
				return;
			}
			blocked.put(location, BlockType.RUNNING_WATER);
			location = location.move(Direction.NORTH);
		}
		if (blocked.get(location) == BlockType.RUNNING_WATER)
		{
			return;
		}
		location = location.move(Direction.SOUTH);
		Point left = location;
		while (blocked.get(left.move(Direction.WEST)) != BlockType.CLAY && blocked.containsKey(left.move(Direction.NORTH)) && blocked.get(left.move(Direction.NORTH)) != BlockType.RUNNING_WATER)
		{
			blocked.put(left, BlockType.RUNNING_WATER);
			left = left.move(Direction.WEST);
			blocked.put(left, BlockType.RUNNING_WATER);
		}
		Point right = location;
		while (blocked.get(right.move(Direction.EAST)) != BlockType.CLAY && blocked.containsKey(right.move(Direction.NORTH)) && blocked.get(right.move(Direction.NORTH)) != BlockType.RUNNING_WATER)
		{
			blocked.put(right, BlockType.RUNNING_WATER);
			right = right.move(Direction.EAST);
			blocked.put(right, BlockType.RUNNING_WATER);
		}
		while (blocked.getOrDefault(left.move(Direction.NORTH), BlockType.RUNNING_WATER) != BlockType.RUNNING_WATER && blocked.getOrDefault(right.move(Direction.NORTH), BlockType.RUNNING_WATER) != BlockType.RUNNING_WATER &&
				blocked.get(left.move(Direction.WEST)) == BlockType.CLAY && blocked.get(right.move(Direction.EAST)) == BlockType.CLAY)
		{
			Point.range(left, right).forEach(p -> blocked.put(p, BlockType.STILL_WATER));
			location = location.move(Direction.SOUTH);
			left = location;
			while (blocked.get(left.move(Direction.WEST)) != BlockType.CLAY && blocked.containsKey(left.move(Direction.NORTH)) && blocked.get(left.move(Direction.NORTH)) != BlockType.RUNNING_WATER)
			{
				blocked.put(left, BlockType.RUNNING_WATER);
				left = left.move(Direction.WEST);
				blocked.put(left, BlockType.RUNNING_WATER);
			}
			right = location;
			while (blocked.get(right.move(Direction.EAST)) != BlockType.CLAY && blocked.containsKey(right.move(Direction.NORTH)) && blocked.get(right.move(Direction.NORTH)) != BlockType.RUNNING_WATER)
			{
				blocked.put(right, BlockType.RUNNING_WATER);
				right = right.move(Direction.EAST);
				blocked.put(right, BlockType.RUNNING_WATER);
			}
		}
		if (!blocked.containsKey(left.move(Direction.NORTH)))
		{
			spring(left, blocked);
		}
		if (!blocked.containsKey(right.move(Direction.NORTH)))
		{
			spring(right, blocked);
		}
	}

	private static final void printMap(Map<Point, BlockType> blocked)
	{
		long minY = Point.minY(blocked.keySet());
		long maxY = Point.maxY(blocked.keySet());
		long minX = Point.minX(blocked.keySet());
		long maxX = Point.maxX(blocked.keySet());

		System.out.print("   ");
		for (long x = minX ; x <= maxX ; x++)
		{
			System.out.print(x / 100);
		}
		System.out.println();
		System.out.print("   ");
		for (long x = minX ; x <= maxX ; x++)
		{
			System.out.print((x / 10) % 10);
		}
		System.out.println();
		System.out.print("   ");
		for (long x = minX ; x <= maxX ; x++)
		{
			System.out.print(x % 10);
		}
		System.out.println();

		for (long y = minY ; y <= maxY ; y++)
		{
			System.out.print(y / 100);
			System.out.print((y / 10) % 10);
			System.out.print(y % 10);
			for (long x = minX ; x <= maxX ; x++)
			{
				System.out.print(blocked.getOrDefault(new Point(x,y), BlockType.EPMTY).getSymbol());
			}
			System.out.println();
			
		}

	}
}
