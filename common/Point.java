package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Point implements Serializable {
	private static final long serialVersionUID = -3548660992611442533L;

	private final List<Long> coordinates;

	public Point(String str) {
		coordinates = Stream.of(str.split(",")).map(Long::valueOf).collect(Collectors.toCollection(ArrayList::new));
	}

	public Point(long... cords) {
		coordinates = LongStream.of(cords).mapToObj(Long::valueOf).collect(Collectors.toList());
	}

	public Point(Collection<Long> cords) {
		coordinates = new ArrayList<>(cords);
	}

	public Point move(long... change) {
		List<Long> newCoords = new ArrayList<>();
		for (int idx = 0; idx < coordinates.size(); idx++) {
			newCoords.add(coordinates.get(idx) + change[idx]);
		}
		return new Point(newCoords);
	}

	public Point move(Direction direction) {
		return move(direction.getDx(), direction.getDy());
	}

	public Point move(Direction direction, long distance) {
		return move(direction.getDx() * distance, direction.getDy() * distance);
	}

	public Point move(List<Long> change) {
		List<Long> newCoords = new ArrayList<>();
		for (int idx = 0; idx < coordinates.size(); idx++) {
			newCoords.add(coordinates.get(idx) + change.get(idx));
		}
		return new Point(newCoords);
	}

	public Point move(Point change) {
		return move(change.coordinates);
	}

	public Point negate() {
		List<Long> negated = new ArrayList<>();
		for (Long d : coordinates) {
			negated.add(-d);
		}
		return new Point(negated);
	}

	public long getManhattanDistance(Point other) {
		long distance = 0;
		for (int idx = 0; idx < coordinates.size(); idx++) {
			distance += Math.abs(coordinates.get(idx) - other.coordinates.get(idx));
		}
		return distance;
	}

	public long getX() {
		return coordinates.get(0);
	}

	public long getY() {
		return coordinates.get(1);
	}

	public long getZ() {
		return coordinates.get(2);
	}

	public long getDimenssion(int dim) {
		return coordinates.get(dim);
	}

	public int getDimensionSize() {
		return coordinates.size();
	}

	public Set<Point> getNeighbours() {
		Set<List<Long>> result = new HashSet<>();
		result.add(new ArrayList<>());
		for (long dimCoord : coordinates) {
			Set<List<Long>> newResult = new HashSet<>();
			newResult.addAll(result.stream().map(coordList -> {
				List<Long> copy = new ArrayList<>(coordList);
				copy.add(dimCoord);
				return copy;
			}).collect(Collectors.toSet()));
			newResult.addAll(result.stream().map(coordList -> {
				List<Long> copy = new ArrayList<>(coordList);
				copy.add(dimCoord + 1);
				return copy;
			}).collect(Collectors.toSet()));
			newResult.addAll(result.stream().map(coordList -> {
				List<Long> copy = new ArrayList<>(coordList);
				copy.add(dimCoord - 1);
				return copy;
			}).collect(Collectors.toSet()));
			result = newResult;
		}
		return result.stream().map(Point::new).filter(p -> !p.equals(this)).collect(Collectors.toSet());
	}

	public Set<Point> getNonDiagonalNeighbours() {
		return getNeighbours().stream().filter(neight -> {
			long differentCount = 0;
			for (int dim = 0; dim < neight.coordinates.size(); dim++) {
				if (!neight.coordinates.get(dim).equals(coordinates.get(dim))) {
					differentCount++;
				}
			}
			return differentCount <= 1;
		}).collect(Collectors.toSet());
	}

	public static Stream<Point> range(Point begining, Point end) {
		Set<Point> pointsInRange = new HashSet<>();
		Point current = begining;
		while (!current.equals(end)) {
			pointsInRange.add(current);
			List<Long> newCoords = new ArrayList<>(current.coordinates);
			int dim = 0;
			while (newCoords.get(dim).equals(end.coordinates.get(dim))) {
				dim++;
			}
			if (newCoords.get(dim) > end.coordinates.get(dim)) {
				newCoords.set(dim, newCoords.get(dim) - 1);
			} else {
				newCoords.set(dim, newCoords.get(dim) + 1);
			}
			for (int i = 0; i < dim; i++) {
				newCoords.set(i, begining.coordinates.get(i));
			}
			current = new Point(newCoords);
		}
		pointsInRange.add(end);
		return pointsInRange.stream();
	}

	public static long minX(Set<Point> points) {
		return points.stream().mapToLong(Point::getX).min().getAsLong();
	}

	public static long maxX(Set<Point> points) {
		return points.stream().mapToLong(Point::getX).max().getAsLong();
	}

	public static long minY(Set<Point> points) {
		return points.stream().mapToLong(Point::getY).min().getAsLong();
	}

	public static long maxY(Set<Point> points) {
		return points.stream().mapToLong(Point::getY).max().getAsLong();
	}

	public static long minZ(Set<Point> points) {
		return points.stream().mapToLong(Point::getZ).min().getAsLong();
	}

	public static long maxZ(Set<Point> points) {
		return points.stream().mapToLong(Point::getZ).max().getAsLong();
	}

	public static Point mid(Point from, Point to) {
		long d[] = new long[from.coordinates.size()];
		for (int dim = 0; dim < from.coordinates.size(); dim++) {
			d[dim] = (from.coordinates.get(dim) + to.coordinates.get(dim)) / 2;
		}
		return new Point(d);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinates == null) ? 0 : coordinates.hashCode());
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
		if (coordinates == null) {
			if (other.coordinates != null)
				return false;
		} else if (!coordinates.equals(other.coordinates))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("(");
		coordinates.forEach(coord -> sb.append(coord).append(", "));
		sb.replace(sb.length() - 2, sb.length(), ")");
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(new Point(2, 3).getNonDiagonalNeighbours());
	}
}
