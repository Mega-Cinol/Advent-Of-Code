package y2023.day24;

import common.AdventSolution;
import common.Point;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

public class Day24 extends AdventSolution {

	private static final long MIN_COORD = 200000000000000L;
	private static final long MAX_COORD = 400000000000000L;

	private record HailRock(Point initialPosition, Point moveVector) {
		public static HailRock fromString(String rockDescription) {
			var rockPattern = Pattern.compile("(-?\\d+),\\s*(-?\\d+),\\s*(-?\\d+)\\s*@\\s*(-?\\d+),\\s*(-?\\d+),\\s*(-?\\d+)");
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
	}

	private record DoublePoint(double x, double y) {
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

    private record BigHailRock(BigPoint initialPosition, BigPoint moveVector) {
        public static BigHailRock fromString(String rockDescription) {
            var rockPattern = Pattern.compile("(-?\\d+),\\s*(-?\\d+),\\s*(-?\\d+)\\s*@\\s*(-?\\d+),\\s*(-?\\d+),\\s*(-?\\d+)");
            var rockMatcher = rockPattern.matcher(rockDescription);
            if (!rockMatcher.matches()) {
                throw new IllegalArgumentException(rockDescription);
            }
            var initial = new BigPoint(new BigInteger(rockMatcher.group(1)), new BigInteger(rockMatcher.group(2)),
                    new BigInteger(rockMatcher.group(3)));
            var moveVector = new BigPoint(new BigInteger(rockMatcher.group(4)), new BigInteger(rockMatcher.group(5)),
                    new BigInteger(rockMatcher.group(6)));
            return new BigHailRock(initial, moveVector);
        }
    }
    private record BigPoint(BigInteger x, BigInteger y, BigInteger z) {
        public BigPoint move(BigPoint moveVector) {
            return new BigPoint(x.add(moveVector.x), y.add(moveVector.y), z.add(moveVector.z));
        }
        public BigPoint negate() {
            return new BigPoint(x.multiply(new BigInteger("-1")), y.multiply(new BigInteger("-1")), z.multiply(new BigInteger("-1")));
        }
        public BigPoint crossProduct(BigPoint other) {
            var newX = y.multiply(other.z).subtract(z.multiply(other.y));
            var newY = z.multiply(other.x).subtract(x.multiply(other.z));
            var newZ = x.multiply(other.y).subtract(y.multiply(other.x));
            return new BigPoint(newX, newY, newZ);
        }
        public BigInteger dotProduct(BigPoint other) {
            return x.multiply(other.x).add(y.multiply(other.y)).add(z.multiply(other.z));
        }
        public BigPoint multiply(BigInteger multiplier) {
            return new BigPoint(x.multiply(multiplier), y.multiply(multiplier), z.multiply(multiplier));
        }
    }

	@Override
	public Object part2Solution() {
		var hailRocks = getInput().map(BigHailRock::fromString).toList();

        var rock1 = hailRocks.get(4);
        var rock2 = hailRocks.get(6);
        var rock3 = hailRocks.get(7);

        var rock2Relative = new BigHailRock(rock2.initialPosition.move(rock1.initialPosition.negate()), rock2.moveVector.move(rock1.moveVector.negate()));
        var rock3Relative = new BigHailRock(rock3.initialPosition.move(rock1.initialPosition.negate()), rock3.moveVector.move(rock1.moveVector.negate()));
        var t2 = rock2Relative.initialPosition.crossProduct(rock3Relative.initialPosition).dotProduct(rock3Relative.moveVector).negate().divide(rock2Relative.moveVector.crossProduct(rock3Relative.initialPosition).dotProduct(rock3Relative.moveVector));
        var t3 = rock2Relative.initialPosition.crossProduct(rock3Relative.initialPosition).dotProduct(rock2Relative.moveVector).negate().divide(rock2Relative.initialPosition.crossProduct(rock3Relative.moveVector).dotProduct(rock2Relative.moveVector));
        var collisionPoint1 = rock2.initialPosition.move(rock2.moveVector().multiply(t2));
        var collisionPoint2 = rock3.initialPosition.move(rock3.moveVector().multiply(t3));
        var t2MinusT3 = t2.subtract(t3);
        var stoneMoveVector = new BigPoint((collisionPoint1.x().subtract(collisionPoint2.x())).divide(t2MinusT3), (collisionPoint1.y().subtract(collisionPoint2.y())).divide(t2MinusT3), (collisionPoint1.z().subtract(collisionPoint2.z())).divide(t2MinusT3));
        var stoneInitialPosition = collisionPoint1.move(stoneMoveVector.multiply(t2.negate()));
        System.out.println(stoneMoveVector);
        System.out.println(stoneInitialPosition);
        System.out.println(collisionPoint2);
        System.out.println(t2);
        System.out.println(t3);
        return stoneInitialPosition.x().longValue() + stoneInitialPosition.y().longValue() + stoneInitialPosition.z().longValue();
	}

	public static void main(String[] args) {
		new Day24().solve();
	}
}
