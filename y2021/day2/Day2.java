package y2021.day2;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Direction;
import common.Input;
import common.Point;

public class Day2 {
	public static void main(String[] args) {
		Submarine part1 = new Part1Submarine();
		Submarine part2 = new Part2Submarine();
		Input.parseLines("y2021/day2/day2.txt", Function.identity(), cmd -> {
			part1.move(cmd);
			part2.move(cmd);
		});
		System.out.println(part1.getPosition());
		System.out.println(part2.getPosition());
	}

	private interface Submarine
	{
		void move(String command);
		long getPosition();
	}

	private static class Part1Submarine implements Submarine {
		private Point pos = new Point(0, 0);

		@Override
		public void move(String command) {
			Pattern p = Pattern.compile("(\\w*) (\\d*)");
			Matcher m = p.matcher(command);
			if (!m.matches()) {
				throw new IllegalArgumentException("regex: " + command);
			}
			switch (m.group(1)) {
			case "down":
				pos = pos.move(Direction.NORTH, Long.parseLong(m.group(2)));
				break;
			case "up":
				pos = pos.move(Direction.SOUTH, Long.parseLong(m.group(2)));
				break;
			case "forward":
				pos = pos.move(Direction.EAST, Long.parseLong(m.group(2)));
				break;
			default:
				throw new IllegalArgumentException("move: " + command);
			}
		}

		@Override
		public long getPosition() {
			return pos.getX() * pos.getY();
		}
	}
	private static class Part2Submarine implements Submarine {
		private Point pos = new Point(0, 0);
		long aim = 0;

		@Override
		public void move(String command) {
			Pattern p = Pattern.compile("(\\w*) (\\d*)");
			Matcher m = p.matcher(command);
			if (!m.matches()) {
				throw new IllegalArgumentException("regex: " + command);
			}
			switch (m.group(1)) {
			case "down":
				aim += Long.parseLong(m.group(2));
				break;
			case "up":
				aim -= Long.parseLong(m.group(2));
				break;
			case "forward":
				long distance = Long.parseLong(m.group(2));
				pos = pos.move(new Point(distance, distance * aim));
				break;
			default:
				throw new IllegalArgumentException("move: " + command);
			}
		}

		@Override
		public long getPosition() {
			return pos.getX() * pos.getY();
		}
	}
}
