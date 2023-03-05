package y2021.day19;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;
import common.Point;

public class Day19 {
	private static final int COMMON_PROBES = 12;
	private static final int COMMON_EDGES = COMMON_PROBES * (COMMON_PROBES - 1) / 2;

	public static void main(String[] args) {
		List<String> input = Input.parseLines("y2021/day19/day19.txt").collect(Collectors.toList());
		List<Scanner> scanners = new ArrayList<>();
		Scanner current = null;
		for (String in : input) {
			if (in.startsWith("---")) {
				if (current != null) {
					scanners.add(current);
				}
				current = new Scanner();
				continue;
			}
			if (!in.isEmpty()) {
				current.addProbe(new Point(in));
			}
		}
		scanners.add(current);
		Map<Integer, Map<Integer, Translation>> translations = new HashMap<>();
		for (int i = 0; i < scanners.size() - 1; i++) {
			for (int j = i + 1; j < scanners.size(); j++) {
				Translation t = scanners.get(i).getTranslation(scanners.get(j));
				if (t != null) {
					translations.computeIfAbsent(i, k -> new HashMap<>()).put(j, t);
					translations.computeIfAbsent(j, k -> new HashMap<>()).put(i, t.reversed());
				}
			}
		}
		List<Point> scannerLocations = new ArrayList<>();
		Set<Point> allProbes = new HashSet<>();
		allProbes.addAll(scanners.get(0).getProbes());
		scannerLocations.add(new Point(0, 0, 0));
		for (int i = 1; i < scanners.size(); i++) {
			Deque<Translation> translationChain = getTranslationChain(translations, i, 0, new HashSet<>());
			Stream<Point> translatedProbes = scanners.get(i).getProbes().stream();
			Point scannerLocation = new Point(0, 0, 0);
			for (Translation t : translationChain) {
				translatedProbes = translatedProbes.map(t);
				scannerLocation = t.apply(scannerLocation);
			}
			translatedProbes.forEach(allProbes::add);
			scannerLocations.add(scannerLocation);
		}
		System.out.println(allProbes.size());
		// Part 2
		long maxDistance = 0;
		for (int i = 0 ; i < scannerLocations.size() - 1 ; i++)
		{
			for (int j = i + 1 ; j < scannerLocations.size() ; j++)
			{
				long dist = scannerLocations.get(i).getManhattanDistance(scannerLocations.get(j));
				if (dist > maxDistance)
				{
					maxDistance = dist;
				}
			}
		}
		System.out.println(maxDistance);
	}

	private static Deque<Translation> getTranslationChain(Map<Integer, Map<Integer, Translation>> translations,
			int source, int target, Set<Integer> visited) {
		visited.add(source);
		if (translations.get(source).containsKey(target)) {
			Deque<Translation> result = new ArrayDeque<>();
			result.add(translations.get(source).get(target));
			visited.remove(source);
			return result;
		}
		for (int next : translations.get(source).keySet()) {
			if (visited.contains(next)) {
				continue;
			}
			Deque<Translation> result = getTranslationChain(translations, next, target, visited);
			if (result != null) {
				result.addFirst(translations.get(source).get(next));
				visited.remove(source);
				return result;
			}
		}
		visited.remove(source);
		return null;
	}

	private static class Scanner {
		private final Set<Point> probes = new HashSet<>();
		private final Map<Point, Map<Point, Point>> distances = new HashMap<>();
		private final Map<Point, Integer> distancesCount = new HashMap<>();

		public void addProbe(Point probe) {
			for (Point otherProbe : probes) {
				Point distance = getKindOfDistance(probe, otherProbe);
				distances.computeIfAbsent(probe, p -> new HashMap<>()).put(otherProbe, distance);
				distances.computeIfAbsent(otherProbe, p -> new HashMap<>()).put(probe, distance);
				distancesCount.merge(distance, 1, (o, n) -> o + n);
			}
			probes.add(probe);
		}

		private Point getKindOfDistance(Point p1, Point p2) {
			long x = Math.abs(p1.getX() - p2.getX());
			long y = Math.abs(p1.getY() - p2.getY());
			long z = Math.abs(p1.getZ() - p2.getZ());
			long max = Math.max(Math.max(x, y), z);
			long min = Math.min(Math.min(x, y), z);
			long mid = x + y + z - max - min;
			return new Point(min, mid, max);
		}

		public Translation getTranslation(Scanner other) {
			{
				Set<Point> commonDistances = new HashSet<>(distancesCount.keySet());
				commonDistances.retainAll(other.distancesCount.keySet());
				if (commonDistances.stream()
						.map(dist -> Math.min(distancesCount.get(dist), other.distancesCount.get(dist)))
						.mapToInt(Integer::intValue).sum() < COMMON_EDGES) {
					return null;
				}
			}
			for (Point myProbe : probes) {
				for (Point otherProbe : other.probes) {
					List<Point> commonDistances = distances.get(myProbe).values().stream()
							.filter(dist -> other.distances.get(otherProbe).values().contains(dist))
							.collect(Collectors.toList());
					if (commonDistances.size() >= COMMON_PROBES - 1) {
						Map<Point, Point> secondPoints = findUniquePoints(distances.get(myProbe),
								other.distances.get(otherProbe), commonDistances);
						for (Map.Entry<Point, Point> secondPoint : secondPoints.entrySet()) {
							Set<Translation> possibleTranslation = Translation.from(myProbe, secondPoint.getKey(),
									otherProbe, secondPoint.getValue());
							for (Translation translationCandidate : possibleTranslation) {
								if (getProbesTranslated(translationCandidate).stream().filter(other.probes::contains)
										.count() >= COMMON_PROBES) {
									return translationCandidate;
								}
							}
						}
					}
				}
			}
			return null;
		}

		private Map<Point, Point> findUniquePoints(Map<Point, Point> myDistancesMap,
				Map<Point, Point> otherDistancesMap, List<Point> commonDistances) {
			Set<Point> distinctCommonDistances = Stream
					.concat(myDistancesMap.values().stream(), otherDistancesMap.values().stream())
					.filter(commonDistances::contains)
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
					.filter(e -> e.getValue() == 2).map(Map.Entry::getKey).collect(Collectors.toSet());
			Map<Point, Point> points = new HashMap<>();
			for (Point distinctDistance : distinctCommonDistances) {
				Point mine = myDistancesMap.entrySet().stream().filter(e -> e.getValue().equals(distinctDistance))
						.map(Map.Entry::getKey).findAny().get();
				Point other = otherDistancesMap.entrySet().stream().filter(e -> e.getValue().equals(distinctDistance))
						.map(Map.Entry::getKey).findAny().get();
				points.put(mine, other);
			}
			return points;
		}

		public Set<Point> getProbesTranslated(Translation translation) {
			return probes.stream().map(translation).collect(Collectors.toSet());
		}

		public Set<Point> getProbes() {
			return probes;
		}
	}

	private enum CoordFunctions implements ToLongFunction<Point> {
		X {
			@Override
			public long applyAsLong(Point p) {
				return p.getX();
			}

			@Override
			public String toString() {
				return "x";
			}
		},
		Y {
			@Override
			public long applyAsLong(Point p) {
				return p.getY();
			}

			@Override
			public String toString() {
				return "y";
			}
		},
		Z {
			@Override
			public long applyAsLong(Point p) {
				return p.getZ();
			}

			@Override
			public String toString() {
				return "z";
			}
		},
		N_X {
			@Override
			public long applyAsLong(Point p) {
				return -p.getX();
			}

			@Override
			public String toString() {
				return "-x";
			}
		},
		N_Y {
			@Override
			public long applyAsLong(Point p) {
				return -p.getY();
			}

			@Override
			public String toString() {
				return "-y";
			}
		},
		N_Z {
			@Override
			public long applyAsLong(Point p) {
				return -p.getZ();
			}

			@Override
			public String toString() {
				return "-z";
			}
		};

		public CoordFunctions negate() {
			return negate(this);
		}

		public static CoordFunctions negate(CoordFunctions fun) {
			switch (fun) {
			case X:
				return N_X;
			case Y:
				return N_Y;
			case Z:
				return N_Z;
			case N_X:
				return X;
			case N_Y:
				return Y;
			case N_Z:
				return Z;
			default:
				throw new IllegalArgumentException(fun.toString());
			}
		}
	}

	private static class Translation implements UnaryOperator<Point> {
		private final CoordFunctions getX;
		private final CoordFunctions getY;
		private final CoordFunctions getZ;
		private final long xOffset;
		private final long yOffset;
		private final long zOffset;

		public Translation(CoordFunctions getX, long xOffset, CoordFunctions getY, long yOffset, CoordFunctions getZ,
				long zOffset) {
			this.getX = getX;
			this.xOffset = xOffset;
			this.getY = getY;
			this.yOffset = yOffset;
			this.getZ = getZ;
			this.zOffset = zOffset;
		}

		// a -> b
		public static Set<Translation> from(Point a1, Point a2, Point b1, Point b2) {
			long adx = a1.getX() - a2.getX();
			long ady = a1.getY() - a2.getY();
			long adz = a1.getZ() - a2.getZ();

			long bdx = b1.getX() - b2.getX();
			long bdy = b1.getY() - b2.getY();
			long bdz = b1.getZ() - b2.getZ();

			Set<Translation> matchingTranslations = new HashSet<>();
			// X Y Z
			if ((Math.abs(adx) == Math.abs(bdx)) && (Math.abs(ady) == Math.abs(bdy))
					&& (Math.abs(adz) == Math.abs(bdz))) {
				matchingTranslations.addAll(posNegCombinations(bdx, adx, bdy, ady, bdz, adz, CoordFunctions.X,
						CoordFunctions.Y, CoordFunctions.Z, a1, b1));
			}
			// X Z Y
			if ((Math.abs(adx) == Math.abs(bdx)) && (Math.abs(ady) == Math.abs(bdz))
					&& (Math.abs(adz) == Math.abs(bdy))) {
				matchingTranslations.addAll(posNegCombinations(bdx, adx, bdy, adz, bdz, ady, CoordFunctions.X,
						CoordFunctions.Z, CoordFunctions.Y, a1, b1));
			}
			// Y X Z
			if ((Math.abs(adx) == Math.abs(bdy)) && (Math.abs(ady) == Math.abs(bdx))
					&& (Math.abs(adz) == Math.abs(bdz))) {
				matchingTranslations.addAll(posNegCombinations(bdx, ady, bdy, adx, bdz, adz, CoordFunctions.Y,
						CoordFunctions.X, CoordFunctions.Z, a1, b1));
			}
			// Y Z X
			if ((Math.abs(adx) == Math.abs(bdy)) && (Math.abs(ady) == Math.abs(bdz))
					&& (Math.abs(adz) == Math.abs(bdx))) {
				matchingTranslations.addAll(posNegCombinations(bdx, adz, bdy, adx, bdz, ady, CoordFunctions.Z,
						CoordFunctions.X, CoordFunctions.Y, a1, b1));
			}
			// Z X Y
			if ((Math.abs(adx) == Math.abs(bdz)) && (Math.abs(ady) == Math.abs(bdx))
					&& (Math.abs(adz) == Math.abs(bdy))) {
				matchingTranslations.addAll(posNegCombinations(bdx, ady, bdy, adz, bdz, adx, CoordFunctions.Y,
						CoordFunctions.Z, CoordFunctions.X, a1, b1));
			}
			// Z Y X
			if ((Math.abs(adx) == Math.abs(bdz)) && (Math.abs(ady) == Math.abs(bdy))
					&& (Math.abs(adz) == Math.abs(bdx))) {
				matchingTranslations.addAll(posNegCombinations(bdx, adz, bdy, ady, bdz, adx, CoordFunctions.Z,
						CoordFunctions.Y, CoordFunctions.X, a1, b1));
			}
			matchingTranslations.stream().filter(t -> !t.apply(a1).equals(b1) || !t.apply(a2).equals(b2))
					.forEach(t -> System.out.println("ERROR: Translation " + t + " doesn't translate " + a1 + " -> "
							+ b1 + " and " + a2 + " -> " + b2));
			return matchingTranslations;
		}

		public Translation reversed() {
			CoordFunctions newGetX = null;
			CoordFunctions newGetY = null;
			CoordFunctions newGetZ = null;
			long newOffsetX = 0;
			long newOffsetY = 0;
			long newOffsetZ = 0;
			switch (getX) {
			case X:
				newGetX = CoordFunctions.X;
				newOffsetX = -xOffset;
				break;
			case N_X:
				newGetX = CoordFunctions.N_X;
				newOffsetX = xOffset;
				break;
			case Y:
				newGetY = CoordFunctions.X;
				newOffsetY = -xOffset;
				break;
			case N_Y:
				newGetY = CoordFunctions.N_X;
				newOffsetY = xOffset;
				break;
			case Z:
				newGetZ = CoordFunctions.X;
				newOffsetZ = -xOffset;
				break;
			case N_Z:
				newGetZ = CoordFunctions.N_X;
				newOffsetZ = xOffset;
				break;
			}
			switch (getY) {
			case X:
				newGetX = CoordFunctions.Y;
				newOffsetX = -yOffset;
				break;
			case N_X:
				newGetX = CoordFunctions.N_Y;
				newOffsetX = yOffset;
				break;
			case Y:
				newGetY = CoordFunctions.Y;
				newOffsetY = -yOffset;
				break;
			case N_Y:
				newGetY = CoordFunctions.N_Y;
				newOffsetY = yOffset;
				break;
			case Z:
				newGetZ = CoordFunctions.Y;
				newOffsetZ = -yOffset;
				break;
			case N_Z:
				newGetZ = CoordFunctions.N_Y;
				newOffsetZ = yOffset;
				break;
			}
			switch (getZ) {
			case X:
				newGetX = CoordFunctions.Z;
				newOffsetX = -zOffset;
				break;
			case N_X:
				newGetX = CoordFunctions.N_Z;
				newOffsetX = zOffset;
				break;
			case Y:
				newGetY = CoordFunctions.Z;
				newOffsetY = -zOffset;
				break;
			case N_Y:
				newGetY = CoordFunctions.N_Z;
				newOffsetY = zOffset;
				break;
			case Z:
				newGetZ = CoordFunctions.Z;
				newOffsetZ = -zOffset;
				break;
			case N_Z:
				newGetZ = CoordFunctions.N_Z;
				newOffsetZ = zOffset;
				break;
			}
			return new Translation(newGetX, newOffsetX, newGetY, newOffsetY, newGetZ, newOffsetZ);
		}

		private static Set<Translation> posNegCombinations(long adx, long bdx, long ady, long bdy, long adz, long bdz,
				CoordFunctions toX, CoordFunctions toY, CoordFunctions toZ, Point a, Point b) {
			Set<Translation> matchingTranslations = new HashSet<>();
			if ((adx == bdx) && (ady == bdy) && (adz == bdz)) {
				long ox = toX.applyAsLong(a) - b.getX();
				long oy = toY.applyAsLong(a) - b.getY();
				long oz = toZ.applyAsLong(a) - b.getZ();
				matchingTranslations.add(new Translation(toX, -ox, toY, -oy, toZ, -oz));
			}
			if ((adx == bdx) && (ady == bdy) && (adz == -bdz)) {
				long ox = toX.applyAsLong(a) - b.getX();
				long oy = toY.applyAsLong(a) - b.getY();
				long oz = -toZ.negate().applyAsLong(a) + b.getZ();
				matchingTranslations.add(new Translation(toX, -ox, toY, -oy, toZ.negate(), oz));
			}
			if ((adx == bdx) && (ady == -bdy) && (adz == bdz)) {
				long ox = toX.applyAsLong(a) - b.getX();
				long oy = -toY.negate().applyAsLong(a) + b.getY();
				long oz = toZ.applyAsLong(a) - b.getZ();
				matchingTranslations.add(new Translation(toX, -ox, toY.negate(), oy, toZ, -oz));
			}
			if ((adx == bdx) && (ady == -bdy) && (adz == -bdz)) {
				long ox = toX.applyAsLong(a) - b.getX();
				long oy = -toY.negate().applyAsLong(a) + b.getY();
				long oz = -toZ.negate().applyAsLong(a) + b.getZ();
				matchingTranslations.add(new Translation(toX, -ox, toY.negate(), oy, toZ.negate(), oz));
			}
			if ((adx == -bdx) && (ady == bdy) && (adz == bdz)) {
				long ox = -toX.negate().applyAsLong(a) + b.getX();
				long oy = toY.applyAsLong(a) - b.getY();
				long oz = toZ.applyAsLong(a) - b.getZ();
				matchingTranslations.add(new Translation(toX.negate(), ox, toY, -oy, toZ, -oz));
			}
			if ((adx == -bdx) && (ady == bdy) && (adz == -bdz)) {
				long ox = -toX.negate().applyAsLong(a) + b.getX();
				long oy = toY.applyAsLong(a) - b.getY();
				long oz = -toZ.negate().applyAsLong(a) + b.getZ();
				matchingTranslations.add(new Translation(toX.negate(), ox, toY, -oy, toZ.negate(), oz));
			}
			if ((adx == -bdx) && (ady == -bdy) && (adz == bdz)) {
				long ox = -toX.negate().applyAsLong(a) + b.getX();
				long oy = -toY.negate().applyAsLong(a) + b.getY();
				long oz = toZ.applyAsLong(a) - b.getZ();
				matchingTranslations.add(new Translation(toX.negate(), ox, toY.negate(), oy, toZ, -oz));
			}
			if ((adx == -bdx) && (ady == -bdy) && (adz == -bdz)) {
				long ox = -toX.negate().applyAsLong(a) + b.getX();
				long oy = -toY.negate().applyAsLong(a) + b.getY();
				long oz = -toZ.negate().applyAsLong(a) + b.getZ();
				matchingTranslations.add(new Translation(toX.negate(), ox, toY.negate(), oy, toZ.negate(), oz));
			}
			return matchingTranslations;
		}

		public Point apply(Point p) {
			return new Point(getX.applyAsLong(p) + xOffset, getY.applyAsLong(p) + yOffset,
					getZ.applyAsLong(p) + zOffset);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((getX == null) ? 0 : getX.hashCode());
			result = prime * result + ((getY == null) ? 0 : getY.hashCode());
			result = prime * result + ((getZ == null) ? 0 : getZ.hashCode());
			result = prime * result + (int) (xOffset ^ (xOffset >>> 32));
			result = prime * result + (int) (yOffset ^ (yOffset >>> 32));
			result = prime * result + (int) (zOffset ^ (zOffset >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Translation other = (Translation) obj;
			if (getX != other.getX)
				return false;
			if (getY != other.getY)
				return false;
			if (getZ != other.getZ)
				return false;
			if (xOffset != other.xOffset)
				return false;
			if (yOffset != other.yOffset)
				return false;
			if (zOffset != other.zOffset)
				return false;
			return true;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getX);
			sb.append(" ");
			if (xOffset != 0) {
				sb.append(xOffset > 0 ? "+" : "-");
				sb.append(" ");
				sb.append(Math.abs(xOffset));
			}
			sb.append("\t");
			sb.append(getY);
			sb.append(" ");
			if (yOffset != 0) {
				sb.append(yOffset > 0 ? "+" : "-");
				sb.append(" ");
				sb.append(Math.abs(yOffset));
			}
			sb.append("\t");
			sb.append(getZ);
			sb.append(" ");
			if (zOffset != 0) {
				sb.append(zOffset > 0 ? "+" : "-");
				sb.append(" ");
				sb.append(Math.abs(zOffset));
			}

			return sb.toString();
		}
	}
}
