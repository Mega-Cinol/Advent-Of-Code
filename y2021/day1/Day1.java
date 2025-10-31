package y2021.day1;

import java.util.List;
import java.util.stream.Collectors;

import common.Input;

public class Day1 {
	public static void main(String[] args)
	{
		List<Integer> depths = Input.parseLines("y2021/day1/day1.txt", Integer::valueOf).collect(Collectors.toList());
		// part 1
		int increasedCount = 0;
		for (int i = 1 ; i < depths.size() ; i++)
		{
			if (depths.get(i) > depths.get(i-1))
			{
				increasedCount++;
			}
		}
		System.out.println(increasedCount);
		// part 2
		increasedCount = 0;
		for (int i = 2 ; i < depths.size() -1 ; i++)
		{
			if (depths.get(i + 1) > depths.get(i-2))
			{
				increasedCount++;
			}
		}
		System.out.println(increasedCount);
	}
}
