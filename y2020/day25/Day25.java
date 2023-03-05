package y2020.day25;

public class Day25 {

	public static void main(String[] args) {
		long doorPubKey = 5764801;
		long cardPubKey = 17807724;
		doorPubKey = 363891;
		cardPubKey = 335121;
		int cardLoopSize = guessLoopSize(cardPubKey);
		System.out.println(cardLoopSize);
		System.out.println(transform(doorPubKey, cardLoopSize));
	}

	private static int guessLoopSize(long publicKey)
	{
		int loopSize = 1;
		long value = 1;
		while (value != publicKey) {
			value *= 7;
			value %= 20201227;
			loopSize++;
		}
		return loopSize-1;
	}
	private static long transform(long subject, int loopSize)
	{
		long result = 1;
		for (int i = 0 ; i < loopSize ; i++)
		{
			result *= subject;
			result %= 20201227;
		}
		return result;
	}
}
