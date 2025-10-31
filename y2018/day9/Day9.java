package y2018.day9;

import java.util.HashMap;
import java.util.Map;

public class Day9 {

	public static void main(String[] args) {
		Node current = new Node();
		current.next = current;
		current.previous = current;
		current.value = 0;

		Map<Integer, Long> scores = new HashMap<>();
		int players = 465;
		int steps = 7149800;
		for (int step = 1; step <= steps; step++) {
			int player = (players + step - 1) % players;
			if (step % 23 != 0) {
				Node newNode = new Node();
				newNode.value = step;
				newNode.next = current.next.next;
				newNode.previous = current.next;
				newNode.next.previous = newNode;
				newNode.previous.next = newNode;
				current = newNode;
			} else {
				Node toRemove = current.previous.previous.previous.previous.previous.previous.previous;
				toRemove.previous.next = toRemove.next;
				toRemove.next.previous = toRemove.previous;
				current = toRemove.next;
				scores.merge(player, (long) (step + toRemove.value), (o, n) -> o + n);
			}
		}
		long max = 0;
		int maxPlayer = -1;
		for (Map.Entry<Integer, Long> score : scores.entrySet())
		{
			if (score.getValue() > max)
			{
				max = score.getValue();
				maxPlayer = score.getKey();
			}
		}
		System.out.println(maxPlayer + " with score " + max);
	}

	private static class Node {
		public Node next;
		public Node previous;
		public int value;
	}
}
