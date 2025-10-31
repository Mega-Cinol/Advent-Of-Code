package y2023.day4;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import common.Input;

public class Day4 {

	public static void main(String[] args) {
		// part 1
		System.out.println(Input.parseLines("y2023/day4/day4.txt").mapToLong(Day4::cardValue).sum());

		// part 2
		var cardsCopies = new HashMap<Integer, Counter>();
		Input.parseLines("y2023/day4/day4.txt").forEach(card -> countCards(card, cardsCopies));
		System.out.println(cardsCopies.values().stream().filter(Counter::isConfirmed).mapToLong(Counter::get).sum());
	}

	private static long cardValue(String card) {
		var matchCount = matchCount(card);
		return matchCount > 0 ? (long) Math.pow(2, matchCount - 1) : 0L;
	}

	private static void countCards(String card, Map<Integer, Counter> cardsCount) {
		var cardId = getCardId(card);
		var matches = matchCount(card);

		cardsCount.computeIfAbsent(cardId, key -> new Counter()).confirm();
		var currentCount = cardsCount.get(cardId).get();
		for (int i = cardId + 1 ; i <= cardId + matches ; i++) {
			cardsCount.computeIfAbsent(i, key -> new Counter()).increment(currentCount);
		}
	}

	private static int getCardId(String card) {
		var cardIdPattern = Pattern.compile("Card\\s*(\\d+):");
		var cardIdMatcher = cardIdPattern.matcher(card);
		if (!cardIdMatcher.find()) {
			throw new IllegalArgumentException(card);
		}
		return Integer.parseInt(cardIdMatcher.group(1));
	}

	private static long matchCount(String card) {
		var cardPattern = Pattern.compile("Card\\s*(\\d+):([^\\|]*)\\|([^\\|]*)");
		var cardMatcher = cardPattern.matcher(card);
		if (!cardMatcher.find()) {
			throw new IllegalArgumentException(card);
		}
		var winningNumbers = getNumbers(cardMatcher.group(2), HashSet::new);
		var cardNumbers = getNumbers(cardMatcher.group(3), ArrayDeque::new);
		return cardNumbers.stream().filter(winningNumbers::contains).count();
	}

	private static <T extends Collection<Long>> T getNumbers(String numbersString, Supplier<T> collectionCreator) {
		var numberPattern = Pattern.compile("\\d+");
		var numberMatcher = numberPattern.matcher(numbersString);
		var targetCollection = collectionCreator.get();
		while (numberMatcher.find()) {
			targetCollection.add(Long.parseLong(numberMatcher.group()));
		}
		return targetCollection;
	}

	private static class Counter {
		private long count = 1;
		private boolean confirmed = false;

		public void confirm() {
			confirmed = true;;
		}

		public boolean isConfirmed() {
			return confirmed;
		}

		public void increment(long step) {
			count += step;
		}

		public long get() {
			return count;
		}
	}
}
