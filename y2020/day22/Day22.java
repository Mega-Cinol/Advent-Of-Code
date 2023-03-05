package y2020.day22;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import common.Input;

public class Day22 {

	public static void main(String[] args) {
		Combat combat = new Combat();
		Input.parseLines("y2020/day22/day22.txt", Function.identity(), combat::parseLine);
		System.out.println(combat.play());
	}

	private static class Combat {
		private final Queue<Integer> player1 = new ArrayDeque<>();
		private final Queue<Integer> player2 = new ArrayDeque<>();
		private boolean player2Read = false;

		public void parseLine(String line) {
			if (line.startsWith("Player")) {
				return;
			}
			if (line.isEmpty()) {
				player2Read = true;
				return;
			}
			if (player2Read) {
				player2.add(Integer.valueOf(line));
			} else {
				player1.add(Integer.valueOf(line));
			}
		}

		public int play() {
			Set<Pair> oldDecks = new HashSet<>();
			while (!player1.isEmpty() && !player2.isEmpty()) {
				if (round(player1, player2, oldDecks)) {
					player1.add(player1.remove());
					player1.add(player2.remove());
				} else {
					player2.add(player2.remove());
					player2.add(player1.remove());
				}
			}
			Queue<Integer> winningDeck = player1.isEmpty() ? player2 : player1;
			int result = 0;
			int id = winningDeck.size();
			while (!winningDeck.isEmpty()) {
				result += id-- * winningDeck.remove();
			}
			return result;
		}

		public boolean round(Queue<Integer> player1Deck, Queue<Integer> player2Deck, Set<Pair> oldDecks) {
			if (oldDecks.contains(new Pair(player1Deck, player2Deck))) {
				return true;
			}
			oldDecks.add(new Pair(player1Deck, player2Deck));
			int p1 = player1Deck.element();
			int p2 = player2Deck.element();
			if ((p1 > player1Deck.size() - 1) || (p2 > player2Deck.size() - 1)) {
				return p1 > p2;
			}
			Queue<Integer> sub1 = new ArrayDeque<Integer>(player1Deck);
			Queue<Integer> sub2 = new ArrayDeque<Integer>(player2Deck);
			sub1.remove();
			sub2.remove();
			return subGame(sub1.stream().limit(p1).collect(Collectors.toCollection(ArrayDeque::new)),
					sub2.stream().limit(p2).collect(Collectors.toCollection(ArrayDeque::new)));
		}

		public boolean subGame(Queue<Integer> deck1, Queue<Integer> deck2) {
			Set<Pair> oldDecks = new HashSet<>();
			while (!deck1.isEmpty() && !deck2.isEmpty()) {
				if (round(deck1, deck2, oldDecks)) {
					deck1.add(deck1.remove());
					deck1.add(deck2.remove());
				} else {
					deck2.add(deck2.remove());
					deck2.add(deck1.remove());
				}
			}
			return !deck1.isEmpty();
		}

		private static class Pair {
			private final List<Integer> q1;
			private final List<Integer> q2;

			public Pair(Queue<Integer> q1, Queue<Integer> q2) {
				this.q1 = new ArrayList<>(q1);
				this.q2 = new ArrayList<>(q2);
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((q1 == null) ? 0 : q1.hashCode());
				result = prime * result + ((q2 == null) ? 0 : q2.hashCode());
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
				Pair other = (Pair) obj;
				if (q1 == null) {
					if (other.q1 != null)
						return false;
				} else if (!q1.equals(other.q1))
					return false;
				if (q2 == null) {
					if (other.q2 != null)
						return false;
				} else if (!q2.equals(other.q2))
					return false;
				return true;
			}
		}
	}
}
