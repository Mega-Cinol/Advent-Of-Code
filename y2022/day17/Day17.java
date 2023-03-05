package y2022.day17;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day17 {

	public static void main(String[] args) {
		var first = Set.of(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0));
		var second = Set.of(new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2));
		var third = Set.of(new Point(2, 2), new Point(2, 1), new Point(0, 0), new Point(1, 0), new Point(2, 0));
		var fourth = Set.of(new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3));
		var fifth = Set.of(new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1));
		var shapes = List.of(first, second, third, fourth, fifth);
		var horizontalMoves = Input.parseLines("y2022/day17/day17.txt").findAny().get();

		Set<Point> occupied = new HashSet<>();
		long topLevel = 0;
		var cnt = 0;

		// arena x from -2 to 4
		for (int i = 0; i < 2022; i++) {
			var shape = new Shape(shapes.get(i % shapes.size()));
			shape.setInitY(topLevel);
//			print(occupied, shape);
			while (shape.step(occupied, horizontalMoves, cnt++)) {
//				print(occupied, shape);
			}
			topLevel = Math.max(topLevel, Point.maxY(shape.getPoints()));
			occupied.addAll(shape.getPoints());
		}
		System.out.println(topLevel);

		occupied = new HashSet<>();
		topLevel = 0;
		cnt = 0;
		var cycleAt = 0L;
		var states = new HashMap<State, Integer>();
		var prefixSize = 0;
		for (long i = 0; i < 1000000000000L; i++) {
			var shape = new Shape(shapes.get((int) (i % shapes.size())));
			shape.setInitY(topLevel);
			while (shape.step(occupied, horizontalMoves, cnt++)) {
			}
			topLevel = Math.max(topLevel, Point.maxY(shape.getPoints()));
			occupied.addAll(shape.getPoints());
			if (states.containsKey(new State(occupied, cnt % horizontalMoves.length(), i % shapes.size()))) {
				cycleAt = i;
				prefixSize = states.get(new State(occupied, cnt % horizontalMoves.length(), i % shapes.size()));
				break;
			}
			states.put(new State(occupied, cnt % horizontalMoves.length(), i % shapes.size()), (int) i);
		}
		var numberOfCycles = (1000000000000L - prefixSize) / (cycleAt - prefixSize);
		var stepsLeft = (1000000000000L - prefixSize) % (cycleAt - prefixSize);
		occupied = new HashSet<>();
		topLevel = 0;
		cnt = 0;
		for (long i = 0; i < prefixSize; i++) {
			var shape = new Shape(shapes.get((int) (i % shapes.size())));
			shape.setInitY(topLevel);
			while (shape.step(occupied, horizontalMoves, cnt++)) {
			}
			topLevel = Math.max(topLevel, Point.maxY(shape.getPoints()));
			occupied.addAll(shape.getPoints());
		}
		var prefixLevel = topLevel;
		for (long i = prefixSize; i < cycleAt; i++) {
			var shape = new Shape(shapes.get((int) (i % shapes.size())));
			shape.setInitY(topLevel);
			while (shape.step(occupied, horizontalMoves, cnt++)) {
			}
			topLevel = Math.max(topLevel, Point.maxY(shape.getPoints()));
			occupied.addAll(shape.getPoints());
		}
		var cycleIncrease = topLevel - prefixLevel;
		for (long i = cycleAt; i < cycleAt + stepsLeft; i++) {
			var shape = new Shape(shapes.get((int) (i % shapes.size())));
			shape.setInitY(topLevel);
			while (shape.step(occupied, horizontalMoves, cnt++)) {
			}
			topLevel = Math.max(topLevel, Point.maxY(shape.getPoints()));
			occupied.addAll(shape.getPoints());
		}
		var lastSteps = topLevel - cycleIncrease - prefixLevel;
		System.out.println(prefixLevel + numberOfCycles * cycleIncrease + lastSteps);
	}

//	private static void print(Set<Point> occupied, Shape shape) {
//		long minX = -2;
//		long maxX = 4;
//		long maxY = Point.maxY(shape.getPoints());
//		System.out.println();
//		for (long y = maxY; y > 0; y--) {
//			System.out.print("|");
//			for (long x = minX; x <= maxX; x++) {
//				System.out.print(occupied.contains(new Point(x, y)) ? "#"
//						: shape.getPoints().contains(new Point(x, y)) ? "@" : " ");
//			}
//			System.out.println("|");
//		}
//		System.out.println("_________");
//	}

	private static class Shape {
		private Set<Point> points = new HashSet<>();

		public Shape(Set<Point> points) {
			this.points.addAll(points);
		}

		public void setInitY(long topLevel) {
			var newPoints = points.stream().map(p -> p.move(0, topLevel + 4)).toList();
			points.clear();
			points.addAll(newPoints);
		}

		public void moveX(char direction, Set<Point> occupied) {
			int offset = direction == '>' ? 1 : -1;
			var newPoints = points.stream().map(p -> p.move(offset, 0)).collect(Collectors.toSet());
			if (newPoints.stream().map(Point::getX).anyMatch(y -> y > 4 || y < -2)) {
				return;
			}
			if (newPoints.stream().anyMatch(occupied::contains)) {
				return;
			}
			points.clear();
			points.addAll(newPoints);

		}

		public boolean moveDown(Set<Point> occupied) {
			var newPoints = points.stream().map(p -> p.move(0, -1)).collect(Collectors.toSet());
			if (newPoints.stream().anyMatch(occupied::contains)) {
				return false;
			}
			if (newPoints.stream().anyMatch(p -> p.getY() <= 0)) {
				return false;
			}
			points.clear();
			points.addAll(newPoints);
			return true;

		}

		public boolean step(Set<Point> occupied, String gases, int cnt) {
			moveX(gases.charAt(cnt % gases.length()), occupied);
			return moveDown(occupied);
		}

		public Set<Point> getPoints() {
			return points;
		}
	}

	private record State(List<Long> ys, int gasPos, long shapeId) {
		public State(Set<Point> occupied, int gasPos, long shapeId) {
			this(evaluateYs(occupied), gasPos, shapeId);
		}

		private static List<Long> evaluateYs(Set<Point> occupied) {
			var topYs = occupied.stream()
					.collect(Collectors.groupingBy(Point::getX,
							Collectors.mapping(Point::getY, Collectors.maxBy(Long::compare))))
					.entrySet().stream().map(Map.Entry::getValue).map(Optional::get).toList();
			return topYs.stream().map(y -> y - topYs.get(0)).toList();
		}
	}
}
