package y2020.day17;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Input;

public class Day17 {
	public static void main(String[] args) {
		List<String> initial = Input.parseLines("y2020/day17/day17.txt", Function.identity())
				.collect(Collectors.toList());
		Cubes cubes = new Cubes();
		cubes.initialize(initial);
		cubes.cycle();
		cubes.cycle();
		cubes.cycle();
		cubes.cycle();
		cubes.cycle();
		cubes.cycle();
		System.out.println(cubes.countActive());
	}

	private static class Cubes {
		private Set<Point> activePoints = new HashSet<>();

		public void initialize(List<String> rows) {
			Iterator<String> iterator = rows.iterator();
			for (int y = -rows.size() / 2; y <= rows.size() / 2; y++) {
				if (!iterator.hasNext()) {
					return;
				}
				String row = iterator.next();
				for (int i = 0, x = -row.length() / 2; i < row.length(); i++, x++) {
					if (i == row.length()) {
						break;
					}
					if (row.charAt(i) == '#') {
						activePoints.add(new Point(x, y, 0, 0));
					}
				}
			}
		}

		public void cycle() {
			activePoints = activePoints.stream().map(Point::getNeighbours).flatMap(Set::stream).filter(point -> {
				boolean isActive = activePoints.contains(point);
				Set<Point> neighbours = point.getNeighbours();
				neighbours.retainAll(activePoints);
				int activeNeighbours = neighbours.size();
				return (isActive && activeNeighbours == 2) || (activeNeighbours == 3);
			}).collect(Collectors.toSet());
		}

		public int countActive() {
			return activePoints.size();
		}
	}

	private static class Point {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
			result = prime * result + w;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			if (z != other.z)
				return false;
			if (w != other.w)
				return false;
			return true;
		}

		private final int x;
		private final int y;
		private final int z;
		private final int w;

		public Point(int x, int y, int z, int w) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
		}

		public Set<Point> getNeighbours() {
			Set<Point> neights = new HashSet<>();
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					for (int dz = -1; dz <= 1; dz++) {
						for (int dw = -1; dw <= 1; dw++) {
							if ((dx != 0) || (dy != 0) || (dz != 0) || (dw != 0)) {
								neights.add(new Point(x + dx, y + dy, z + dz, w + dw));
							}
						}
					}
				}
			}
			return neights;
		}
	}
}
