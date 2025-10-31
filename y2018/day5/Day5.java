package y2018.day5;

import common.Input;

public class Day5 {

	public static void main(String[] args) {
		String input = Input.parseLines("y2018/day5/day5.txt").findAny().get();
		int min = countReductions(input);
		System.out.println(min);
		for (char c = 'a' ; c <= 'z' ; c++)
		{
			String shorter = input.replaceAll("[" + c + (char)(c - 32) + "]", "");
			int shorterMin = countReductions(shorter);
			if (shorterMin < min)
			{
				min = shorterMin;
			}
		}
		System.out.println(min);
	}

	private static int countReductions(String input)
	{
		Node current = new Node(input.charAt(0));
		Node first = current;
		for (int i = 1 ; i < input.trim().length() ; i++)
		{
			Node next = new Node(input.charAt(i));
			next.setPrev(current);
			current.setNext(next);
			current = next;
		}
		current = first;
		while (current.getNext() != null)
		{
			if (Math.abs(current.getValue() - current.getNext().getValue()) == 32)
			{
				if (current.getPrev() == null)
				{
					current = current.getNext().getNext();
					first = current;
					current.setPrev(null);
				} else {
					Node newNext = current.getNext().getNext();
					current = current.getPrev();
					current.setNext(newNext);
					if (newNext != null)
					{
						newNext.setPrev(current);
					}
				}
			}
			else
			{
				current = current.getNext();
			}
		}
		int length = 0;
		current = first;
		while (current != null)
		{
			current = current.getNext();
			length++;
		}
		return length;
	}

	private static class Node
	{
		private Node next;
		private Node prev;
		private final char value;
		public Node(char value)
		{
			this.value = value;
		}

		public Node getNext() {
			return next;
		}

		public void setNext(Node next) {
			this.next = next;
		}

		public Node getPrev() {
			return prev;
		}

		public void setPrev(Node prev) {
			this.prev = prev;
		}

		public char getValue() {
			return value;
		}
	}
}
