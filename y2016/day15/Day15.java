package y2016.day15;

public class Day15 {

	public static void main(String[] args) {
		long time = findModulo(12, 13, 19, 8);
		time = findModulo(time, 13 * 19, 3, 2);
		time = findModulo(time, 13 * 19 * 3, 7, 3);
		time = findModulo(time, 13 * 19 * 3 * 7, 5, 3);
		time = findModulo(time, 13 * 19 * 3 * 7 * 5, 17, 7);
		time = findModulo(time, 13 * 19 * 3 * 7 * 5 * 17, 11, 5);
		System.out.println(time % (13 * 19 * 3 * 7 * 5 * 17 * 11));
		System.out.println((time + 1) % 13);
		System.out.println((time + 10) % 19);
		System.out.println((time + 2) % 3);
		System.out.println((time + 1) % 7);
		System.out.println((time + 3) % 5);
		System.out.println((time + 5) % 17);
		System.out.println((time) % 11);
	}

	private static long findModulo(long initial, long step, long divideBy, long expectedModulo) {
		long n = 0;
		while ((initial + n * step) % divideBy != expectedModulo) {
			n++;
		}
		return initial + n * step;
	}
}
