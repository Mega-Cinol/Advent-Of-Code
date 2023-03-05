package y2017.day9;

import common.Input;

public class Day9 {
	public static void main(String[] args) {
		String input = Input.parseLines("y2017/day9/day9.txt").findFirst().get();
		long score = 0;
		long depth = 0;
		boolean insideGarbage = false;
		long garbageCount = 0;
		for (int i = 0 ; i < input.length() ; i++)
		{
			if (input.charAt(i) == '!')
			{
				i++;
				continue;
			}
			if (insideGarbage)
			{
				if (input.charAt(i) == '>')
				{
					insideGarbage = false;
				}
				else {
					garbageCount++;
				}
				continue;
			}
			if (input.charAt(i) == '<')
			{
				insideGarbage = true;
			}
			if (input.charAt(i) == '{') {
				depth++;
				score += depth;
				System.out.println(score);
			}
			if (input.charAt(i) == '}') {
				depth--;
			}
		}
		if (depth != 0)
		{
			System.out.println(depth);
			throw new IllegalStateException();
		}
		System.out.println(score);
		System.out.println(garbageCount);
	}
}
