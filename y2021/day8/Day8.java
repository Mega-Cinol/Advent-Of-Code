package y2021.day8;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day8 {
	public static void main(String[] args)
	{
		List<String> inputs = Input.parseLines("y2021/day8/day8.txt").collect(Collectors.toList());
		// part1
		System.out.println(inputs.stream()
			.map(input -> input.split("\\|")[1])
			.map(String::trim)
			.map(input -> input.split(" "))
			.flatMap(Stream::of)
			.map(String::trim)
			.filter(input -> input.length() == 2 ||  input.length() == 3 || input.length() == 4 || input.length() == 7)
			.count());
		// part2
		System.out.println(inputs.stream()
				.mapToInt(Day8::decode)
				.sum());
	}
/*
	private enum DisplaySegment
	{
		TOP,
		TOP_LEFT,
		TOP_RIGHT,
		MID,
		BOTTOM_LEFT,
		BOTTOM_RIGHT,
		BOTTOM
	}
	private enum Digits
	{
		ZERO(DisplaySegment.TOP, DisplaySegment.TOP_RIGHT, DisplaySegment.BOTTOM_RIGHT, DisplaySegment.BOTTOM, DisplaySegment.BOTTOM_LEFT, DisplaySegment.TOP_LEFT),
		ONE(DisplaySegment.TOP_RIGHT, DisplaySegment.BOTTOM_RIGHT),
		TWO(DisplaySegment.TOP, DisplaySegment.TOP_RIGHT, DisplaySegment.MID, DisplaySegment.BOTTOM_LEFT, DisplaySegment.BOTTOM),
		THREE(DisplaySegment.TOP, DisplaySegment.TOP_RIGHT, DisplaySegment.MID, DisplaySegment.BOTTOM_RIGHT, DisplaySegment.BOTTOM),
		FOUR(DisplaySegment.TOP_LEFT, DisplaySegment.TOP_RIGHT, DisplaySegment.MID, DisplaySegment.BOTTOM_RIGHT),
		FIVE(DisplaySegment.TOP, DisplaySegment.TOP_LEFT, DisplaySegment.MID, DisplaySegment.BOTTOM_RIGHT, DisplaySegment.BOTTOM),
		SIX(DisplaySegment.TOP, DisplaySegment.TOP_LEFT, DisplaySegment.MID, DisplaySegment.BOTTOM_RIGHT, DisplaySegment.BOTTOM, DisplaySegment.BOTTOM_LEFT),
		SEVEN(DisplaySegment.TOP, DisplaySegment.TOP_RIGHT, DisplaySegment.BOTTOM_RIGHT),
		EIGHT(DisplaySegment.TOP, DisplaySegment.TOP_RIGHT, DisplaySegment.BOTTOM_RIGHT, DisplaySegment.BOTTOM, DisplaySegment.BOTTOM_LEFT, DisplaySegment.TOP_LEFT, DisplaySegment.MID),
		NINE(DisplaySegment.TOP, DisplaySegment.TOP_RIGHT, DisplaySegment.BOTTOM_RIGHT, DisplaySegment.BOTTOM, DisplaySegment.TOP_LEFT, DisplaySegment.MID),
		;
		private final Set<DisplaySegment> segments;
		Digits(DisplaySegment... segments)
		{
			this.segments = Stream.of(segments).collect(Collectors.toSet());
		}
		public Set<DisplaySegment> getSegments()
		{
			return segments;
		}
	}*/
	public static int decode(String input)
	{
		Set<String> digitCandidates = Stream.of(input.split("\\|")[0].trim().split(" ")).map(String::trim)
				.collect(Collectors.toCollection(HashSet::new));
		/*
		Map<DisplaySegment, Set<Character>> segmentCandidates = new HashMap<>();
		digitCandidates.stream().forEach(candidate -> {
			Stream.of(Digits.values()).filter(d -> d.getSegments().size() == candidate.length())
					.flatMap(d -> d.getSegments().stream())
					.forEach(ds -> candidate.chars().mapToObj(c -> Character.valueOf((char) c))
							.forEach(c -> segmentCandidates.computeIfAbsent(ds, key -> new HashSet<>()).add(c)));
		});
		System.out.println(segmentCandidates);
		*/
		Map<String, Integer> digits = new HashMap<>();
		digitCandidates.stream().filter(c -> c.length() == 2).forEach(one -> addAndVerify(digits, one, 1));
		digitCandidates.removeAll(digits.keySet());
		digitCandidates.stream().filter(c -> c.length() == 3).forEach(seven -> addAndVerify(digits, seven, 7));
		digitCandidates.removeAll(digits.keySet());
		digitCandidates.stream().filter(c -> c.length() == 4).forEach(four -> addAndVerify(digits, four, 4));
		digitCandidates.removeAll(digits.keySet());
		digitCandidates.stream().filter(c -> c.length() == 7).forEach(eight -> addAndVerify(digits, eight, 8));
		digitCandidates.removeAll(digits.keySet());

		digitCandidates.stream().filter(c -> c.length() == 5).filter(threeC -> has(threeC, byValue(digits, 1), 2)).forEach(three -> addAndVerify(digits, three, 3));
		digitCandidates.removeAll(digits.keySet());

		digitCandidates.stream().filter(c -> c.length() == 6).filter(sixC -> has(sixC, byValue(digits, 1), 1)).forEach(six -> addAndVerify(digits, six, 6));
		digitCandidates.removeAll(digits.keySet());

		String six = byValue(digits, 6);
		char upperRightCandidate = '\0';
		for (char c = 'a' ; c <= 'g' ; c++)
		{
			if (six.indexOf(c) == -1)
			{
				upperRightCandidate = c;
				break;
			}
		}
		char upperRight = upperRightCandidate;

		digitCandidates.stream().filter(c -> c.length() == 5).filter(twoC -> twoC.indexOf(upperRight) != -1).forEach(two -> addAndVerify(digits, two, 2));
		digitCandidates.removeAll(digits.keySet());
		digitCandidates.stream().filter(c -> c.length() == 5).forEach(five -> addAndVerify(digits, five, 5));
		digitCandidates.removeAll(digits.keySet());

		String two = byValue(digits, 2);
		String five = byValue(digits, 5);
		char bottomLeftCandidate = '\0';
		for (int i = 0 ; i < five.length() ; i++)
		{
			if (five.indexOf(two.charAt(i)) == -1 && upperRight != two.charAt(i))
			{
				bottomLeftCandidate = two.charAt(i);
				break;
			}
		}
		char bottomLeft = bottomLeftCandidate;

		digitCandidates.stream().filter(c -> c.length() == 6).filter(zeroC -> has(zeroC, "" + bottomLeft, 1)).forEach(zero -> addAndVerify(digits, zero, 0));
		digitCandidates.removeAll(digits.keySet());
		digitCandidates.stream().forEach(nine -> addAndVerify(digits, nine, 9));
		digitCandidates.removeAll(digits.keySet());

		List<Integer> decodedDigits = Stream.of(input.split("\\|")[1].trim().split(" ")).map(String::trim).map(str -> findInMap(digits, str)).collect(Collectors.toList());
		return decodedDigits.get(0) * 1000 + decodedDigits.get(1) * 100 + decodedDigits.get(2) * 10 + decodedDigits.get(3);
	}

	private static int findInMap(Map<String, Integer> map, String key)
	{
		return map.entrySet().stream().filter(e -> e.getKey().length() == key.length()).filter(e -> has(e.getKey(), key, key.length())).map(Map.Entry::getValue).findAny().get();
	}

	private static void addAndVerify(Map<String, Integer> map, String key, int value)
	{
		if (map.containsKey(key))
		{
			throw new IllegalArgumentException("Key already mapped");
		}
		if (map.values().stream().anyMatch(v -> v == value))
		{
			throw new IllegalArgumentException("Value already mapped");
		}
		map.put(key, value);
	}

	private static boolean has(String str, String letters, int expectedOverlapping)
	{
		int overlapping = 0;
		for (int i = 0 ; i < letters.length() ; i++)
		{
			if (str.indexOf(letters.charAt(i)) != -1)
			{
				overlapping++;
			}
		}
		return overlapping == expectedOverlapping;
	}

	private static String byValue(Map<String, Integer> map, int value)
	{
		assert map.entrySet().stream().filter(e -> e.getValue() == value).map(Map.Entry::getKey).count() == 1;
		return map.entrySet().stream().filter(e -> e.getValue() == value).map(Map.Entry::getKey).findAny().get();
	}
}
