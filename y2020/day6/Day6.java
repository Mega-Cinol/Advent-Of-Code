package y2020.day6;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day6 {

	public static void main(String[] args) {
		Questionaire questionaire = new Questionaire();
		Input.parseLines("y2020/day6/day6.txt", Function.identity(), questionaire::addAnswers);
		System.out.println(questionaire.count());
	}

	private static class Questionaire
	{
		private Deque<Set<Character>> answers = new ArrayDeque<>();

		public Questionaire()
		{
			answers.add(generate());
		}
		public void addAnswers(String answersLine)
		{
			if (answersLine.isEmpty())
			{
				answers.add(generate());
			}
			else
			{
				Set<Character> current = answers.getLast();
				current.retainAll(answersLine.chars().mapToObj(c -> Character.valueOf((char) c)).collect(Collectors.toSet()));
			}
		}
		public long count()
		{
			return answers.stream().mapToLong(Set::size).sum();
		}
		private Set<Character> generate()
		{
			return Stream.iterate('a', c -> (char)((int)c + 1)).limit(26).collect(Collectors.toSet());
		}
	}
}
