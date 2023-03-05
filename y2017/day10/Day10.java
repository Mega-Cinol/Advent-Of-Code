package y2017.day10;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day10 {

	private static final int SIZE = 256;
	private static final String INPUT = "106,118,236,1,130,0,235,254,59,205,2,87,129,25,255,118";

	public static void main(String[] args) {
		System.out.println(hashInputAsStr(INPUT));
		System.out.println(hashInputAsNums(INPUT));
	}

	public static String hashInputAsStr(String in)
	{
		List<Integer> dense = getDense(in);
		return dense.stream().map(num -> {
			String result = "";
			int high = num / 16;
			if (high > 9)
			{
				result += (char)('a' + high - 10);
			}
			else {
				result += (char)('0' + high);
			}
			int low = num % 16;
			if (low > 9)
			{
				result += (char)('a' + low - 10);
			}
			else {
				result += (char)('0' + low);
			}
			return result;
		}).collect(Collectors.joining());
	}

	public static List<Integer> hashInputAsNums(String in)
	{
		List<Integer> dense = getDense(in);
		return dense.stream().map(num -> {
			List<Integer> result = new ArrayList<>();
			result.add(num /16);
			result.add(num % 16);
			return result;
		}).flatMap(List::stream).collect(Collectors.toList());
	}

	private static List<Integer> getDense(String in)
	{
		Node first = new Node();
		first.value = 0;
		Node current = first;
		for (int i = 1; i < SIZE; i++) {
			Node next = new Node();
			next.value = i;
			current.next = next;
			next.previous = current;
			current = next;
		}
		current.next = first;
		first.previous = current;

		List<Integer> steps = in.chars().mapToObj(Integer::valueOf).collect(Collectors.toList());
		steps.add(17);
		steps.add(31);
		steps.add(73);
		steps.add(47);
		steps.add(23);

		current = first;
		int skipSize = 0;
		for (int cnt = 0; cnt < 64; cnt++) {
			for (Integer step : steps) {
				Node to = current;
				for (int i = 1; i < step; i++) {
					to = to.next;
				}
				reverse(current, to);
				if (step > 0) {
					current = to.next;
				}
				for (int i = 0; i < skipSize; i++) {
					current = current.next;
				}
				skipSize++;
			}
		}
		List<Integer> dense = new ArrayList<Integer>();

		int count = 0;
		int currentXor = 0;
		current = first;
		while (dense.size() < 16)
		{
			currentXor = currentXor ^ current.value;
			count++;
			if (count == 16)
			{
				dense.add(currentXor);
				currentXor = 0;
				count = 0;
			}
			current = current.next;
		}
		return dense;
	}

	private static void reverse(Node from, Node to) {
		while (from != to) {
			int tmp = from.value;
			from.value = to.value;
			to.value = tmp;
			from = from.next;
			if (from == to) {
				break;
			}
			to = to.previous;
		}
	}

	private static class Node {
		public Node next;
		public Node previous;
		public int value;
	}
}
