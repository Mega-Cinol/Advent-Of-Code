package y2023.day22;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.AdventSolution;
import common.Point;

public class Day22 extends AdventSolution {

	private record Brick(Point start, Point end, Set<Brick> supportedBy, Set<Brick> supports) {
		public static Brick of(String brickDescription) {
			var brickPattern = Pattern.compile("(\\d+),(\\d+),(\\d+)~(\\d+),(\\d+),(\\d+)");
			var brickMatcher = brickPattern.matcher(brickDescription);
			if (!brickMatcher.matches()) {
				throw new IllegalArgumentException(brickDescription);
			}
			var x1 = Long.valueOf(brickMatcher.group(1));
			var y1 = Long.valueOf(brickMatcher.group(2));
			var z1 = Long.valueOf(brickMatcher.group(3));

			var x2 = Long.valueOf(brickMatcher.group(4));
			var y2 = Long.valueOf(brickMatcher.group(5));
			var z2 = Long.valueOf(brickMatcher.group(6));
			var p1 = new Point(x1, y1, z1);
			var p2 = new Point(x2, y2, z2);
			var start = z1 < z2 ? p1 : p2;
			var end = z1 < z2 ? p2 : p1;
			return new Brick(start, end, new HashSet<>(), new HashSet<>());
		}

		public Brick fell(long newBottomHeight) {
			var height = end.getZ() - start.getZ();
			var newStart = new Point(start.getX(), start.getY(), newBottomHeight);
			var newEnd = new Point(end.getX(), end.getY(), newBottomHeight + height);
			return new Brick(newStart, newEnd, new HashSet<>(), new HashSet<>());
		}

		public Set<Point> getXYPoints() {
			var xyPoints = new HashSet<Point>();
			if (start.getX() == end.getX()) {
				for (var y = Math.min(start.getY(), end.getY()) ; y <= Math.max(start.getY(), end.getY()) ; y++) {
					xyPoints.add(new Point(start.getX(), y));
				}
			} else {
				for (var x = Math.min(start.getX(), end.getX()) ; x <= Math.max(start.getX(), end.getX()) ; x++) {
					xyPoints.add(new Point(x, start.getY()));
				}
			}
			return xyPoints;
		}

		public boolean canBeRemoved() {
			return supports.stream().allMatch(Brick::isSupportedByMoreThanOne);
		}

		public long computeRemovalImpact() {
			var removedBricks = new HashSet<Brick>();
			removedBricks.add(this);
			computeRemovalImpact(removedBricks);
			return removedBricks.size() - 1;
		}

		private void computeRemovalImpact(Set<Brick> removedBricks) {
			var newRemovedBricks = supports.stream().filter(brick -> brick.hasNoSupportLeft(removedBricks)).collect(Collectors.toSet());
			removedBricks.addAll(newRemovedBricks);
			newRemovedBricks.forEach(brick -> brick.computeRemovalImpact(removedBricks));
		}

		private boolean hasNoSupportLeft(Set<Brick> removedBricks) {
			var remainingSupport = new HashSet<>(supportedBy);
			remainingSupport.removeAll(removedBricks);
			return remainingSupport.isEmpty();
		}

		private boolean isSupportedByMoreThanOne() {
			return supportedBy.size() > 1;
		}

		@Override
		public int hashCode() {
			return Objects.hash(end, start);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Brick other = (Brick) obj;
			return Objects.equals(end, other.end) && Objects.equals(start, other.start);
		}

		@Override
		public String toString() {
			return "Brick [start=" + start + ", end=" + end + "]";
		}
	}

	private record SupportInfo(Brick brick, long height) {}

	@Override
	public Object part1Solution() {
		var fallenBricks = fellAllBricks(getInput().map(Brick::of).toList());
		return fallenBricks.stream().filter(Brick::canBeRemoved).count();
	}

	@Override
	public Object part2Solution() {
		var fallenBricks = fellAllBricks(getInput().map(Brick::of).toList());
		return fallenBricks.stream().mapToLong(Brick::computeRemovalImpact).sum();
	}

	public static void main(String[] args) {
		new Day22().solve();
	}

	private List<Brick> fellAllBricks(List<Brick> bricks) {
		var heightMap = new HashMap<Point, SupportInfo>();
		var fallenBricks = new ArrayList<Brick>();
		bricks.stream()
				.sorted(Comparator.comparing(b -> b.start().getZ()))
				.forEach(brick -> fellBrick(brick, fallenBricks, heightMap));
		return fallenBricks;
	}

	private void fellBrick(Brick brick, List<Brick> fallenBricks, Map<Point, SupportInfo> heightMap) {
		var xyPoints = brick.getXYPoints();
		var bricksBelow = heightMap.entrySet().stream().filter(e -> xyPoints.contains(e.getKey()))
				.map(Map.Entry::getValue).collect(Collectors.toSet());
		var newHeight = bricksBelow.stream().mapToLong(SupportInfo::height).max().orElse(0) + 1;
		var supportingBricks = bricksBelow.stream().filter(supportInfo -> supportInfo.height() == newHeight - 1).map(SupportInfo::brick).collect(Collectors.toSet());
		var fallenBrick = brick.fell(newHeight);
		fallenBrick.supportedBy().addAll(supportingBricks);
		supportingBricks.forEach(supportingBrick -> supportingBrick.supports().add(fallenBrick));
		fallenBricks.add(fallenBrick);
		xyPoints.forEach(xyPoint -> heightMap.put(xyPoint, new SupportInfo(fallenBrick, Math.max(fallenBrick.start().getZ(), fallenBrick.end().getZ()))));
	}
}
