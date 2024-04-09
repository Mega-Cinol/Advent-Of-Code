package y2023.day21;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import common.AdventSolution;
import common.PathFinding;
import common.Point;

public class Day21 extends AdventSolution {

	public static void main(String[] args) {
		new Day21().solve();
	}

	private enum GridElement {
		ROCK('#'), PLOT('.'), START('S');

		private final char symbol;

		GridElement(char symbol) {
			this.symbol = symbol;
		}

		public static GridElement from(char element) {
			return Arrays.stream(values()).filter(e -> e.symbol == element).findAny().orElse(null);
		}
	}

	@Override
	public Object part1Solution() {
		var grid = parseGrid(GridElement::from);
		var accessiblePoints = grid.entrySet().stream().filter(e -> e.getValue() != GridElement.ROCK)
				.map(Map.Entry::getKey).collect(Collectors.toSet());
		var startPoint = grid.entrySet().stream().filter(e -> e.getValue() == GridElement.START).map(Map.Entry::getKey)
				.findAny().get();
		var paths = PathFinding.pathWithWeights(startPoint, m -> false, p -> p.getNonDiagonalNeighbours().stream()
				.filter(accessiblePoints::contains).collect(Collectors.toMap(Function.identity(), e -> 1)));
		return paths.entrySet().stream().filter(e -> e.getValue() <= 64).filter(e -> e.getValue() % 2 == 0).count();
	}

	private static final long PART2_STEPS = 26501365;

	private enum Edge {
		LEFT {
			@Override
			public boolean containsPoint(Point point, long maxX, long maxY) {
				return point.getX() == 0;
			}

			@Override
			public Point reversePoint(Point point, long maxX, long maxY) {
				return new Point(maxX, point.getY());
			}
		},
		RIGHT {
			@Override
			public boolean containsPoint(Point point, long maxX, long maxY) {
				return point.getX() == maxX;
			}

			@Override
			public Point reversePoint(Point point, long maxX, long maxY) {
				return new Point(0, point.getY());
			}
		},
		TOP {
			@Override
			public boolean containsPoint(Point point, long maxX, long maxY) {
				return point.getY() == 0;
			}

			@Override
			public Point reversePoint(Point point, long maxX, long maxY) {
				return new Point(point.getX(), maxY);
			}
		},
		BOTTOM {
			@Override
			public boolean containsPoint(Point point, long maxX, long maxY) {
				return point.getY() == maxY;
			}

			@Override
			public Point reversePoint(Point point, long maxX, long maxY) {
				return new Point(point.getX(), 0);
			}
		};
		public abstract boolean containsPoint(Point point, long maxX, long maxY);
		public abstract Point reversePoint(Point point, long maxX, long maxY);
	}

	private static class EntryPointDistancesCache {
		private final Set<Point> accessiblePoints;
		private final Map<Point, Map<Point, Integer>> distances = new HashMap<>();

		public EntryPointDistancesCache(Set<Point> accessiblePoints) {
			this.accessiblePoints = Set.copyOf(accessiblePoints);
		}

		public Map<Point, Integer> getDistancesFromPoint(Point startPoint) {
			return distances.computeIfAbsent(startPoint, this::calculateDistancesFromPoint);
		}

		private Map<Point, Integer> calculateDistancesFromPoint(Point startPoint) {
			return PathFinding.pathWithWeights(startPoint, m -> false, p -> p.getNonDiagonalNeighbours().stream()
					.filter(accessiblePoints::contains).collect(Collectors.toMap(Function.identity(), e -> 1)));
		}
	}

	private Set<Point> getAccessibleCount(EntryPointDistancesCache distances, Point startPoint, long limit) {
		return distances.getDistancesFromPoint(startPoint).entrySet().stream()
				.filter(e -> e.getValue() % 2 == limit % 2).filter(e -> e.getValue() <= limit).map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}

	private Map<Point, Integer> nearestPointsOnEdge(Map<Point, Integer> distances, Predicate<Map.Entry<Point, Integer>> wallSelector) {
		var orderedPoints = distances.entrySet().stream().filter(wallSelector)
				.sorted(Comparator.comparing((Map.Entry<Point, Integer> e) -> e.getKey().getX())
						.thenComparing((Map.Entry<Point, Integer> e) -> e.getKey().getY()))
				.toList();
		var extreme = new HashMap<Point, Integer>();
		if (orderedPoints.get(0).getValue() < orderedPoints.get(1).getValue()) {
			extreme.put(orderedPoints.get(0).getKey(), orderedPoints.get(0).getValue());
		}
		if (orderedPoints.get(orderedPoints.size() - 1).getValue() < orderedPoints.get(orderedPoints.size() - 2).getValue()) {
			extreme.put(orderedPoints.get(orderedPoints.size() - 1).getKey(), orderedPoints.get(orderedPoints.size() - 1).getValue());
		}
		for (int i = 1 ; i < orderedPoints.size() - 1 ; i++) {
			if ((orderedPoints.get(i).getValue() < orderedPoints.get(i + 1).getValue()) && (orderedPoints.get(i).getValue() < orderedPoints.get(i - 1).getValue())) {
				extreme.put(orderedPoints.get(i).getKey(), orderedPoints.get(i).getValue());
			}
		}
		return extreme;
	}

	private static record StraightLineSummary(long fullyVisitedTiles, long visitedFields) {
	}

	private StraightLineSummary straightLine(EntryPointDistancesCache distancesCache, Point startPoint, Edge edge,
			long stepsLimit, long maxX, long maxY) {
		var distancesFromInit = distancesCache.getDistancesFromPoint(startPoint);
		var edgeStartPoints = nearestPointsOnEdge(distancesFromInit, e -> edge.containsPoint(e.getKey(), maxX, maxY));
		var distancesFromEdge = edgeStartPoints.entrySet().stream()
				.map(e -> Map.entry(edge.reversePoint(e.getKey(), maxX, maxY), e.getValue() + 1))
				.map(edgePoint -> distancesCache.getDistancesFromPoint(edgePoint.getKey()).entrySet().stream()
						.map(e -> Map.entry(e.getKey(), e.getValue() + edgePoint.getValue()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
				.reduce(this::mergeDistances).get();

		var fullyReachableTiles = 0;
		var visitedFields = 0;
		while(allReachable(distancesFromEdge, stepsLimit)) {
			fullyReachableTiles++;
			visitedFields += distancesFromEdge.values().stream().filter(v -> v % 2 == stepsLimit % 2).count();

			edgeStartPoints = nearestPointsOnEdge(distancesFromEdge, e -> edge.containsPoint(e.getKey(), maxX, maxY));
			distancesFromEdge = edgeStartPoints.entrySet().stream()
					.map(e -> Map.entry(edge.reversePoint(e.getKey(), maxX, maxY), e.getValue() + 1))
					.map(edgePoint -> distancesCache.getDistancesFromPoint(edgePoint.getKey()).entrySet().stream()
							.map(e -> Map.entry(e.getKey(), e.getValue() + edgePoint.getValue()))
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
					.reduce(this::mergeDistances).get();
		}
		while(anyReachable(distancesFromEdge, stepsLimit)) {
			visitedFields += distancesFromEdge.values().stream().filter(v -> v % 2 == stepsLimit % 2)
					.filter(v -> v <= stepsLimit).count();

			edgeStartPoints = nearestPointsOnEdge(distancesFromEdge, e -> edge.containsPoint(e.getKey(), maxX, maxY));
			distancesFromEdge = edgeStartPoints.entrySet().stream()
					.map(e -> Map.entry(edge.reversePoint(e.getKey(), maxX, maxY), e.getValue() + 1))
					.map(edgePoint -> distancesCache.getDistancesFromPoint(edgePoint.getKey()).entrySet().stream()
							.map(e -> Map.entry(e.getKey(), e.getValue() + edgePoint.getValue()))
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
					.reduce(this::mergeDistances).get();
		}
		return new StraightLineSummary(fullyReachableTiles, visitedFields);
	}

	private boolean allReachable(Map<Point, Integer> distances, long stepsLeft) {
		return distances.values().stream().allMatch(d -> d <= stepsLeft);
	}
	private boolean anyReachable(Map<Point, Integer> distances, long stepsLeft) {
		return distances.values().stream().anyMatch(d -> d <= stepsLeft);
	}
	private Map<Point, Integer> mergeDistances(Map<Point, Integer> first, Map<Point, Integer> second) {
		return first.entrySet().stream().map(e -> {
			var shorterDistance = e.getValue() <= second.get(e.getKey()) ? e.getValue() : second.get(e.getKey());
			return Map.entry(e.getKey(), shorterDistance);
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private long getAllAccessibleFields(Set<Point> accessiblePoints, Point startPoint, long stepsLimit) {
		var maxX = Point.maxX(accessiblePoints);
		var maxY = Point.maxY(accessiblePoints);
		var distancesCache = new EntryPointDistancesCache(accessiblePoints);

		var accessibleInitialTile = getAccessibleCount(distancesCache, startPoint, stepsLimit).size();

		// Straight line
		var lineLeft = straightLine(distancesCache, startPoint, Edge.LEFT, stepsLimit, maxX, maxY);
		var accessibleLeft = lineLeft.visitedFields();

		var lineRight = straightLine(distancesCache, startPoint, Edge.RIGHT, stepsLimit, maxX, maxY);
		var accessibleRight = lineRight.visitedFields();

		var accessibleTop = straightLine(distancesCache, startPoint, Edge.TOP, stepsLimit, maxX, maxY).visitedFields();

		var accessibleBottom = straightLine(distancesCache, startPoint, Edge.BOTTOM, stepsLimit, maxX, maxY).visitedFields();

		var topLeftFullTriangle = getTriangle(distancesCache, new Point(maxX, maxY), new Point(0, maxY),
				stepsLimit - distancesCache.getDistancesFromPoint(startPoint).get(new Point(0L, 0L)) - 2);
		var bottomLeftFullTriangle = getTriangle(distancesCache, new Point(maxX, 0), new Point(0, 0),
				stepsLimit - distancesCache.getDistancesFromPoint(startPoint).get(new Point(0L, maxY)) - 2); 
		var topRightFullTriangle = getTriangle(distancesCache, new Point(0, maxY), new Point(maxX, maxY),
				stepsLimit - distancesCache.getDistancesFromPoint(startPoint).get(new Point(maxX, 0L)) - 2);
		var bottomRightFullTriangle = getTriangle(distancesCache, new Point(0, 0), new Point(maxX, 0),
				stepsLimit - distancesCache.getDistancesFromPoint(startPoint).get(new Point(maxX, maxY)) - 2);

		return accessibleInitialTile + accessibleLeft + accessibleRight + accessibleTop + accessibleBottom + topLeftFullTriangle
				+ bottomLeftFullTriangle + topRightFullTriangle + bottomRightFullTriangle;
	}

	@Override
	public Object part2Solution() {
		var grid = parseGrid(GridElement::from);
		var accessiblePoints = grid.entrySet().stream().filter(e -> e.getValue() != GridElement.ROCK)
				.map(Map.Entry::getKey).collect(Collectors.toSet());
		var startPoint = grid.entrySet().stream().filter(e -> e.getValue() == GridElement.START).map(Map.Entry::getKey)
				.findAny().get();

		return getAllAccessibleFields(accessiblePoints, startPoint, PART2_STEPS);
	}

	private long getTriangle(EntryPointDistancesCache distances, Point startPoint, Point endPoint, long stepsLeft) {
		var distancesFromStart = distances.getDistancesFromPoint(startPoint);
		var distanceToEnd = distancesFromStart.get(endPoint);
		var longestDistance = distancesFromStart.values().stream().max(Comparator.naturalOrder()).get();
		var mapCount = (stepsLeft - longestDistance + distanceToEnd + 1) / (distanceToEnd + 1);
		var stepsLeftAfterFull = stepsLeft - mapCount * (distanceToEnd + 1);

		var sameAsInitial = mapCount % 2 == 0 ? mapCount * mapCount / 4 : (mapCount + 1) / 2 * (mapCount / 2 + 1);
		var oppositeToInitial = mapCount % 2 == 0 ? (mapCount + 2) * mapCount / 4 : (mapCount + 2) / 2 * (mapCount / 2);

		var accessibleFieldsAsInitial = distancesFromStart.values().stream().filter(v -> v % 2 == stepsLeft % 2).count();
		var accessibleFieldsOppositeToInitial = distancesFromStart.values().stream().filter(v -> v % 2 != stepsLeft % 2)
				.count();

		var fullTriangles = accessibleFieldsAsInitial * sameAsInitial + accessibleFieldsOppositeToInitial * oppositeToInitial;

		var multiplier = mapCount + 1;
		while (stepsLeftAfterFull >= 0) {
			var finalStepsLeft = stepsLeftAfterFull; 
			fullTriangles += distancesFromStart.values().stream().filter(v -> v % 2 == finalStepsLeft % 2).filter(v -> v <= finalStepsLeft).count() * multiplier;
			multiplier++;
			stepsLeftAfterFull -= distanceToEnd + 1;
		}
		return fullTriangles;
	}
}
