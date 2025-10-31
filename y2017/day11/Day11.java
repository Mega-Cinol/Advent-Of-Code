package y2017.day11;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import common.Input;
import common.Point;

public class Day11 {

	public static void main(String[] args) {
		System.out.println(Input.parseLines("y2017/day11/day11.txt").findFirst().map(Day11::fromString).get());

	}

	private static String fromString(String address) {
		Stream<String> steps = Stream.of(address.split(","));
		Point current = new Point(0, 0);
		Set<Point> visited = new HashSet<>();
		visited.add(current);
		steps.map(Day11::pointMover).reduce(current, (p, op) -> {
			Point dest = op.apply(p);
			visited.add(dest);
			return dest;
		}, (op1, op2) -> {
			throw new IllegalStateException();
		});
		visited.stream().mapToLong(Day11::dist).max().ifPresent(System.out::println);
		return "";
	}

	private static long dist(Point point)
	{
		return Math.abs(point.getX()) + Math.max(0, Math.abs(point.getY()) - Math.abs(point.getX())) / 2;
	}
	private static UnaryOperator<Point> pointMover(String direction) {
		switch (direction) {
		case "ne":
			return p -> p.move(1, -1);
		case "nw":
			return p -> p.move(-1, -1);
		case "n":
			return p -> p.move(0, -2);
		case "se":
			return p -> p.move(1, 1);
		case "sw":
			return p -> p.move(-1, 1);
		case "s":
			return p -> p.move(0, 2);
		default:
			throw new IllegalArgumentException(direction);

		}
	}

}
