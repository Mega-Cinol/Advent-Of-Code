package y2018.day10;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day10 {

	public static void main(String[] args) {
		Set<MovingPoint> points = Input.parseLines("y2018/day10/day10.txt", MovingPoint::new).collect(Collectors.toSet());
		int i = 0;
		long size = Long.MAX_VALUE;
		long prevSize = size;
		while (true)
		{
			int iCopy = i;
			Set<Point> pointsAfter = points.stream().map(p -> p.getAfter(iCopy)).collect(Collectors.toSet());
			prevSize = size;
			size = getSize(pointsAfter);
			System.out.println(size);
			if (size > prevSize)
			{
				print(points.stream().map(p -> p.getAfter(iCopy - 1)).collect(Collectors.toSet()));
				break;
			}
			i++;
		}
		System.out.println(i-1);
	}

	private static void print(Set<Point> points) {
		long minX = points.stream().mapToLong(Point::getX).min().getAsLong();
		long maxX = points.stream().mapToLong(Point::getX).max().getAsLong();
		long minY = points.stream().mapToLong(Point::getY).min().getAsLong();
		long maxY = points.stream().mapToLong(Point::getY).max().getAsLong();
		for (long y = minY ; y <= maxY ; y++)
		{
			for (long x = minX ; x <= maxX ; x++)
			{
				if (points.contains(new Point(x, y)))
				{
					System.out.print('#');
				} else {
					System.out.print(' ');
				}
			}
			System.out.println();
		}
	}

	private static long getSize(Set<Point> points)
	{
		long minX = points.stream().mapToLong(Point::getX).min().getAsLong();
		long maxX = points.stream().mapToLong(Point::getX).max().getAsLong();
		long minY = points.stream().mapToLong(Point::getY).min().getAsLong();
		long maxY = points.stream().mapToLong(Point::getY).max().getAsLong();
		return maxX + maxY - minX - minY;
	}

	private static class MovingPoint
	{
		private final Point pos;
		private final Point vel;

		public MovingPoint(String desc)
		{
			Pattern p = Pattern.compile("position=<\\s*([0-9-]*),\\s*([0-9-]*)> velocity=<\\s*([0-9-]*),\\s*([0-9-]*)>");
			Matcher m = p.matcher(desc);
			m.matches();
			pos = new Point(Long.parseLong(m.group(1)), Long.parseLong(m.group(2)));
			vel = new Point(Long.parseLong(m.group(3)), Long.parseLong(m.group(4)));
		}

		public Point getAfter(int seconds)
		{
			return new Point(pos.getX() + vel.getX() * seconds, pos.getY() + vel.getY() * seconds);
		}
	}
}
