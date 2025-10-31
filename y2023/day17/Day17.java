package y2023.day17;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import common.AdventSolution;
import common.Direction;
import common.PathFinding;
import common.Point;

public class Day17 extends AdventSolution {

	public static void main(String[] args) {
		new Day17().solve();
	}

	private static record PointWithHistory(Point current, Direction lastDirection, int directionCount) {
		public PointWithHistory(Point current) {
			this(current, null, 0);
		}
	}

	@Override
	public Object part1Solution() {
		var input = parseGrid(c -> Integer.parseInt("" + c));
		var maxX = Point.maxX(input.keySet());
		var maxY = Point.maxY(input.keySet());
		var paths = PathFinding.pathWithWeights(new PointWithHistory(new Point(0, 0)), result -> false,
				p -> getNextMoves(p, input));
		return paths.entrySet().stream().filter(p -> p.getKey().current().equals(new Point(maxX, maxY)))
				.mapToInt(Map.Entry::getValue).min().getAsInt();
	}

	private Map<PointWithHistory, Integer> getNextMoves(PointWithHistory p, Map<Point, Integer> map) {
		var currentDirection = p.lastDirection();
		var allowedDirections = new HashSet<Direction>();
		if (currentDirection == null) {
			allowedDirections.add(Direction.UP);
			allowedDirections.add(Direction.DOWN);
			allowedDirections.add(Direction.LEFT);
			allowedDirections.add(Direction.RIGHT);
		} else {
			allowedDirections.add(currentDirection.left());
			allowedDirections.add(currentDirection.right());
			if (p.directionCount() < 3) {
				allowedDirections.add(currentDirection);
			}
		}
		return allowedDirections.stream().filter(d -> map.containsKey(p.current().move(d))).collect(Collectors.toMap(
				d -> new PointWithHistory(p.current().move(d), d, currentDirection == d ? p.directionCount() + 1 : 1),
				d -> map.get(p.current().move(d))));
	}

	@Override
	public Object part2Solution() {
		var input = parseGrid(c -> Integer.parseInt("" + c));
		var maxX = Point.maxX(input.keySet());
		var maxY = Point.maxY(input.keySet());
		var paths = PathFinding.pathWithWeights(new PointWithHistory(new Point(0, 0)), result -> false,
				p -> getNextMoves2(p, input));
		return paths.entrySet().stream().filter(p -> p.getKey().current().equals(new Point(maxX, maxY)))
				.mapToInt(Map.Entry::getValue).min().getAsInt();
	}

	private Map<PointWithHistory, Integer> getNextMoves2(PointWithHistory p, Map<Point, Integer> map) {
		var currentDirection = p.lastDirection();
		var allowedDirections = new HashSet<Direction>();
		if (currentDirection == null) {
			allowedDirections.add(Direction.UP);
			allowedDirections.add(Direction.DOWN);
			allowedDirections.add(Direction.LEFT);
			allowedDirections.add(Direction.RIGHT);
		} else {
			allowedDirections.add(currentDirection.left());
			allowedDirections.add(currentDirection.right());
			if (p.directionCount() < 10) {
				allowedDirections.add(currentDirection);
			}
		}

		var nextMoves = new HashMap<PointWithHistory, Integer>();
		for (var newDirection : allowedDirections) {
			var targetPoint = p.lastDirection() == newDirection ? p.current().move(newDirection)
					: p.current().move(newDirection, 4);
			if (!map.containsKey(targetPoint)) {
				continue;
			}
			var targetPointWithHistory = new PointWithHistory(targetPoint, newDirection,
					currentDirection == newDirection ? p.directionCount() + 1
							: newDirection != currentDirection ? 4 : 1);
			var targetCost = 0;
			var currentPoint = p.current();
			while (!currentPoint.equals(targetPoint)) {
				currentPoint = currentPoint.move(newDirection);
				targetCost += map.get(currentPoint);
			}
			nextMoves.put(targetPointWithHistory, targetCost);
		}
		return nextMoves;
	}
}
