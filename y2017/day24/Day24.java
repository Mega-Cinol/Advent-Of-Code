package y2017.day24;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;
import common.Point;

public class Day24 {
	private static Set<Point> points;
	public static void main(String[] args) {
		points = Input.parseLines("y2017/day24/day24.txt", line -> line.split("/"))
				.map(arr -> Stream.of(arr).map(Long::valueOf).collect(Collectors.toList())).map(Point::new)
				.collect(Collectors.toSet());
		System.out.println(findHighestValue(0));
		findPaths(0).stream().sorted(new PathCmp()).forEachOrdered(Day24::printPath);;
	}

	private static void printPath(Set<Point> path)
	{
		long strength = path.stream().mapToLong(p -> p.getX() + p.getY()).sum();
		System.out.println(path + " strength: " + strength + " length: " + path.size());
	}
	private static long findHighestValue(long start)
	{
		long result = 0;
		Set<Point> possibleNext = points.stream()
				.filter(p -> p.getX() == start || p.getY() == start)
				.collect(Collectors.toSet());
		for (Point next : possibleNext)
		{
			long otherEnd = next.getX() == start ? next.getY() : next.getX();
			long tileValue = next.getX() + next.getY();
			points.remove(next);
			tileValue += findHighestValue(otherEnd);
			points.add(next);
			result = Math.max(tileValue, result);
		}
		return result;
	}

	private static Set<Set<Point>> findPaths(long start)
	{
		Set<Set<Point>> result = new HashSet<>();
		Set<Point> possibleNext = points.stream()
				.filter(p -> p.getX() == start || p.getY() == start)
				.collect(Collectors.toSet());
		for (Point next : possibleNext)
		{
			long otherEnd = next.getX() == start ? next.getY() : next.getX();
			points.remove(next);
			Set<Set<Point>> tilePaths = findPaths(otherEnd);
			tilePaths.forEach(p -> p.add(next));
			Set<Point> selfPath = new HashSet<>();
			selfPath.add(next);
			tilePaths.add(selfPath);
			result.addAll(tilePaths);
			points.add(next);
		}
		return result;
	}

	private static class PathCmp implements Comparator<Set<Point>>
	{
		@Override
		public int compare(Set<Point> o1, Set<Point> o2) {
			long strength1 = o1.stream().mapToLong(p -> p.getX() + p.getY()).sum();
			long strength2 = o2.stream().mapToLong(p -> p.getX() + p.getY()).sum();
			if (o1.size() == o2.size())
			{
				return (int) (strength1 - strength2);
			}
			return o1.size() - o2.size();
		}
	}
}
