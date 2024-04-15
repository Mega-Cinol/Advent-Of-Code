package y2023.day23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.AdventSolution;
import common.Direction;
import common.Point;

public class Day23 extends AdventSolution {

	private enum Tile {
		WALL('#'),
		PATH('.'),
		SLOPE_UP('^'),
		SLOPE_DOWN('v'),
		SLOPE_LEFT('<'),
		SLOPE_RIGHT('>');

		private final char symbol;

		Tile(char symbol) {
			this.symbol = symbol;
		}

		public static Tile fromSymbol(char symbol) {
			return Arrays.stream(values()).filter(v -> v.symbol == symbol).findFirst().orElseThrow();
		}
	}

	@Override
	public Object part1Solution() {
		var map = parseGrid(Tile::fromSymbol);
		var minY = Point.minY(map.keySet());
		var maxY = Point.maxY(map.keySet());
		var start = map.entrySet().stream().filter(e -> e.getKey().getY() == minY).filter(e -> e.getValue() == Tile.PATH).map(Map.Entry::getKey).findAny().get();
		var end = map.entrySet().stream().filter(e -> e.getKey().getY() == maxY).filter(e -> e.getValue() == Tile.PATH).map(Map.Entry::getKey).findAny().get();

		Function<Point, Set<Point>> getPossibleMoves = current -> {
			return switch (map.get(current)) {
			case SLOPE_UP -> Set.of(current.move(Direction.UP));
			case SLOPE_DOWN -> Set.of(current.move(Direction.DOWN));
			case SLOPE_LEFT -> Set.of(current.move(Direction.LEFT));
			case SLOPE_RIGHT -> Set.of(current.move(Direction.RIGHT));
			case PATH -> current.getNonDiagonalNeighbours().stream()
					.filter(map::containsKey)
					.filter(p -> map.get(p) != Tile.WALL)
					.collect(Collectors.toSet());
			case WALL -> throw new UnsupportedOperationException("Unimplemented case: " + map.get(current));
			default -> throw new IllegalArgumentException("Unexpected value: " + map.get(current));
			};
		};
		return longestPath(start, end, getPossibleMoves, map);
	}

	@Override
	public Object part2Solution() {
		var map = parseGrid(Tile::fromSymbol);
		var minY = Point.minY(map.keySet());
		var maxY = Point.maxY(map.keySet());
		var start = map.entrySet().stream().filter(e -> e.getKey().getY() == minY).filter(e -> e.getValue() == Tile.PATH).map(Map.Entry::getKey).findAny().get();
		var end = map.entrySet().stream().filter(e -> e.getKey().getY() == maxY).filter(e -> e.getValue() == Tile.PATH).map(Map.Entry::getKey).findAny().get();

		Function<Point, Set<Point>> getPossibleMoves = current -> {
			return switch (map.get(current)) {
			case PATH, SLOPE_UP, SLOPE_DOWN, SLOPE_LEFT, SLOPE_RIGHT -> current.getNonDiagonalNeighbours().stream()
					.filter(map::containsKey)
					.filter(p -> map.get(p) != Tile.WALL)
					.collect(Collectors.toSet());
			case WALL -> throw new UnsupportedOperationException("Unimplemented case: " + map.get(current));
			default -> throw new IllegalArgumentException("Unexpected value: " + map.get(current));
			};
		};
		return longestPath(start, end, getPossibleMoves, map);
	}

	private long longestPath(Point start, Point end, Function<Point, Set<Point>> getPossibleMoves, Map<Point, Tile> map) {
		var nodes = map.entrySet().stream()
				.filter(e -> e.getValue() != Tile.WALL)
				.map(Map.Entry::getKey)
				.filter(p -> getPossibleMoves.apply(p).size() > 2)
				.collect(Collectors.toCollection(HashSet::new));
		nodes.add(start);
		nodes.add(end);

		var edges = nodes.stream()
				.collect(Collectors.toMap(Function.identity(), node -> findEdgesFrom(node, nodes, getPossibleMoves)));

		var currentPaths = new HashSet<List<Point>>();
		currentPaths.add(List.of(start));
		var completedPaths = new HashSet<List<Point>>();
		while (!currentPaths.isEmpty()) {
			var newPaths = new HashSet<List<Point>>();
			for (var path : currentPaths) {
				var lastNode = path.get(path.size() - 1);
				if (lastNode.equals(end)) {
					completedPaths.add(path);
					continue;
				}
				var nextNodes = edges.get(lastNode)
						.stream()
						.map(Edge::target)
						.filter(node -> !path.contains(node))
						.collect(Collectors.toSet());
				if (nextNodes.isEmpty()) {
					continue;
				}
				nextNodes.stream().map(node -> {
					var newPath = new ArrayList<>(path);
					newPath.add(node);
					return newPath;
				}).forEach(newPaths::add);
			}
			currentPaths.clear();
			currentPaths.addAll(newPaths);
		}
		return completedPaths.stream()
				.mapToLong(path -> pathLength(path, edges))
				.max().getAsLong();
	}

	private long pathLength(List<Point> nodes, Map<Point, Set<Edge>> edges) {
		var pathLength = 0L;
		for (var i = 1 ; i < nodes.size() ; i++) {
			var finalI = i;
			pathLength += edges.get(nodes.get(i - 1)).stream().filter(e -> e.target().equals(nodes.get(finalI))).findFirst()
					.map(Edge::distance).get();
		}
		return pathLength;
	}

	private record Edge(Point target, long distance) {}

	private Set<Edge> findEdgesFrom(Point start, Set<Point> nodes, Function<Point, Set<Point>> getPossibleMoves) {
		return getPossibleMoves.apply(start).stream()
		.map(next -> findNextNode(start, next, nodes, getPossibleMoves))
		.filter(Objects::nonNull)
		.collect(Collectors.toSet());
	}

	private Edge findNextNode(Point start, Point next, Set<Point> nodes, Function<Point, Set<Point>> getPossibleMoves) {
		var distance = 1L;
		var current = next;
		var visited = new HashSet<>();
		visited.add(start);
		while (!nodes.contains(current)) {
			var nextNodes = new HashSet<>(getPossibleMoves.apply(current));
			nextNodes.removeAll(visited);
			if (nextNodes.size() > 1) {
				throw new RuntimeException("Something went wrong");
			}
			if (nextNodes.isEmpty()) {
				return null;
			}
			distance++;
			visited.add(current);
			current = nextNodes.stream().findAny().get();
		}
		return new Edge(current, distance);
	}

	public static void main(String[] args) {
		new Day23().solve();
	}
}
