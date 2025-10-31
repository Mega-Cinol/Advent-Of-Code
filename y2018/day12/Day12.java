package y2018.day12;

import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import common.Input;

public class Day12 {

	public static void main(String[] args) {
		String init = "#..####.##..#.##.#..#.....##..#.###.#..###....##.##.#.#....#.##.####.#..##.###.#.......#............";
		Set<Integer> activityRules = new HashSet<>();
		Input.parseLines("y2018/day12/day12.txt").forEach(rule -> parseRule(rule,activityRules));
		NavigableSet<Integer> currentGen = new TreeSet<>();
		for (int i = 0 ; i < init.length() ; i++)
		{
			if (init.charAt(i) == '#')
			{
				currentGen.add(i);
			}
		}

		for (long i = 0 ; i < 1000 ; i++)
		{
			NavigableSet<Integer> nextGen = new TreeSet<>();
			Set<Integer> checked = new HashSet<>();
			for (Integer pt : currentGen)
			{
				for (int p = pt - 2; p <= pt + 2; p++) {
					if (!checked.add(p))
					{
						continue;
					}
					int key = 0;
					for (int pos = -2; pos <= 2; pos++) {
						if (currentGen.contains(p + pos)) {
							key += Math.pow(2, pos + 2);
						}
					}
					if (activityRules.contains(key)) {
						nextGen.add(p);
					}
				}
			}
			if (i % 50_000 == 0)
			{
				System.out.println(i);
			}
			currentGen = nextGen;
		}
		System.out.println(currentGen.stream().mapToLong(Integer::longValue).sum() + 36 * 49_999_999_000L);
	}

	private static void parseRule(String rule, Set<Integer> rules)
	{
		int pattern = 0;
		for (int i = 0 ; i < 5 ; i++)
		{
			if (rule.charAt(i) == '#')
			{
				pattern += Math.pow(2, i);
			}
		}
		if (rule.charAt(9) == '#')
		{
			rules.add(pattern);
		}
	}
}
