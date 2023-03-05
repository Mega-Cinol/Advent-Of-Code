package y2018.day6;

import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day6 {

	public static void main(String[] args) {
		Set<Point> allPoints = Input.parseLines("y2018/day6/day6.txt", Day6::strToPoint).collect(Collectors.toSet());
		LongSummaryStatistics longStatisticsX = allPoints.stream().mapToLong(Point::getX).summaryStatistics();
		long minX = longStatisticsX.getMin();
		long maxX = longStatisticsX.getMax();
		LongSummaryStatistics longStatisticsY = allPoints.stream().mapToLong(Point::getY).summaryStatistics();
		long minY = longStatisticsY.getMin();
		long maxY = longStatisticsY.getMax();
		Map<Point, Long> areas = Point.range(new Point(minX, minY), new Point(maxX, maxY))
				.map(p -> getNearest(p, allPoints)).filter(p -> p != null)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		Point.range(new Point(minX, minY), new Point(minX, maxY)).map(p -> getNearest(p, allPoints))
				.filter(p -> p != null).distinct().forEach(areas::remove);
		Point.range(new Point(minX, maxY), new Point(maxX, maxY)).map(p -> getNearest(p, allPoints))
				.filter(p -> p != null).distinct().forEach(areas::remove);
		Point.range(new Point(maxX, maxY), new Point(maxX, minY)).map(p -> getNearest(p, allPoints))
				.filter(p -> p != null).distinct().forEach(areas::remove);
		Point.range(new Point(maxX, minY), new Point(minX, minY)).map(p -> getNearest(p, allPoints))
				.filter(p -> p != null).distinct().forEach(areas::remove);
		System.out.println(areas);
		areas.values().stream().mapToLong(Long::longValue).max().ifPresent(System.out::println);
		System.out.println(Point.range(new Point(minX, minY), new Point(maxX, maxY)).mapToLong(p -> getSumDistance(p, allPoints)).filter(d -> d < 10000).count());
	}

	private static long getSumDistance(Point point, Set<Point> allPoints)
	{
		return allPoints.stream().mapToLong(p -> Math.abs(point.getX() - p.getX()) + Math.abs(point.getY() - p.getY())).sum();
	}

	private static Point getNearest(Point point, Set<Point> allPoints) {
		long minDist = Integer.MAX_VALUE;
		Point nearest = null;
		for (Point candidate : allPoints) {
			long dist = point.getManhattanDistance(candidate);
			if (dist < minDist) {
				minDist = dist;
				nearest = candidate;
			} else if (dist == minDist) {
				nearest = null;
			}
		}
		return nearest;
	}

	private static Point strToPoint(String str) {
		String[] coords = str.split(",");
		int x = Integer.parseInt(coords[0].trim());
		int y = Integer.parseInt(coords[1].trim());
		return new Point(x, y);
	}
}
