package y2022.day3;

import java.util.Set;
import java.util.stream.Collectors;

import common.Input;

class Day3 {
	public static void main(String[] args) {
		System.out.println(Input.parseLines("y2022/day3/day3.txt").mapToInt(Day3::toPriority).sum());

		var rucksacks = Input.parseLines("y2022/day3/day3.txt").toList();
		Set<Integer> commonItems = null;
		int prioSum = 0;
		for (int i = 0 ; i < rucksacks.size() ; i++) {
			if (i % 3 == 0) {
				commonItems = strToCharSet(rucksacks.get(i));
			} else {
				commonItems.retainAll(strToCharSet(rucksacks.get(i)));
			}
			if (i % 3 == 2) {
				prioSum += charToPrio(commonItems.stream().findAny().get());
			}
		}
		System.out.println(prioSum);
	}

	private static int toPriority(String items) {
		Set<Integer> chars = items.chars().limit(items.length() / 2).mapToObj(Integer::valueOf)
				.collect(Collectors.toSet());
		return items.chars().skip(items.length() / 2).filter(chars::contains).map(Day3::charToPrio).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Illegal items " + items));
	}

	private static int charToPrio(int character) {
		if (character >= 'a' && character <= 'z') {
			return character - 'a' + 1;
		}
		if (character >= 'A' && character <= 'Z') {
			return character - 'A' + 27;
		}
		throw new IllegalArgumentException("Illegal character " + character);
	}

	private static Set<Integer> strToCharSet(String input) {
		return input.chars().mapToObj(Integer::valueOf).collect(Collectors.toSet());
	}
}
