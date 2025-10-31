package y2022.day23;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Direction;
import common.Input;
import common.Point;

public class Day23 {

	public static void main(String[] args) {
		var fieldsMap = Input.parseGrid("y2022/day23/day23.txt", c -> c == '#');
		var elves = fieldsMap.entrySet().stream().filter(e -> e.getValue()).map(Map.Entry::getKey)
				.collect(Collectors.toSet());
		Queue<ElfMove> directionsToMove = new ArrayDeque<>();
		directionsToMove.add(ElfMove.NORTH);
		directionsToMove.add(ElfMove.SOUTH);
		directionsToMove.add(ElfMove.WEST);
		directionsToMove.add(ElfMove.EAST);
		for (int i = 0; i < 10; i++) {
			var newPoints = elves.stream().collect(Collectors.toMap(Function.identity(),
					position -> calculateProposeMove(position, elves, directionsToMove)));
			var duplicates = findDuplicates(newPoints);
			elves.clear();
			newPoints.entrySet().stream().map(e -> duplicates.contains(e.getValue()) ? e.getKey() : e.getValue())
					.forEach(elves::add);
			directionsToMove.add(directionsToMove.remove());
//			print(elves);
		}
		long allFields = (Point.maxX(elves) - Point.minX(elves) + 1) * (1 + Point.maxY(elves) - Point.minY(elves));
		System.out.println(allFields - elves.size());
		boolean elfMoved = true;
		long round = 10;
		while (elfMoved) {
			var old = new HashSet<Point>(elves);
			var newPoints = elves.stream().collect(Collectors.toMap(Function.identity(),
					position -> calculateProposeMove(position, elves, directionsToMove)));
			var duplicates = findDuplicates(newPoints);
			elves.clear();
			newPoints.entrySet().stream().map(e -> duplicates.contains(e.getValue()) ? e.getKey() : e.getValue())
					.forEach(elves::add);
			elfMoved = !old.equals(elves);
			round++;
			directionsToMove.add(directionsToMove.remove());
		}
		System.out.println(round);
	}

	private static Set<Point> findDuplicates(Map<Point, Point> moves) {
		return moves.entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.counting()))
				.entrySet().stream().filter(e -> e.getValue() > 1).map(Map.Entry::getKey).collect(Collectors.toSet());
	}

	private static Point calculateProposeMove(Point position, Set<Point> others, Queue<ElfMove> directionsToMove) {
		if (position.getNeighbours().stream().noneMatch(others::contains)) {
			return position;
		}
		for (var directionToMove : directionsToMove) {
			if (directionToMove.canMove(position, others)) {
				return directionToMove.move(position);
			}
		}
		return position;
	}

//	private static void print(Set<Point> elves) {
//		var maxX = Point.maxX(elves);
//		var minX = Point.minX(elves);
//		var maxY = Point.maxY(elves);
//		var minY = Point.minY(elves);
//		for (long y = minY; y <= maxY; y++) {
//			for (long x = minX; x <= maxX; x++) {
//				System.out.print(elves.contains(new Point(x, y)) ? '#' : ' ');
//			}
//			System.out.println();
//		}
//	}

	private enum ElfMove {
		NORTH(Direction.UP, Direction.LEFT, Direction.RIGHT), SOUTH(Direction.DOWN, Direction.LEFT, Direction.RIGHT),
		EAST(Direction.RIGHT, Direction.UP, Direction.DOWN), WEST(Direction.LEFT, Direction.UP, Direction.DOWN);

		private final Direction direction;
		private final Set<Direction> freeNeights;

		ElfMove(Direction direction, Direction... freeNeights) {
			this.direction = direction;
			this.freeNeights = Set.of(freeNeights);
		}

		public boolean canMove(Point current, Set<Point> others) {
			return !others.contains(current.move(direction))
					&& freeNeights.stream().map(d -> current.move(direction).move(d)).noneMatch(others::contains);
		}

		public Point move(Point current) {
			return current.move(direction);
		}
	}
}
