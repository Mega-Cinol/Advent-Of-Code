package common;

public class CircularLinkedList<E> {
	private Node first = null;
	private long listSize = 0;

	public Node add(E e) {
		if (first == null) {
			first = new Node(e);
			first.next = first;
			first.previous = first;
			listSize++;
			return first;
		} else {
			Node next = new Node(e);
			first.addBefore(next);
			return next;
		}
	}

	public Node get(long index) {
		if (first == null) {
			throw new IllegalStateException("The list is empty");
		}
		return first.browse(index);
	}

	public Node findFirst(E value) {
		int visited = 0;
		var node = first;
		if (first == null) {
			return null;
		}
		while (visited < listSize && !node.getValue().equals(value)) {
			visited++;
			node = node.next;
		}
		return visited < listSize ? node : null;
	}

	public long size() {
		return listSize;
	}

	@Override
	public String toString() {
		if (first == null) {
			return "[]";
		}
		Node current = first;
		StringBuilder resultBuilder = new StringBuilder();
		resultBuilder.append("[ ");
		do {
			resultBuilder.append(current.toString() + ", ");
			current = current.browse(1);
		} while (current != first);
		resultBuilder.replace(resultBuilder.length() - 2, resultBuilder.length(), " ]");
		return resultBuilder.toString();
	}

	public class Node {
		private final E value;
		private Node next;
		private Node previous;

		private Node(E value) {
			this.value = value;
		}

		public void addAfter(Node newNode) {
			newNode.next = next;
			newNode.previous = this;
			newNode.next.previous = newNode;
			newNode.previous.next = newNode;
			listSize++;
		}

		public void addBefore(Node newNode) {
			newNode.next = this;
			newNode.previous = previous;
			newNode.next.previous = newNode;
			newNode.previous.next = newNode;
			listSize++;
		}

		public void remove() {
			next.previous = previous;
			previous.next = next;
			next = null;
			previous = null;
			listSize--;
		}

		public E getValue() {
			return value;
		}

		@Override
		public String toString() {
			return value.toString();
		}

		public Node browse(long step) {
			if (step > 0) {
				return forward(step);
			} else {
				return backward(step);
			}
		}

		public Node move(long step) {
			var newLocation = previous;
			remove();
			newLocation = newLocation.browse(step % listSize);
			newLocation.addAfter(this);
			return this;
		}

		private Node forward(long step) {
			Node nextNode = this;
			for (int i = 0; i < step; i++) {
				nextNode = nextNode.next;
			}
			return nextNode;
		}

		private Node backward(long step) {
			Node nextNode = this;
			for (int i = 0; i < Math.abs(step); i++) {
				nextNode = nextNode.previous;
			}
			return nextNode;
		}
	}
}
