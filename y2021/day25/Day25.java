package y2021.day25;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day25 {
	private static int xSize;
	private static int ySize;

	public static void main(String[] args) {
		Set<Point> east = new HashSet<>();
		Set<Point> south = new HashSet<>();
		List<String> input = Input.parseLines("y2021/day25/day25.txt").collect(Collectors.toList());
		ySize = input.size();
		xSize = input.get(0).length();
		for (int y = 0; y < input.size(); y++) {
			for (int x = 0; x < input.get(y).length(); x++) {
				switch (input.get(y).charAt(x)) {
				case '>':
					east.add(new Point(x, y));
					break;
				case 'v':
					south.add(new Point(x, y));
					break;
				}
			}
		}
		print(east, south);
		int stepCnt = 0;
		while (step(east, south)) {
			stepCnt++;
		}
		print(east, south);
		System.out.println(stepCnt+1);
	}

	private static boolean step(Set<Point> east, Set<Point> south) {
		Set<Point> newEast = new HashSet<Point>();
		east.stream().map(p -> moveRight(p, east, south)).forEach(newEast::add);
		boolean eastMoved = !east.equals(newEast);
		east.clear();
		east.addAll(newEast);

		Set<Point> newSouth = new HashSet<Point>();
		south.stream().map(p -> moveDown(p, east, south)).forEach(newSouth::add);
		boolean southMoved = !south.equals(newSouth);
		south.clear();
		south.addAll(newSouth);
		return eastMoved || southMoved;
	}

	private static Point moveRight(Point p, Set<Point> east, Set<Point> south) {
		Point movedPoint = new Point((p.getX() + 1) % xSize, p.getY());
		if (east.contains(movedPoint) || south.contains(movedPoint)) {
			return p;
		}
		return movedPoint;
	}

	private static Point moveDown(Point p, Set<Point> east, Set<Point> south) {
		Point movedPoint = new Point(p.getX(), (p.getY() + 1) % ySize);
		if (east.contains(movedPoint) || south.contains(movedPoint)) {
			return p;
		}
		return movedPoint;
	}

	private static void print(Set<Point> east, Set<Point> south) {
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if (east.contains(new Point(x, y))) {
					System.out.print('>');
				} else if (south.contains(new Point(x, y))) {
					System.out.print('v');
				} else {
					System.out.print('.');
				}
			}
			System.out.println();
		}
		System.out.println("===========================================================================");
	}
}
