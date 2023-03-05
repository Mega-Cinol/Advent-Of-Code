package y2021.day15;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Direction;
import common.Input;
import common.PathFinding;
import common.Point;

public class Day15 {

	public static void main(String[] args) {
		Map<Point, Integer> risks = new HashMap<>();
		List<String> input = Input.parseLines("y2021/day15/day15.txt").collect(Collectors.toList());
		for (int row = 0; row < input.size(); row++) {
			for (int col = 0; col < input.get(row).length(); col++) {
				risks.put(new Point(col, row), input.get(row).charAt(col) - '0');
			}
		}

		// part1
		Point last = new Point(Point.maxX(risks.keySet()), Point.maxY(risks.keySet()));
		Map<Point, Integer> totalRisks = PathFinding.pathWithWeights(new Point(0, 0), found -> found.containsKey(last),
				p -> {
					Set<Point> neights = p.getNonDiagonalNeighbours();
					return neights.stream().filter(risks::containsKey).collect(Collectors.toMap(Function.identity(), risks::get));
				});
		System.out.println(totalRisks.get(last));

		// part2
		Map<Point, Integer> largeRisks = enlarge(risks);
		Point last2 = new Point(Point.maxX(largeRisks.keySet()), Point.maxY(largeRisks.keySet()));
		totalRisks = PathFinding.pathWithWeights(new Point(0, 0), found -> {
			return found.containsKey(last2);
		}, p -> {
			Set<Point> neights = p.getNonDiagonalNeighbours();
			return neights.stream().filter(largeRisks::containsKey).collect(Collectors.toMap(Function.identity(), largeRisks::get));
		});
		System.out.println(totalRisks.get(last2));
	}

	private static Map<Point, Integer> enlarge(Map<Point, Integer> risks) {
		Map<Point, Integer> enlarged = new HashMap<>();
		Map<Point, Integer> firstInCol = risks;
		for (int col = 0; col < 5; col++) {
			Map<Point, Integer> currentTile = firstInCol;
			for (int row = 0; row < 5; row++) {
				enlarged.putAll(currentTile);
				currentTile = move(currentTile, Direction.NORTH);
			}
			firstInCol = move(firstInCol, Direction.EAST);
		}
		return enlarged;
	}

	private static Map<Point, Integer> move(Map<Point, Integer> tile, Direction direction) {
		Point first = new Point(Point.minX(tile.keySet()), Point.minY(tile.keySet()));
		Point last = new Point(Point.maxX(tile.keySet()), Point.maxY(tile.keySet()));
		long stepSize = (direction.isVertical() ? last.getY() - first.getY() : last.getX() - first.getX()) + 1;
		return tile.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().move(direction, stepSize),
				e -> e.getValue() + 1 > 9 ? e.getValue() - 8 : e.getValue() + 1));
	}
}
