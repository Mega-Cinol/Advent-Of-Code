package common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Unavailable in java 17
// import javax.xml.bind.DatatypeConverter;

public class MD5 {
	public static String getHash(String prefix, int number)
	{
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update((prefix + number).getBytes());
//			int digits = (int)Math.log10(number) + 1;
//			while (digits > 0)
//			{
//				int tenPower = (int)Math.pow(10, digits - 1);
//				digest.update((byte) (number / tenPower + '0'));
//				number %= tenPower;
//				digits--;
//			}
			// Unavailable in java 17
			return "";//DatatypeConverter.printHexBinary(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
	}
	public static String getHash(String prefix)
	{
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(prefix.getBytes());
			// Unavailable in java 17
			return "";//DatatypeConverter.printHexBinary(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
