package y2023.day18;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import common.AdventSolution;
import common.Direction;
import common.Pair;
import common.Point;

public class Day18 extends AdventSolution {

	public static void main(String[] args) {
		new Day18().solve();
	}

	@Override
	public Object part1Solution() {
		var commands = getInput().map(DigCommand::fromString).toList();
		var edge = new HashSet<DigResult>();
		var current = new Point(0, 0);
		for (var command : commands) {
			var digResult = command.dig(current);
			current = digResult.endPosition();
			edge.add(digResult);
		}
//		var allPoints = edge.stream().flatMap(d -> Stream.of(d.startPosition(), d.endPosition()))
//				.collect(Collectors.toSet());
//		var minX = Point.minX(allPoints);
//		var maxX = Point.maxX(allPoints);
		var ySorted = edge.stream().map(e -> List.of(e.startPosition().getY(), e.endPosition.getY()))
				.flatMap(List::stream).sorted().distinct().toList();
		var area = 0L;
		var xIn = List.<Pair<Long>>of();
		for (int i = 0; i < ySorted.size() - 1; i++) {
			var currentY = ySorted.get(i);
			var nextY = ySorted.get(i + 1);
			var xRanges = edge.stream()
					.filter(d -> d.startPosition().getY() == currentY && d.endPosition().getY() == currentY)
					.map(e -> Pair.of(Math.min(e.startPosition().getX(), e.endPosition().getX()),
							Math.max(e.startPosition().getX(), e.endPosition().getX())))
					.sorted(Comparator.comparing(Pair::getFirst)).toList();
			var midLength = jointLength(xIn, xRanges);
			xIn = evaluateXIn(xIn, xRanges);
			var length = xIn.stream().mapToLong(p -> p.getSecond() - p.getFirst() + 1).sum();
			area += length * (nextY - currentY - 1) + midLength;
		}
		var currentY = ySorted.get(ySorted.size() - 1);
		area += edge.stream().filter(d -> d.startPosition().getY() == currentY && d.endPosition().getY() == currentY)
				.map(e -> Pair.of(Math.min(e.startPosition().getX(), e.endPosition().getX()),
						Math.max(e.startPosition().getX(), e.endPosition().getX())))
				.sorted(Comparator.comparing(Pair::getFirst)).mapToLong(p -> p.getSecond() - p.getFirst() + 1).sum();

		return area;
	}

//	private void print(List<Pair<Long>> xRanges, List<Pair<Long>> xIn, long ys, long minX, long maxX) {
//		var xsIn = xIn.stream().flatMap(p -> Stream.of(p.getFirst(), p.getSecond())).collect(Collectors.toSet());
//		for (var x = minX ; x <= maxX ; x++) {
//			var finalX = x;
//			if (xRanges.stream().anyMatch(p -> p.getFirst() <= finalX && p.getSecond() >= finalX) || xsIn.contains(x)) {
//				System.out.print('#');
//			} else {
//				System.out.print(' ');
//			}
//		}
//		System.out.println();
//		for (var y = 0 ; y <  ys ; y++) {
//			for (var x = minX ; x <= maxX ; x++) {
//				if (xsIn.contains(x)) {
//					System.out.print('#');
//				} else {
//					System.out.print(' ');
//				}
//			}
//			System.out.println();
//		}
//	}
//	private void print(Collection<DigResult> edges) {
//		var allPoints = edges.stream().flatMap(d -> Stream.of(d.startPosition(), d.endPosition()))
//				.collect(Collectors.toSet());
//		var minY = Point.minY(allPoints);
//		var maxY = Point.maxY(allPoints);
//		var minX = Point.minX(allPoints);
//		var maxX = Point.maxX(allPoints);
//		for (var y = minY; y <= maxY; y++) {
//			for (var x = minX; x <= maxX; x++) {
//				var point = new Point(x, y);
//				if (edges.stream().anyMatch(e -> e.contains(point))) {
//					System.out.print("#");
//				} else {
//					System.out.print(" ");
//				}
//			}
//			System.out.println();
//		}
//	}

	private List<Pair<Long>> evaluateXIn(List<Pair<Long>> current, List<Pair<Long>> newIn) {
		if (current.isEmpty()) {
			return newIn;
		}
		if (newIn.isEmpty()) {
			return current;
		}
		var resultXIn = new ArrayList<Pair<Long>>();
		var newInIdx = 0;
		outer: for (var currentElement : current) {
			var currentToAdd = currentElement;
			while (newInIdx < newIn.size()) {
				var newElement = newIn.get(newInIdx++);
				if (newElement.getSecond().longValue() < currentToAdd.getFirst().longValue()) {
					resultXIn.add(newElement);
				} else if (newElement.getSecond().longValue() == currentToAdd.getFirst().longValue()) {
					currentToAdd = Pair.of(newElement.getFirst(), currentToAdd.getSecond());
				} else if (newElement.getFirst().longValue() < currentToAdd.getFirst().longValue()
						&& newElement.getSecond().longValue() > currentToAdd.getFirst().longValue()) {
					throw new UnsupportedOperationException("?");
				} else if (newElement.getFirst().longValue() == currentToAdd.getFirst().longValue()
						&& newElement.getSecond().longValue() == currentToAdd.getSecond().longValue()) {
					continue outer;
				} else if (newElement.getFirst().longValue() == currentToAdd.getFirst().longValue()) {
					if (newElement.getSecond().longValue() > currentToAdd.getSecond().longValue()) {
						throw new UnsupportedOperationException("??");
					}
					currentToAdd = Pair.of(newElement.getSecond(), currentToAdd.getSecond());
				} else if (newElement.getFirst().longValue() > currentToAdd.getFirst().longValue()
						&& newElement.getSecond().longValue() < currentToAdd.getSecond().longValue()) {
					resultXIn.add(Pair.of(currentToAdd.getFirst(), newElement.getFirst()));
					currentToAdd = Pair.of(newElement.getSecond(), currentToAdd.getSecond());
				} else if (newElement.getFirst().longValue() > currentToAdd.getFirst().longValue()
						&& newElement.getSecond().longValue() == currentToAdd.getSecond().longValue()) {
					currentToAdd = Pair.of(currentToAdd.getFirst(), newElement.getFirst());
				} else if (newElement.getFirst().longValue() < currentToAdd.getSecond().longValue()
						&& newElement.getSecond().longValue() > currentToAdd.getSecond().longValue()) {
					throw new UnsupportedOperationException("???");
				} else if (newElement.getFirst().longValue() == currentToAdd.getSecond().longValue()) {
					currentToAdd = Pair.of(currentToAdd.getFirst(), newElement.getSecond());
				} else if (newElement.getFirst().longValue() > currentElement.getSecond().longValue()) {
					newInIdx--;
					break;
				}
			}
			resultXIn.add(currentToAdd);
		}
		while (newInIdx < newIn.size()) {
			resultXIn.add(newIn.get(newInIdx++));
		}
		Collections.sort(resultXIn, Comparator.comparing(Pair::getFirst));
		for (int i = 0; i < resultXIn.size() - 1; i++) {
			if (resultXIn.get(i).getSecond().longValue() == resultXIn.get(i + 1).getFirst().longValue()) {
				resultXIn.set(i, Pair.of(resultXIn.get(i).getFirst(), resultXIn.get(i + 1).getSecond()));
				resultXIn.remove(i + 1);
				i--;
			}
		}
		return resultXIn;
	}

	private long jointLength(List<Pair<Long>> current, List<Pair<Long>> newIn) {
		var jointXs = new ArrayList<Pair<Long>>();
		if (current.isEmpty()) {
			jointXs.addAll(newIn);
		}
		if (newIn.isEmpty()) {
			jointXs.addAll(current);
		}
		if (!current.isEmpty() && !newIn.isEmpty()) {
			var newInIdx = 0;
			for (var currentElement : current) {
				var currentToAdd = currentElement;
				while (newInIdx < newIn.size()) {
					var newElement = newIn.get(newInIdx++);
					if (newElement.getSecond().longValue() < currentToAdd.getFirst().longValue()) {
						jointXs.add(newElement);
					} else if (newElement.getSecond().longValue() == currentToAdd.getFirst().longValue()) {
						currentToAdd = Pair.of(newElement.getFirst(), currentToAdd.getSecond());
					} else if (newElement.getFirst().longValue() == currentToAdd.getSecond().longValue()) {
						currentToAdd = Pair.of(currentToAdd.getFirst(), newElement.getSecond());
					} else if (newElement.getFirst().longValue() > currentElement.getSecond().longValue()) {
						newInIdx--;
						break;
					}
				}
				jointXs.add(currentToAdd);
			}
			while (newInIdx < newIn.size()) {
				jointXs.add(newIn.get(newInIdx++));
			}
			Collections.sort(jointXs, Comparator.comparing(Pair::getFirst));
			for (int i = 0; i < jointXs.size() - 1; i++) {
				if (jointXs.get(i).getSecond().longValue() == jointXs.get(i + 1).getFirst().longValue()) {
					jointXs.set(i, Pair.of(jointXs.get(i).getFirst(), jointXs.get(i + 1).getSecond()));
					jointXs.remove(i + 1);
					i--;
				}
			}
		}
		return jointXs.stream().mapToLong(p -> p.getSecond() - p.getFirst() + 1).sum();
	}

	@Override
	public Object part2Solution() {
		var commands = getInput().map(DigCommand::fromString).toList();
		var edge = new HashSet<DigResult>();
		var current = new Point(0, 0);
		for (var command : commands) {
			var digResult = command.part2Converted().dig(current);
			current = digResult.endPosition();
			edge.add(digResult);
		}
		var ySorted = edge.stream().map(e -> List.of(e.startPosition().getY(), e.endPosition.getY()))
				.flatMap(List::stream).sorted().distinct().toList();
		var area = 0L;
		var xIn = List.<Pair<Long>>of();
		for (int i = 0; i < ySorted.size() - 1; i++) {
			var currentY = ySorted.get(i);
			var nextY = ySorted.get(i + 1);
			var xRanges = edge.stream()
					.filter(d -> d.startPosition().getY() == currentY && d.endPosition().getY() == currentY)
					.map(e -> Pair.of(Math.min(e.startPosition().getX(), e.endPosition().getX()),
							Math.max(e.startPosition().getX(), e.endPosition().getX())))
					.sorted(Comparator.comparing(Pair::getFirst)).toList();
			var midLength = jointLength(xIn, xRanges);
			xIn = evaluateXIn(xIn, xRanges);
			var length = xIn.stream().mapToLong(p -> p.getSecond() - p.getFirst() + 1).sum();
			area += length * (nextY - currentY - 1) + midLength;
		}
		var currentY = ySorted.get(ySorted.size() - 1);
		area += edge.stream().filter(d -> d.startPosition().getY() == currentY && d.endPosition().getY() == currentY)
				.map(e -> Pair.of(Math.min(e.startPosition().getX(), e.endPosition().getX()),
						Math.max(e.startPosition().getX(), e.endPosition().getX())))
				.sorted(Comparator.comparing(Pair::getFirst)).mapToLong(p -> p.getSecond() - p.getFirst() + 1).sum();

		return area;
	}

	private static record DigResult(Point startPosition, Point endPosition) {
//		public boolean contains(Point p) {
//			if (startPosition.getX() == endPosition.getX()) {
//				var xMatch = p.getX() == startPosition.getX();
//				var yMatch = p.getY() >= Math.min(startPosition.getY(), endPosition.getY()) && p.getY() <= Math.max(startPosition.getY(), endPosition.getY());
//				return xMatch && yMatch;
//			}
//			else {
//				var yMatch = p.getY() == startPosition.getY();
//				var xMatch = p.getX() >= Math.min(startPosition.getX(), endPosition.getX()) && p.getX() <= Math.max(startPosition.getX(), endPosition.getX());
//				return xMatch && yMatch;
//			}
//		}
	}

	private static record DigCommand(Direction direction, long steps, String color) {
		public static DigCommand fromString(String commandDescription) {
			var digPattern = Pattern.compile("([RLUD]) (\\d+) \\(#([a-f0-9]{6})\\)");
			var digCommandMatcher = digPattern.matcher(commandDescription);
			if (!digCommandMatcher.matches()) {
				throw new IllegalArgumentException(commandDescription);
			}
			var direction = switch (digCommandMatcher.group(1)) {
			case "U" -> Direction.UP;
			case "D" -> Direction.DOWN;
			case "L" -> Direction.LEFT;
			case "R" -> Direction.RIGHT;
			default -> throw new IllegalArgumentException("Unsupported direction symbol " + digCommandMatcher.group(1));
			};
			return new DigCommand(direction, Long.parseLong(digCommandMatcher.group(2)), digCommandMatcher.group(3));
		}

		public DigCommand part2Converted() {
			var direction = switch (color().substring(5)) {
			case "0" -> Direction.RIGHT;
			case "1" -> Direction.DOWN;
			case "2" -> Direction.LEFT;
			case "3" -> Direction.UP;
			default -> throw new IllegalArgumentException(color());
			};
			var step = hexToDec(color().substring(0, 5));
			return new DigCommand(direction, step, "");
		}

		private long hexToDec(String hex) {
			var result = 0L;
			for (int i = 0; i < 5; i++) {
				var value = switch (hex.charAt(i)) {
				case '0' -> 0;
				case '1' -> 1;
				case '2' -> 2;
				case '3' -> 3;
				case '4' -> 4;
				case '5' -> 5;
				case '6' -> 6;
				case '7' -> 7;
				case '8' -> 8;
				case '9' -> 9;
				case 'a' -> 10;
				case 'b' -> 11;
				case 'c' -> 12;
				case 'd' -> 13;
				case 'e' -> 14;
				case 'f' -> 15;
				default -> throw new IllegalArgumentException("Unexpected value: " + hex.charAt(i));
				};
				result += (long) (Math.pow(16, 4 - i) * value);
			}
			return result;
		}

		public DigResult dig(Point from) {
			return new DigResult(from, from.move(direction, steps));
		}
	}
}
