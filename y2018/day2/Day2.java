package y2018.day2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import common.Input;

public class Day2 {
	private static int count2 = 0;
	private static int count3 = 0;

	public static void main(String[] args) {
		Input.parseLines("y2018/day2/day2.txt").forEach(Day2::count);
		System.out.println(count2 * count3);
		List<String> boxes = Input.parseLines("y2018/day2/day2.txt").collect(Collectors.toList());
		for (int i = 0 ; i < boxes.size() - 1 ; i++)
		{
			middle:
			for (int j = i + 1 ; j < boxes.size() ; j++)
			{
				boolean diffFound = false;
				String box1 = boxes.get(i);
				String box2 = boxes.get(j);
				for (int letter = 0 ; letter < boxes.get(i).length() ; letter++)
				{
					if (box1.charAt(letter) != box2.charAt(letter))
					{
						if (diffFound)
						{
							continue middle;
						}
						diffFound = true;
					}
				}
				System.out.println(box1);
				System.out.println(box2);
				return;
			}
		}
	}

	private static void count(String txt)
	{
		Map<Character, Integer> letters = new HashMap<>();
		for (int i = 0 ; i < txt.length() ; i++)
		{
			letters.merge(txt.charAt(i), 1, (o, n) -> o + n);
		}
		if (letters.containsValue(2))
		{
			count2++;
		}
		if (letters.containsValue(3))
		{
			count3++;
		}
	}
}
