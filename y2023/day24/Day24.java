package y2023.day24;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToLongFunction;
import java.util.regex.Pattern;

import common.AdventSolution;
import common.Point;
import common.equations.Equation;
import common.equations.Expression;
import common.equations.Nomynal;
import common.equations.Variable;

public class Day24 extends AdventSolution {

	private static final long MIN_COORD = 200000000000000L;
	private static final long MAX_COORD = 400000000000000L;

	private static record HailRock(Point initialPosition, Point moveVector) {
		public static HailRock fromString(String rockDescription) {
			var rockPattern = Pattern.compile("(-?\\d+), (-?\\d+), (-?\\d+) @ (-?\\d+), (-?\\d+), (-?\\d+)");
			var rockMatcher = rockPattern.matcher(rockDescription);
			if (!rockMatcher.matches()) {
				throw new IllegalArgumentException(rockDescription);
			}
			var initial = new Point(Long.parseLong(rockMatcher.group(1)), Long.parseLong(rockMatcher.group(2)),
					Long.parseLong(rockMatcher.group(3)));
			var moveVector = new Point(Long.parseLong(rockMatcher.group(4)), Long.parseLong(rockMatcher.group(5)),
					Long.parseLong(rockMatcher.group(6)));
			return new HailRock(initial, moveVector);
		}

		public Optional<DoublePoint> getXYCrossPoint(HailRock other) {
			// i1x + t1 * v1x == i2x + t2 * v2x ; t1 == ((i2x + t2 * v2x - i1x) / v1x)
			// i1y + t1 * v1y == i2y + t2 * v2y ; (i1y + i2x * v1y / v1x - i1x * v1y / v1x -
			// i2y) / (v2y - v2x * v1y / v1x) == t2
			// v2y - v2x * v1y / v1x == 0 ; v2y * v1x == v2x * v1y ;

			var i1x = (double) initialPosition.getX();
			var i1y = (double) initialPosition.getY();
			var i2x = (double) other.initialPosition.getX();
			var i2y = (double) other.initialPosition.getY();
			var v1x = (double) moveVector.getX();
			var v1y = (double) moveVector.getY();
			var v2x = (double) other.moveVector.getX();
			var v2y = (double) other.moveVector.getY();

			// Parallel
			if (v2y - v2x * v1y / v1x == 0) {
				return Optional.empty();
			}
			var t2 = (i1y + i2x * v1y / v1x - i1x * v1y / v1x - i2y) / (v2y - v2x * v1y / v1x);
			var t1 = ((i2x + t2 * v2x - i1x) / v1x);
			if (t1 < 0 || t2 < 0) {
				return Optional.empty();
			}
			return Optional.of(new DoublePoint(i1x + v1x * t1, i1y + v1y * t1));
		}

		public Expression getXFormula(String timeVariable) {
			return getFormula(timeVariable, Point::getX);
		}

		public Expression getYFormula(String timeVariable) {
			return getFormula(timeVariable, Point::getY);
		}

		public Expression getZFormula(String timeVariable) {
			return getFormula(timeVariable, Point::getZ);
		}

		private Expression getFormula(String timeVariable, ToLongFunction<Point> coordinateExtractor) {
			var constPart = new Nomynal(coordinateExtractor.applyAsLong(initialPosition), List.of());
			var changingPart = new Nomynal(coordinateExtractor.applyAsLong(moveVector),
					List.of(new Variable(timeVariable)));
			return new Expression(List.of(constPart, changingPart), null);
		}
		public Point positionAt(long t) {
			return initialPosition.move(moveVector.multiply(t));
		}
	}

	private static record SolutionHailRock(ExpressionPoint initialPosition, ExpressionPoint moveVector) {
		public static double getDifference(SolutionHailRock... hailRocks) {
			return getDifference(List.of(hailRocks));
		}
		public SolutionHailRock normalize() {
			var initialNormalized = initialPosition.normalized();
			var moveNormalized = moveVector.normalized();
			return new SolutionHailRock(initialNormalized, moveNormalized);
		}
		public static double getDifference(Collection<SolutionHailRock> hailRocks) {
			var hailRocksList = hailRocks.stream().filter(r -> r != null).toList();
			var totalDiff = 0.;
			var rocksCount = 0;
			for (var i = 0 ; i < hailRocksList.size() - 1 ; i++) {
				for (var j = i + 1 ; j < hailRocksList.size() ; j++) {
					totalDiff += hailRocksList.get(i).compare(hailRocksList.get(j));
					rocksCount++;
				}
			}
			return rocksCount == 0 ? 0 : totalDiff;
		}
		public double getDifference(HailRock hailRock) {
			// ix1 + vx1 *t = ix2 + vx2 * t
			// t = (ix2 - ix1) / (vx2 - vx1) 
			var tBottomUp = moveVector.xDivided - hailRock.moveVector().getX() * moveVector.xDivisor;
			if (tBottomUp == 0) {
				return 0;
			}
			var tBottomDown = moveVector.xDivisor;
			var tTopUp = hailRock.initialPosition().getX() * initialPosition.xDivisor - initialPosition.xDivided;
			var ttopDown = initialPosition.xDivisor;
			var tTop = tTopUp * tBottomDown;
			var tBottom = tBottomUp * ttopDown;

			// it/ib + vt/vb * tt/tb = (it*vb*tb + vt*tt*ib )/ ib*vb*tb
			var ySolutionTop = initialPosition.yDivided * moveVector.yDivisor * tBottom + initialPosition.yDivisor * tTop * moveVector.yDivided;
			var ySolutionBottom = initialPosition.yDivisor * moveVector.yDivisor * tBottom;

			var yRockTop = hailRock.initialPosition.getY() * tBottom + tTop * hailRock.moveVector.getY();
			var yRockBottom = tBottom;

			return (double) (ySolutionTop * yRockBottom - yRockTop * ySolutionBottom) / (ySolutionBottom * yRockBottom);
		}
		private double compare(SolutionHailRock other) {
			var ixDiff = Math.abs((double) (initialPosition.xDivided() * other.initialPosition().xDivisor()
					- other.initialPosition().xDivided() * initialPosition.xDivisor())
					/ (other.initialPosition().xDivisor() * initialPosition.xDivisor()));
			var iyDiff = Math.abs((double) (initialPosition.yDivided() * other.initialPosition().yDivisor()
					- other.initialPosition().yDivided() * initialPosition.yDivisor())
					/ (other.initialPosition().yDivisor() * initialPosition.yDivisor()));
			var vxDiff = Math.abs((double) (moveVector.xDivided() * other.moveVector().xDivisor()
					- other.moveVector().xDivided() * moveVector.xDivisor())
					/ (other.moveVector().xDivisor() * moveVector.xDivisor()));
			var vyDiff = Math.abs((double) (moveVector.yDivided() * other.moveVector().yDivisor()
					- other.moveVector().yDivided() * moveVector.yDivisor())
					/ (other.moveVector().yDivisor() * moveVector.yDivisor()));
			return ixDiff + iyDiff + vxDiff + vyDiff;
		}
	}

	private static record DoublePoint(double x, double y) {
	}

	private static record ExpressionPoint(long xDivided, long xDivisor, long yDivided, long yDivisor) {
		public static ExpressionPoint fromExpressions(Expression ex, Expression ey) {
			if (!ex.hasNumericValue()) {
				throw new IllegalArgumentException(ex.toString());
			}
			if (!ey.hasNumericValue()) {
				throw new IllegalArgumentException(ey.toString());
			}
			return new ExpressionPoint(ex.getDividedNumericValue(), ex.getDivisorNumericValue(),
					ey.getDividedNumericValue(), ey.getDivisorNumericValue());
		}
		public ExpressionPoint normalized() {
			var commonFactorX = highestCommonFactor(Math.abs(xDivided), Math.abs(xDivisor));
			var newXDivided = xDivided / commonFactorX;
			var newXDivisor = xDivisor / commonFactorX;
			var commonFactorY = highestCommonFactor(Math.abs(yDivided), Math.abs(yDivisor));
			var newYDivided = yDivided / commonFactorY;
			var newYDivisor = yDivisor / commonFactorY;
			return new ExpressionPoint(newXDivided, newXDivisor, newYDivided, newYDivisor);
		}
		private static long highestCommonFactor(long a, long b) {
		    while (b != 0) {
		    	var t = b;
		    	b = a % b;
		    	a = t;
		    }
		    return a;
		}

		@Override
		public String toString() {
			return "(%f, %f)".formatted((double)xDivided / xDivisor, (double) yDivided / yDivisor);
		}
	}

	@Override
	public Object part1Solution() {
		var hailRocks = getInput().map(HailRock::fromString).toList();
		var crossPoints = new ArrayList<DoublePoint>();
		for (var i = 0; i < hailRocks.size() - 1; i++) {
			for (var j = i + 1; j < hailRocks.size(); j++) {
				hailRocks.get(i).getXYCrossPoint(hailRocks.get(j)).ifPresent(crossPoints::add);
			}
		}
		return crossPoints.stream().filter(p -> p.x() >= MIN_COORD).filter(p -> p.x() <= MAX_COORD)
				.filter(p -> p.y() >= MIN_COORD).filter(p -> p.y() <= MAX_COORD).count();
	}

	@Override
	public Object part2Solution() {
		var hailRocks = getInput().map(HailRock::fromString).toList();
		var xStats = hailRocks.stream().map(HailRock::initialPosition).mapToLong(Point::getX).summaryStatistics();
		System.out.println(xStats.getMax() - xStats.getMin());
		var rock1 = hailRocks.get(2);
		var rock2 = hailRocks.get(3);
		var rock3 = hailRocks.get(4);
		var rock4 = hailRocks.get(1);
		var rock5 = hailRocks.get(0);
		for (var t = -10 ; t < 10 ; t+= 1) {
//		for (var t = 76089413220846L ; t < 9176089413220846L ; t+= 1000000000) {
//			System.out.println("====================================================");
			System.out.println("t = " + t);
			var solutions = new ArrayList<SolutionHailRock>();
			solutions.add(solve(rock1, rock2, rock3, t));
//			solutions.add(solve(rock1, rock2, rock4, t));
//			solutions.add(solve(rock1, rock2, rock5, t));
//			solutions.add(solve(rock1, rock3, rock4, t));
//			solutions.add(solve(rock1, rock3, rock5, t));
//			solutions.add(solve(rock1, rock4, rock5, t));
			solutions.removeIf(o -> o == null);
//			System.out.println(SolutionHailRock.getDifference(solutions));
			System.out.println(solutions);
			solutions.stream()
					.map(SolutionHailRock::normalize)
					.mapToDouble(sol -> sol.getDifference(rock4))
					.forEach(System.out::println);
			System.out.println("====================================================");
		}
/*
		var ax = rock1.getXFormula("ta");
		var ay = rock1.getYFormula("ta");
		var az = rock1.getZFormula("ta");

		var bx = rock2.getXFormula("tb");
		var by = rock2.getYFormula("tb");
		var bz = rock2.getZFormula("tb");

		var cx = rock3.getXFormula("tc");
		var cy = rock3.getYFormula("tc");
		var cz = rock3.getZFormula("tc");

		// (ax - bx) / (ay - by) == (ax - cx) / (ay - cy)
		var topLeft1 = ax.minus(bx);
		var bottomLeft1 = ay.minus(by);
		var topRight1 = ax.minus(cx);
		var bottomRight1 = ay.minus(cy);

		var left1 = new Expression(topLeft1.divided(), bottomLeft1.divided());
		var right1 = new Expression(topRight1.divided(), bottomRight1.divided());
		var equation1 = new Equation(List.of(left1), List.of(right1));
		equation1 = equation1.normalize();

		// (ax - bx) / (az - bz) == (ax - cx) / (az - cz)
		var topLeft2 = ax.minus(bx);
		var bottomLeft2 = az.minus(bz);
		var topRight2 = ax.minus(cx);
		var bottomRight2 = az.minus(cz);

		var left2 = new Expression(topLeft2.divided(), bottomLeft2.divided());
		var right2 = new Expression(topRight2.divided(), bottomRight2.divided());
		var equation2 = new Equation(List.of(left2), List.of(right2));
		equation2 = equation2.normalize();

		// (az - bz) / (ay - by) == (az - cz) / (ay - cy)
		var topLeft3 = az.minus(bz);
		var bottomLeft3 = ay.minus(by);
		var topRight3 = az.minus(cz);
		var bottomRight3 = ay.minus(cy);

		var left3 = new Expression(topLeft3.divided(), bottomLeft3.divided());
		var right3 = new Expression(topRight3.divided(), bottomRight3.divided());
		var equation3 = new Equation(List.of(left3), List.of(right3));
		equation3 = equation3.normalize();

//		equation1 = equation1.setVarValue("ta", 5);
//		equation1 = equation1.setVarValue("tb", 3);
//		equation1 = equation1.setVarValue("tc", 6);
		var taExpression = equation1.extractLinearVariableValue("ta");
		var taResolved1 = equation2.setVarValue("ta", taExpression);
		var taResolved2 = equation3.setVarValue("ta", taExpression);
		System.out.println(taResolved1);
		System.out.println(taResolved2);
		var tbGuess = 9;
		System.out.println(solveSquareEquation(taResolved1.setVarValue("tb", tbGuess).extractSqaureEquation("tc")));
		System.out.println(solveSquareEquation(taResolved2.setVarValue("tb", tbGuess).extractSqaureEquation("tc")));

		var taResolvedLinear = new Equation(List.of(taResolved1.left().get(0).plus(taResolved2.left().get(0))), List.of(new Expression(List.of(new Nomynal(0, List.of())), null)));
		var tbExpression = taResolvedLinear.extractLinearVariableValue("tb");

		var squareEqationParts1 = taResolved1.extractSqaureEquation("tb");
		var squareEqationParts2 = taResolved2.extractSqaureEquation("tb");

		System.out.println(Equation.buildSquareEquation(squareEqationParts1, tbExpression));
		System.out.println(Equation.buildSquareEquation(squareEqationParts2, tbExpression));

		return equation2.setVarValue("ta", taExpression);//.setVarValue("tb", 3);//.setVarValue("tc", 6);*/
		var lowestDistance = Long.MAX_VALUE;
		var lowestI = 0;
		var lowestJ = 0;
		var lowestTime = 0L;
		for (var i = 0 ; i < hailRocks.size() ; i++) {
			for (var j = 1 ; j < i ; j++) {
				var minTime = getMinDistanceTime(hailRocks.get(i), hailRocks.get(j));
				var minDistance = getDistance(hailRocks.get(i), hailRocks.get(j), minTime);
				if (minDistance < lowestDistance) {
					lowestDistance = minDistance;
					lowestI = i;
					lowestJ = j;
					lowestTime = minTime;
				}
			}
		}
		System.out.println(hailRocks.get(lowestI));
		System.out.println(hailRocks.get(lowestJ));
		System.out.println(lowestDistance);
		System.out.println(lowestTime);
		var otherIdx = 0;
		while (otherIdx == lowestI || otherIdx == lowestJ) {
			otherIdx++;
		}
		for (var tDiff = 1 ; tDiff < 100000 ; tDiff++) {
			for (var tOffset = -10000 ; tOffset <= 10000 ; tOffset++) {
				var solution = guessSolution(hailRocks.get(lowestJ), hailRocks.get(lowestI), lowestTime + tOffset, lowestTime + tOffset + tDiff);
				if (verify(solution, hailRocks.get(otherIdx))) {
					System.out.println("Found! " + solution);
				}
				solution = guessSolution(hailRocks.get(lowestI), hailRocks.get(lowestJ), lowestTime + tOffset, lowestTime + tOffset + tDiff);
				if (verify(solution, hailRocks.get(otherIdx))) {
					System.out.println("Found! " + solution);
				}
			}
		}
		return "";
	}

	private HailRock guessSolution(HailRock r1, HailRock r2, long t1, long t2) {
		var tDiff = Math.abs(t2 - t1);
		var colision1 = r1.positionAt(t1);
		var colision2 = r2.positionAt(t2);
		var vx = (colision2.getX() - colision1.getX()) / tDiff;
		var vy = (colision2.getY() - colision1.getY()) / tDiff;
		var vz = (colision2.getZ() - colision1.getZ()) / tDiff;
		var ix = colision1.getX() - t1 * vx;
		var iy = colision1.getY() - t1 * vy;
		var iz = colision1.getZ() - t1 * vz;
		return new HailRock(new Point(ix, iy, iz), new Point(vx, vy, vz));
	}
	private boolean verify(HailRock rock1, HailRock rock2) {
		//v1*t1 + i1 = v2*t1 + i2 => t1*(v1 - v2) = i2 - i1 => t = (i2 - i1) / (v1 - v2)
		// (i2Top - i1 * i2Bottom) / i2Bottom / 
		// (v1 * v2Bottom - v2Top) / v2Bottom

		// (i2Top - i1 * i2Bottom) * v2Bottom / (v1 * v2Bottom - v2Top) * i2Bottom
		var v1x = rock1.moveVector().getX();
		var v1y = rock1.moveVector().getY();
		var v1z = rock1.moveVector().getZ();
		var v2x = rock2.moveVector().getX();
		var v2y = rock2.moveVector().getY();
		var v2z = rock2.moveVector().getZ();
		var i1x = rock1.initialPosition().getX();
		var i1y = rock1.initialPosition().getY();
		var i1z = rock1.initialPosition().getZ();
		var i2x = rock2.initialPosition().getX();
		var i2y = rock2.initialPosition().getY();
		var i2z = rock2.initialPosition().getZ();
		if (v1x == v2x && v1y == v2y && v1z == v2z) {
			return i1x == i2x && i1y == i2y && i1z == i2z;
		}

		var tTop = v1x == v2x ? ( v1y == v2y ? i2z - i1z : i2y - i1y ) : i2x - i1x; 
		var tBottom = v1x == v2x ? ( v1y == v2y ? v1z - v2z : v1y - v2y ) : v1x - v2x;

		// i1 + v1*tTop / tBottom = i2Top / i2Bottom + (v2Top * tTop) / (v2Bottom*tBottom)
		// i1 * tBottom*v2Bottom*i2Bottom + v1*v2Bottom*i2Bottom = i2Top*v2Bottom*tBottom + v2Top*tTop*i2Bottom
		var xLeft = i1x * tBottom + v1x *tTop;
		var xRight = i2x * tBottom + v2x*tTop;
		var xMeet = xLeft == xRight;

		var yLeft = i1y * tBottom + v1y *tTop;
		var yRight = i2y * tBottom + v2y*tTop;
		var yMeet = yLeft == yRight;

		var zLeft = i1z * tBottom + v1z *tTop;
		var zRight = i2z * tBottom + v2z*tTop;
		var zMeet = zLeft == zRight;
		return xMeet && yMeet &&  zMeet;

	}
	private long getMinDistanceTime(HailRock r1, HailRock r2) {
		var distanceConstX = r1.initialPosition().getX() - r2.initialPosition().getX();
		var distanceVarX = r1.moveVector().getX() - r2.moveVector().getX();
		var distanceConstY = r1.initialPosition().getY() - r2.initialPosition().getY();
		var distanceVarY = r1.moveVector().getY() - r2.moveVector().getY();
		var distanceConstZ = r1.initialPosition().getZ() - r2.initialPosition().getZ();
		var distanceVarZ = r1.moveVector().getZ() - r2.moveVector().getZ();
		var potentialT = new ArrayList<Long>();
		if (distanceVarX != 0) {
			var tx = -1 * distanceConstX / distanceVarX;
			potentialT.add(tx);
			potentialT.add(tx + 1);
		}
		if (distanceVarY != 0) {
			var ty = -1 * distanceConstY / distanceVarY;
			potentialT.add(ty);
			potentialT.add(ty + 1);
		}
		if (distanceVarZ != 0) {
			var tz = -1 * distanceConstZ / distanceVarZ;
			potentialT.add(tz);
			potentialT.add(tz + 1);
		}
		potentialT.removeIf(pt -> pt < 0);
		if (potentialT.isEmpty()) {
			return -1;
		}
		var bestT = -1L;
		var bestTVal = Long.MAX_VALUE;
		for (var i = 0 ; i < potentialT.size() ; i++) {
			var tTest = potentialT.get(i);
			var dist = getDistance(r1, r2, tTest);
			if (dist < bestTVal) {
				bestT = tTest;
				bestTVal = dist;
			}
		}
		return bestT;
	}

	private long getDistance(HailRock r1, HailRock r2, long t) {
		var r1Position = r1.positionAt(t);
		var r2Position = r2.positionAt(t);
		var x = Math.abs(r1Position.getX() - r2Position.getX());
		var y = Math.abs(r1Position.getY() - r2Position.getY());
		var z = Math.abs(r1Position.getZ() - r2Position.getZ());
		return x + y + z;
	}
	private SolutionHailRock solve(HailRock rock1, HailRock rock2, HailRock rock3, long t1) {
		var rock1X = rock1.initialPosition().getX() + t1 * rock1.moveVector().getX();
		var rock1Y = rock1.initialPosition().getY() + t1 * rock1.moveVector().getY();

		var rock1XEquation = Equation.parse("ix+%d*vx=%d".formatted(t1, rock1X));
		var rock1YEquation = Equation.parse("iy+%d*vy=%d".formatted(t1, rock1Y));
		var rock2XEquation = Equation.parse("ix+t2*vx=%d+t2*%d".formatted(rock2.initialPosition().getX(), rock2.moveVector().getX()));
		var rock2YEquation = Equation.parse("iy+t2*vy=%d+t2*%d".formatted(rock2.initialPosition().getY(), rock2.moveVector().getY()));
		var rock3XEquation = Equation.parse("ix+t3*vx=%d+t3*%d".formatted(rock3.initialPosition().getX(), rock3.moveVector().getX()));
		var rock3YEquation = Equation.parse("iy+t3*vy=%d+t3*%d".formatted(rock3.initialPosition().getY(), rock3.moveVector().getY()));
		var ixExpression = rock1XEquation.extractLinearVariableValue("ix");
		var iyExpression = rock1YEquation.extractLinearVariableValue("iy");
		var t2Expression1 = rock2XEquation.setVarValue("ix", ixExpression).extractLinearVariableValue("t2");
		if (t2Expression1.hasNumericValue()) {
			return null;
		}
		var t2Expression2 = rock2YEquation.setVarValue("iy", iyExpression).extractLinearVariableValue("t2");
		if (t2Expression2.hasNumericValue()) {
			return null;
		}
		var t3Expression1 = rock3XEquation.setVarValue("ix", ixExpression).extractLinearVariableValue("t3");
		if (t3Expression1.hasNumericValue()) {
			return null;
		}
		var t3Expression2 = rock3YEquation.setVarValue("iy", iyExpression).extractLinearVariableValue("t3");
		if (t3Expression2.hasNumericValue()) {
			return null;
		}
		var t2Equation = new Equation(List.of(t2Expression1), List.of(t2Expression2)).normalize();
		var t3Equation = new Equation(List.of(t3Expression1), List.of(t3Expression2)).normalize();
		var vxExpression = t2Equation.extractLinearVariableValue("vx");
		t3Equation = t3Equation.setVarValue("vx", vxExpression);
		if (t3Equation.hasInfiniteOrNoSolutions()) {
			return null;
		}
		var vy = t3Equation.extractLinearVariableValue("vy");
		var vx = vxExpression.setVarValue("vy", vy);
//		System.out.println("vx = " + vx);
//		System.out.println("vy = " + vy);
		var ix = rock1XEquation.setVarValue("vx", vx).extractLinearVariableValue("ix");
//		System.out.println("ix = " + ix);
		var iy = rock1YEquation.setVarValue("vy", vy).extractLinearVariableValue("iy");
//		System.out.println("iy = " + iy);
		var solution = new SolutionHailRock(ExpressionPoint.fromExpressions(ix, iy),
				ExpressionPoint.fromExpressions(vx, vy));
		var valid = validate(rock1, rock2, rock3, solution);
		if (!valid) {
			System.out.println("Invalid");
			return null;
		}
		return solution;
	}

	private boolean validate(HailRock rock1, HailRock rock2, HailRock rock3, SolutionHailRock solution) {
		return rocksMeet(rock1, solution) && rocksMeet(rock2, solution) && rocksMeet(rock3, solution);
	}

	private boolean rocksMeet(HailRock rock1, SolutionHailRock rock2) {
		//v1*t1 + i1 = v2*t1 + i2 => t1*(v1 - v2) = i2 - i1 => t = (i2 - i1) / (v1 - v2)
		// (i2Top - i1 * i2Bottom) / i2Bottom / 
		// (v1 * v2Bottom - v2Top) / v2Bottom

		// (i2Top - i1 * i2Bottom) * v2Bottom / (v1 * v2Bottom - v2Top) * i2Bottom
		var v1x = rock1.moveVector().getX();
		var v1y = rock1.moveVector().getY();
		var v2xTop = rock2.moveVector().xDivided();
		var v2xBottom = rock2.moveVector().xDivisor();
		var v2yTop = rock2.moveVector().yDivided();
		var v2yBottom = rock2.moveVector().yDivisor();
		var i1x = rock1.initialPosition().getX();
		var i1y = rock1.initialPosition().getY();
		var i2xTop = rock2.initialPosition().xDivided();
		var i2xBottom = rock2.initialPosition().xDivisor();
		var i2yTop = rock2.initialPosition().yDivided();
		var i2yBottom = rock2.initialPosition().yDivisor();
		if (v1x * v2xBottom == v2xTop && v1y * v2yBottom == v2yTop) {
			return i1x * i2xBottom == i2xTop && i1y * i2yBottom == i2yTop;
		}

		var tTop = v1x * v2xBottom == v2xTop ? i2yTop * v2yBottom - i1y * i2yBottom * v2yBottom : i2xTop * v2xBottom - i1x * i2xBottom * v2xBottom; 
		var tBottom = v1x * v2xBottom == v2xTop ? v1y * v2yBottom * i2yBottom - v2yTop * i2yBottom : v1x * v2xBottom * i2xBottom - v2xTop * i2xBottom;

		// i1 + v1*tTop / tBottom = i2Top / i2Bottom + (v2Top * tTop) / (v2Bottom*tBottom)
		// i1 * tBottom*v2Bottom*i2Bottom + v1*v2Bottom*i2Bottom = i2Top*v2Bottom*tBottom + v2Top*tTop*i2Bottom
		var xLeft = i1x * tBottom*v2xBottom*i2xBottom + v1x*v2xBottom*i2xBottom *tTop;
		var xRight = i2xTop*v2xBottom*tBottom + v2xTop*tTop*i2xBottom;
		var xMeet = xLeft == xRight;

		var yLeft = i1y * tBottom*v2yBottom*i2yBottom + v1y*v2yBottom*i2yBottom * tTop;
		var yRight = i2yTop*v2yBottom*tBottom + v2yTop*tTop*i2yBottom;
		var yMeet = yLeft == yRight;
		return xMeet && yMeet;
	}

	private Set<Double> solveSquareEquation(Map<String, Expression> equation) {
		var a = equation.get("a").getNumericValue();
		var b = equation.get("b").getNumericValue();
		var c = equation.get("c").getNumericValue();
		var delta = b * b - 4 *a *c;
		if (delta < 0) {
			return Set.of();
		}
		return Set.of((-1*b - Math.sqrt(delta)) / 2 / a, (-1*b + Math.sqrt(delta)) / 2 / a);
	}

	public static void main(String[] args) {
		new Day24().solve();
	}
}
