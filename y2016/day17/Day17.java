package y2016.day17;

import java.util.HashSet;
import java.util.Set;

import common.Direction;
import common.MD5;
import common.PathFinding;
import common.Point;

public class Day17 {
	private static final Set<Character> ALLOWED_CHARS = new HashSet<>();
	static {
		ALLOWED_CHARS.add('b');
		ALLOWED_CHARS.add('c');
		ALLOWED_CHARS.add('d');
		ALLOWED_CHARS.add('e');
		ALLOWED_CHARS.add('f');
	}
	public static void main(String[] args) {
		PathFinding.countSteps("", Day17::isDone, Day17::getPossibleMoves);
	}

	private static boolean isDone(String path, int depth)
	{
		Point location = pathToLocation(path);
		if (location.equals(new Point(3, -3)))
		{
			System.out.println(depth + " - " + path);
		}
		return false;
	}

	private static Set<String> getPossibleMoves(String path)
	{
		String hash = MD5.getHash("pxxbnzuo" + path);
		Point location = pathToLocation(path);
		Set<String> possibleMoves = new HashSet<>();
		if (location.equals(new Point(3, -3)))
		{
			return possibleMoves;
		}
		if (ALLOWED_CHARS.contains(hash.toLowerCase().charAt(0)) && location.getY() < 0)
		{
			possibleMoves.add(path + 'U');
		}
		if (ALLOWED_CHARS.contains(hash.toLowerCase().charAt(1)) && location.getY() > -3)
		{
			possibleMoves.add(path + 'D');
		}
		if (ALLOWED_CHARS.contains(hash.toLowerCase().charAt(2)) && location.getX() > 0)
		{
			possibleMoves.add(path + 'L');
		}
		if (ALLOWED_CHARS.contains(hash.toLowerCase().charAt(3)) && location.getX() < 3)
		{
			possibleMoves.add(path + 'R');
		}
		return possibleMoves;
	}

	private static Point pathToLocation(String path)
	{
		Point location = new Point(0,0);
		for (int i = 0 ; i < path.length() ; i++)
		{
			switch (path.charAt(i)) {
			case 'U':
				location = location.move(Direction.NORTH);
				break;
			case 'D':
				location = location.move(Direction.SOUTH);
				break;
			case 'L':
				location = location.move(Direction.WEST);
				break;
			case 'R':
				location = location.move(Direction.EAST);
				break;
			default:
				break;
			}
		}
		return location;
	}
}
