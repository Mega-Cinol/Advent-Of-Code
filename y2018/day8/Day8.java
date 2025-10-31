package y2018.day8;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day8 {
	public static void main(String[] args) {
		Queue<Integer> input = Stream.of(Input.parseLines("y2018/day8/day8.txt").findAny().get().split(" "))
				.map(Integer::valueOf).collect(Collectors.toCollection(ArrayDeque::new));
		Node root = new Node();
		populateNode(root, input);
		System.out.println(root.metaSum());
		System.out.println(root.getValue());
	}

	private static void populateNode(Node node, Queue<Integer> input) {
		int childCount = input.remove();
		int metaCount = input.remove();
		for (int i = 0; i < childCount; i++) {
			Node child = new Node();
			node.children.add(child);
			populateNode(child, input);
		}
		for (int i = 0; i < metaCount; i++) {
			node.metaData.add(input.remove());
		}
	}

	private static class Node {
		public List<Node> children = new ArrayList<>();
		public List<Integer> metaData = new ArrayList<>();

		public long metaSum() {
			long sum = metaData.stream().mapToLong(Integer::longValue).sum();
			sum += children.stream().mapToLong(Node::metaSum).sum();
			return sum;
		}

		public long getValue() {
			if (children.isEmpty()) {
				return metaData.stream().mapToLong(Integer::longValue).sum();
			}
			return metaData.stream().mapToInt(i -> i - 1).filter(i -> i >= 0 && i < children.size())
					.mapToObj(children::get).mapToLong(Node::getValue).sum();
		}
	}
}
