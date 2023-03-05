package y2017.day22;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import common.Direction;
import common.Input;
import common.Point;

public class Day22 {

	enum State
	{
		CLEAN, WEAKEND, INFECTED, FLAGGED;
	}
	public static void main(String[] args) {
		List<String> rows = Input.parseLines("y2017/day22/day22.txt").collect(Collectors.toList());
		Map<Point, State> map = new HashMap<>();
		for (int row = 0 ; row < rows.size() ; row++)
		{
			for (int col = 0 ; col < rows.get(row).length() ; col++)
			{
				if (rows.get(row).charAt(col) == '#')
				{
					map.put(new Point(col, row), State.INFECTED);
				}
			}
		}
		Point currentPos = new Point(rows.size() / 2,rows.get(0).length() / 2);
		Direction direction = Direction.SOUTH;
		int infected = 0;
		for (int i = 0 ; i < 10000000 ; i++)
		{
			State current = map.getOrDefault(currentPos, State.CLEAN);
			switch (current) {
			case CLEAN:
				direction = direction.right();
				map.put(currentPos, State.WEAKEND);
				break;
			case WEAKEND:
				map.put(currentPos, State.INFECTED);
				infected++;
				break;
			case INFECTED:
				direction = direction.left();
				map.put(currentPos, State.FLAGGED);
				break;
			case FLAGGED:
				direction = direction.left().left();
				map.put(currentPos, State.CLEAN);
				break;
			}
			currentPos = currentPos.move(direction);
		}
		System.out.println(infected);
	}

}
