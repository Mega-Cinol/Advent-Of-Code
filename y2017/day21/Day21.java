package y2017.day21;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day21 {

	public static void main(String[] args) {
		Set<TilePattern> patterns = Input.parseLines("y2017/day21/day21.txt", TilePattern::new)
				.collect(Collectors.toSet());
		Set<Point> image = new HashSet<>();
		image.add(new Point(0, 1));
		image.add(new Point(1, 2));
		image.add(new Point(2, 0));
		image.add(new Point(2, 1));
		image.add(new Point(2, 2));
		int size = 3;
		print(image, 3);
		for (int i = 0; i < 18; i++) {
			Set<Point> newImage = new HashSet<>();
			if (size % 2 == 0) {
				for (int row = 0; row < size / 2; row++) {
					for (int col = 0; col < size / 2; col++) {
						int finalRow = row;
						int finalCol = col;
						patterns.stream().filter(p -> p.getSize() == 2).map(p -> p.apply(image, finalRow, finalCol))
								.forEach(newImage::addAll);
					}
				}
				size += size / 2;
			} else if (size % 3 == 0) {
				for (int row = 0; row < size / 3; row++) {
					for (int col = 0; col < size / 3; col++) {
						int finalRow = row;
						int finalCol = col;
						patterns.stream().filter(p -> p.getSize() == 3).map(p -> p.apply(image, finalRow, finalCol))
								.forEach(newImage::addAll);
					}
				}
				size += size / 3;
			}
			image.clear();
			image.addAll(newImage);
//			print(image, size);
			System.out.println(i);
		}
		System.out.println(image.size());
	}

	private static void print(Set<Point> image, int size) {
		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				if (image.contains(new Point(row, col))) {
					System.out.print("#");
				} else {
					System.out.print(".");
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	private static class TilePattern {
		private final Set<Set<Point>> input = new HashSet<>();
		private final Set<Integer> inputFast = new HashSet<>();
		private final Set<Point> output = new HashSet<>();
		private final int outSize;

		public Set<Point> apply(Set<Point> current, int row, int col) {
//			Set<Point> pattern = current.stream()
//					.filter(p -> p.getX() >= row * outSize && p.getX() < (row + 1) * outSize)
//					.filter(p -> p.getY() >= col * outSize && p.getY() < (col + 1) * outSize)
//					.map(p -> new Point(p.getX() - row * outSize, p.getY() - col * outSize))
//					.collect(Collectors.toSet());
			int checkSum = 0;
			for (int x = 0; x < outSize; x++) {
				for (int y = 0; y < outSize; y++) {
					if (current.contains(new Point(row * outSize + x, col * outSize + y))) {
						checkSum += Math.pow(2, (x ) + outSize * (y ));
					}
				}
			}
			if (!inputFast.contains(checkSum)) {
				return new HashSet<>();
			} else {
				return output.stream()
						.map(p -> new Point(p.getX() + row * (outSize + 1), p.getY() + col * (outSize + 1)))
						.collect(Collectors.toSet());
			}
		}

		public int getSize() {
			return outSize;
		}

		public TilePattern(String desc) {
			String in = desc.split("=")[0].substring(0, desc.split("=")[0].length() - 1);
			Set<Point> init = new HashSet<>();
			int rowId = 0;
			for (String row : in.split("/")) {
				for (int col = 0; col < row.length(); col++) {
					if (row.charAt(col) == '#') {
						init.add(new Point(rowId, col));
					}
				}
				rowId++;
			}
			int rowCnt = rowId - 1;
			outSize = rowId;
			input.add(init);
			input.add(init.stream().map(p -> new Point(p.getX(), rowCnt - p.getY())).collect(Collectors.toSet()));
			input.add(init.stream().map(p -> new Point(rowCnt - p.getX(), p.getY())).collect(Collectors.toSet()));
			input.add(init.stream().map(p -> new Point(rowCnt - p.getX(), rowCnt - p.getY()))
					.collect(Collectors.toSet()));
			Set<Point> rotated = init.stream().map(p -> new Point(rowCnt - p.getY(), p.getX()))
					.collect(Collectors.toSet());
			input.add(rotated);
			input.add(rotated.stream().map(p -> new Point(p.getX(), rowCnt - p.getY())).collect(Collectors.toSet()));
			input.add(rotated.stream().map(p -> new Point(rowCnt - p.getX(), p.getY())).collect(Collectors.toSet()));
			input.add(rotated.stream().map(p -> new Point(rowCnt - p.getX(), rowCnt - p.getY()))
					.collect(Collectors.toSet()));
			input.stream().mapToLong(points -> points.stream().mapToLong(p -> p.getX() + outSize * p.getY())
					.map(x -> (int) Math.pow(2, x)).sum()).mapToObj(Long::valueOf).mapToInt(Long::intValue).forEach(inputFast::add);
			String out = desc.split("=")[1].substring(2);
			rowId = 0;
			for (String row : out.split("/")) {
				for (int col = 0; col < row.length(); col++) {
					if (row.charAt(col) == '#') {
						output.add(new Point(rowId, col));
					}
				}
				rowId++;
			}
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("From:\n");
			for (int row = 0; row < outSize; row++) {
				for (Set<Point> singleInput : input) {
					for (int col = 0; col < outSize; col++) {
						if (singleInput.contains(new Point(row, col))) {
							sb.append("#");
						} else {
							sb.append(".");
						}
					}
					sb.append(" ");
				}
				sb.append("\n");
			}
			sb.append("To:\n");
			for (int row = 0; row < outSize + 1; row++) {
				for (int col = 0; col < outSize + 1; col++) {
					if (output.contains(new Point(row, col))) {
						sb.append("#");
					} else {
						sb.append(".");
					}
				}
				sb.append("\n");
			}
			sb.append("\n");
			return sb.toString();
		}
	}
}
