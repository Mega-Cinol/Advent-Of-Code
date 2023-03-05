package y2015.day25;

public class Day25 {
	// 3009 * 1505 + 1
	// (6027 + 3011) / 2 * 3018
	public static void main(String[] args)
	{
		long code = 20151125; 
		for (int diag = 1 ; diag < 6029 ;  diag++)
		{
			for (int row = diag ; row > 0 ; row--)
			{
				int col = diag - row +1;
				if ((row == 3010) && (col == 3019))
				{
					System.out.println(code);
					return;
				}
				code = nextCode(code);
			}
		}
	}
	private static long nextCode(long code) {
		code *= 252533;
		code %= 33554393;
		return code;
	}
}
