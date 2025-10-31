package y2015.day4;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//Unavailable in java 17
//import javax.xml.bind.DatatypeConverter;

public class Day4 {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		int num = 0;
		while (!getHash(digest, num++).startsWith("000000"));
		System.out.println(num - 1);
	}

	private static String getHash(MessageDigest digest, int number)
	{
		digest.update(new byte[] { 'c', 'k', 'c', 'z', 'p', 'p', 'o', 'm' });
		int digits = (int)Math.log10(number) + 1;
		while (digits > 0)
		{
			int tenPower = (int)Math.pow(10, digits - 1);
			digest.update((byte) (number / tenPower + '0'));
			number %= tenPower;
			digits--;
		}
		// Unavailable in java 17
		return ""; // DatatypeConverter.printHexBinary(digest.digest());
	}
}
