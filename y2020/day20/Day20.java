package y2020.day20;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

@SuppressWarnings("unused")
public class Day20 {

	public static void main(String[] args) {
		Tile tile = new Tile(666);
		tile.addRow(".#........");
		tile.addRow("..........");
		tile.addRow(".........#");
		tile.addRow(".........#");
		tile.addRow(".........#");
		tile.addRow(".........#");
		tile.addRow("..........");
		tile.addRow("#.........");
		tile.addRow("#.........");
		tile.addRow("......###.");

		tile.print();
		System.out.println();
		tile.rotate();
		tile.print();

		Puzzle puzzle = new Puzzle();
		Input.parseLines("y2020/day20/day20.txt", Function.identity(), puzzle::parseLine);
		System.out.println(puzzle.tiles.size());
		System.out.println(puzzle.cornersChecksum());
		puzzle.solve2();
		Set<Point> redered = puzzle.renderPuzzle();
		System.out.println(puzzle.findMonsters(redered));
	}

	private static class Puzzle {
		private final Set<Tile> tiles = new HashSet<>();
		private Tile lastTile;
		private final Map<Point, Tile> solvedTiles = new HashMap<>();

		public void parseLine(String line) {
			if (line.isEmpty()) {
				return;
			}
			Pattern tiletle = Pattern.compile("Tile ([0-9]*):");
			Matcher tiletleMatcher = tiletle.matcher(line);
			if (tiletleMatcher.matches()) {
				lastTile = new Tile(Long.valueOf(tiletleMatcher.group(1)));
				tiles.add(lastTile);
				return;
			}
			lastTile.addRow(line);
		}

		public long cornersChecksum() {
			Set<Integer> sums = new HashSet<>();
			tiles.stream().forEach(tile -> sums.addAll(tile.getEdgesChecksum()));
			Map<Integer, Set<Tile>> tilesEdges = new HashMap<>();
			for (Tile tile : tiles) {
				tile.getEdgesChecksum().stream().map(edge -> tilesEdges.computeIfAbsent(edge, k -> new HashSet<>()))
						.forEach(list -> list.add(tile));
			}
			System.out.println(tiles.stream().filter(tile -> isCornerTile(tile, tilesEdges)).count());
			return tiles.stream().filter(tile -> isCornerTile(tile, tilesEdges)).mapToLong(Tile::getId).reduce(1,
					(a, b) -> a * b);
		}

		public boolean isCornerTile(Tile tile, Map<Integer, Set<Tile>> tilesEdges) {
			return tile.getEdgesChecksum().stream().filter(edge -> tilesEdges.get(edge).size() == 1).count() == 2;
		}

		public void solve() {
			solvedTiles.clear();
			Map<Integer, Set<Tile>> tilesEdges = new HashMap<>();
			for (Tile tile : tiles) {
				tile.getEdgesChecksum().stream().map(edge -> tilesEdges.computeIfAbsent(edge, k -> new HashSet<>()))
						.forEach(list -> list.add(tile));
			}
			solve(lastTile, new Point(0, 0), tilesEdges, null, null, null, null);
		}

		private boolean solve(Tile tile, Point location, Map<Integer, Set<Tile>> tilesEdges, Long topEdge,
				Long bottomEdge, Long leftEdge, Long rightEdge) {
			List<Boolean> adjusted = new ArrayList<Boolean>();
			adjusted.add(false);
			boolean tileMatches = tileMatches(tile, Direction.LEFT, location.move(-1, 0), adjusted);
			tileMatches &= tileMatches(tile, Direction.RIGHT, location.move(1, 0), adjusted);
			tileMatches &= tileMatches(tile, Direction.TOP, location.move(0, 1), adjusted);
			tileMatches &= tileMatches(tile, Direction.BOTTOM, location.move(0, -1), adjusted);
			if (!tileMatches) {
				return false;
			}
			solvedTiles.put(location, tile);
			if (solvedTiles.size() == tiles.size()) {
				return true;
			}
			boolean result = true;
			if (topEdge == null || topEdge > location.getY()) {
				Set<Tile> neighbours = tilesEdges.get(tile.topChecksum());
				neighbours.removeAll(solvedTiles.values());
				boolean topResult = false;
				for (Tile nextTile : neighbours) {
					topResult |= solve(nextTile, location.move(0, 1), tilesEdges, topEdge, bottomEdge, leftEdge,
							rightEdge);
				}
				if (!topResult && topEdge == null) {
					topEdge = location.getY();
					topResult = true;
				}
				result &= topResult;
			}
			if (leftEdge == null || leftEdge < location.getX()) {
				Set<Tile> neighbours = tilesEdges.get(tile.leftChecksum());
				neighbours.removeAll(solvedTiles.values());
				boolean leftResult = false;
				for (Tile nextTile : neighbours) {
					leftResult |= solve(nextTile, location.move(-1, 0), tilesEdges, topEdge, bottomEdge, leftEdge,
							rightEdge);
				}
				if (!leftResult && leftEdge == null) {
					leftEdge = location.getX();
					leftResult = true;
				}
				result &= leftResult;
			}
			if (rightEdge == null || rightEdge > location.getX()) {
				Set<Tile> neighbours = tilesEdges.get(tile.rightChecksum());
				neighbours.removeAll(solvedTiles.values());
				boolean rightResult = false;
				for (Tile nextTile : neighbours) {
					rightResult |= solve(nextTile, location.move(1, 0), tilesEdges, topEdge, bottomEdge, leftEdge,
							rightEdge);
				}
				if (!rightResult && rightEdge == null) {
					rightEdge = location.getX();
					rightResult = true;
				}
				result &= rightResult;
			}
			if (bottomEdge == null || bottomEdge < location.getY()) {
				Set<Tile> neighbours = tilesEdges.get(tile.bottomChecksum());
				neighbours.removeAll(solvedTiles.values());
				boolean bottomResult = false;
				for (Tile nextTile : neighbours) {
					bottomResult |= solve(nextTile, location.move(0, -1), tilesEdges, topEdge, bottomEdge, leftEdge,
							rightEdge);
				}
				if (!bottomResult && bottomEdge == null) {
					bottomEdge = location.getY();
					bottomResult = true;
				}
				result &= bottomResult;
			}

			return result;
		}

		public void solve2() {
			solvedTiles.clear();
			Map<Integer, Set<Tile>> tilesEdges = new HashMap<>();
			for (Tile tile : tiles) {
				tile.getEdgesChecksum().stream().map(edge -> tilesEdges.computeIfAbsent(edge, k -> new HashSet<>()))
						.forEach(list -> list.add(tile));
			}
			Tile first = tiles.stream().filter(tile -> isCornerTile(tile, tilesEdges)).findAny().get();
			while (tilesEdges.get(first.leftChecksum()).size() > 1) {
				first.rotate();
			}
			if (tilesEdges.get(first.bottomChecksum()).size() > 1) {
				first.flipH();
			}
			for (int x = 0; x < 12; x++) {
				Tile current = first;
				solvedTiles.put(new Point(x, 0), current);
				for (int y = 0; y < 11; y++) {
					Tile tmp = current;
					Tile next = tilesEdges.get(current.topChecksum()).stream().filter(t -> t.getId() != tmp.getId())
							.findAny().get();
					next.adjustTo(Direction.BOTTOM, current.topEdge());
					solvedTiles.put(new Point(x, y + 1), next);
					current = next;
				}
				Tile tmp = first;
				first = tilesEdges.get(first.rightChecksum()).stream().filter(t -> t.getId() != tmp.getId()).findAny()
						.orElse(null);
				if (first != null) {
					first.adjustTo(Direction.LEFT, tmp.rightEdge());
				}
			}
//			first.adjustTo(Direction.LEFT, edge);
//			solve2(lastTile, new Point(0, 0), tilesEdges);
		}

		private void solve2(Tile tile, Point location, Map<Integer, Set<Tile>> tilesEdges) {
			if (solvedTiles.containsKey(location)) {
				return;
			}
			if (solvedTiles.size() == 144) {
				return;
			}
			solvedTiles.put(location, tile);
			Tile left = tilesEdges.get(tile.leftChecksum()).stream().filter(nTile -> nTile.getId() != tile.getId())
					.findAny().orElse(null);
			if (left != null) {
				left.adjustTo(Direction.RIGHT, tile.leftEdge());
				solve2(left, location.move(-1, 0), tilesEdges);
			}
			Tile right = tilesEdges.get(tile.rightChecksum()).stream().filter(nTile -> nTile.getId() != tile.getId())
					.findAny().orElse(null);
			if (right != null) {
				right.adjustTo(Direction.LEFT, tile.rightEdge());
				solve2(right, location.move(1, 0), tilesEdges);
			}
			Tile top = tilesEdges.get(tile.topChecksum()).stream().filter(nTile -> nTile.getId() != tile.getId())
					.findAny().orElse(null);
			if (top != null) {
				top.adjustTo(Direction.BOTTOM, tile.topEdge());
				solve2(top, location.move(0, 1), tilesEdges);
			}
			Tile bottom = tilesEdges.get(tile.rightChecksum()).stream().filter(nTile -> nTile.getId() != tile.getId())
					.findAny().orElse(null);
			if (bottom != null) {
				bottom.adjustTo(Direction.TOP, bottom.bottomEdge());
				solve2(bottom, location.move(0, -1), tilesEdges);
			}
		}

		private boolean tileMatches(Tile tile, Direction direction, Point location, List<Boolean> adjusted) {
			if (solvedTiles.containsKey(location)) {
				if (!adjusted.get(0)) {
					tile.adjustTo(direction, solvedTiles.get(location).edge(direction.opposite()));
					adjusted.set(0, true);
				} else {
					if (!solvedTiles.get(location).edge(direction.opposite()).equals(tile.edge(direction))) {
						return false;
					}
				}
			}
			return true;
		}

		public Set<Point> renderPuzzle() {
			long lowestX = solvedTiles.keySet().stream().mapToLong(Point::getX).min().getAsLong();
			long lowestY = solvedTiles.keySet().stream().mapToLong(Point::getY).min().getAsLong();
			solvedTiles.keySet().stream().sorted(Comparator.comparingLong(Point::getX).thenComparingLong(Point::getY))
					.forEach(System.out::println);
			Set<Point> rendered = new HashSet<>();
			for (Map.Entry<Point, Tile> tileEntry : solvedTiles.entrySet()) {
				Point tileLocation = tileEntry.getKey();
				Tile tile = tileEntry.getValue();

				long tileX = tileLocation.getX() - lowestX;
				long tileY = tileLocation.getY() - lowestY;

				for (int rowIndex = 8; rowIndex > 0; rowIndex--) {
					Set<Integer> tileRow = tile.rows.get(rowIndex);
					tileRow.remove(0);
					tileRow.remove(9);
					for (int colIndex : tileRow) {
						rendered.add(new Point(tileX * 8 + colIndex - 1, tileY * 8 + (8 - rowIndex + 1)));
					}
				}
			}
			for (int x = 0; x < 96; x++) {
				for (int y = 0; y < 96; y++) {
					if (rendered.contains(new Point(x, y))) {
						System.out.print("#");
					} else {
						System.out.print(".");
					}
				}
				System.out.println();
			}
			return rendered;
		}

		public int findMonsters(Set<Point> rendered)
		{
			Set<Point> monsterPoints = new HashSet<Point>();
			for (Point point : rendered)
			{
				Set<Set<Point>> possibleMonsters = seaMonsters(point);
				possibleMonsters.stream().filter(possible -> possible.stream().allMatch(rendered::contains))
						.flatMap(Set::stream).forEach(monsterPoints::add);
			}
			for (int x = 0; x < 96; x++) {
				for (int y = 0; y < 96; y++) {
					if (monsterPoints.contains(new Point(x, y))) {
						System.out.print("#");
					} else {
						System.out.print(".");
					}
				}
				System.out.println();
			}

			rendered.removeAll(monsterPoints);
			return rendered.size();
		}
		private Set<Set<Point>> seaMonsters(Point start)
		{
//                  # 
//#    ##    ##    ###
// #  #  #  #  #  #   
			Set<Point> normal = new HashSet<>();
			normal.add(start);
			normal.add(start.move(1, -1));
			normal.add(start.move(4, -1));
			normal.add(start.move(5, 0));
			normal.add(start.move(6, 0));
			normal.add(start.move(7, -1));
			normal.add(start.move(10, -1));
			normal.add(start.move(11, 0));
			normal.add(start.move(12, 0));
			normal.add(start.move(13, -1));
			normal.add(start.move(16, -1));
			normal.add(start.move(17, 0));
			normal.add(start.move(18, 0));
			normal.add(start.move(19, 0));
			normal.add(start.move(18, 1));

			Set<Point> normalFlipH = normal.stream().map(p -> new Point(p.getX(), -1 * p.getY()))
					.collect(Collectors.toSet());
			Set<Point> normalFlipV = normal.stream().map(p -> new Point(-1 *p.getX(), p.getY()))
					.collect(Collectors.toSet());
			Set<Point> normalFlipHandV = normal.stream().map(p -> new Point(-1 * p.getX(), -1 * p.getY()))
					.collect(Collectors.toSet());

			Set<Point> rot = normal.stream().map(p -> new Point(p.getY(), p.getX()))
					.collect(Collectors.toSet());
			Set<Point> rotFlipH = rot.stream().map(p -> new Point(p.getX(), -1 * p.getY()))
					.collect(Collectors.toSet());
			Set<Point> rotFlipV = rot.stream().map(p -> new Point(-1 *p.getX(), p.getY()))
					.collect(Collectors.toSet());
			Set<Point> rotFlipHandV = rot.stream().map(p -> new Point(-1 * p.getX(), -1 * p.getY()))
					.collect(Collectors.toSet());

			Set<Set<Point>> monsters = new HashSet<Set<Point>>();
			monsters.add(normal);
			monsters.add(normalFlipH);
			monsters.add(normalFlipV);
			monsters.add(normalFlipHandV);
			monsters.add(rot);
			monsters.add(rotFlipH);
			monsters.add(rotFlipV);
			monsters.add(rotFlipHandV);
			return monsters;
		}
	}

	private static class Tile {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (id ^ (id >>> 32));
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
			Tile other = (Tile) obj;
			if (id != other.id)
				return false;
			return true;
		}

		private final long id;
		private final List<Set<Integer>> rows = new ArrayList<>();

		public Tile(long id) {
			this.id = id;
		}

		public long getId() {
			return id;
		}

		public void addRow(String row) {
			Set<Integer> columns = new HashSet<>();
			for (int i = 0; i < row.length(); i++) {
				if (row.charAt(i) == '#') {
					columns.add(i);
				}
			}
			rows.add(columns);
		}

		public Set<Integer> topEdge() {
			return rows.get(0);
		}

		public Set<Integer> bottomEdge() {
			return rows.get(9);
		}

		public Set<Integer> leftEdge() {
			Set<Integer> leftEdge = new HashSet<>();
			for (int row = 0; row < rows.size(); row++) {
				if (rows.get(row).contains(0)) {
					leftEdge.add(row);
				}
			}
			return leftEdge;
		}

		public Set<Integer> rightEdge() {
			Set<Integer> rightEdge = new HashSet<>();
			for (int row = 0; row < rows.size(); row++) {
				if (rows.get(row).contains(9)) {
					rightEdge.add(row);
				}
			}
			return rightEdge;
		}

		public Set<Integer> edge(Direction direction) {
			switch (direction) {
			case TOP:
				return topEdge();
			case BOTTOM:
				return bottomEdge();
			case LEFT:
				return leftEdge();
			case RIGHT:
				return rightEdge();
			}
			return null;
		}

		private int checksum(Set<Integer> edge) {
			int normal = edge.stream().mapToInt(col -> (int) Math.pow(2, col)).sum();
			int reversed = edge.stream().mapToInt(col -> (int) Math.pow(2, 9 - col)).sum();
			int lower = Math.min(normal, reversed);
			int higher = Math.max(normal, reversed);
			return 1024 * lower + higher;
		}

		public int topChecksum() {
			return checksum(topEdge());
		}

		public int bottomChecksum() {
			return checksum(bottomEdge());
		}

		public int leftChecksum() {
			return checksum(leftEdge());
		}

		public int rightChecksum() {
			return checksum(rightEdge());
		}

		public Set<Integer> getEdgesChecksum() {
			Set<Integer> result = new HashSet<>();
			result.add(topChecksum());
			result.add(bottomChecksum());
			result.add(leftChecksum());
			result.add(rightChecksum());
			return result;
		}

		public void rotate() // clockwise
		{
			List<Set<Integer>> newRows = new ArrayList<>();
			for (int col = 0; col < 10; col++) {
				Set<Integer> newRow = new HashSet<Integer>();
				for (int row = 0; row < rows.size(); row++) {
					if (rows.get(row).contains(col)) {
						newRow.add(9 - row);
					}
				}
				newRows.add(newRow);
			}
			rows.clear();
			rows.addAll(newRows);
		}

		public void flipV() // mirror Y-axis
		{
			rows.forEach(row -> {
				Set<Integer> newRow = row.stream().map(col -> 9 - col).collect(Collectors.toSet());
				row.clear();
				row.addAll(newRow);
			});
		}

		public void flipH() // mirror X-axis
		{
			for (int row = 0; row < 5; row++) {
				Set<Integer> tmp = rows.get(row);
				rows.set(row, rows.get(9 - row));
				rows.set(9 - row, tmp);
			}
		}

		public void adjustTo(Direction direction, Set<Integer> edge) {
			for (int i = 0; i < 4; i++) {
				if (checksum(edge(direction)) == checksum(edge)) {
					break;
				}
				System.out.println(leftChecksum());
				rotate();
			}
			if (checksum(edge(direction)) != checksum(edge)) {
				throw new IllegalArgumentException();
			}
			if (!edge(direction).equals(edge)) {
				switch (direction) {
				case LEFT:
				case RIGHT:
					flipH();
					break;
				case BOTTOM:
				case TOP:
					flipV();
					break;
				}
			}
			if (!edge(direction).equals(edge)) {
				throw new IllegalArgumentException();
			}
		}

		public void print() {
			rows.forEach(row -> {
				for (int i = 0; i < 10; i++) {
					if (row.contains(i)) {
						System.out.print("#");
					} else {
						System.out.print(".");
					}
				}
				System.out.println();
			});
		}

		@Override
		public String toString() {
			return "Tile: " + id;
		}
	}

	private enum Direction {
		TOP, BOTTOM, LEFT, RIGHT;

		public Direction opposite() {
			switch (this) {
			case TOP:
				return BOTTOM;
			case BOTTOM:
				return TOP;
			case LEFT:
				return RIGHT;
			case RIGHT:
				return LEFT;
			}
			return null;
		}
	}
}
