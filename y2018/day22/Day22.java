package y2018.day22;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import common.Direction;
import common.PathFinding;
import common.Point;

public class Day22 {
	private static final int DEPTH = 3066;
	private static final Point TARGET = new Point(13, 726);
	private static final Map<Point, Long> GEO_INDEX_CACHE = new HashMap<>();

	public static void main(String[] args) {
		System.out.println(Point.range(new Point(0, 0), TARGET).mapToLong(Day22::getType).sum());

		Point.range(new Point(0, 0), TARGET.move(10, 10)).mapToLong(Day22::getType).forEach(p -> {
		});
		Map<PointWithEquip, Integer> distances = PathFinding.pathWithWeights(new PointWithEquip(new Point(0,0), Equip.TORCH), visited -> visited.containsKey(new PointWithEquip(TARGET, Equip.TORCH)), PointWithEquip::getNeights);
		System.out.println(distances.get(new PointWithEquip(TARGET, Equip.TORCH)));
	}

	private static long getErosionIndex(Point location) {
		return (GEO_INDEX_CACHE.computeIfAbsent(location, Day22::getGeoIndex) + DEPTH) % 20183;
	}

	private static long getType(Point location) {
		return getErosionIndex(location) % 3;
	}

	private static long getGeoIndex(Point location) {
		if (GEO_INDEX_CACHE.containsKey(location)) {
			return GEO_INDEX_CACHE.get(location);
		}
		long geoIndex = 0;
		if (location.equals(new Point(0, 0))) {
			geoIndex = 0;
		} else if (location.equals(TARGET)) {
			geoIndex = 0;
		} else if (location.getX() == 0) {
			geoIndex = location.getY() * 48271;
		} else if (location.getY() == 0) {
			geoIndex = location.getX() * 16807;
		} else {
			geoIndex = getErosionIndex(location.move(Direction.SOUTH)) * getErosionIndex(location.move(Direction.WEST));
		}
		GEO_INDEX_CACHE.put(location, geoIndex);
		return geoIndex;
	}

	private enum Equip {
		NOTHING(0), TORCH(1), CLIMBING(2);

		private final int banedType;

		Equip(int type) {
			banedType = type;
		}

		public int getBanedType() {
			return banedType;
		}
	}

	private static class PointWithEquip {
		private final Point point;
		private final Equip equip;

		public PointWithEquip(Point point, Equip equip) {
			this.point = point;
			this.equip = equip;
		}

		public Map<PointWithEquip, Integer> getNeights()
		{
			Map<PointWithEquip, Integer> neights = new HashMap<>();
			point.getNonDiagonalNeighbours().stream().filter(p -> p.getX() >= 0 && p.getY() >= 0)
					.filter(p -> Day22.getType(p) != equip.getBanedType()).map(p -> new PointWithEquip(p, equip))
					.forEach(p -> neights.put(p, 1));
			Stream.of(Equip.values()).filter(e -> e != equip).filter(e -> e.getBanedType() != Day22.getType(point)).forEach(e -> neights.put(new PointWithEquip(point, e), 7));
			return neights;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((equip == null) ? 0 : equip.hashCode());
			result = prime * result + ((point == null) ? 0 : point.hashCode());
			return result;
		}

		@Override
		public String toString()
		{
			return point + " " + equip;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PointWithEquip other = (PointWithEquip) obj;
			if (equip != other.equip)
				return false;
			if (point == null) {
				if (other.point != null)
					return false;
			} else if (!point.equals(other.point))
				return false;
			return true;
		}

	}
}
