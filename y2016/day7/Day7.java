package y2016.day7;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import common.Input;

public class Day7 {

	public static void main(String[] args)
	{
		System.out.println(Input.parseLines("y2016/day7/day7.txt", Function.identity()).filter(Day7::isTls).count());
		System.out.println(Input.parseLines("y2016/day7/day7.txt", Function.identity()).filter(Day7::isSsl).count());
	}
	private static boolean isSsl(String address) {
		StringBuilder sb = new StringBuilder();
		Set<String> abas = new HashSet<>();
		Set<String> babs = new HashSet<>();
		for (int i = 0; i < address.length(); i++) {
			switch (address.charAt(i)) {
			case '[':
				findAbas(sb.toString(), abas);
				sb = new StringBuilder();
				break;
			case ']':
				findAbas(sb.toString(), babs);
				sb = new StringBuilder();
				break;
			default:
				sb.append(address.charAt(i));
				break;
			}
		}
		findAbas(sb.toString(), abas);
		return abas.stream().map(Day7::abaToBab).filter(babs::contains).findAny().isPresent();
	}

	private static boolean isTls(String address) {
		StringBuilder sb = new StringBuilder();
		boolean requiredAbbaFound = false;
		for (int i = 0; i < address.length(); i++) {
			switch (address.charAt(i)) {
			case '[':
				requiredAbbaFound |= hasAbba(sb.toString());
				sb = new StringBuilder();
				break;
			case ']':
				if (hasAbba(sb.toString()))
				{
					return false;
				}
				sb = new StringBuilder();
				break;
			default:
				sb.append(address.charAt(i));
				break;
			}
		}
		return requiredAbbaFound || hasAbba(sb.toString());
	}

	private static boolean hasAbba(String part) {
		for (int i = 0; i < part.length() - 3; i++) {
			if ((part.charAt(i) == part.charAt(i + 3)) && (part.charAt(i + 1) == part.charAt(i + 2))
					&& (part.charAt(i + 1) != part.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	private static Set<String> findAbas(String part, Set<String> abasFound) {
		for (int i = 0; i < part.length() - 2; i++) {
			if ((part.charAt(i) == part.charAt(i +2)) && (part.charAt(i + 1) != part.charAt(i))) {
				abasFound.add(part.substring(i, i + 3));
			}
		}
		return abasFound;
	}
	private static String abaToBab(String aba)
	{
		return "" + aba.charAt(1) + aba.charAt(0) + aba.charAt(1);
	}
}
