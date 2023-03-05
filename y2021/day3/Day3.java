package y2021.day3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day3 {

	private static final int HALF_INPUT_SIZE = 500;

	public static void main(String[] args) {
		// part1
		Point numbersDigits = Input.parseLines("y2021/day3/day3.txt", s -> {
			List<Long> digits = new ArrayList<>();
			for (int i = 0; i < s.length(); i++) {
				digits.add((long) (s.charAt(i) - '0'));
			}
			return new Point(digits);
		}).reduce((p1, p2) -> p1.move(p2)).get();
		int gamma = 0;
		int epsilon = 0;
		for (int dim = numbersDigits.getDimensionSize() - 1; dim >= 0; dim--) {
			if (numbersDigits.getDimenssion(dim) > HALF_INPUT_SIZE) {
				gamma += Math.pow(2, numbersDigits.getDimensionSize() - dim - 1);
			} else {
				epsilon += Math.pow(2, numbersDigits.getDimensionSize() - dim - 1);
			}
		}
		System.out.println(gamma * epsilon);
		// part2
		List<Point> candidates = Input.parseLines("y2021/day3/day3.txt", s -> {
			List<Long> digits = new ArrayList<>();
			for (int i = 0; i < s.length(); i++) {
				digits.add((long) (s.charAt(i) - '0'));
			}
			return new Point(digits);
		}).collect(Collectors.toList());
		int bitIndex = 0;
		List<Point> oxygenCandidates = new ArrayList<>(candidates);
		while (oxygenCandidates.size() > 1 && bitIndex < candidates.get(0).getDimensionSize()) {
			int bitIndexCopy = bitIndex;
			int expectedBit = getExpectedBit(oxygenCandidates, bitIndex);
			oxygenCandidates = oxygenCandidates.stream().filter(p -> p.getDimenssion(bitIndexCopy) == expectedBit)
					.collect(Collectors.toList());
			bitIndex++;
		}
		bitIndex = 0;
		List<Point> co2Candidates = new ArrayList<>(candidates);
		while (co2Candidates.size() > 1 && bitIndex < candidates.get(0).getDimensionSize()) {
			int bitIndexCopy = bitIndex;
			int expectedBit = 1 - getExpectedBit(co2Candidates, bitIndex);
			co2Candidates = co2Candidates.stream().filter(p -> p.getDimenssion(bitIndexCopy) == expectedBit)
					.collect(Collectors.toList());
			bitIndex++;
		}
		int oxygenValue = 0;
		int co2Value = 0;
		for (int dim = numbersDigits.getDimensionSize() - 1; dim >= 0; dim--) {
			oxygenValue += oxygenCandidates.get(0).getDimenssion(dim)
					* Math.pow(2, oxygenCandidates.get(0).getDimensionSize() - dim - 1);
			co2Value += co2Candidates.get(0).getDimenssion(dim)
					* Math.pow(2, co2Candidates.get(0).getDimensionSize() - dim - 1);
		}
		System.out.println(numbersDigits);
		System.out.println(oxygenCandidates);
		System.out.println(co2Candidates);
		System.out.println(oxygenValue * co2Value);
	}

	private static int getExpectedBit(List<Point> points, int position) {
		int expected = 0;
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).getDimenssion(position) == 1) {
				expected++;
			} else {
				expected--;
			}
		}
		return expected >= 0 ? 1 : 0;
	}
}
