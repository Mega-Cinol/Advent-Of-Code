package y2020.day24;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day24 {
	private static final Set<Point> TILES = new HashSet<>();

	public static void main(String[] args) {
		Input.parseLines("y2020/day24/day24.txt", Day24::fromString, Day24::flip);
		System.out.println(TILES.size());
		for (int i = 0 ; i < 100 ; i++)
		{
			day();
		}
		System.out.println(TILES.size());
	}

	private static void flip(Point tile) {
		if (!TILES.add(tile)) {
			TILES.remove(tile);
		}
	}

	private static Point fromString(String address) {
		Point current = new Point(0, 0);
		int dx = 0;
		int dy = 0;
		for (int i = 0; i < address.length(); i++) {
			switch (address.charAt(i)) {
			case 'n':
				dy = -1;
				continue;
			case 's':
				dy = 1;
				continue;
			case 'w':
				dx = -1;
				break;
			case 'e':
				dx = 1;
				break;
			default:
				throw new IllegalArgumentException(address);
			}
			if (dy == 0) {
				dx *= 2;
			}
			current = current.move(dx, dy);
			dx = 0;
			dy = 0;
		}
		return current;
	}

	private static Set<Point> getNeighbours(Point tile) {
		Set<Point> neights = new HashSet<>();
		neights.add(tile.move(-1, -1));
		neights.add(tile.move(-1, 1));
		neights.add(tile.move(-2, 0));
		neights.add(tile.move(2, 0));
		neights.add(tile.move(1, -1));
		neights.add(tile.move(1, 1));
		return neights;
	}

	private static void day() {
		Set<Point> newBlack = new HashSet<>(TILES);
		newBlack.removeIf(tile -> blackNeights(tile) == 0);
		newBlack.removeIf(tile -> blackNeights(tile) > 2);

		Set<Point> whiteTiles = TILES.stream().map(Day24::getNeighbours).flatMap(Set::stream)
				.filter(tile -> !TILES.contains(tile)).collect(Collectors.toSet());
		whiteTiles.removeIf(tile -> blackNeights(tile) != 2);
		newBlack.addAll(whiteTiles);
		
		TILES.clear();
		TILES.addAll(newBlack);
	}

	private static int blackNeights(Point tile) {
		Set<Point> neights = getNeighbours(tile);
		neights.retainAll(TILES);
		return neights.size();
	}
}
