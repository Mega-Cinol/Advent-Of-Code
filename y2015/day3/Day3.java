package y2015.day3;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import common.Input;
import common.Point;

public class Day3 {
	public static void main(String[] args) {
		Points visited = new Points();
		Input.parseLines("y2015/day3/day3.txt", String::chars).flatMapToInt(Function.identity()).forEach(order -> {
			char character = (char) order;
			switch (character) {
			case '>':
				visited.add(visited.getLast().move(1, 0));
				break;
			case '<':
				visited.add(visited.getLast().move(-1, 0));
				break;
			case '^':
				visited.add(visited.getLast().move(0, 1));
				break;
			case 'v':
				visited.add(visited.getLast().move(0, -1));
				break;
			default:
				break;
			}
		});
		System.out.println(visited.visitedCount());
	}

	private static class Points {
		Set<Point> visited = new HashSet<>();
		Point last = new Point(0, 0);
		Point beforeLast = new Point(0, 0);
		{
			visited.add(last);
		}

		public Point getLast() {
			return beforeLast;
		}

		public void add(Point next) {
			beforeLast = last;
			last = next;
			visited.add(next);
		}

		public int visitedCount() {
			return visited.size();
		}
	}
}