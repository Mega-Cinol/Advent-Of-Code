package y2023.day11;

import java.util.HashSet;
import java.util.stream.Collectors;

import common.AdventSolution;
import common.Direction;
import common.Point;

public class Day11 extends AdventSolution {

	public static void main(String[] args) {
		new Day11().solve();
	}

	@Override
	public Object part1Solution() {
		var input = getInput().toList();
		var stars = new HashSet<Point>();
		for (int y = 0; y < input.size(); y++) {
			for (int x = 0; x < input.get(y).length(); x++) {
				if (input.get(y).charAt(x) == '#') {
					stars.add(new Point(x, y));
				}
			}
		}
		var maxX = Point.maxX(stars);
		for (long x = maxX; x >= 0; x--) {
			long xCopy = x;
			if (stars.stream().map(Point::getX).noneMatch(xCoord -> xCoord == xCopy)) {
				var starsToMove = stars.stream().filter(star -> star.getX() > xCopy).collect(Collectors.toSet());
				stars.removeAll(starsToMove);
				starsToMove.stream().map(star -> star.move(Direction.RIGHT)).forEach(stars::add);
			}
		}
		var maxY = Point.maxY(stars);
		for (long y = maxY; y >= 0; y--) {
			long yCopy = y;
			if (stars.stream().map(Point::getY).noneMatch(yCoord -> yCoord == yCopy)) {
				var starsToMove = stars.stream().filter(star -> star.getY() > yCopy).collect(Collectors.toSet());
				stars.removeAll(starsToMove);
				starsToMove.stream().map(star -> star.move(Direction.DOWN)).forEach(stars::add);
			}
		}
		return stars.stream()
				.mapToLong(star -> stars.stream().mapToLong(otherStar -> star.getManhattanDistance(otherStar)).sum())
				.sum() / 2;
	}

	@Override
	public Object part2Solution() {
		var input = getInput().toList();
		var stars = new HashSet<Point>();
		for (int y = 0; y < input.size(); y++) {
			for (int x = 0; x < input.get(y).length(); x++) {
				if (input.get(y).charAt(x) == '#') {
					stars.add(new Point(x, y));
				}
			}
		}
		var maxX = Point.maxX(stars);
		for (long x = maxX; x >= 0; x--) {
			long xCopy = x;
			if (stars.stream().map(Point::getX).noneMatch(xCoord -> xCoord == xCopy)) {
				var starsToMove = stars.stream().filter(star -> star.getX() > xCopy).collect(Collectors.toSet());
				stars.removeAll(starsToMove);
				starsToMove.stream().map(star -> star.move(Direction.RIGHT, 999999)).forEach(stars::add);
			}
		}
		var maxY = Point.maxY(stars);
		for (long y = maxY; y >= 0; y--) {
			long yCopy = y;
			if (stars.stream().map(Point::getY).noneMatch(yCoord -> yCoord == yCopy)) {
				var starsToMove = stars.stream().filter(star -> star.getY() > yCopy).collect(Collectors.toSet());
				stars.removeAll(starsToMove);
				starsToMove.stream().map(star -> star.move(Direction.DOWN, 999999)).forEach(stars::add);
			}
		}
		return stars.stream()
				.mapToLong(star -> stars.stream().mapToLong(otherStar -> star.getManhattanDistance(otherStar)).sum())
				.sum() / 2;
	}
}
