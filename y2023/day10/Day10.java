package y2023.day10;

import static common.Direction.DOWN;
import static common.Direction.LEFT;
import static common.Direction.RIGHT;
import static common.Direction.UP;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import common.AdventSolution;
import common.Direction;
import common.PathFinding;
import common.Point;

public class Day10 extends AdventSolution {

	@Override
	public Object part1Solution() {
		var pipeline = parseGrid(Pipe::fromSymbol);
		var start = pipeline.entrySet().stream().filter(e -> e.getValue() == Pipe.START).findAny().get().getKey();
		var startNeights = start.getNonDiagonalNeighbours().stream().filter(pipeline::containsKey)
				.filter(node -> pipeline.get(node).getDirections().stream().anyMatch(d -> node.move(d).equals(start)))
				.collect(Collectors.toSet());
		pipeline.put(start, Pipe.fromNeighbours(start, startNeights));
		var current = start.getNonDiagonalNeighbours().stream().filter(neight -> connected(start, neight, pipeline))
				.collect(Collectors.toSet());
		var visited = new HashSet<Point>();
		visited.add(start);
		var step = 1;
		while (current.size() > 1) {
			visited.addAll(current);
			current = current.stream()
					.flatMap(point -> point.getNonDiagonalNeighbours().stream()
							.filter(neight -> connected(point, neight, pipeline)))
					.filter(point -> !visited.contains(point)).collect(Collectors.toSet());
			step++;
		}
		return step;
	}

	private boolean connected(Point a, Point b, Map<Point, Pipe> pipeline) {
		if (!(pipeline.containsKey(a) && pipeline.containsKey(b))) {
			return false;
		}
		return pipeline.get(b).getDirections().stream().anyMatch(direction -> b.move(direction).equals(a)) &&
				pipeline.get(a).getDirections().stream().anyMatch(direction -> a.move(direction).equals(b));
	}

	@Override
	public Object part2Solution() {
		var pipeline = parseGrid(Pipe::fromSymbol);
		var start = pipeline.entrySet().stream().filter(e -> e.getValue() == Pipe.START).findAny().get().getKey();
		var startNeights = start.getNonDiagonalNeighbours().stream().filter(pipeline::containsKey)
				.filter(node -> pipeline.get(node).getDirections().stream().anyMatch(d -> node.move(d).equals(start)))
				.collect(Collectors.toSet());
		pipeline.put(start, Pipe.fromNeighbours(start, startNeights));
		var current = start.getNonDiagonalNeighbours().stream().filter(neight -> connected(start, neight, pipeline))
				.collect(Collectors.toSet());
		var visited = new HashSet<Point>();
		visited.add(start);
		while (current.size() > 1) {
			visited.addAll(current);
			current = current.stream()
					.flatMap(point -> point.getNonDiagonalNeighbours().stream()
							.filter(neight -> connected(point, neight, pipeline)))
					.filter(point -> !visited.contains(point)).collect(Collectors.toSet());
		}
		visited.addAll(current);
		printPipe(visited, pipeline);
		var outsideTiles = PathFinding
				.countSteps(new Point(-1, -1), (p, i) -> false, p -> getPart2Moves(p, pipeline, visited)).keySet();
		var allOutsideTiles = outsideTiles.stream()
				.map(tile -> Set.of(tile, tile.move(RIGHT), tile.move(DOWN), tile.move(RIGHT).move(DOWN)))
				.flatMap(Set::stream).collect(Collectors.toSet());
		return Point.range(new Point(0, 0), new Point(Point.maxX(pipeline.keySet()), Point.maxY(pipeline.keySet())))
				.filter(p -> !allOutsideTiles.contains(p)).count();
	}

	private void printPipe(Set<Point> loop, Map<Point, Pipe> pipeline) {
		long maxX = Point.maxX(pipeline.keySet());
		long maxY = Point.maxY(pipeline.keySet());
		for (long y = 0; y <= maxY; y++) {
			for (long x = 0; x <= maxX; x++) {
				if (loop.contains(new Point(x, y))) {
					System.out.print(pipeline.get(new Point(x, y)).printable);
				} else {
					System.out.print(' ');
				}
			}
			System.out.println();
		}
	}

	private Set<Point> getPart2Moves(Point topLeft, Map<Point, Pipe> pipes, Set<Point> loop) {
		var toVisit = new HashSet<Point>();
		// Up
		if ((pipes.containsKey(topLeft) || pipes.containsKey(topLeft.move(RIGHT))) && !(loop.contains(topLeft)
				&& loop.contains(topLeft.move(RIGHT)) && connected(topLeft, topLeft.move(RIGHT), pipes))) {
			toVisit.add(topLeft.move(UP));
		}
		// Down
		if ((pipes.containsKey(topLeft.move(DOWN)) || pipes.containsKey(topLeft.move(DOWN).move(RIGHT)))
				&& !(loop.contains(topLeft.move(DOWN)) && loop.contains(topLeft.move(DOWN).move(RIGHT))
						&& connected(topLeft.move(DOWN), topLeft.move(DOWN).move(RIGHT), pipes))) {
			toVisit.add(topLeft.move(DOWN));
		}
		// Left
		if ((pipes.containsKey(topLeft) || (pipes.containsKey(topLeft.move(DOWN)))) && !(loop.contains(topLeft)
				&& loop.contains(topLeft.move(DOWN)) && connected(topLeft, topLeft.move(DOWN), pipes))) {
			toVisit.add(topLeft.move(LEFT));
		}
		// Right
		if ((pipes.containsKey(topLeft.move(RIGHT)) || (pipes.containsKey(topLeft.move(RIGHT).move(DOWN))))
				&& !(loop.contains(topLeft.move(RIGHT)) && loop.contains(topLeft.move(RIGHT).move(DOWN))
						&& connected(topLeft.move(RIGHT), topLeft.move(RIGHT).move(DOWN), pipes))) {
			toVisit.add(topLeft.move(RIGHT));
		}
		return toVisit;
	}

	public static void main(String[] args) {
		new Day10().solve();
	}

	private enum Pipe {
		UP_DOWN('|', '║', Direction.UP, Direction.DOWN), LEFT_RIGHT('-', '═', Direction.LEFT, Direction.RIGHT),
		UP_LEFT('J', '╝', Direction.UP, Direction.LEFT), UP_RIGHT('L', '╚', Direction.UP, Direction.RIGHT),
		DOWN_LEFT('7', '╗', Direction.DOWN, Direction.LEFT), DOWN_RIGHT('F', '╔', Direction.DOWN, Direction.RIGHT), NONE('.', ' '),
		START('S', 'S');

		private final char symbol;
		private final char printable;
		private final Set<Direction> directions;

		Pipe(char symbol, char printable, Direction... directions) {
			this.symbol = symbol;
			this.printable = printable;
			this.directions = Set.of(directions);
		}

		public static Pipe fromSymbol(char symbol) {
			return Arrays.stream(Pipe.values()).filter(pipe -> pipe.symbol == symbol).findAny()
					.orElseThrow(() -> new IllegalArgumentException("Unknown symbol '" + symbol + "'"));
		}

		public static Pipe fromNeighbours(Point location, Set<Point> neights) {
			var possibleDirections = Set.of(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
			var directions = possibleDirections.stream().filter(d -> neights.contains(location.move(d)))
					.collect(Collectors.toSet());
			return Arrays.stream(Pipe.values()).filter(pipe -> pipe.directions.equals(directions)).findAny()
					.orElseThrow(() -> new IllegalArgumentException("Cannot determine start type"));
		}

		public Set<Direction> getDirections() {
			return directions;
		}
	}
}
