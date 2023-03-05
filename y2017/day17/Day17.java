package y2017.day17;

public class Day17 {

	public static void main(String[] args) {
		Node start = new Node();
		start.value = 0;
		start.next = start;
		Node current = start;
		int step = 344;
		for (int i = 0 ; i < 50_000_000 ; i++)
		{
			if (i % 500_000 == 0)
			{
				System.out.println(i / 500_000 + "%");
			}
			for (int j = 0 ; j < step ; j++)
			{
				current = current.next;
			}
			Node newNode = new Node();
			newNode.value = i + 1;
			newNode.next = current.next;
			current.next = newNode;
			current = newNode;
		}
		while (current.value != 0)
		{
			current = current.next;
		}
		System.out.println(current.next.value);
	}

	private static class Node
	{
		public Node next;
		public long value;
	}
}
