package y2021.day18;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import common.Input;

public class Day18 {

	public static void main(String[] args) {
		// part1
		Input.parseLines("y2021/day18/day18.txt", SnailNumber::new).reduce((s1, s2) -> {
			s1.add(s2);
			return s1;
		}).map(SnailNumber::magnitude).ifPresent(System.out::println);
		List<SnailNumber> numbers = Input.parseLines("y2021/day18/day18.txt", SnailNumber::new)
				.collect(Collectors.toList());
		// part2
		long max = 0;
		for (int i = 0 ; i < numbers.size() ; i++)
		{
			for (int j = 0 ; j < numbers.size() ; j++)
			{
				SnailNumber sum = new SnailNumber(numbers.get(i));
				sum.add(numbers.get(j));
				long magnitude = sum.magnitude();
				if (magnitude > max)
				{
					max = magnitude;
				}
				if (i == j)
				{
					sum = new SnailNumber(numbers.get(j));
					sum.add(numbers.get(i));
					magnitude = sum.magnitude();
					if (magnitude > max)
					{
						max = magnitude;
					}
				}
			}
		}
		System.out.println(max);
	}

	private static class SnailNumber {
		private final List<SnailNumberPart> numberParts = new ArrayList<>();

		public SnailNumber(SnailNumber original) {
			numberParts.addAll(original.numberParts);
		}

		public SnailNumber(String strNumber) {
			int depthChange = 0;
			int value = 0;
			for (int i = 0; i < strNumber.length(); i++) {
				if (strNumber.charAt(i) == '[') {
					depthChange++;
				} else if (strNumber.charAt(i) == ']') {
					depthChange--;
				} else if (strNumber.charAt(i) >= '0' && strNumber.charAt(i) <= '9') {
					value = strNumber.charAt(i) - '0';
				} else if (strNumber.charAt(i) == ',') {
					numberParts.add(new SnailNumberPart(value, depthChange));
					depthChange = 0;
				}
			}
			numberParts.add(new SnailNumberPart(value, depthChange));
		}

		public void add(SnailNumber other) {
//			System.out.println("  " + this + "\n+ " + other);
			SnailNumberPart first = numberParts.get(0);
			first = new SnailNumberPart(first.getValue(), first.getDepthChange() + 1);
			numberParts.set(0, first);
			numberParts.addAll(other.numberParts);
			SnailNumberPart last = numberParts.get(numberParts.size() - 1);
			last = new SnailNumberPart(last.getValue(), last.getDepthChange() - 1);
			numberParts.set(numberParts.size() - 1, last);
			reduce();
//			System.out.println("= " + this);
//			System.out.println();
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (SnailNumberPart part : numberParts) {
				for (int i = 0; i < part.getDepthChange(); i++) {
					sb.append('[');
				}
				sb.append(part.getValue());
				if (part.getDepthChange() > 0) {
					sb.append(',');
				}
				for (int i = part.getDepthChange(); i < 0; i++) {
					sb.append(']');
				}
				if (part.getDepthChange() < 0) {
					sb.append(',');
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}

		public long magnitude() {
			List<SnailNumberPart> parts = new ArrayList<>(numberParts);
			while (parts.size() > 1) {
				for (int i = 0; i < parts.size() - 1; i++) {
					if (parts.get(i).getDepthChange() > 0 && parts.get(i + 1).getDepthChange() < 0) {
						int newValue = 3 * parts.get(i).getValue() + 2 * parts.get(i + 1).getValue();
						int newDepthChange = parts.get(i).getDepthChange() > 1 ? parts.get(i).getDepthChange() - 1
								: parts.get(i + 1).getDepthChange() + 1;
						SnailNumberPart magnitude = new SnailNumberPart(newValue, newDepthChange);
						parts.set(i, magnitude);
						parts.remove(i + 1);
					}
				}
			}
			return parts.get(0).getValue();
		}

		private void reduce() {
			while (reduceAction())
				;
		}

		private boolean reduceAction() {
			int depth = 0;
			for (int i = 0; i < numberParts.size(); i++) {
				depth += numberParts.get(i).getDepthChange();
				if (depth > 4) {
					if (i > 0) {
						SnailNumberPart newLeft = new SnailNumberPart(
								numberParts.get(i).getValue() + numberParts.get(i - 1).getValue(),
								numberParts.get(i - 1).getDepthChange());
						numberParts.set(i - 1, newLeft);
					}
					if (i < numberParts.size() - 2) {
						SnailNumberPart newRight = new SnailNumberPart(
								numberParts.get(i + 1).getValue() + numberParts.get(i + 2).getValue(),
								numberParts.get(i + 2).getDepthChange());
						numberParts.set(i + 2, newRight);
					}
					int newDepthChange = numberParts.get(i).getDepthChange() > 1
							? numberParts.get(i).getDepthChange() - 1
							: numberParts.get(i + 1).getDepthChange() + 1;
					SnailNumberPart newZeroElement = new SnailNumberPart(0, newDepthChange);
					numberParts.set(i, newZeroElement);
					numberParts.remove(i + 1);
					return true;
				}
			}
			for (int i = 0; i < numberParts.size(); i++) {
				if (numberParts.get(i).getValue() >= 10) {
					int value = numberParts.get(i).getValue();
					int lValue = value / 2;
					int rValue = (value + 1) / 2;
					int lDepthChange = numberParts.get(i).getDepthChange() > 0 ? numberParts.get(i).getDepthChange() + 1
							: 1;
					int rDepthChange = numberParts.get(i).getDepthChange() < 0 ? numberParts.get(i).getDepthChange() - 1
							: -1;
					SnailNumberPart newLeft = new SnailNumberPart(lValue, lDepthChange);
					SnailNumberPart newRight = new SnailNumberPart(rValue, rDepthChange);
					numberParts.set(i, newLeft);
					numberParts.add(i + 1, newRight);
					return true;
				}
			}
			return false;
		}
	}

	private static class SnailNumberPart {
		private final int value;
		private final int depthChange;

		public SnailNumberPart(int value, int depthChange) {
			this.value = value;
			this.depthChange = depthChange;
		}

		public int getValue() {
			return value;
		}

		public int getDepthChange() {
			return depthChange;
		}
	}
}