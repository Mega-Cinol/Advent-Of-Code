package y2016.day16;

public class Day16 {

	public static void main(String[] args) {
		String data = "01000100010010111";
		while (data.length() < 35651584)
		{
			data = generate(data);
		}
		data = data.substring(0, 35651584);
		while (data.length() % 2 == 0)
		{
			data = checksum(data);
		}
		System.out.println(data);
	}

	private static String generate(String input)
	{
		StringBuilder resultBuilder = new StringBuilder();
		resultBuilder.append(input);
		resultBuilder.append('0');
		for (int i = input.length() - 1 ; i >= 0 ; i--)
		{
			if (input.charAt(i) == '0')
			{
				resultBuilder.append('1');
			}
			else
			{
				resultBuilder.append('0');
			}
		}
		return resultBuilder.toString();
	}

	private static String checksum(String data)
	{
		StringBuilder resultBuilder = new StringBuilder();
		for (int i = 0 ; i < data.length() ; i+=2)
		{
			if (data.charAt(i) == data.charAt(i + 1))
			{
				resultBuilder.append('1');
			}
			else
			{
				resultBuilder.append('0');
			}
		}
		return resultBuilder.toString();
	}
}
