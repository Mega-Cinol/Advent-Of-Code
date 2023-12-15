package y2023.day12;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import common.AdventSolution;

public class Day12 extends AdventSolution {

	private static final Pattern subgroupPattern = Pattern.compile("[?#]+");

	public static void main(String[] args) {
		new Day12().solve();
	}

	@Override
	public Object part1Solution() {
		var sum = 0L;
		for (String input : getInput().toList()) {
			var inputsValue = getCombinations1(input);
			sum += inputsValue;
		}
		return sum;
	}

	private long getCombinations1(String line) {
		var lineParts = line.split(" ");
		var hotSprings = lineParts[0];
		var numberPattern = Pattern.compile("\\d+");
		var numberMatcher = numberPattern.matcher(lineParts[1]);
		var expectedCounts = new ArrayList<Integer>();
		while (numberMatcher.find()) {
			expectedCounts.add(Integer.parseInt(numberMatcher.group()));
		}
		return getCombinations(hotSprings, expectedCounts);
	}

	@Override
	public Object part2Solution() {
		var sum = 0L;
		for (String input : getInput().toList()) {
			var inputsValue = getCombinations2(input);
			sum += inputsValue;
		}
		return sum;
	}

	private long getCombinations2(String line) {
		var lineParts = line.split(" ");
		var hotSprings = lineParts[0] + "?" + lineParts[0] + "?" + lineParts[0] + "?" + lineParts[0] + "?" + lineParts[0];
		var numberPattern = Pattern.compile("\\d+");
		var numberMatcher = numberPattern.matcher(
				lineParts[1] + "," + lineParts[1] + "," + lineParts[1] + "," + lineParts[1] + "," + lineParts[1]);
		var expectedCounts = new ArrayList<Integer>();
		while (numberMatcher.find()) {
			expectedCounts.add(Integer.parseInt(numberMatcher.group()));
		}
		return getCombinations(hotSprings, expectedCounts);
	}

	private long getCombinations(String hotSprings, List<Integer> expectedCounts) {
		if (hotSprings.indexOf('#') == -1 && hotSprings.indexOf('.') == -1) {
			var freeSpots = hotSprings.length() - (expectedCounts.size() - 1 + expectedCounts.stream().mapToLong(Integer::longValue).sum());
			var gaps = expectedCounts.size() + 1;
			return sumCombinations(freeSpots, gaps);
		}
		var subgroupMatcher = subgroupPattern.matcher(hotSprings);
		if (!subgroupMatcher.find()) {
			return expectedCounts.isEmpty() ? 1 : 0;
		}
		var subResults = analyzeSubgroup(subgroupMatcher.group(), expectedCounts);
		var hotSpringReminder = hotSprings
				.substring(hotSprings.indexOf(subgroupMatcher.group()) + subgroupMatcher.group().length());
		long combinations = 0L;
		for (var subResult : subResults.entrySet()) {
			var subList = expectedCounts.subList(subResult.getKey(), expectedCounts.size());
			combinations += getCombinations(hotSpringReminder, subList) * subResult.getValue();
		}
		return combinations;
	}

	/**
	 * @param subgroup       substring with only '#' and '?'
	 * @param expectedCounts groups of '#' that can show up within the group
	 * @return map where key is amount of groups matched within subgroup and value
	 *         is number of combinations
	 */
	private Map<Integer, Long> analyzeSubgroup(String subgroup, List<Integer> expectedCounts) {
		var hashStart = subgroup.indexOf('#');
		if (expectedCounts.isEmpty()) {
			return hashStart == -1 ? Map.of(0, 1L) : Map.of();
		}
		if (hashStart == -1) {
			return onlyWildcards(subgroup.length(), expectedCounts);
		}
		var hashSize = 0;
		var idx = hashStart;
		while (idx < subgroup.length() && subgroup.charAt(idx++) == '#') {
			hashSize++;
		}

		var combinations = new HashMap<Integer, Long>();
		var countId = 0;
		var requiredQuestionMarks = expectedCounts.subList(0, countId).stream().mapToLong(Integer::longValue).sum()
				+ countId;
		while (requiredQuestionMarks <= hashStart && countId < expectedCounts.size()) {
			if (hashSize <= expectedCounts.get(countId)) {
				for (var startPos = hashStart
						- (expectedCounts.get(countId) - hashSize); startPos <= hashStart; startPos++) {
					if (startPos < requiredQuestionMarks) {
						continue;
					}
					if (startPos + expectedCounts.get(countId) > subgroup.length()) {
						continue;
					}
					if (startPos + expectedCounts.get(countId) < subgroup.length()
							&& subgroup.charAt(startPos + expectedCounts.get(countId)) == '#') {
						continue;
					}
					var combinationsBeforeHashes = sumCombinations(startPos - requiredQuestionMarks, countId + 1);
					if (startPos + expectedCounts.get(countId) + 1 > subgroup.length()) {
						combinations.merge(countId + 1, combinationsBeforeHashes, (a, b) -> a + b);
						continue;
					}
					var combinationsAfterHashes = analyzeSubgroup(
							subgroup.substring(startPos + expectedCounts.get(countId) + 1),
							expectedCounts.subList(countId + 1, expectedCounts.size()));
					var finalCountId = countId;
					combinationsAfterHashes.entrySet().forEach(e -> {
						combinations.merge(finalCountId + e.getKey() + 1, combinationsBeforeHashes * e.getValue(), (a, b) -> a + b);
					});
				}
			}
			countId++;
			requiredQuestionMarks = expectedCounts.subList(0, countId).stream().mapToLong(Integer::longValue).sum()
					+ countId;
		}
		return combinations;
	}

	private Map<Integer, Long> onlyWildcards(int size, List<Integer> expectedCounts) {
		var combinations = new HashMap<Integer, Long>();
		combinations.put(0, 1L);
		for (int i = 1; i <= expectedCounts.size(); i++) {
			var freeSpots = size - (i - 1 + expectedCounts.subList(0, i).stream().mapToLong(Integer::longValue).sum());
			var gaps = i + 1;
			var subCombinations = sumCombinations(freeSpots, gaps);
			if (subCombinations > 0) {
				combinations.put(i, subCombinations);
			}
		}
		return combinations;
	}

	private long sumCombinations(long sum, long elements) {
		if (sum < 0) {
			return 0;
		}
		var sumElements = sumElements(sum, elements);
		return sumElements.stream().mapToLong(this::permutations).sum();
	}

	private long permutations(Map<Long, Long> elements) {
		var elementsSummary = elements.values().stream().collect(Collectors.summarizingLong(Long::longValue));
		var sum = elementsSummary.getSum();
		var max = elementsSummary.getMax();
		var remaining = elements.values().stream().sorted(Comparator.reverseOrder()).skip(1).mapToLong(Day12::factorial).reduce(1, (a, b) -> a * b);
		return LongStream.rangeClosed(max + 1, sum).reduce(1, (a, b) -> a * b) / remaining;
	}

	private static long factorial(Long value) {
		return LongStream.rangeClosed(2, value).reduce(1, (a, b) -> a * b);
	}

	private Set<Map<Long, Long>> sumElements(long sum, long elements) {
		if (elements < 1) {
			throw new IllegalArgumentException("WTF");
		}
		if (sum == 0) {
			return Set.of(Map.of(0L, elements));
		}
		if (sum == 1) {
			return Set.of(Map.of(1L, 1L, 0L, elements - 1));
		}
		if (elements == 1) {
			return Set.of(Map.of(sum, 1L));
		}
		var lowerValue = sumElements(sum - 1, elements);
		var result = new HashSet<Map<Long, Long>>();
		lowerValue.forEach(combination -> {
			for (var combinationEntry : combination.entrySet()) {
				var newCombination = new HashMap<>(combination);
				if (combinationEntry.getValue() == 1) {
					newCombination.remove(combinationEntry.getKey());
				} else {
					newCombination.put(combinationEntry.getKey(), combinationEntry.getValue() - 1);
				}
				newCombination.merge(combinationEntry.getKey() + 1, 1L, (a, b) -> a + b);
				result.add(newCombination);
			}
		});
		return result;
	}
}
