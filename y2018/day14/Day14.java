package y2018.day14;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import common.CircularLinkedList;

public class Day14 {
	public static void main(String[] args) {
		int size = 939601;
		List<Integer> expected = new ArrayList<>();
		int numOfDigits = (int)Math.log10(size) + 1;
		for (int digit = numOfDigits ; digit > 0 ; digit--)
		{
			int digitPow10 = (int)Math.pow(10, digit);
			expected.add((size % digitPow10) / (digitPow10 / 10));
		}

		CircularLinkedList<Integer> recipies = new CircularLinkedList<Integer>();
		recipies.add(3);
		recipies.add(7);
		Matcher m = new Matcher(expected);
		m.add(3);
		m.add(7);
		CircularLinkedList<Integer>.Node firstElf = recipies.get(0);
		CircularLinkedList<Integer>.Node secondElf = recipies.get(1);

		while (recipies.size() < size + 10) {
			int newRecipie = firstElf.getValue() + secondElf.getValue();
			if (newRecipie > 9) {
				recipies.add(1);
				m.add(1);
			}
			recipies.add(newRecipie % 10);
			m.add(newRecipie % 10);
			firstElf = firstElf.browse(firstElf.getValue() + 1);
			secondElf = secondElf.browse(secondElf.getValue() + 1);
		}
		CircularLinkedList<Integer>.Node lastTen = recipies.get(size - recipies.size());
		for (int i = 0 ; i < 10 ; i++)
		{
			System.out.print(lastTen.getValue());
			lastTen = lastTen.browse(1);
		}
		System.out.println();
		if (m.getMatch().isEmpty()) {
			while (m.getMatch().isEmpty()) {
				int newRecipie = firstElf.getValue() + secondElf.getValue();
				if (newRecipie > 9) {
					recipies.add(1);
					m.add(1);
				}
				recipies.add(newRecipie % 10);
				m.add(newRecipie % 10);
				firstElf = firstElf.browse(firstElf.getValue() + 1);
				secondElf = secondElf.browse(secondElf.getValue() + 1);
			}
		}
		m.getMatch().stream().mapToInt(Integer::intValue).min().ifPresent(v -> System.out.println(v - expected.size()));
	}

	private static class Matcher
	{
		private final List<Integer> expected = new LinkedList<>();
		private Set<Integer> matched = new HashSet<>();
		private Set<Integer> matchAfter = new HashSet<>();
		int receivedCount = 0;

		public Matcher(List<Integer> expected)
		{
			this.expected.addAll(expected);
		}

		public Set<Integer> getMatch()
		{
			return matchAfter;
		}

		public void add(int next)
		{
			receivedCount++;
			matched.removeIf(idx -> expected.get(idx + 1) != next);
			matched = matched.stream().map(i -> i + 1).collect(Collectors.toSet());
			if (matched.contains(expected.size() - 1))
			{
				matchAfter.add(receivedCount);
				matched.remove(expected.size() - 1);
			}
			if (next == expected.get(0))
			{
				matched.add(0);
			}
		}
	}
}
