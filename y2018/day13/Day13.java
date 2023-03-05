package y2018.day13;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import common.Direction;
import common.Input;
import common.Point;

public class Day13 {

	public static void main(String[] args) throws InterruptedException {
		boolean print = false;
		Map<Point, Character> track = new HashMap<>();
		Set<Cart> carts = new HashSet<>();
		List<String> trackInput = Input.parseLines("y2018/day13/day13.txt").collect(Collectors.toList());
		for (int y = 0; y < trackInput.size(); y++) {
			for (int x = 0; x < trackInput.get(y).length(); x++) {
				switch (trackInput.get(y).charAt(x)) {
				case 'v':
				case '^':
				case '<':
				case '>':
					carts.add(new Cart(new Point(x, y), trackInput.get(y).charAt(x)));
					break;
				case '/':
				case '\\':
				case '+':
				case '|':
				case '-':
					track.put(new Point(x, y), trackInput.get(y).charAt(x));
				default:
					break;
				}
			}
		}
		while (carts.size() > 1) {
			if (print) {
				print(track, carts);
				Thread.sleep(300);
			}
			List<Cart> sortedCarts = carts.stream().sorted(Comparator.comparingLong((Cart c) -> c.getLocation().getY())
					.thenComparingLong(c -> c.getLocation().getX())).collect(Collectors.toList());
			for (Cart c : sortedCarts)
			{
				if (!carts.contains(c))
				{
					continue;
				}
				c.tick(track);
				Set<Cart> cartsAtLocation = carts.stream().filter(c2 -> c2.getLocation().equals(c.getLocation())).collect(Collectors.toSet());
				if (cartsAtLocation.size() > 1)
				{
					System.out.println("Colision at: " + c.getLocation());
					carts.removeAll(cartsAtLocation);
				}
			}
		}
		carts.stream().map(Cart::getLocation).forEach(System.out::println);
	}

	private static void print(Map<Point, Character> track, Set<Cart> carts) {
		for (long y = Point.minY(track.keySet()); y <= Point.maxY(track.keySet()); y++) {
			for (long x = Point.minX(track.keySet()); x <= Point.maxX(track.keySet()); x++) {
				Point p = new Point(x, y);
				char toPrint = ' ';// track.getOrDefault(p, ' ');
				Optional<Cart> cart = carts.stream().filter(c -> c.getLocation().equals(p)).findAny();
				if (cart.isPresent()) {
					System.out.print(cart.get().getCharacter());
				} else {
					System.out.print(toPrint);
				}
			}
			System.out.println();
		}
		System.out.println("===========================");
	}

	private static class Cart {
		private Direction currentDirection;
		private final static List<UnaryOperator<Direction>> intersectionOperation = new ArrayList<>();
		static {
			intersectionOperation.add(Direction::right);
			intersectionOperation.add(UnaryOperator.identity());
			intersectionOperation.add(Direction::left);
		}
		private int currentOperation = 0;
		private Point location;

		public Cart(Point location, char direction) {
			this(location, parseDirection(direction));
		}

		private static Direction parseDirection(char chr) {
			switch (chr) {
			case 'v':
				return Direction.NORTH;
			case '^':
				return Direction.SOUTH;
			case '<':
				return Direction.WEST;
			case '>':
				return Direction.EAST;
			default:
				return null;
			}
		}

		public Cart(Point location, Direction direction) {
			this.location = location;
			currentDirection = direction;
		}

		public Point getLocation() {
			return location;
		}

		public char getCharacter() {
			switch (currentDirection) {
			case SOUTH:
				return '^';
			case NORTH:
				return 'v';
			case EAST:
				return '>';
			case WEST:
				return '<';
			default:
				return 'X';
			}
		}

		public Point tick(Map<Point, Character> track) {
			location = location.move(currentDirection);
			if (!track.containsKey(location)) {
				return location;
			}
			char currentElement = track.get(location);
			switch (currentElement) {
			case '/':
				if (currentDirection == Direction.SOUTH) {
					currentDirection = Direction.EAST;
				} else if (currentDirection == Direction.NORTH) {
					currentDirection = Direction.WEST;
				} else if (currentDirection == Direction.WEST) {
					currentDirection = Direction.NORTH;
				} else if (currentDirection == Direction.EAST) {
					currentDirection = Direction.SOUTH;
				}
				break;
			case '\\':
				if (currentDirection == Direction.SOUTH) {
					currentDirection = Direction.WEST;
				} else if (currentDirection == Direction.NORTH) {
					currentDirection = Direction.EAST;
				} else if (currentDirection == Direction.WEST) {
					currentDirection = Direction.SOUTH;
				} else if (currentDirection == Direction.EAST) {
					currentDirection = Direction.NORTH;
				}
				break;
			case '+':
				currentDirection = intersectionOperation.get(currentOperation++).apply(currentDirection);
				currentOperation %= 3;
				break;
			default:
				break;
			}
			return location;
		}
	}
}
