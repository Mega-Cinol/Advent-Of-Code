package y2022.day2;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day2 {

	enum RockPaperScisors {
		ROCK {
			@Override
			public char getOwnSymbol() {
				return 'X';
			}

			@Override
			public char getOtherSymbol() {
				return 'A';
			}

			@Override
			public int getScore() {
				return 1;
			}

			@Override
			public int getVsScore(RockPaperScisors other) {
				return switch (other) {
				case ROCK -> 3;
				case PAPER -> 0;
				case SCISORS -> 6;
				};
			}

			@Override
			public RockPaperScisors getForOutcome(char outcome) {
				return switch (outcome) {
				case 'X' -> SCISORS;
				case 'Y' -> ROCK;
				case 'Z' -> PAPER;
				default -> throw new IllegalArgumentException("Unexpected value: " + outcome);
				};
			}
		},
		PAPER {
			@Override
			public char getOwnSymbol() {
				return 'Y';
			}

			@Override
			public char getOtherSymbol() {
				return 'B';
			}

			@Override
			public int getScore() {
				return 2;
			}

			@Override
			public int getVsScore(RockPaperScisors other) {
				return switch (other) {
				case ROCK -> 6;
				case PAPER -> 3;
				case SCISORS -> 0;
				};
			}

			@Override
			public RockPaperScisors getForOutcome(char outcome) {
				return switch (outcome) {
				case 'X' -> ROCK;
				case 'Y' -> PAPER;
				case 'Z' -> SCISORS;
				default -> throw new IllegalArgumentException("Unexpected value: " + outcome);
				};
			}
		},
		SCISORS {
			@Override
			public char getOwnSymbol() {
				return 'Z';
			}

			@Override
			public char getOtherSymbol() {
				return 'C';
			}

			@Override
			public int getScore() {
				return 3;
			}

			@Override
			public int getVsScore(RockPaperScisors other) {
				return switch (other) {
				case ROCK -> 0;
				case PAPER -> 6;
				case SCISORS -> 3;
				};
			}

			@Override
			public RockPaperScisors getForOutcome(char outcome) {
				return switch (outcome) {
				case 'X' -> PAPER;
				case 'Y' -> SCISORS;
				case 'Z' -> ROCK;
				default -> throw new IllegalArgumentException("Unexpected value: " + outcome);
				};
			}
		};

		public abstract char getOwnSymbol();

		public abstract char getOtherSymbol();

		public abstract int getScore();

		public abstract int getVsScore(RockPaperScisors other);

		public abstract RockPaperScisors getForOutcome(char outcome);

		public static RockPaperScisors byOwnSymbol(char symbol) {
			return Stream.of(RockPaperScisors.values()).filter(v -> v.getOwnSymbol() == symbol).findAny().get();
		}

		public static RockPaperScisors byOtherSymbol(char symbol) {
			return Stream.of(RockPaperScisors.values()).filter(v -> v.getOtherSymbol() == symbol).findAny().get();
		}
	}

	public static void main(String[] args) {
		System.out.println(Input.parseLines("y2022/day2/day2.txt", Day2::matchUp).collect(Collectors.summingInt(Integer::valueOf)));
		System.out.println(Input.parseLines("y2022/day2/day2.txt", Day2::matchUpPart2).collect(Collectors.summingInt(Integer::valueOf)));
	}

	private static int matchUp(String description) {
		RockPaperScisors own = RockPaperScisors.byOwnSymbol(description.charAt(2));
		RockPaperScisors other = RockPaperScisors.byOtherSymbol(description.charAt(0));

		return own.getScore() + own.getVsScore(other);
	}

	private static int matchUpPart2(String description) {
		RockPaperScisors other = RockPaperScisors.byOtherSymbol(description.charAt(0));

		RockPaperScisors own = other.getForOutcome(description.charAt(2));

		return own.getScore() + own.getVsScore(other);
	}
}
