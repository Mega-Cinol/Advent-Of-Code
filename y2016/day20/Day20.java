package y2016.day20;

import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;
import common.Point;

public class Day20 {
	private final static long MAX = 4294967295L;
	public static void main(String[] args) {
		Set<Point> ranges = Input.parseLines("y2016/day20/day20.txt", s -> {
			return new Point(Stream.of(s.split("-"))
					.map(Long::parseLong)
					.collect(Collectors.toList()));
		}).collect(Collectors.toSet());
		long min = 0;
		while (!isAllowed(min, ranges))
		{
			min = findNextMin(min, ranges);
		}
		System.out.println(min);
		long allowedCount = 0;
		OptionalLong nextBlocked = findNextBlocked(min, ranges);
		while (nextBlocked.isPresent())
		{
			allowedCount += nextBlocked.getAsLong() - min;
			min = nextBlocked.getAsLong();
			while (!isAllowed(min, ranges))
			{
				min = findNextMin(min, ranges);
			}
			if (min > MAX)
			{
				break;
			}
			nextBlocked = findNextBlocked(min, ranges);
		}
		System.out.println(allowedCount);
	}
	private static boolean isAllowed(long value, Set<Point> ranges)
	{
		return ranges.stream()
				.filter(range -> range.getX() <= value)
				.noneMatch(range -> range.getY() >= value);
	}
	private static long findNextMin(long min, Set<Point> ranges)
	{
		return ranges.stream()
				.filter(range -> range.getX() <= min)
				.filter(range -> range.getY() >= min)
				.mapToLong(Point::getY)
				.max()
				.getAsLong() + 1;
	}
	private static OptionalLong findNextBlocked(long value, Set<Point> ranges)
	{
		return ranges.stream()
				.filter(range -> range.getX() > value)
				.mapToLong(Point::getX)
				.min();
	}
}
