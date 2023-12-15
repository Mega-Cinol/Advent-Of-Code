package y2023.day13;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import common.AdventSolution;
import common.Pair;
import common.Point;

public class Day13 extends AdventSolution {

	public static void main(String[] args) {
		new Day13().solve();
	}

	@Override
	public Object part1Solution() {
		var input = getInput().toList();
		var totalValue = 0L;
		while (!input.isEmpty()) {
			var patternEnd = input.indexOf("");
			var patternList = patternEnd >= 0 ? input.subList(0, patternEnd) : input;
			var pattern = stringToPattern(patternList);
			totalValue += getMirrorValue(pattern);
			input = patternEnd >= 0 ? input.subList(patternEnd + 1, input.size()) : List.of();
		}
		return totalValue;
	}

	private Set<Point> stringToPattern(List<String> rows) {
		var pattern = new HashSet<Point>();
		for (var y = 0; y < rows.size(); y++) {
			for (var x = 0; x < rows.get(y).length(); x++) {
				if (rows.get(y).charAt(x) == '#') {
					pattern.add(new Point(x, y));
				}
			}
		}
		return pattern;
	}

	private long getMirrorValue(Set<Point> pattern) {
		var rows = pattern.stream()
				.collect(Collectors.groupingBy(Point::getX, Collectors.mapping(Point::getY, Collectors.toSet())));
		var equalRows = rows.entrySet().stream().collect(
				Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toSet())))
				.values();
		var mid = getMid(equalRows);
		if (mid >= 0) {
			return mid + 1;
		}

		var cols = pattern.stream()
				.collect(Collectors.groupingBy(Point::getY, Collectors.mapping(Point::getX, Collectors.toSet())));
		var equalCols = cols.entrySet().stream().collect(
				Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toSet())))
				.values();
		mid = getMid(equalCols);
		if (mid >= 0) {
			return 100 * (mid + 1);
		}
		throw new IllegalArgumentException("Can't find mirror for" + pattern);
	}

	private long getMid(Collection<Set<Long>> equals) {
		var potentialMids = new HashSet<Long>();
		equals.forEach(eqGroup -> {
			var eqList = List.copyOf(eqGroup);
			if (eqList.size() < 2) {
				return;
			}
			for (int i = 0; i < eqList.size() - 1; i++) {
				for (int j = i + 1; j < eqList.size(); j++) {
					potentialMids
							.add(Math.min(eqList.get(i), eqList.get(j)) + Math.abs(eqList.get(i) - eqList.get(j)) / 2);
				}
			}
		});
		var max = equals.stream().flatMap(Set::stream).mapToLong(Long::longValue).max().getAsLong();
		var validMids = potentialMids.stream().filter(mid -> {
			var bottom = mid;
			var top = mid + 1;
			while (bottom >= 0 && top <= max) {
				var bottomCopy = bottom;
				var group = equals.stream().filter(gr -> gr.contains(bottomCopy)).findAny();
				if (!group.isPresent() || !group.get().contains(top)) {
					return false;
				}
				top++;
				bottom--;
			}
			return true;
		}).toList();
		if (validMids.size() > 1) {
			throw new IllegalArgumentException("More than one mid found! " + equals);
		}
		if (validMids.isEmpty()) {
			return -1;
		}
		return validMids.get(0);
	}

	@Override
	public Object part2Solution() {
		var input = getInput().toList();
		var totalValue = 0L;
		while (!input.isEmpty()) {
			var patternEnd = input.indexOf("");
			var patternList = patternEnd >= 0 ? input.subList(0, patternEnd) : input;
			var pattern = stringToPattern(patternList);
			totalValue += getMirrorValue2(pattern);
			input = patternEnd >= 0 ? input.subList(patternEnd + 1, input.size()) : List.of();
		}
		return totalValue;
	}

	private long getMirrorValue2(Set<Point> pattern) {
		var rows = pattern.stream()
				.collect(Collectors.groupingBy(Point::getX, Collectors.mapping(Point::getY, Collectors.toSet())));
		var equalRows = rows.entrySet().stream().collect(
				Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toSet())))
				.values();
		var almostEqualRows = findAlmostEqual(rows);
		var mid = getMid2(equalRows, almostEqualRows);
		if (mid >= 0) {
			return mid + 1;
		}

		var cols = pattern.stream()
				.collect(Collectors.groupingBy(Point::getY, Collectors.mapping(Point::getX, Collectors.toSet())));
		var equalCols = cols.entrySet().stream().collect(
				Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toSet())))
				.values();
		var almostEqualCols = findAlmostEqual(cols);
		mid = getMid2(equalCols, almostEqualCols);
		if (mid >= 0) {
			return 100 * (mid + 1);
		}
		throw new IllegalArgumentException("Can't find mirror for" + pattern);
	}

	private Set<Pair<Long>> findAlmostEqual(Map<Long, Set<Long>> rows) {
		var almostEqual = new HashSet<Pair<Long>>();
		var keyList = List.copyOf(rows.keySet());
		for (int i = 0; i < keyList.size() - 1; i++) {
			for (int j = i + 1; j < keyList.size(); j++) {
				var set1 = rows.get(keyList.get(i));
				var set2 = rows.get(keyList.get(j));
				var largerSet = set1.size() > set2.size() ? set1 : set2;
				var smallerSet = set1.size() > set2.size() ? set2 : set1;
				if ((largerSet.size() - smallerSet.size() == 1) && (largerSet.containsAll(smallerSet))) {
					almostEqual.add(Pair.of(keyList.get(i), keyList.get(j)));
				}
			}
		}
		return almostEqual;
	}

	private long getMid2(Collection<Set<Long>> equals, Set<Pair<Long>> almostEquals) {
		var potentialMids = new HashSet<Long>();
		equals.forEach(eqGroup -> {
			var eqList = List.copyOf(eqGroup);
			if (eqList.size() < 2) {
				return;
			}
			for (int i = 0; i < eqList.size() - 1; i++) {
				for (int j = i + 1; j < eqList.size(); j++) {
					potentialMids
							.add(Math.min(eqList.get(i), eqList.get(j)) + Math.abs(eqList.get(i) - eqList.get(j)) / 2);
				}
			}
		});
		var max = equals.stream().flatMap(Set::stream).mapToLong(Long::longValue).max().getAsLong();
		potentialMids.add(0L);
		potentialMids.add(max - 1);
		var validMids = potentialMids.stream().filter(mid -> {
			var bottom = mid;
			var top = mid + 1;
			var faultCount = 0;
			while (bottom >= 0 && top <= max) {
				var bottomCopy = bottom;
				var group = equals.stream().filter(gr -> gr.contains(bottomCopy)).findAny();
				if (!group.isPresent() || !group.get().contains(top)) {
					if (almostEquals.contains(Pair.of(top, bottomCopy))) {
						faultCount++;
					} else {
						return false;
					}
				}
				top++;
				bottom--;
			}
			return faultCount == 1;
		}).toList();
		if (validMids.size() > 1) {
			throw new IllegalArgumentException("More than one mid found! " + equals);
		}
		if (validMids.isEmpty()) {
			return -1;
		}
		return validMids.get(0);
	}
}
