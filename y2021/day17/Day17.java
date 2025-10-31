package y2021.day17;

import common.Point;

public class Day17 {

	private static final int INPUT_MIN_X = 34;
	private static final int INPUT_MAX_X = 67;
	private static final int INPUT_MIN_Y = -215;
	private static final int INPUT_MAX_Y = -186;

	public static void main(String[] args) {
		// part 1
		// assume max y < 0
		System.out.println(INPUT_MIN_Y * (INPUT_MIN_Y + 1) / 2);

		// part 2
		// assume max y < 0
		// assume min x > 0
		int maxXSpeed = INPUT_MAX_X;
		// x*x + x - 68 < 0
		// delta = 249
		// x = 7
		int minXSpeed = 7;

		int maxYSpeed = -(INPUT_MIN_Y + 1);
		int minYSpeed = INPUT_MIN_X;

		int validSpeeds = 0;
		for (int xSpeed = minXSpeed ; xSpeed <= maxXSpeed ; xSpeed++)
		{
			for (int ySpeed = minYSpeed ; ySpeed <= maxYSpeed ; ySpeed++)
			{
				if (isSpeedValid(new Point(xSpeed, ySpeed)))
				{
					validSpeeds++;
				}
			}
		}
		System.out.println(validSpeeds);
	}

	private static boolean isSpeedValid(Point speed)
	{
		Point current = new Point(0,0);
		while (current.getX() <= INPUT_MAX_X && current.getY() >= INPUT_MIN_Y)
		{
			if (current.getX() >= INPUT_MIN_X && current.getY() <= INPUT_MAX_Y)
			{
				return true;
			}
			current = current.move(speed);
			speed = new Point(speed.getX() > 0 ? speed.getX() - 1 : 0L, speed.getY() - 1);
		}
		return false;
	}
}
