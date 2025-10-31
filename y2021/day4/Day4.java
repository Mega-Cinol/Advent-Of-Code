package y2021.day4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Direction;
import common.Input;
import common.Point;

public class Day4 {

	public static void main(String[] args) {
		List<String> input = Input.parseLines("y2021/day4/day4.txt").collect(Collectors.toList());
		BingoGame game = new BingoGame(input);
		System.out.println(game.getWinningScore());
		System.out.println(game.getLosingScore());
	}

	private static class BingoGame {
		private final List<Integer> numbers = new ArrayList<>();
		private final Set<BingoBoard> boards = new HashSet<>();

		public BingoGame(List<String> input) {
			String numberStr = input.get(0);
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(numberStr);
			while (m.find())
			{
				numbers.add(Integer.parseInt(m.group()));
			}
			int currentStart = 2;
			for (int i = 2 ; i < input.size() ; i++)
			{
				if (input.get(i).isEmpty())
				{
					boards.add(new BingoBoard(input.subList(currentStart, i)));
					currentStart = i+1;
				}
			}
			boards.add(new BingoBoard(input.subList(currentStart, input.size())));
		}

		public long getWinningScore() {
			return numbers.stream().map(
					n -> boards.stream().map(board -> board.markAndGetScore(n)).reduce(Optional.empty(), (o1, o2) -> {
						if (o1.isPresent()) {
							return o1;
						} else {
							return o2;
						}
					})).filter(Optional::isPresent).findFirst().get().get();
		}
		public long getLosingScore() {
			Set<BingoBoard> boardsCopy = new HashSet<>(boards);
			Iterator<Integer> numbersIterator = numbers.iterator();
			while (boardsCopy.size() > 1)
			{
				int number = numbersIterator.next();
				boardsCopy.removeIf(b -> b.markAndGetScore(number).isPresent());
			}
			Optional<Long> result = Optional.empty();
			BingoBoard lastBoard = boardsCopy.iterator().next();
			while (!result.isPresent())
			{
				result = lastBoard.markAndGetScore(numbersIterator.next());
			}
			return result.get();
		}
	}

	private static class BingoBoard {
		private final Set<BingoField> bingoFields = new HashSet<>();

		public BingoBoard(List<String> input) {
			Pattern p = Pattern.compile("\\d+");
			for (int row = 0 ; row < input.size() ; row++)
			{
				Matcher m = p.matcher(input.get(row));
				int col = 0;
				while (m.find())
				{
					Point location = new Point(row, col);
					int number = Integer.parseInt(m.group());
					bingoFields.add(new BingoField(location, number));
					col++;
				}
			}
		}

		public Optional<Long> markAndGetScore(int number) {
			Optional<BingoField> optionalFieldToMark = bingoFields.stream().filter(f -> f.getNumber() == number)
					.findAny();
			if (!optionalFieldToMark.isPresent()) {
				return Optional.empty();
			}
			BingoField fieldToMark = optionalFieldToMark.get();
			fieldToMark.mark();
			boolean north = allMarked(fieldToMark.getPosition(), Direction.NORTH);
			boolean south = allMarked(fieldToMark.getPosition(), Direction.SOUTH);
			boolean east = allMarked(fieldToMark.getPosition(), Direction.EAST);
			boolean west = allMarked(fieldToMark.getPosition(), Direction.WEST);
			if ((north && south) || (east && west)) {
				return Optional
						.of((long) bingoFields.stream().filter(f -> !f.isMarked()).mapToInt(BingoField::getNumber).sum()
								* number);
			}
			return Optional.empty();
		}

		private boolean allMarked(Point location, Direction direction) {
			Point currentLocation = location.move(direction);
			BingoField currentField = getByLocation(currentLocation);
			while (currentField != null && currentField.isMarked()) {
				currentLocation = currentLocation.move(direction);
				currentField = getByLocation(currentLocation);
			}
			if (currentField == null) {
				return true;
			}
			return false;
		}

		private BingoField getByLocation(Point location) {
			return bingoFields.stream().filter(f -> f.getPosition().equals(location)).findAny().orElse(null);
		}
		@Override
		public String toString()
		{
			Set<Point> points = bingoFields.stream().map(BingoField::getPosition).collect(Collectors.toSet());
			StringBuilder sb = new StringBuilder();
			for (int row = 0 ; row <= Point.maxY(points) ; row++)
			{
				for (int col = 0 ; col <= Point.maxX(points) ; col++)
				{
					BingoField field = getByLocation(new Point(col, row));
					sb.append(field.isMarked() ? "X" : field.getNumber());
					sb.append("\t");
				}
				sb.append("\n");
			}
			return sb.toString();
		}
	}

	private static class BingoField {
		private final Point position;
		private final int number;
		private boolean marked = false;

		public BingoField(Point position, int number) {
			this.position = position;
			this.number = number;
		}

		public void mark() {
			marked = true;
		}

		public boolean isMarked() {
			return marked;
		}

		public Point getPosition() {
			return position;
		}

		public int getNumber() {
			return number;
		}
	}
}
