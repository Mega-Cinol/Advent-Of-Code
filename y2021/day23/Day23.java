package y2021.day23;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import common.PathFinding;
import common.Point;

public class Day23 {

	public static void main(String[] args) {
		// Part 1
		Map<Point, Character> amphibsPart1 = new HashMap<>();
		amphibsPart1.put(new Point(Burrow.ROOM_C_X, 1), 'A');
		amphibsPart1.put(new Point(Burrow.ROOM_D_X, 1), 'B');
		amphibsPart1.put(new Point(Burrow.ROOM_B_X, 1), 'C');
		amphibsPart1.put(new Point(Burrow.ROOM_A_X, 1), 'D');
		amphibsPart1.put(new Point(Burrow.ROOM_D_X, 2), 'A');
		amphibsPart1.put(new Point(Burrow.ROOM_C_X, 2), 'B');
		amphibsPart1.put(new Point(Burrow.ROOM_B_X, 2), 'C');
		amphibsPart1.put(new Point(Burrow.ROOM_A_X, 2), 'D');
		Map<Burrow, Integer> result = PathFinding.pathWithWeights(new Burrow(amphibsPart1), results -> false,
				b -> b.getAvailableMoves().stream().collect(Collectors.toMap(m -> b.applyMove(m), m -> m.cost)));
		System.out.println(
				result.entrySet().stream().filter(e -> e.getKey().isDone()).map(Map.Entry::getValue).findAny().get());

		// Part 2
		Map<Point, Character> amphibsPart2 = new HashMap<>();
		amphibsPart2.put(new Point(Burrow.ROOM_A_X, 1), 'D');
		amphibsPart2.put(new Point(Burrow.ROOM_B_X, 1), 'C');
		amphibsPart2.put(new Point(Burrow.ROOM_C_X, 1), 'A');
		amphibsPart2.put(new Point(Burrow.ROOM_D_X, 1), 'B');

		amphibsPart2.put(new Point(Burrow.ROOM_A_X, 2), 'D');
		amphibsPart2.put(new Point(Burrow.ROOM_B_X, 2), 'C');
		amphibsPart2.put(new Point(Burrow.ROOM_C_X, 2), 'B');
		amphibsPart2.put(new Point(Burrow.ROOM_D_X, 2), 'A');

		amphibsPart2.put(new Point(Burrow.ROOM_A_X, 3), 'D');
		amphibsPart2.put(new Point(Burrow.ROOM_B_X, 3), 'B');
		amphibsPart2.put(new Point(Burrow.ROOM_C_X, 3), 'A');
		amphibsPart2.put(new Point(Burrow.ROOM_D_X, 3), 'C');

		amphibsPart2.put(new Point(Burrow.ROOM_A_X, 4), 'D');
		amphibsPart2.put(new Point(Burrow.ROOM_B_X, 4), 'C');
		amphibsPart2.put(new Point(Burrow.ROOM_C_X, 4), 'B');
		amphibsPart2.put(new Point(Burrow.ROOM_D_X, 4), 'A');
		result = PathFinding.pathWithWeights(new Burrow(amphibsPart2), results -> false,
				b -> b.getAvailableMoves().stream().collect(Collectors.toMap(m -> b.applyMove(m), m -> m.cost)));
		System.out.println(
				result.entrySet().stream().filter(e -> e.getKey().isDone()).map(Map.Entry::getValue).findAny().get());
	}

	private static class Burrow {
		private final static int ROOM_A_X = 2;
		private final static int ROOM_B_X = 4;
		private final static int ROOM_C_X = 6;
		private final static int ROOM_D_X = 8;
		private final static int MIN_X = 0;
		private final static int MAX_X = 10;

		private final int maxY;
		private final Map<Point, Character> amphibs;

		public Burrow(Map<Point, Character> amphibs) {
			this.amphibs = amphibs;
			maxY = amphibs.keySet().stream().map(Point::getY).mapToInt(Long::intValue).max().getAsInt();
		}

		public boolean isDone() {
			boolean aDone = LongStream.rangeClosed(1, maxY).mapToObj(y -> new Point(ROOM_A_X, y))
					.map(p -> amphibs.getOrDefault(p, ' ')).allMatch(c -> c == 'A');
			boolean bDone = LongStream.rangeClosed(1, maxY).mapToObj(y -> new Point(ROOM_B_X, y))
					.map(p -> amphibs.getOrDefault(p, ' ')).allMatch(c -> c == 'B');
			boolean cDone = LongStream.rangeClosed(1, maxY).mapToObj(y -> new Point(ROOM_C_X, y))
					.map(p -> amphibs.getOrDefault(p, ' ')).allMatch(c -> c == 'C');
			boolean dDone = LongStream.rangeClosed(1, maxY).mapToObj(y -> new Point(ROOM_D_X, y))
					.map(p -> amphibs.getOrDefault(p, ' ')).allMatch(c -> c == 'D');
			return aDone && bDone && cDone && dDone;
		}

		public Burrow applyMove(Move move) {
			char amphib = amphibs.get(move.from);
			Map<Point, Character> afterMove = new HashMap<>();
			afterMove.putAll(amphibs);
			afterMove.remove(move.from);
			afterMove.put(move.to, amphib);
			return new Burrow(afterMove);
		}

		private int getMoveCost(Point from) {
			return (int) Math.pow(10, amphibs.get(from) - 'A');
		}

		public List<Move> getAvailableMoves() {
			List<Move> availableMoves = new ArrayList<>();
			// From hallway
			amphibs.entrySet().stream().filter(e -> e.getKey().getY() == 0).forEach(e -> {
				Point from = e.getKey();
				Point to = canEnterRoom(e.getValue());
				if (to != null && isPathFree(from, to)) {
					int cost = (int) (getMoveCost(from) * from.getManhattanDistance(to));
					Move move = new Move(from, to, cost);
					availableMoves.add(move);
				}
			});

			availableMoves.addAll(availableMovesFromRoom(ROOM_A_X, 'A'));
			availableMoves.addAll(availableMovesFromRoom(ROOM_B_X, 'B'));
			availableMoves.addAll(availableMovesFromRoom(ROOM_C_X, 'C'));
			availableMoves.addAll(availableMovesFromRoom(ROOM_D_X, 'D'));
			return availableMoves;
		}

		private List<Move> availableMovesFromRoom(int roomX, char expectedAmphib) {
			List<Move> availableMoves = new ArrayList<>();
			Point amphibInRoom = null;
			for (int y = 1; y <= maxY; y++) {
				if (amphibs.containsKey(new Point(roomX, y))) {
					amphibInRoom = new Point(roomX, y);
					break;
				}
			}
			if (amphibInRoom != null) {
				boolean shouldMove = false;
				for (long y = amphibInRoom.getY(); y <= maxY; y++) {
					if (amphibs.get(new Point(roomX, y)) != expectedAmphib) {
						shouldMove = true;
						break;
					}
				}
				if (!shouldMove) {
					return availableMoves;
				}
				for (int x = roomX - 1; x >= MIN_X; x--) {
					if (x == ROOM_A_X || x == ROOM_B_X || x == ROOM_C_X || x == ROOM_D_X) {
						continue;
					}
					if (amphibs.containsKey(new Point(x, 0))) {
						break;
					}
					int cost = (int) (getMoveCost(amphibInRoom) * amphibInRoom.getManhattanDistance(new Point(x, 0)));
					availableMoves.add(new Move(amphibInRoom, new Point(x, 0), cost));
				}
				for (int x = roomX + 1; x <= MAX_X; x++) {
					if (x == ROOM_A_X || x == ROOM_B_X || x == ROOM_C_X || x == ROOM_D_X) {
						continue;
					}
					if (amphibs.containsKey(new Point(x, 0))) {
						break;
					}
					int cost = (int) (getMoveCost(amphibInRoom) * amphibInRoom.getManhattanDistance(new Point(x, 0)));
					availableMoves.add(new Move(amphibInRoom, new Point(x, 0), cost));
				}
			}
			return availableMoves;
		}

		private boolean isPathFree(Point from, Point to) {
			boolean hallwayClear = LongStream
					.rangeClosed(Math.min(from.getX(), to.getX()), Math.max(from.getX(), to.getX()))
					.filter(x -> x != from.getX()).mapToObj(x -> new Point(x, 0)).noneMatch(amphibs::containsKey);
			return hallwayClear && !amphibs.containsKey(to);
		}

		private Point canEnterRoom(char amphibType) {
			long roomX = -1;
			switch (amphibType) {
			case 'A':
				roomX = ROOM_A_X;
				break;
			case 'B':
				roomX = ROOM_B_X;
				break;
			case 'C':
				roomX = ROOM_C_X;
				break;
			case 'D':
				roomX = ROOM_D_X;
				break;
			default:
				throw new IllegalArgumentException("" + amphibType);
			}
			for (int y = maxY; y >= 1; y--) {
				Point current = new Point(roomX, y);
				if (amphibs.get(current) == null) {
					return current;
				}
				if (amphibs.get(current) == amphibType) {
					continue;
				}
				break;
			}
			return null;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((amphibs == null) ? 0 : amphibs.hashCode());
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
			Burrow other = (Burrow) obj;
			if (amphibs == null) {
				if (other.amphibs != null)
					return false;
			} else if (!amphibs.equals(other.amphibs))
				return false;
			return true;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("#############\n");
			sb.append("#");
			for (int x = MIN_X; x <= MAX_X; x++) {
				if (amphibs.containsKey(new Point(x, 0))) {
					sb.append(amphibs.get(new Point(x, 0)));
				} else {
					sb.append(' ');
				}
			}
			sb.append("#\n");
			sb.append("###");
			sb.append(amphibs.getOrDefault(new Point(ROOM_A_X, 1), ' '));
			sb.append("#");
			sb.append(amphibs.getOrDefault(new Point(ROOM_B_X, 1), ' '));
			sb.append("#");
			sb.append(amphibs.getOrDefault(new Point(ROOM_C_X, 1), ' '));
			sb.append("#");
			sb.append(amphibs.getOrDefault(new Point(ROOM_D_X, 1), ' '));
			sb.append("###\n");
			for (int y = 2; y <= maxY; y++) {
				sb.append("  #");
				sb.append(amphibs.getOrDefault(new Point(ROOM_A_X, 2), ' '));
				sb.append("#");
				sb.append(amphibs.getOrDefault(new Point(ROOM_B_X, 2), ' '));
				sb.append("#");
				sb.append(amphibs.getOrDefault(new Point(ROOM_C_X, 2), ' '));
				sb.append("#");
				sb.append(amphibs.getOrDefault(new Point(ROOM_D_X, 2), ' '));
				sb.append("#\n");
			}
			sb.append("  #########");
			return sb.toString();
		}
	}

	private static class Move {
		private final Point from;
		private final Point to;
		private final int cost;

		public Move(Point from, Point to, int cost) {
			this.from = from;
			this.to = to;
			this.cost = cost;
		}
	}
}
