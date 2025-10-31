package y2015.day10;

public class Day10 {
	public static void main(String[] args)
	{
		String str = "1321131112";
		for (int i = 0 ; i < 50 ; i++)
		{
			System.out.println(str);
			str = transform(str);
		}
		System.out.println(str.length());
	}

	private static String transform(String input)
	{
		StringBuilder result = new StringBuilder();
		char previous = input.charAt(0);
		int previousCount = 1;
		for (int i = 1 ; i < input.length() ; i++)
		{
			if (input.charAt(i) == previous)
			{
				previousCount++;
			}
			else
			{
				result.append(previousCount);
				result.append(previous);
				previous = input.charAt(i);
				previousCount = 1;
			}
		}
		result.append(previousCount);
		result.append(previous);
		return result.toString();
	}
}
