package y2022.day15;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

import common.Input;
import common.Point;

public class Day15 {

	public static void main(String[] args) {
		var sensors = Input.parseLines("y2022/day15/day15.txt", Sensor::fromString).toList();
		var ranges = sensors.stream().map(s -> s.getExcludedPointAtRow(2000000)).filter(r -> r != null).toList();
		Ranges mergedRanges = new Ranges();
		ranges.forEach(mergedRanges::addRange);
		System.out.println(mergedRanges.getRanges().stream().mapToLong(Range::size).sum() - sensors.stream()
				.map(Sensor::getBeacon).distinct().mapToLong(Point::getY).filter(y -> y == 2000000).count());

		for (long y = 0; y <= 4000000; y++) {
			long yCord = y;
			ranges = sensors.stream().map(s -> s.getExcludedPointAtRow(yCord)).filter(r -> r != null).toList();
			mergedRanges = new Ranges();
			ranges.forEach(mergedRanges::addRange);
			if (mergedRanges.getRanges().size() > 1 || mergedRanges.getRanges().iterator().next().from() > 0
					|| mergedRanges.getRanges().iterator().next().to() < 4000000) {
				var beaconX = (long)LongStream
						.concat(mergedRanges.getRanges().stream().mapToLong(Range::from),
								mergedRanges.getRanges().stream().mapToLong(Range::to))
						.filter(x -> x >= 0).filter(x -> x <= 4000000).average().getAsDouble();
				System.out.println(beaconX * 4000000 + yCord);
			}
		}
	}

	private static class Sensor {
		private final Point center;
		private final Point beacon;

		private Sensor(Point center, Point beacon) {
			this.center = center;
			this.beacon = beacon;
		}

		public static Sensor fromString(String sensorStr) {
			var p = Pattern.compile("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)");
			var m = p.matcher(sensorStr);
			if (!m.matches()) {
				throw new IllegalArgumentException(sensorStr);
			}
			var center = new Point(Long.parseLong(m.group(1)), Long.parseLong(m.group(2)));
			var beacon = new Point(Long.parseLong(m.group(3)), Long.parseLong(m.group(4)));
			return new Sensor(center, beacon);
		}

		public Range getExcludedPointAtRow(long row) {
			long radius = center.getManhattanDistance(beacon);
			long remaining = radius - Math.abs(row - center.getY());
			if (remaining >= 0) {
				return new Range(center.getX() - remaining, center.getX() + remaining);
			}
			return null;
		}

		public Point getBeacon() {
			return beacon;
		}
	}

	private static class Ranges {
		private final Set<Range> ranges = new HashSet<>();

		public void addRange(Range newRange) {
			Optional<Range> toRemove = Optional.empty();
			Range toAdd = newRange;
			boolean merged = false;
			do {
				for (var range : ranges) {
					if (range.overlaping(newRange) > 0 && !range.equals(newRange)) {
						toAdd = newRange.merge(range).get(0);
						toRemove = Optional.of(range);
						merged = true;
						newRange = toAdd;
						break;
					}
					merged = false;
				}
				toRemove.ifPresent(ranges::remove);
				ranges.add(toAdd);
			} while (merged);
		}

		public Set<Range> getRanges() {
			return ranges;
		}
	}

	private static record Range(long from, long to) {
		public long size() {
			return to - from + 1;
		}

		public long overlaping(Range other) {
			if (to < other.from() || from > other.to()) {
				return 0;
			}
			if (other.to() < to) {
				return other.to() - from + 1;
			} else {
				return to - other.from() + 1;
			}
		}

		public List<Range> merge(Range other) {
			if (to < other.from() || from > other.to()) {
				return List.of(this, other);
			} else {
				return List.of(new Range(Math.min(from, other.from()), Math.max(to, other.to())));
			}
		}
	};
}
