package common;

public enum Direction {
	NORTH(0, 1),
	SOUTH(0, -1),
	EAST(1, 0),
	WEST(-1, 0),
	UP(0, -1),
	DOWN(0, 1),
	LEFT(-1, 0),
	RIGHT(1, 0);

	private final int dx;
	private final int dy;

	Direction(int dx, int dy)
	{
		this.dx = dx;
		this.dy = dy;
	}

    public static Direction fromCharacter(char c) {
		return switch (c) {
			case '^' -> UP;
			case 'v' -> DOWN;
			case '<' -> LEFT;
			case '>' -> RIGHT;
			default -> throw new IllegalArgumentException("Unexpected character " + c);
		};
    }

    public int getDx()
	{
		return dx;
	}

	public int getDy()
	{
		return dy;
	}

	public boolean isVertical()
	{
		return dy != 0;
	}

	public Direction left()
	{
		switch (this) {
		case NORTH:
			return WEST;
		case WEST:
			return SOUTH;
		case SOUTH:
			return EAST;
		case EAST:
			return NORTH;

		case DOWN:
			return RIGHT;
		case LEFT:
			return DOWN;
		case RIGHT:
			return UP;
		case UP:
			return LEFT;
		default:
			throw new IllegalArgumentException(this.toString());
		}
	}

	public Direction right()
	{
		switch (this) {
		case NORTH:
			return EAST;
		case EAST:
			return SOUTH;
		case SOUTH:
			return WEST;
		case WEST:
			return NORTH;

		case DOWN:
			return LEFT;
		case LEFT:
			return UP;
		case RIGHT:
			return DOWN;
		case UP:
			return RIGHT;
		}
		return null;
	}

	public Direction opposite()
	{
		switch (this) {
		case NORTH:
			return SOUTH;
		case EAST:
			return WEST;
		case SOUTH:
			return NORTH;
		case WEST:
			return EAST;

		case DOWN:
			return UP;
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		case UP:
			return DOWN;
		}
		return null;
	}

	public static Direction betweenPoints(Point from, Point to) {
		if (from.getDimensionSize() != 2 || to.getDimensionSize() != 2) {
			throw new IllegalArgumentException("From %s, To %s".formatted(from, to));
		}
		if (from.equals(to)) {
			throw new IllegalArgumentException("From %s, To %s".formatted(from, to));
		}
		if (from.getX() != to.getX() && from.getY() != to.getY()) {
			throw new IllegalArgumentException("From %s, To %s".formatted(from, to));
		}
		if (from.getX() == to.getX()) {
			return from.getY() > to.getY() ? UP : DOWN;
		} else {
			return from.getX() > to.getX() ? LEFT : RIGHT;
		}
	}
}
