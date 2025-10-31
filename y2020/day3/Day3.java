package y2020.day3;

import java.util.HashSet;
import java.util.Set;

import common.Input;

public class Day3 {

	public static void main(String[] args) {
		SlopeMap slope = new SlopeMap();
		Input.parseLines("y2020/day3/day3.txt", line -> {
			Set<Integer> rowTrees = new HashSet<>();
			for (int pos = 0; pos < line.length(); pos++) {
				if (line.charAt(pos) == '#') {
					rowTrees.add(pos);
				}
			}
			return rowTrees;
		}, slope::addRow);
		long result = 1;
		result *= countTrees(1, 1, slope);
		result *= countTrees(3, 1, slope);
		result *= countTrees(5, 1, slope);
		result *= countTrees(7, 1, slope);
		result *= countTrees(1, 2, slope);
		System.out.println(result);
	}

	private static int countTrees(int dx, int dy, SlopeMap slope)
	{
		int x = 0;
		int y = 0;
		int treeCount = 0;
		while (!slope.reachedEnd(y)) {
			if (slope.hasTree(x, y)) {
				treeCount++;
			}
			x += dx;
			y += dy;
		}
		return treeCount;
	}

	private static class SlopeMap {
		private static class Point {
			private final int x;
			private final int y;

			public Point(int x, int y) {
				this.x = x;
				this.y = y;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + x;
				result = prime * result + y;
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
				if (x != other.x)
					return false;
				if (y != other.y)
					return false;
				return true;
			}
		}

		private Set<Point> trees = new HashSet<>();
		private final int xSize = 31;
		private int ySize = 0;

		public void addRow(Set<Integer> rowTrees) {
			rowTrees.stream().map(x -> new Point(x, ySize)).forEach(trees::add);
			ySize++;
		}

		public boolean hasTree(int x, int y) {
			x %= xSize;
			return trees.contains(new Point(x, y));
		}

		public boolean reachedEnd(int y) {
			return y >= ySize;
		}
	}
}
