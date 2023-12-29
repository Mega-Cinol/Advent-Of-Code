package y2023.day16;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.LongStream;

import common.AdventSolution;
import common.Direction;
import common.Point;

public class Day16 extends AdventSolution {

	public static void main(String[] args) {
		new Day16().solve();
	}

	@Override
	public Object part1Solution() {
		var grid = parseGrid(Type::fromSymbol);
		return beam(grid, new Point(0, 0), Direction.RIGHT, new HashSet<Visited>()).stream().map(Visited::p).distinct()
				.count();
	}

	private static record Visited(Point p, Direction d) {
	}

	private Set<Visited> beam(Map<Point, Type> grid, Point start, Direction direction, Set<Visited> visited) {
		var current = start;
		while (grid.containsKey(current)) {
			if (!visited.add(new Visited(current, direction))) {
				return visited;
			}
			var directions = grid.get(current).updateDirection(direction);
			direction = directions.get(0);
			current = current.move(direction);
			if (directions.size() > 1) {
				visited.addAll(beam(grid, current.move(directions.get(1)), directions.get(1), visited));
			}
		}
		return visited;
	}

	@Override
	public Object part2Solution() {
		var grid = parseGrid(Type::fromSymbol);
		var maxX = Point.maxX(grid.keySet());
		var maxY = Point.maxY(grid.keySet());
		var maxLeftEdge = LongStream.rangeClosed(0, maxY).map(y -> energizedCount(grid, new Point(0, y), Direction.RIGHT)).max().getAsLong();
		var maxRightEdge = LongStream.rangeClosed(0, maxY).map(y -> energizedCount(grid, new Point(maxX, y), Direction.LEFT)).max().getAsLong();
		var maxUpEdge = LongStream.rangeClosed(0, maxX).map(x -> energizedCount(grid, new Point(x, 0), Direction.DOWN)).max().getAsLong();
		var maxDownEdge = LongStream.rangeClosed(0, maxX).map(x -> energizedCount(grid, new Point(x, maxY), Direction.UP)).max().getAsLong();
		return Math.max(Math.max(maxLeftEdge, maxRightEdge), Math.max(maxUpEdge, maxDownEdge));
	}

	private long energizedCount(Map<Point, Type> grid, Point start, Direction direction) {
		var energized = beam(grid, start, direction, new HashSet<>());
		return energized.stream().map(Visited::p).distinct()
				.count();
	}
	private enum Type {
		MIRROR_UP('/'), MIRROR_DOWN('\\'), SPLITTER_V('|'), SPLITTER_H('-'), EMPTY('.');

		private final char symbol;

		Type(char symbol) {
			this.symbol = symbol;
		}

		public List<Direction> updateDirection(Direction currentDirection) {
			return switch (this) {
			case EMPTY -> List.of(currentDirection);
			case MIRROR_DOWN -> switch (currentDirection) {
			case UP -> List.of(Direction.LEFT);
			case DOWN -> List.of(Direction.RIGHT);
			case LEFT -> List.of(Direction.UP);
			case RIGHT -> List.of(Direction.DOWN);
			default -> throw new IllegalArgumentException("Unexpected value: " + currentDirection);
			};
			case MIRROR_UP -> switch (currentDirection) {
			case UP -> List.of(Direction.RIGHT);
			case DOWN -> List.of(Direction.LEFT);
			case LEFT -> List.of(Direction.DOWN);
			case RIGHT -> List.of(Direction.UP);
			default -> throw new IllegalArgumentException("Unexpected value: " + currentDirection);
			};
			case SPLITTER_H -> switch (currentDirection) {
			case UP, DOWN -> List.of(Direction.LEFT, Direction.RIGHT);
			case LEFT, RIGHT -> List.of(currentDirection);
			default -> throw new IllegalArgumentException("Unexpected value: " + currentDirection);
			};
			case SPLITTER_V -> switch (currentDirection) {
			case UP, DOWN -> List.of(currentDirection);
			case LEFT, RIGHT -> List.of(Direction.UP, Direction.DOWN);
			default -> throw new IllegalArgumentException("Unexpected value: " + currentDirection);
			};
			default -> throw new IllegalArgumentException("Unexpected value: " + this);
			};
		}

		public static Type fromSymbol(char symbol) {
			return Arrays.stream(Type.values()).filter(t -> t.symbol == symbol).findFirst().orElse(null);
		}
	}
}
