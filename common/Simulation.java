package common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Simulation {
	private final Set<Point> initialState = new HashSet<>();
	private BiPredicate<Point, Set<Point>> activateCondition;
	private BiPredicate<Point, Set<Point>> deactivateCondition;
	private Consumer<Set<Point>> onStepAction = points -> {};
	private Integer maxX = null;
	private Integer maxY = null;

	public void setInitialState(List<String> printedState, char active, boolean fixedGridSize)
	{
		initialState.clear();
		maxY = fixedGridSize ? printedState.get(0).length() - 1 : null;
		maxX = fixedGridSize ? printedState.size() - 1 : null;
		for (int row = 0 ; row < printedState.size() ; row++)
		{
			String rowStr = printedState.get(row);
			for (int col = 0 ; col < rowStr.length() ; col++)
			{
				if (rowStr.charAt(col) == active)
				{
					initialState.add(new Point(row, col));
				}
			}
		}
	}

	public Set<Point> run(int steps)
	{
		return run(steps, false);
	}

	public Set<Point> run(int steps, boolean printSteps)
	{
		Set<Point> current = new HashSet<>(initialState);
		Set<Point> next = new HashSet<>(current);
		for (int step = 0 ; step < steps ; step++)
		{
			next = new HashSet<>(current);
			next.removeIf(point -> deactivateCondition.test(point, current));
			current.stream().map(Point::getNeighbours).flatMap(Set::stream).filter(p -> {
				boolean inRange = (maxX == null) || (p.getX() >= 0 && p.getX() <= maxX);
				inRange &= (maxY == null) || (p.getY() >= 0 && p.getY() <= maxY);
				return inRange;
			}).filter(point -> !current.contains(point))
					.filter(point -> activateCondition.test(point, current)).forEach(next::add);
			if (printSteps)
			{
				System.out.println("Step: " + (step + 1));
				print(current);
			}
			onStepAction.accept(current);
			current.clear();
			current.addAll(next);
		}
		return next;
	}

	public Set<Point> getInitialState() {
		return initialState;
	}

	public void setActivateCondition(BiPredicate<Point, Set<Point>> activateCondition) {
		this.activateCondition = activateCondition;
	}

	public void setDeactivateCondition(BiPredicate<Point, Set<Point>> deactivateCondition) {
		this.deactivateCondition = deactivateCondition;
	}

	public void setOnStepAction(Consumer<Set<Point>> action)
	{
		onStepAction = action;
	}

	public BiPredicate<Point, Set<Point>> activeNeighbourCountCondition(Predicate<Integer> countCondition)
	{
		return (point, activePoints) -> {
			Set<Point> activeNeights = point.getNeighbours();
			activeNeights.retainAll(activePoints);
			return countCondition.test(activeNeights.size());
		};
	}

	public static void print(Set<Point> points)
	{
		long maxX = points.stream().mapToLong(Point::getX).max().getAsLong();
		long maxY = points.stream().mapToLong(Point::getY).max().getAsLong();
		for (int x = 0; x <= maxX ; x++)
		{
			for (int y = 0 ; y <= maxY ; y++)
			{
				if (points.contains(new Point(x, y)))
				{
					System.out.print("#");
				}
				else
				{
					System.out.print(".");
				}
			}
			System.out.println();
		}
	}
}
