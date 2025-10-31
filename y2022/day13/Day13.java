package y2022.day13;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import common.Input;

public class Day13 {

	public static void main(String[] args) {
		var input = Input.parseLines("y2022/day13/day13.txt").toList();

		int sum = 0;
		for (int i = 0; i < input.size(); i += 3) {
			var left = input.get(i);
			var right = input.get(i + 1);
			if (compare(left.substring(1, left.length() - 1), right.substring(1, right.length() - 1))) {
				sum += i / 3 + 1;
			}
		}
		System.out.println(sum);

		var allPackets = Input.parseLines("y2022/day13/day13.txt").filter(s -> !s.isBlank())
				.map(s -> s.substring(1, s.length() - 1)).map(PacketList::new).collect(Collectors.toList());
		allPackets.add(new PacketList("[2]"));
		allPackets.add(new PacketList("[6]"));
		var sortedStrPackets = allPackets.stream().sorted(Comparator.reverseOrder()).map(Packet::toString).toList();
		System.out.println((sortedStrPackets.indexOf("[[2]]") + 1) * (sortedStrPackets.indexOf("[[6]]") + 1));
	}

	public static boolean compare(String left, String right) {
		return new PacketList(left).compareTo(new PacketList(right)) > 0;
	}

	private interface Packet extends Comparable<Packet> {
	}

	private static class PacketList implements Packet {
		private final List<Packet> value = new ArrayList<>();

		public PacketList(String input) {
			int element = 0;
			boolean digitFound = false;
			for (int pos = 0; pos < input.length(); pos++) {
				if (input.charAt(pos) == '[') {
					int start = pos;
					int opened = 1;
					pos++;
					while (opened > 0) {
						if (input.charAt(pos) == '[') {
							opened++;
						}
						if (input.charAt(pos) == ']') {
							opened--;
						}
						pos++;
					}
					value.add(new PacketList(input.substring(start + 1, pos - 1)));
				} else if (input.charAt(pos) == ',') {
					if (digitFound) {
						digitFound = false;
						value.add(new PacketElement(element));
						element = 0;
					}
				} else { // digit
					digitFound = true;
					element *= 10;
					element += input.charAt(pos) - '0';
				}
			}
			if (digitFound) {
				value.add(new PacketElement(element));
				element = 0;
			}

		}

		public PacketList(PacketElement element) {
			value.add(element);
		}

		@Override
		public int compareTo(Packet other) {
			if (other instanceof PacketElement element) {
				return compareTo(new PacketList(element));
			} else {
				var otherList = (PacketList) other;
				for (int i = 0; i < Math.min(value.size(), otherList.value.size()); i++) {
					var result = value.get(i).compareTo(otherList.value.get(i));
					if (result != 0) {
						return result;
					}
				}
				return otherList.value.size() - value.size();
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			sb.append(value.stream().map(Packet::toString).collect(Collectors.joining(",")));
			sb.append(']');
			return sb.toString();
		}
	}

	private static class PacketElement implements Packet {
		private final int value;

		public PacketElement(int value) {
			this.value = value;
		}

		@Override
		public int compareTo(Packet other) {
			if (other instanceof PacketElement element) {
				return element.value - value;
			} else {
				return new PacketList(this).compareTo(other);
			}
		}

		@Override
		public String toString() {
			return "" + value;
		}
	}
}
