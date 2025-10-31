package y2021.day21;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Day21 {

	private static final int PLAYER_1_START = 10;
	private static final int PLAYER_2_START = 8;
	private static final Map<Integer, Long> SINGLE_THROW_COMBINATIONS = new HashMap<>();

	static {
		SINGLE_THROW_COMBINATIONS.put(3, 1L);
		SINGLE_THROW_COMBINATIONS.put(4, 3L);
		SINGLE_THROW_COMBINATIONS.put(5, 6L);
		SINGLE_THROW_COMBINATIONS.put(6, 7L);
		SINGLE_THROW_COMBINATIONS.put(7, 6L);
		SINGLE_THROW_COMBINATIONS.put(8, 3L);
		SINGLE_THROW_COMBINATIONS.put(9, 1L);
	}

	public static void main(String[] args) {
		part1();
		part2();
	}

	private static void part2()
	{
		long player1Wins = 0;
		long player2Wins = 0;

		Map<GameState, Long> gameStateCombinations = new HashMap<>();
		GameState initialState = new GameState(new PlayerState(0, PLAYER_1_START), new PlayerState(0, PLAYER_2_START));
		gameStateCombinations.put(initialState, 1L);
		while (!gameStateCombinations.isEmpty())
		{
			Map<GameState, Long> newGameStateCombinations = new HashMap<>();
			for (Map.Entry<Integer, Long> throwCombination : SINGLE_THROW_COMBINATIONS.entrySet()) {
				for (Map.Entry<GameState, Long> gameStateCombination : gameStateCombinations.entrySet()) {
					newGameStateCombinations.merge(gameStateCombination.getKey().movePlayer1(throwCombination.getKey()),
							gameStateCombination.getValue() * throwCombination.getValue(), (o, n) -> o + n);
				}
				player1Wins += newGameStateCombinations.entrySet().stream().filter(e -> e.getKey().gameEnded())
						.mapToLong(Map.Entry<GameState, Long>::getValue).sum();
				newGameStateCombinations.entrySet().removeIf(e -> e.getKey().gameEnded());
			}
			gameStateCombinations = newGameStateCombinations;
			newGameStateCombinations = new HashMap<>();
			if (!gameStateCombinations.isEmpty())
			{
				for (Map.Entry<Integer, Long> throwCombination : SINGLE_THROW_COMBINATIONS.entrySet()) {
					for (Map.Entry<GameState, Long> gameStateCombination : gameStateCombinations.entrySet()) {
						newGameStateCombinations.merge(gameStateCombination.getKey().movePlayer2(throwCombination.getKey()),
								gameStateCombination.getValue() * throwCombination.getValue(), (o, n) -> o + n);
					}
					player2Wins += newGameStateCombinations.entrySet().stream().filter(e -> e.getKey().gameEnded())
							.mapToLong(Map.Entry<GameState, Long>::getValue).sum();
					newGameStateCombinations.entrySet().removeIf(e -> e.getKey().gameEnded());
				}
				gameStateCombinations = newGameStateCombinations;
			}
		}
		System.out.println(Math.max(player1Wins, player2Wins));
	}

	private static class GameState {
		private final PlayerState player1;
		private final PlayerState player2;

		public GameState(PlayerState player1, PlayerState player2) {
			this.player1 = player1;
			this.player2 = player2;
		}

		public GameState movePlayer1(int fields) {
			PlayerState newPlayer1 = player1.move(fields);
			return new GameState(newPlayer1, player2);
		}

		public GameState movePlayer2(int fields) {
			PlayerState newPlayer2 = player2.move(fields);
			return new GameState(player1, newPlayer2);
		}

		public boolean gameEnded()
		{
			return player1.getScore() >= 21 || player2.getScore() >= 21;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((player1 == null) ? 0 : player1.hashCode());
			result = prime * result + ((player2 == null) ? 0 : player2.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GameState other = (GameState) obj;
			if (player1 == null) {
				if (other.player1 != null)
					return false;
			} else if (!player1.equals(other.player1))
				return false;
			if (player2 == null) {
				if (other.player2 != null)
					return false;
			} else if (!player2.equals(other.player2))
				return false;
			return true;
		}
	}

	private static class PlayerState {
		private final int score;
		private final int position;

		public PlayerState(int score, int position) {
			this.score = score;
			this.position = position;
		}

		public int getScore() {
			return score;
		}

		public PlayerState move(int fields) {
			int newField = (position + fields - 1) % 10 + 1;
			return new PlayerState(score + newField, newField);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + position;
			result = prime * result + score;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PlayerState other = (PlayerState) obj;
			if (position != other.position)
				return false;
			if (score != other.score)
				return false;
			return true;
		}
	}

	private static void part1() {
		Deque<Integer> player1Cycle = new ArrayDeque<>();
		player1Cycle.add(player1(6));
		player1Cycle.add(player1(0));
		player1Cycle.add(player1(2));
		player1Cycle.add(player1(2));
		player1Cycle.add(player1(0));
		Deque<Integer> player2Cycle = new ArrayDeque<>();
		player2Cycle.add(player2(5));
		player2Cycle.add(player2(8));
		player2Cycle.add(player2(9));
		player2Cycle.add(player2(8));
		player2Cycle.add(player2(5));
		player2Cycle.add(player2(0));
		player2Cycle.add(player2(3));
		player2Cycle.add(player2(4));
		player2Cycle.add(player2(3));
		player2Cycle.add(player2(0));

		int player1TurnsToWin = playersTurnsToWin(new ArrayDeque<>(player1Cycle));
		int player2TurnsToWin = playersTurnsToWin(new ArrayDeque<>(player2Cycle));

		int turnsToEnd = Math.min(player1TurnsToWin, player2TurnsToWin);
		int player1FinalScore = playerAfterTurns(new ArrayDeque<>(player1Cycle), turnsToEnd);
		int player2FinalScore = playerAfterTurns(new ArrayDeque<>(player2Cycle),
				player1TurnsToWin == turnsToEnd ? turnsToEnd - 1 : turnsToEnd);
		int castsToEnd = 3 * (player1TurnsToWin == turnsToEnd ? turnsToEnd * 2 - 1 : turnsToEnd * 2);
		System.out.println(castsToEnd * Math.min(player1FinalScore, player2FinalScore));
	}

	private static int playersTurnsToWin(Deque<Integer> cycle) {
		int fullCycleValue = cycle.stream().mapToInt(Integer::intValue).sum();
		int fullCyclesToWin = 1000 / fullCycleValue;
		int valueAfterFullCycles = fullCyclesToWin * fullCycleValue;

		int remainingTurnsToWin = 0;
		while (valueAfterFullCycles < 1000) {
			int next = cycle.removeFirst();
			cycle.add(next);
			valueAfterFullCycles += next;
			remainingTurnsToWin++;
		}
		return fullCyclesToWin * cycle.size() + remainingTurnsToWin;
	}

	private static int playerAfterTurns(Deque<Integer> cycle, int turns) {
		int fullCycleValue = cycle.stream().mapToInt(Integer::intValue).sum();
		int fullCycles = turns / cycle.size();
		int turnsLeft = turns % cycle.size();
		int remainingValue = 0;
		for (int i = 0; i < turnsLeft; i++) {
			remainingValue += cycle.removeFirst();
		}
		return fullCycles * fullCycleValue + remainingValue;
	}

	private static int player1(int offset) {
		return (PLAYER_1_START - 1 + offset) % 10 + 1;
	}

	private static int player2(int offset) {
		return (PLAYER_2_START - 1 + offset) % 10 + 1;
	}
}
