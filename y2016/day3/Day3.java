package y2016.day3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;

public class Day3 {

	public static void main(String[] args) {
		List<List<Integer>> values = Input.parseLines("y2016/day3/day3.txt", Day3::parse).collect(Collectors.toList());
		System.out.println(countValid(values));
	}

	private static List<Integer> parse(String triangle)
	{
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(triangle);
		List<Integer> result = new ArrayList<Integer>();
		while (matcher.find())
		{
			result.add(Integer.parseInt(matcher.group()));
		}
		return result;
	}
	private static int countValid(List<List<Integer>> input)
	{
		int validCount = 0;
		List<Integer> triangle = new ArrayList<Integer>();
		for (int col = 0 ; col < 3 ; col++)
		{
			for (int row = 0 ; row < input.size() ; row++)
			{
				triangle.add(input.get(row).get(col));
				if (triangle.size() == 3)
				{
					if (isValid(triangle)) {
						validCount++;
					}
					triangle.clear();
				}
			}
		}
		return validCount;
	}
	private static boolean isValid(List<Integer> triangle)
	{
		Collections.sort(triangle, Comparator.reverseOrder());
		System.out.println(triangle);

		int value = triangle.get(0);
		triangle.remove(0);
		return value - triangle.stream().mapToInt(Integer::intValue).sum() < 0;
	}
}
