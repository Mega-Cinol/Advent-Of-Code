package y2020.day12;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;

public class Day12 {

	public static void main(String[] args) {
		Ship ship = new Ship();
		Input.parseLines("y2020/day12/day12.txt", CommandFactory::fromString, cmd -> cmd.accept(ship));
		System.out.println(ship.manhattan());
	}

	private enum CommandFactory implements Function<Integer, Consumer<Ship>> {
		N(dist -> ship -> ship.north(dist)), S(dist -> ship -> ship.south(dist)), W(dist -> ship -> ship.west(dist)),
		E(dist -> ship -> ship.east(dist)), L(angle -> ship -> ship.left(angle)), R(angle -> ship -> ship.right(angle)),
		F(dist -> ship -> ship.forward(dist));

		private final Function<Integer, Consumer<Ship>> function;

		CommandFactory(Function<Integer, Consumer<Ship>> function) {
			this.function = function;
		}

		@Override
		public Consumer<Ship> apply(Integer param) {
			return function.apply(param);
		}

		public static Consumer<Ship> fromString(String strCommand) {
			Pattern cmdPattern = Pattern.compile("([NSWEFLR])([0-9]*)");
			Matcher cmdMatcher = cmdPattern.matcher(strCommand);
			cmdMatcher.matches();
			System.out.println(cmdMatcher.group(1) + " " + cmdMatcher.group(2));
			return CommandFactory.valueOf(cmdMatcher.group(1)).apply(Integer.parseInt(cmdMatcher.group(2)));
		}
	}

	private static class Ship {
		long wpX = 10;
		long wpY = 1;
		long x = 0;
		long y = 0;

		public void forward(int dist) {
			x += dist * wpX;
			y += dist * wpY;
			printShip();
		}

		public void left(int angle) {
			long oldWpx = wpX;
			switch (angle) {
			case 90:
				wpX = -wpY;
				wpY = oldWpx;
				break;
			case 180:
				wpX = -wpX;
				wpY = -wpY;
				break;
			case 270:
				wpX = wpY;
				wpY = -oldWpx;
				break;
			default:
				throw new IllegalArgumentException("Unexpected angle: " + angle);
			}
			printShip();
		}

		public void right(int angle) {
			left(360 - angle);
		}

		public void north(int dist) {
			wpY += dist;
			printShip();
		}

		public void south(int dist) {
			wpY -= dist;
			printShip();
		}

		public void east(int dist) {
			wpX += dist;
			printShip();
		}

		public void west(int dist) {
			wpX -= dist;
			printShip();
		}

		public long manhattan() {
			return Math.abs(x) + Math.abs(y);
		}

		private void printShip() {
			System.out.println("Ship x " + x + " y " + y + " wpx " + wpX + " wpY " + wpY);
		}

	}
}
