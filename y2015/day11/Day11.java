package y2015.day11;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;

public class Day11 {

	public static void main(String[] args) {
		String input = Input.parseLines("y2015/day11/day11_2.txt", Function.identity()).findFirst().get();
		Pattern numPattern = Pattern.compile("-?\\d+");
		Matcher numMatcher = numPattern.matcher(input);
		int result = 0;
		while (numMatcher.find())
		{
			result += Integer.parseInt(numMatcher.group(0));
		}
		System.out.println(result);
	}
}
