package y2016.day5;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import common.MD5;

public class Day5 {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		int num = 0;
		Map<Integer, Character> pass = new HashMap<>();
		while (pass.size() < 8)
		{
			String hash = MD5.getHash("cxdnnyjw", num++);
			if (hash.startsWith("00000"))
			{
				int pos = hash.charAt(5) - '0';
				if (pos > 7)
				{
					continue;
				}
				pass.computeIfAbsent(pos, k -> hash.charAt(6));
				printPass(pass);
			}
		}
	}
	private static void printPass(Map<Integer, Character> pass)
	{
		for (int i = 0 ; i < 8 ; i++)
		{
			if (pass.containsKey(i))
			{
				System.out.print(pass.get(i));
			} else {
				System.out.print(".");
			}
		}
		System.out.println();
	}
}
