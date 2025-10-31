package y2023.day3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day3 {

	public static void main(String[] args) {
		// part 1
		var engine = Input.parseGrid("y2023/day3/day3.txt", Function.identity());
		var maxX = Point.maxX(engine.keySet());
		var maxY = Point.maxY(engine.keySet());
		var partNumbers = new ArrayList<PartNumber>();
		PartNumber current = null;
		for (var y = 0L; y <= maxY; y++) {
			for (var x = 0L; x <= maxX; x++) {
				if (Character.isDigit(engine.get(new Point(x, y)))) {
					if (current == null) {
						current = new PartNumber();
						partNumbers.add(current);
					}
					var symbols = new Point(x, y).getNeighbours().stream().filter(engine::containsKey)
							.filter(s -> engine.get(s) != '.').filter(s -> !Character.isDigit(engine.get(s)))
							.collect(Collectors.toSet());
					current.addDigit(Integer.parseInt("" + engine.get(new Point(x, y))), symbols);
				} else {
					current = null;
				}
			}
		}
		System.out.println(
				partNumbers.stream().filter(p -> !p.getSymbols().isEmpty()).mapToLong(PartNumber::getNumber).sum());
		// part2
		var gears = new HashMap<Point, List<PartNumber>>();
		partNumbers.stream().forEach(p -> p.getSymbols().stream().filter(s -> engine.get(s) == '*')
				.forEach(s -> gears.computeIfAbsent(s, sy -> new ArrayList<>()).add(p)));
		System.out.println(gears.entrySet().stream().filter(e -> e.getValue().size() == 2)
				.mapToLong(e -> e.getValue().get(0).getNumber() * e.getValue().get(1).getNumber()).sum());
	}

	private static class PartNumber {
		private Long number = 0L;
		private Set<Point> symbols = new HashSet<>();

		public void addDigit(int digit, Set<Point> symbols) {
			number *= 10;
			number += digit;
			this.symbols.addAll(symbols);
		}

		public long getNumber() {
			return number;
		}

		public Set<Point> getSymbols() {
			return symbols;
		}
	}
}
