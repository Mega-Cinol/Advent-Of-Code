package y2021.day20;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day20 {

	public static void main(String[] args) {
		List<String> input = Input.parseLines("y2021/day20/day20.txt").collect(Collectors.toList());
		String algorithm = input.get(0);

		Set<Point> currentImage = new HashSet<>();
		for (int y = 2; y < input.size(); y++) {
			for (int x = 0; x < input.get(y).length(); x++) {
				if (input.get(y).charAt(x) == '#') {
					currentImage.add(new Point(x, y));
				}
			}
		}
		// part1
		runSteps(currentImage, algorithm, 2);
		// part2
		runSteps(currentImage, algorithm, 50);

	}

	private static void runSteps(Set<Point> currentImage, String algorithm, int stepsCount) {
		Map<Point, Integer> neightValues = new HashMap<>();
		neightValues.put(new Point(-1, -1), 256);
		neightValues.put(new Point(0, -1), 128);
		neightValues.put(new Point(1, -1), 64);
		neightValues.put(new Point(-1, 0), 32);
		neightValues.put(new Point(0, 0), 16);
		neightValues.put(new Point(1, 0), 8);
		neightValues.put(new Point(-1, 1), 4);
		neightValues.put(new Point(0, 1), 2);
		neightValues.put(new Point(1, 1), 1);

		for (int step = 0; step < stepsCount; step++) {
			Set<Point> nextStep = new HashSet<>();
			long minX = Point.minX(currentImage);
			long minY = Point.minY(currentImage);
			long maxX = Point.maxX(currentImage);
			long maxY = Point.maxY(currentImage);
			for (long y = minY - 2; y <= maxY + 2; y++) {
				for (long x = minX - 2; x <= maxX + 2; x++) {
					Point currentPoint = new Point(x, y);
					int totalValue = 0;
					for (Map.Entry<Point, Integer> neight : neightValues.entrySet()) {
						Point neightLocation = currentPoint.move(neight.getKey());
						if (currentImage.contains(neightLocation)
								|| (step % 2 == 1 && ((neightLocation.getX() < minX) || (neightLocation.getX() > maxX)
										|| (neightLocation.getY() < minY) || (neightLocation.getY() > maxY)))) {
							totalValue += neight.getValue();
						}
					}
					if (algorithm.charAt(totalValue) == '#') {
						nextStep.add(currentPoint);
					}
				}
			}
			currentImage = nextStep;
		}
		System.out.println(currentImage.size());
	}
}
