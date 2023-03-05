package y2020.day23;

import java.util.HashMap;
import java.util.Map;

public class Day23 {

	public static void main(String[] args) {
		String initial = "389125467";
//		String initial = "247819356";
		Map<Integer, Node> values = new HashMap<>();
		Node current = new Node(initial.charAt(0) - '0');
		values.put(current.getValue(), current);
		Node pointer = current;
		for (int i = 1; i < initial.length(); i++) {
			Node next = new Node(initial.charAt(i) - '0');
			values.put(next.getValue(), next);
			pointer.add(next);
			pointer = next;
		}
		int max = initial.length();
		for (int i = 10; i <= 1_000_000; i++) {
			Node next = new Node(i);
			values.put(next.getValue(), next);
			pointer.add(next);
			pointer = next;
			max++;
		}
		for (int i = 0; i < 10_000_000; i++) {
			int dest = current.getValue();
			do {
				dest = dest(dest, max);
			} while (current.isPickedUp(dest));
			current.move3To(values.get(dest));
			current = current.next;
		}
		System.out.println((long)values.get(1).next.getValue() * (long)values.get(1).next.next.getValue());
	}

	private static int dest(int current, int max)
	{
		int newDest = current - 1;
		if (newDest == 0)
		{
			newDest = max;
		}
		return newDest;
	}


	private static class Node {
		private Node next;
		private final int value;

		public Node(int value) {
			this.value = value;
			next = this;
		}

		public void add(Node newNext) {
			newNext.next = next;
			next = newNext;
		}

		public void move3To(Node dest) {
			Node first = next;
			Node third = next.next.next;
			next = third.next;

			third.next = dest.next;
			dest.next = first;
		}

		public boolean isPickedUp(int value) {
			return next.value == value || next.next.value == value || next.next.next.value == value;
		}

		public int getValue() {
			return value;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			Node pointer = this;
			do
			{
				sb.append(pointer.getValue());
				pointer = pointer.next;
			} while (pointer != this);
			return sb.toString();
		}
	}
}
