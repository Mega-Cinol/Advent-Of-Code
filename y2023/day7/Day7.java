package y2023.day7;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;

public class Day7 {

	private static final List<HandType> HANDS_ORDER = List.of(HandType.FIVE, HandType.FOUR, HandType.FULL,
			HandType.THREE, HandType.TWO_PAIR, HandType.ONE_PAIR, HandType.HIGH_CARD);

	public static void main(String[] args) {
		var handsList = Input.parseLines("y2023/day7/day7.txt").map(Hand::from).toList();
		var mutableHandsList = new ArrayList<Hand>(handsList);
		// part 1
		Collections.sort(mutableHandsList, new HandComparatorPart1());
		System.out.println(sortedHandValue(mutableHandsList));
		// part 2
		Collections.sort(mutableHandsList, new HandComparatorPart2());
		System.out.println(mutableHandsList);
		System.out.println(sortedHandValue(mutableHandsList));
	}

	private static long sortedHandValue(List<Hand> sortedHand) {
		var result = 0L;
		for (int i = 0; i < sortedHand.size(); i++) {
			result += sortedHand.get(i).bid() * (i + 1);
		}
		return result;
	}

	private static record Hand(String hand, long bid) {
		public static Hand from(String handDescriptor) {
			var handPattern = Pattern.compile("([2-9AKQJT]{5}) (\\d+)");
			var handMatcher = handPattern.matcher(handDescriptor);
			if (!handMatcher.find()) {
				throw new IllegalArgumentException(handDescriptor);
			}
			return new Hand(handMatcher.group(1), Long.parseLong(handMatcher.group(2)));
		}
	}

	private static class HandComparatorPart1 implements Comparator<Hand> {

		private static final List<Character> CARDS_ORDER = List.of('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5',
				'4', '3', '2');

		@Override
		public int compare(Hand hand1, Hand hand2) {
			var type1 = cardsToHandType(hand1.hand());
			var type2 = cardsToHandType(hand2.hand());
			var type1Value = HANDS_ORDER.indexOf(type1);
			var type2Value = HANDS_ORDER.indexOf(type2);
			if (type1Value != type2Value) {
				return type2Value - type1Value;
			}
			int chrIdx = 0;
			while (chrIdx < Math.min(hand1.hand().length(), hand2.hand().length())) {
				if (CARDS_ORDER.indexOf(hand2.hand().charAt(chrIdx))
						- CARDS_ORDER.indexOf(hand1.hand().charAt(chrIdx)) != 0) {
					return CARDS_ORDER.indexOf(hand2.hand().charAt(chrIdx))
							- CARDS_ORDER.indexOf(hand1.hand().charAt(chrIdx));
				}
				chrIdx++;
			}
			return 0;
		}

		private HandType cardsToHandType(String cards) {
			var groupedCards = cards.chars().mapToObj(chr -> "" + chr)
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			var cardCount = groupedCards.values().stream().sorted().map(val -> "" + val).collect(Collectors.joining());
			return switch (cardCount) {
			case "5" -> HandType.FIVE;
			case "14" -> HandType.FOUR;
			case "23" -> HandType.FULL;
			case "113" -> HandType.THREE;
			case "122" -> HandType.TWO_PAIR;
			case "1112" -> HandType.ONE_PAIR;
			case "11111" -> HandType.HIGH_CARD;
			default -> throw new IllegalArgumentException("Unexpected value: " + cardCount);
			};
		}
	}

	private static class HandComparatorPart2 implements Comparator<Hand> {

		private static final List<Character> CARDS_ORDER = List.of('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4',
				'3', '2', 'J');

		@Override
		public int compare(Hand hand1, Hand hand2) {
			var type1 = cardsToHandType(hand1.hand());
			var type2 = cardsToHandType(hand2.hand());
			var type1Value = HANDS_ORDER.indexOf(type1);
			var type2Value = HANDS_ORDER.indexOf(type2);
			if (type1Value != type2Value) {
				return type2Value - type1Value;
			}
			int chrIdx = 0;
			while (chrIdx < Math.min(hand1.hand().length(), hand2.hand().length())) {
				if (CARDS_ORDER.indexOf(hand2.hand().charAt(chrIdx))
						- CARDS_ORDER.indexOf(hand1.hand().charAt(chrIdx)) != 0) {
					return CARDS_ORDER.indexOf(hand2.hand().charAt(chrIdx))
							- CARDS_ORDER.indexOf(hand1.hand().charAt(chrIdx));
				}
				chrIdx++;
			}
			return 0;
		}

		private static HandType cardsToHandType(String cards) {
			var groupedCards = cards.chars().mapToObj(chr -> "" + (char) chr)
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			var jokerCount = groupedCards.remove("J");
			if (jokerCount != null) {
				if (jokerCount == 5) {
					return HandType.FIVE;
				}
				var highest = groupedCards.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get();
				highest.setValue(highest.getValue() + jokerCount);
			}
			var cardCount = groupedCards.values().stream().sorted().map(val -> "" + val).collect(Collectors.joining());
			return switch (cardCount) {
			case "5" -> HandType.FIVE;
			case "14" -> HandType.FOUR;
			case "23" -> HandType.FULL;
			case "113" -> HandType.THREE;
			case "122" -> HandType.TWO_PAIR;
			case "1112" -> HandType.ONE_PAIR;
			case "11111" -> HandType.HIGH_CARD;
			default -> throw new IllegalArgumentException("Unexpected value: " + cardCount);
			};
		}
	}

	private static enum HandType {
		FIVE, FOUR, FULL, THREE, TWO_PAIR, ONE_PAIR, HIGH_CARD
	}
}
