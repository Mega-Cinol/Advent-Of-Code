package y2021.day13;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day13 {

	public static void main(String[] args) {
		List<String> input = Input.parseLines("y2021/day13/day13.txt").collect(Collectors.toList());
		int line = 0;
		Set<Point> dots = new HashSet<>();
		while (!input.get(line).isEmpty())
		{
			dots.add(new Point(input.get(line)));
			line++;
		}
		line++;
		int foldCount = 0;
		while (line < input.size())
		{
			char orientation = input.get(line).charAt(11);
			int edgeLocation = Integer.parseInt(input.get(line).substring(13));
			line++;
			foldCount++;

			Function<Point, Long> coordToUpdate = orientation == 'x' ? Point::getX : Point::getY;
			BiFunction<Point, Integer, Point> coordUpdator = orientation == 'x' ? (p, e) -> new Point(2 * e - p.getX(), p.getY()) : (p, e) -> new Point(p.getX(), 2 * e - p.getY());
			dots = fold(dots, coordToUpdate, edgeLocation, coordUpdator);
			if (foldCount == 1)
			{
				System.out.println(dots.size());
			}
		}
		for (long y = Point.minY(dots) ; y <= Point.maxY(dots) ; y++)
		{
			for (long x = Point.minX(dots) ; x <= Point.maxX(dots) ; x++)
			{
				System.out.print(dots.contains(new Point(x,y)) ? '#' : ' ');
			}
			System.out.println();
		}
	}

	private static final Set<Point> fold(Set<Point> dots, Function<Point, Long> coordToUpdate, int division, BiFunction<Point, Integer, Point> coordUpdator) {
		Set<Point> newDots = new HashSet<>();
		dots.stream().filter(p -> coordToUpdate.apply(p) <= division).forEach(newDots::add);
		dots.stream().filter(p -> coordToUpdate.apply(p) > division).map(p -> coordUpdator.apply(p, division)).forEach(newDots::add);
		return newDots;
	}
}
