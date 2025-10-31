package y2022.day18;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day18 {

	public static void main(String[] args) {
		var cubes = Input.parseLines("y2022/day18/day18.txt", Point::new).collect(Collectors.toSet());

		System.out.println(cubes.stream().map(Point::getNonDiagonalNeighbours)
				.mapToLong(neights -> neights.stream().filter(n -> !cubes.contains(n)).count()).sum());

		System.out.println(countVisible(cubes, new Point(-1, -1, -1)));
	}

	private static long countVisible(Set<Point> cubes, Point current) {
		Set<Point> toVisit = new HashSet<>();
		Set<Point> visited = new HashSet<>();
		toVisit.add(current);
		long visible = 0;
		while (!toVisit.isEmpty()) {
			var next = toVisit.iterator().next();
			toVisit.remove(next);
			visible += next.getNonDiagonalNeighbours().stream().filter(cubes::contains).count();
			next.getNonDiagonalNeighbours().stream().filter(n -> !cubes.contains(n)).filter(p -> p.getX() >= -1)
					.filter(p -> p.getX() <= 20).filter(p -> p.getY() >= -1).filter(p -> p.getY() <= 20)
					.filter(p -> p.getZ() >= -1).filter(p -> p.getZ() <= 20).filter(n -> !visited.contains(n))
					.forEach(toVisit::add);
			visited.add(next);
		}
		return visible;
	}
}
