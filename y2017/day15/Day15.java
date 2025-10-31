package y2017.day15;

public class Day15 {

	public static void main(String[] args) {
		long count = 0;
		long a = 618;
		long b = 814;
		for (int i = 0 ; i < 5_000_000 ; i++)
		{
			if (i % 400_000 == 0)
			{
				System.out.println(i / 50_000 + "%");
			}
			do
			{
				a = (a * 16807) % 2147483647L;
			} while ((a & 3) != 0);
			do
			{
				b = (b * 48271) % 2147483647L;
			}while ((b & 7) != 0);
			if ((a & 0xffff) == (b & 0xffff))
			{
				count++;
			}
		}
		System.out.println(count);
	}

}
