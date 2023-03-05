package y2016.day19;

public class Day19 {

	public static void main(String[] args) {
		long count = 3001330;
//		long count = 26;
		long offset = 0;
		long step = 2;
		long current = count;
		while (current > 1)
		{
			if (current % 2 == 1)
			{
				offset += step;
			}
			current /= 2;
			step *= 2;
		}
		System.out.println(offset + 1);

		Node previous = null;
		Node first = null;
		Node half = null;
		for (int i = 1 ; i <= count ; i++)
		{
			Node node = new Node();
			node.id = i;
			if (i == count / 2)
			{
				half = node;
			}
			if (first == null)
			{
				first = node;
			}
			if (previous != null)
			{
				previous.next = node;
			}
			previous = node;
		}
		previous.next = first;
		Node curr = half;
		int s = 0;
		while (curr.next != curr)
		{
			if (s % 3 != 2)
			{
				curr.next = curr.next.next;
			} else {
				curr = curr.next;
			}
			s++;
		}
		System.out.println(curr.id);
	}

	private static class Node
	{
		public int id;
		public Node next;
	}
}
   