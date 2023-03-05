package y2015.day20;

public class Day20 {
	public static void main(String[] args)
	{
		int houseId = 300000;
		while (presentCount(houseId) < 33100000) { System.out.println(houseId);houseId++; }
		System.out.println(houseId -1);
	}
	private static int presentCount(int houseId)
	{
		int result = 0;
		for (int i = 1 ; i <= 50 ; i++)
		{
			if (houseId % i == 0)
			{
				result += houseId / i;
			}
		}
//		System.out.println(result);
		return result * 11;
	}
}
