package y2020.day9;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import common.Input;

public class Day9 {

	public static void main(String[] args) {
		List<Long> numbers = new ArrayList<>();
		Input.parseLines("y2020/day9/day9.txt", Long::parseLong, numbers::add);
		long invalidNumber = -1;
		for (int i = 25 ; i < numbers.size() ; i++)
		{
			boolean valid = false;
			outer:
			for (int p1 = i - 25 ; p1 < i -1 ; p1++)
			{
				for (int p2 = p1 + 1 ; p2 < i ; p2++)
				{
					if (numbers.get(p1) != numbers.get(p2) && numbers.get(p1) + numbers.get(p2) == numbers.get(i))
					{
						valid = true;
						break outer;
					}
				}
			}
			if (!valid)
			{
				invalidNumber = numbers.get(i);
				break;
			}
		}
		final long invalid = invalidNumber;
		NavigableSet<Long> sequence = new TreeSet<>();
		Set<Set<Long>> sums = new HashSet<>();
		for (int i = 0 ; i < numbers.size() ; i++)
		{
			final int index = i;
			sums.forEach(seq -> seq.add(numbers.get(index)));
			Set<Long> newSeq = new HashSet<>();
			newSeq.add(numbers.get(index));
			sums.add(newSeq);
			sums.stream().filter(seq -> seq.stream().mapToLong(Long::longValue).sum() == invalid).findAny()
					.ifPresent(sequence::addAll);
			if (!sequence.isEmpty())
			{
				break;
			}
			sums.removeIf(seq -> seq.stream().mapToLong(Long::longValue).sum() > invalid);
		}
		System.out.println(sequence.higher(0L) + sequence.floor(invalid));
	}

}
