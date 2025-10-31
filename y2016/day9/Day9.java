package y2016.day9;

import common.Input;

public class Day9 {

	public static void main(String[] args) {
		Input.parseLines("y2016/day9/day9.txt", Day9::strLength).forEach(System.out::println);
	}
	private static long strLength(String encoded)
	{
		long count = 0;
		for (int i = 0 ; i < encoded.length() ; i++)
		{
			if (encoded.charAt(i) == '(')
			{
				int numberOfChars = Integer.parseInt(encoded.substring(i + 1, encoded.indexOf('x', i)));
				int repeat = Integer.parseInt(encoded.substring(encoded.indexOf('x', i) + 1, encoded.indexOf(')', i)));
				count += strLength(encoded.substring(encoded.indexOf(')', i) + 1, encoded.indexOf(')', i) + 1 + numberOfChars)) * repeat;
				i = encoded.indexOf(')', i) + numberOfChars;
			} else {
				count++;
			}
		}
		return count;
	}
}
