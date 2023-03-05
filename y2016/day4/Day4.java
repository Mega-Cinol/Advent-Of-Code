package y2016.day4;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;

public class Day4 {

	public static void main(String[] args) {
		Input.parseLines("y2016/day4/day4.txt", Day4::roomDesc).filter(s -> !s.isEmpty()).forEach(System.out::println);
	}

	private static String roomDesc(String room)
	{
		Pattern pattern = Pattern.compile("([a-z-]*)-([0-9]*)\\[([a-z]{5})\\]");
		Matcher roomMatcher = pattern.matcher(room);
		if (!roomMatcher.matches())
		{
			throw new IllegalArgumentException(room);
		}
		String roomId = roomMatcher.group(1);
		Map<Character, Integer> charCount = new HashMap<>();
		for (int i = 0 ; i < roomId.length() ; i++)
		{
			if (roomId.charAt(i) == '-')
			{
				continue;
			}
			charCount.merge(roomId.charAt(i), 1, (oldV, newV) -> oldV + newV);
		}
		TreeMap<Integer, NavigableSet<Character>> frequentChars = charCount.entrySet().stream()
				.collect(Collectors.groupingBy(Map.Entry::getValue, () -> new TreeMap<Integer, NavigableSet<Character>>(Comparator.reverseOrder()), Collectors.mapping(Map.Entry::getKey, Collectors.toCollection(TreeSet::new))));
		String checksum = frequentChars.entrySet().stream().flatMap(e -> e.getValue().stream()).limit(5).map(c -> "" + c).collect(Collectors.joining());

		if (!checksum.equals(roomMatcher.group(3)))
		{
			return "";
		}
		int roomSectorId = Integer.parseInt(roomMatcher.group(2));
		StringBuilder decoded = new StringBuilder();
		for (int i = 0 ; i < roomId.length() ; i++)
		{
			if (roomId.charAt(i) == '-')
			{
				decoded.append(' ');
				continue;
			}
			int offset = roomId.charAt(i) - 'a' + roomSectorId;
			decoded.append((char)('a' + (offset % ('z' -'a' + 1))));
		}

		return decoded.append(" - ").append(roomSectorId).toString();
	}
}
