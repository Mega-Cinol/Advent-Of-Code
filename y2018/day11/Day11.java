package y2018.day11;

import java.util.HashMap;
import java.util.Map;

import common.Point;

public class Day11 {
	private static final Map<Point, Long> pointValues = new HashMap<>();

	public static void main(String[] args) {
		Point.range(new Point(1, 1), new Point(300, 300)).forEach(p -> pointValues.put(p, getPointValue(p)));
		long max = -10;
		int maxSize = 0;
		int minSize = 0;
		Point maxPoint = null;
		for (int x = 1; x <= 300; x++) {
			if (x + minSize > 300)
			{
				break;
			}
			for (int y = 1; y <= 300; y++) {
				if (y + minSize > 300)
				{
					break;
				}
				long previous = Point.range(new Point(x,y), new Point(x + minSize, y + minSize)).mapToLong(pointValues::get).sum();
				for (int size = minSize + 1; size < Math.min(300 - x, 300 - y); size++) {
					long current = previous;
					for (int i = 0 ; i <= size ; i++)
					{
						current += pointValues.get(new Point(x + i, y + size));
					}
					for (int i = 0 ; i < size ; i++)
					{
						current += pointValues.get(new Point(x + size, y + i));
					}
					if (current > max) {
						max = current;
						maxPoint = new Point(x, y);
						maxSize = size + 1;
						minSize = getMinSize(max);
					}
					previous = current;
				}
				System.out.println(((x - 1) * 300 + (y - 1)) / 900. + "%");
			}
		}
		System.out.println(maxPoint);
		System.out.println(maxSize);
	}

	private static int getMinSize(long max)
	{
		int minSize = 0;
		while (max >= 9 * minSize * minSize)
		{
			minSize++;
		}
		System.out.println("new min size: " + (minSize - 1) + " from max " + max);
		return minSize - 1;
	}

	private static long getPointValue(Point p) {
		return (((((p.getX() + 10) * p.getY() + 1308) * (p.getX() + 10)) / 100) % 10) - 5;
	}
}
