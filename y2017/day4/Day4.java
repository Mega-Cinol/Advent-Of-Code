package y2017.day4;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import common.Input;

public class Day4 {

	public static void main(String[] args) {
		System.out.println(Input.parseLines("y2017/day4/day4.txt").filter(Day4::isValid).count());
	}
	private static boolean isValid(String pass)
	{
		String[] words = pass.split(" ");
		Set<String> unique = new HashSet<>();
		Stream.of(words).map(Day4::stats).forEach(unique::add);
		return unique.size() == words.length;
	}

	private static String stats(String word)
	{
		StringBuilder statsBuilder =new StringBuilder();
		for (char letter = 'a' ; letter <= 'z' ; letter++)
		{
			statsBuilder.append(letter);
			int count = 0;
			for (int i = 0 ; i < word.length() ; i++)
			{
				if (word.charAt(i) == letter)
				{
					count++;
				}
			}
			statsBuilder.append(count);
		}
		return statsBuilder.toString();
	}
}
