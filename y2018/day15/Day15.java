package y2018.day15;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Direction;
import common.Input;
import common.PathFinding;
import common.Point;

public class Day15 {
	private final static Comparator<Point> READING_ORDER = Comparator.comparing(Point::getY).thenComparing(Point::getX);
	private final static Comparator<Warrior> ATTACK_TARGET = Comparator.comparing(Warrior::getHp)
			.thenComparing(Warrior::getLocation, READING_ORDER);

	public static void main(String[] args) {
		System.out.println("Part 1:");
		simulation(3);
		System.out.println("Part 2:");
		int elfAttack = 4;
		System.out.println("Attack: " + elfAttack);
		while (!simulation(elfAttack++))
		{
			System.out.println("Attack: " + elfAttack);
		}
	}

	private static boolean simulation(int elfAttack)
	{
		Set<Point> map = new HashSet<>();
		Set<Warrior> warriors = new TreeSet<>();
		List<String> input = Input.parseLines("y2018/day15/day15.txt").collect(Collectors.toList());
		for (int y = 0; y < input.size(); y++) {
			parseLine(input.get(y), y, map, warriors);
		}
		warriors.stream().filter(w -> w.getSide() == Side.ELF).forEach(w -> w.setAttack(elfAttack));
		int turnsCompleted = 0;
		int turnsIncomplete = 0;
		while (hasBothSides(warriors)) {
			for (Warrior warrior : warriors) {
				if (warrior.getHp() <= 0) {
					continue;
				}
				Set<Warrior> alive = new HashSet<>(warriors);
				alive.removeIf(w -> w.getHp() <= 0);
				if (!hasBothSides(alive)) {
					turnsIncomplete = 1;
				}
//				System.out.println(warrior);
				Set<Point> neights = warrior.getLocation().getNonDiagonalNeighbours();
				Set<Warrior> neightEnemy = alive.stream().filter(w -> w.getSide() != warrior.getSide())
						.filter(w -> neights.contains(w.getLocation())).collect(Collectors.toSet());
				if (neightEnemy.isEmpty()) {
//					System.out.println("Moving");
					Set<Point> moveTargets = alive.stream().filter(w -> w.getSide() != warrior.getSide())
							.map(Warrior::getLocation).map(Point::getNonDiagonalNeighbours).flatMap(Set::stream)
							.filter(map::contains)
							.filter(p -> alive.stream().noneMatch(w -> w.getLocation().equals(p)))
							.collect(Collectors.toSet());
//					System.out.println("Can move to: " + moveTargets);
					Map<Point, Integer> targetsDistances = PathFinding.countSteps(warrior.getLocation(), (visited) -> moveTargets.stream().anyMatch(visited::containsKey),
							p -> p.getNonDiagonalNeighbours().stream().filter(map::contains).filter(
									pt -> alive.stream().map(Warrior::getLocation).noneMatch(l -> l.equals(pt)))
									.collect(Collectors.toSet()));
					
					targetsDistances.keySet().stream().filter(moveTargets::contains).min(
					READING_ORDER).ifPresent(selectedTarget -> {
//						System.out.println("Heading to " + selectedTarget);
						Point nextStep = getNextStep(warrior.getLocation(), selectedTarget,p -> p.getNonDiagonalNeighbours().stream().filter(map::contains).filter(
								pt -> alive.stream().map(Warrior::getLocation).noneMatch(l -> l.equals(pt)))
								.collect(Collectors.toSet()), targetsDistances.get(selectedTarget));
//						System.out.println("Moving to " + nextStep);
						warrior.setLocation(nextStep);
					});
				}
				Optional<Warrior> attacked = alive.stream().filter(w -> w.getSide() != warrior.getSide())
						.filter(w -> warrior.getLocation().getNonDiagonalNeighbours().contains(w.getLocation()))
						.min(ATTACK_TARGET);
				if (attacked.isPresent()) {
//							System.out.println("Attacking " + attacked);
					attacked.get().setHp(attacked.get().getHp() - warrior.getAttack());
					if (attacked.get().getHp() <= 0) {
//								System.out.println("Warrior at " + attacked.getLocation() + " dies");
						if (attacked.get().getSide() == Side.ELF && elfAttack > 3) {
							System.out.println("Elf died");
							return false;
						}
					}
				}
			}
			warriors.removeIf(w -> w.getHp() <= 0);
			Set<Warrior> temp = new HashSet<>(warriors);
			warriors.clear();
			warriors.addAll(temp);
			System.out.println(warriors.stream().map(Warrior::toString).collect(Collectors.joining("\n")));
			turnsCompleted++;
			System.out.println(turnsCompleted + " " + warriors.stream().mapToInt(Warrior::getHp).sum());
			printMap(map, warriors);
		}
		System.out.println(warriors);
		int hpSum = warriors.stream().mapToInt(Warrior::getHp).sum();
		System.out.println(hpSum + " * " + (turnsCompleted - turnsIncomplete) + " = "
				+ hpSum * (turnsCompleted - turnsIncomplete));
		return true;
	}


	private static void printMap(Set<Point> map, Set<Warrior> warriors) {
		long maxX = Point.maxX(map);
		long maxY = Point.maxY(map);
		for (int y = 0; y <= maxY + 1; y++) {
			for (int x = 0; x <= maxX + 1; x++) {
				if (!map.contains(new Point(x, y))) {
					System.out.print("#");
				} else {
					int tempX = x;
					int tempY = y;
					Optional<Warrior> warrior = warriors.stream()
							.filter(w -> w.getLocation().equals(new Point(tempX, tempY))).findAny();
					if (warrior.isPresent()) {
						if (warrior.get().getSide() == Side.ELF) {
							System.out.print("E");
						} else {
							System.out.print("G");
						}
					} else {
						System.out.print(".");
					}
				}
			}
			System.out.println();
		}
	}

	private static boolean hasBothSides(Set<Warrior> warriors) {
		boolean elfFound = false;
		boolean goblinFound = false;
		for (Warrior warrior : warriors) {
			if (warrior.getSide() == Side.ELF) {
				elfFound = true;
			} else {
				goblinFound = true;
			}
			if (elfFound && goblinFound) {
				return true;
			}
		}
		return false;
	}

	private static Point getNextStep(Point src, Point dst, Function<Point, Set<Point>> getNeighbours, int distance) {
		Set<Point> validMoves = getNeighbours.apply(src);
		if (validMoves.contains(src.move(Direction.SOUTH))
				&& PathFinding.countSteps(src.move(Direction.SOUTH), (point, dist) -> point.equals(dst), getNeighbours)
						.getOrDefault(dst, Integer.MAX_VALUE) == distance - 1) {
			return src.move(Direction.SOUTH);
		}
		if (validMoves.contains(src.move(Direction.WEST))
				&& PathFinding.countSteps(src.move(Direction.WEST), (point, dist) -> point.equals(dst), getNeighbours)
						.getOrDefault(dst, Integer.MAX_VALUE) == distance - 1) {
			return src.move(Direction.WEST);
		}
		if (validMoves.contains(src.move(Direction.EAST))
				&& PathFinding.countSteps(src.move(Direction.EAST), (point, dist) -> point.equals(dst), getNeighbours)
						.getOrDefault(dst, Integer.MAX_VALUE) == distance - 1) {
			return src.move(Direction.EAST);
		}
		if (validMoves.contains(src.move(Direction.NORTH))
				&& PathFinding.countSteps(src.move(Direction.NORTH), (point, dist) -> point.equals(dst), getNeighbours)
						.getOrDefault(dst, Integer.MAX_VALUE) == distance - 1) {
			return src.move(Direction.NORTH);
		}
		throw new IllegalStateException(
				"Can't find the shortest path from " + src + " to " + dst + " size < " + distance);
	}

	private static void parseLine(String line, int row, Set<Point> map, Set<Warrior> warriors) {
		for (int x = 0; x < line.length(); x++) {
			if (line.charAt(x) == '.' || line.charAt(x) == 'G' || line.charAt(x) == 'E') {
				map.add(new Point(x, row));
			}
			if (line.charAt(x) == 'E') {
				warriors.add(new Warrior(new Point(x, row), Side.ELF));
			}
			if (line.charAt(x) == 'G') {
				warriors.add(new Warrior(new Point(x, row), Side.GOBLIN));
			}
		}
	}

	private static class Warrior implements Comparable<Warrior> {
		private final int id;
		private int hp = 200;
		private int attack = 3;
		private Point location;
		private final Side side;

		public Warrior(Point location, Side side) {
			this.side = side;
			this.location = location;
			id = location.hashCode();
		}

		@Override
		public int compareTo(Warrior o) {
			return READING_ORDER.compare(location, o.location);
		}

		public int getHp() {
			return hp;
		}

		public void setHp(int hp) {
			this.hp = hp;
		}

		public int getAttack() {
			return attack;
		}

		public void setAttack(int attack) {
			this.attack = attack;
		}

		public Point getLocation() {
			return location;
		}

		public void setLocation(Point location) {
			this.location = location;
		}

		public Side getSide() {
			return side;
		}

		@Override
		public String toString() {
			return side.name() + " at " + location + " with " + hp + " hp";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
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
			Warrior other = (Warrior) obj;
			if (id != other.id)
				return false;
			return true;
		}
	}

	private enum Side {
		GOBLIN, ELF;
	}
}
