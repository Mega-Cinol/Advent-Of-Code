package y2016.day14;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import common.MD5;

public class Day14 {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		Set<Integer> keysFound = new TreeSet<>();
		Map<Integer, String> knownHashes = new HashMap<>();
		getKey(0, knownHashes);
		int idx = 0;
		while (keysFound.size() < 67) {
			int idxCopy = idx;
			String hash = getKey(idx, knownHashes);
			if (findRepeated(hash, 3).stream().anyMatch(c -> confirm(c, idxCopy, knownHashes))) {
				keysFound.add(idx);
			}
			idx++;
		}
		System.out.println(idx-1);
		System.out.println(keysFound);
	}

	private static String getKey(int idx, Map<Integer, String> knownHashes)
	{
		return knownHashes.computeIfAbsent(idx, i -> {
			String key = MD5.getHash("qzyelonm", i).toLowerCase();
			for (int cnt = 0 ; cnt < 2016 ; cnt++)
			{
				key = MD5.getHash(key).toLowerCase();
			}
			return key;
		});
	}
	private static boolean confirm(char c, int idx, Map<Integer, String> knownHashes) {
		for (int i = idx + 1; i <= idx + 1000; i++) {
			String hash = getKey(i, knownHashes);
			if (findRepeated(hash, 5).contains(c)) {
				return true;
			}
		}
		return false;
	}

	private static Set<Character> findRepeated(String input, int count) {
		Set<Character> repeatedChars = new HashSet<>();
		int sequenceLength = 0;
		char previous = '\0';
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == previous) {
				sequenceLength++;
			} else {
				if (sequenceLength >= count) {
					repeatedChars.add(previous);
				}
				sequenceLength = 1;
				previous = input.charAt(i);
			}
		}
		if (sequenceLength >= count) {
			repeatedChars.add(previous);
		}
		return repeatedChars;
	}
}
