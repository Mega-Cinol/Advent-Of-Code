package y2016.day1;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import common.Direction;
import common.Input;
import common.Point;

public class Day1 {

	public static void main(String[] args) {
		Me me = new Me();
		Input.parseLines("y2016/day1/day1.txt", Function.identity()).forEach(cmd -> {
			if (cmd.startsWith("L"))
			{
				me.turnLeft();
			} else {
				me.turnRight();
			}
			me.walk(Integer.parseInt(cmd.substring(1)));
		});
		System.out.println(me.getResult());
	}

	private static class Me
	{
		private Point location = new Point(0,0);
		private Set<Point> history = new HashSet<>();
		private Direction direction = Direction.NORTH;

		public Me()
		{
			history.add(location);
		}
		public void turnLeft()
		{
			direction = direction.left();
		}
		public void turnRight()
		{
			direction = direction.right();
		}
		public void walk(int distance)
		{
			while (distance-- > 0)
			{
			location = location.move(direction.getDx(), direction.getDy());
			if (!history.add(location))
				System.out.println(getResult());
			}
		}

		public long getResult()
		{
			return Math.abs(location.getX()) + Math.abs(location.getY());
		}
	}
}
