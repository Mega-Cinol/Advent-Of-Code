package y2017.day25;

import java.util.HashSet;
import java.util.Set;

public class Day25 {

	public static void main(String[] args) {
		Set<Long> tape = new HashSet<>();
		State state = State.A;
		long current = 0;
		long stepCount = 12_919_244;
		for (long step = 0; step < stepCount; step++) {
			switch (state) {
			case A:
				if (!tape.contains(current)) {
					tape.add(current);
					current++;
					state = State.B;
				} else {
					tape.remove(current);
					current--;
					state = State.C;
				}
				break;
			case B:
				if (!tape.contains(current)) {
					tape.add(current);
					current--;
					state = State.A;
				} else {
					current++;
					state = State.D;
				}
				break;
			case C:
				if (!tape.contains(current)) {
					tape.add(current);
					current++;
					state = State.A;
				} else {
					tape.remove(current);
					current--;
					state = State.E;
				}
				break;
			case D:
				if (!tape.contains(current)) {
					tape.add(current);
					current++;
					state = State.A;
				} else {
					tape.remove(current);
					current++;
					state = State.B;
				}
				break;
			case E:
				if (!tape.contains(current)) {
					tape.add(current);
					current--;
					state = State.F;
				} else {
					current--;
					state = State.C;
				}
				break;
			case F:
				if (!tape.contains(current)) {
					tape.add(current);
					current++;
					state = State.D;
				} else {
					current++;
					state = State.A;
				}
				break;
			}
//			switch (state) {
//			case A:
//				if (!tape.contains(current)) {
//					tape.add(current);
//					current++;
//					state = State.B;
//				} else {
//					tape.remove(current);
//					current--;
//					state = State.B;
//				}
//				break;
//			case B:
//				if (!tape.contains(current)) {
//					tape.add(current);
//					current--;
//					state = State.A;
//				} else {
//					current++;
//					state = State.A;
//				}
//				break;
//			}
		}
		System.out.println(tape.size());
	}

	enum State {
		A, B, C, D, E, F;
	}
}
