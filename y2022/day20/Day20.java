package y2022.day20;

import common.CircularLinkedList;
import common.Input;

public class Day20 {

	public static void main(String[] args) {
		// part 1
		var circularList = new CircularLinkedList<Integer>();
		var initial = Input.parseLines("y2022/day20/day20.txt", Integer::valueOf).toList();
		var nodesList = initial.stream().map(circularList::add).toList();
		nodesList.forEach(node -> node.move(node.getValue()));
		var zero = circularList.findFirst(0);
		System.out.println(zero.browse(1000).getValue() + zero.browse(2000).getValue() + zero.browse(3000).getValue());
		// part 2
		var circularList2 = new CircularLinkedList<Long>();
		var initial2 = Input.parseLines("y2022/day20/day20.txt", Long::valueOf).map(i -> i * 811589153).toList();
		var nodesList2 = initial2.stream().map(circularList2::add).toList();
		for (int i = 0 ; i < 10 ; i++) {
			nodesList2.forEach(node -> node.move(node.getValue()));
		}
		var zero2 = circularList2.findFirst(0L);
		System.out.println(zero2.browse(1000).getValue() + zero2.browse(2000).getValue() + zero2.browse(3000).getValue());
	}

}
