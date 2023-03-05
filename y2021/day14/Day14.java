package y2021.day14;

import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

import common.Input;

public class Day14 {
	private static final int PART_1_STEPS = 10;
	private static final int PART_2_STEPS = 40;
	private static final int PART_2_STEPS_HALF = PART_2_STEPS / 2;
	public static void main(String[] args) {
		List<String> input = Input.parseLines("y2021/day14/day14.txt").collect(Collectors.toList());
		String polymer = input.get(0);
		Map<String, String> additions = new HashMap<>();
		input.subList(2, input.size()).forEach(str -> additions.put(str.substring(0, 2), str.substring(6, 7)));

		// part1
		LongSummaryStatistics statistics = getStatisticForString(polymer, additions, PART_1_STEPS).values().stream()
				.mapToLong(Long::longValue).summaryStatistics();
		System.out.println(statistics.getMax() - statistics.getMin());

		// part2
		Map<String, Map<Character, Long>> pairsAfter20Steps = additions.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> getStatisticForString(e.getKey(), additions, PART_2_STEPS_HALF)));
		String polymerAfter20Steps = convertString(polymer, additions, PART_2_STEPS_HALF);

		Map<Character, Long> charactersAfter40Steps = new HashMap<>();
		for (int i = 0; i < polymerAfter20Steps.length() - 1; i++) {
			Map<Character, Long> pairMap = pairsAfter20Steps.get(polymerAfter20Steps.substring(i, i + 2));
			for (Map.Entry<Character, Long> letter : pairMap.entrySet())
			{
				long value = letter.getValue();
				if ((letter.getKey() == polymerAfter20Steps.charAt(i)) || (letter.getKey() == polymerAfter20Steps.charAt(i + 1)))
				{
					value--;
				}
				if ((letter.getKey() == polymerAfter20Steps.charAt(i)) && (letter.getKey() == polymerAfter20Steps.charAt(i + 1)))
				{
					value--;
				}
				charactersAfter40Steps.merge(letter.getKey(), value, (o, n) -> o + n);
			}
			charactersAfter40Steps.merge(polymerAfter20Steps.charAt(i), 1L, (o, n) -> o + n);
		}
		charactersAfter40Steps.merge(polymerAfter20Steps.charAt(polymerAfter20Steps.length() - 1), 1L, (o, n) -> o + n);
		statistics = charactersAfter40Steps.values().stream()
				.mapToLong(Long::longValue).summaryStatistics();
		System.out.println(statistics.getMax() - statistics.getMin());

//		//part2
//		polymer = input.get(0);
//		for (int step = 0; step < 40; step++) {
//			System.out.println(step);
//			StringBuilder polymerBuilder = new StringBuilder();
//			for (int i = 0; i < polymer.length() - 1; i++) {
//				polymerBuilder.append(polymer.charAt(i));
//				polymerBuilder.append(additions.get(polymer.substring(i, i + 2)));
//			}
//			polymerBuilder.append(polymer.charAt(polymer.length() - 1));
//			polymer = polymerBuilder.toString();
//		}
//		characters = new HashMap<>();
//		for (int i = 0; i < polymer.length(); i++) {
//			characters.merge(polymer.charAt(i), 1, (o, n) -> o + n);
//		}
//		statistics = characters.values().stream().mapToInt(Integer::intValue).summaryStatistics();
//		System.out.println(statistics.getMax() - statistics.getMin());

	}

	private static Map<Character, Long> getStatisticForString(String str, Map<String, String> additions,
			int expectedSteps) {
		str = convertString(str, additions, expectedSteps);
		Map<Character, Long> characters = new HashMap<>();
		for (int i = 0; i < str.length(); i++) {
			characters.merge(str.charAt(i), 1L, (o, n) -> o + n);
		}
		return characters;
	}

	private static String convertString(String str, Map<String, String> additions,
			int expectedSteps)
	{
		for (int step = 0; step < expectedSteps; step++) {
			StringBuilder polymerBuilder = new StringBuilder();
			for (int i = 0; i < str.length() - 1; i++) {
				polymerBuilder.append(str.charAt(i));
				polymerBuilder.append(additions.get(str.substring(i, i + 2)));
			}
			polymerBuilder.append(str.charAt(str.length() - 1));
			str = polymerBuilder.toString();
		}
		return str;
	}
}
