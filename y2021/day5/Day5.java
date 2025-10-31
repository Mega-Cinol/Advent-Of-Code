package y2021.day5;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;
import common.Point;

public class Day5 {

	public static void main(String[] args) {
		// Part1
		long overlap = Input.parseLines("y2021/day5/day5.txt", Line::fromString).filter(Line::isNonDiagonal)
				.flatMap(Line::getPoints).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.values()
				.stream()
				.filter(v -> v > 1)
				.count();
		System.out.println(overlap);
		// Part2
		overlap = Input.parseLines("y2021/day5/day5.txt", Line::fromString)
				.flatMap(Line::getPoints).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.values()
				.stream()
				.filter(v -> v > 1)
				.count();
		System.out.println(overlap);
	}

	private static class Line {
		private final Point start;
		private final Point end;

		public static Line fromString(String str) {
			Pattern p = Pattern.compile("(\\d+),(\\d+) -> (\\d+),(\\d+)");
			Matcher m = p.matcher(str);
			if (!m.matches()) {
				throw new IllegalArgumentException(str);
			}
			return new Line(new Point(Long.parseLong(m.group(1)), Long.parseLong(m.group(2))),
					new Point(Long.parseLong(m.group(3)), Long.parseLong(m.group(4))));
		}

		public Line(Point start, Point end) {
			this.start = start;
			this.end = end;
		}

		public boolean isNonDiagonal() {
			return start.getX() == end.getX() || start.getY() == end.getY();
		}

		public Stream<Point> getPoints() {
			if (isNonDiagonal())
			{
				return Point.range(start, end);
			}
			else
			{
				int dx = start.getX() > end.getX() ? -1 : 1;
				int dy = start.getY() > end.getY() ? -1 : 1;
				return Stream.iterate(start, p -> p.move(dx,dy)).limit(Math.abs(start.getX() - end.getX()) + 1);
			}
		}
	}
}
