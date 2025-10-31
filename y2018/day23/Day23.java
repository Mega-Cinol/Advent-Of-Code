package y2018.day23;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;
import common.Point;

public class Day23 {
//	private static final Map<Point, Set<NanoBot>> IN_RANGE = new HashMap<>();

	public static void main(String[] args) {
		List<NanoBot> bots = Input.parseLines("y2018/day23/day23.txt", NanoBot::fromString)
				.collect(Collectors.toList());
		NanoBot max = bots.stream().max(Comparator.comparingInt(NanoBot::getR)).get();
		System.out.println(
				bots.stream().filter(b -> b.getPos().getManhattanDistance(max.getPos()) <= max.getR()).count());

		Map<NanoBot, Set<Integer>> overlapping = new HashMap<>();
		for (int i = 0 ; i < bots.size() - 1; i++)
		{
			for (int j = i + 1 ; j < bots.size() ; j++)
			{
				if (bots.get(i).getPos().getManhattanDistance(bots.get(j).getPos()) <= bots.get(i).getR()
						+ bots.get(j).getR()) {
					overlapping.computeIfAbsent(bots.get(i), k -> new HashSet<>()).add(j);
					overlapping.computeIfAbsent(bots.get(j), k -> new HashSet<>()).add(i);
				}
			}
		}
		Set<NanoBot> commonPoints = new HashSet<>();
		bots.stream().sorted(Comparator.comparing(bot -> overlapping.get(bot).size()).reversed()).forEach(bot -> {
			int idx = bots.indexOf(bot);
			if (commonPoints.stream().map(overlapping::get).allMatch(s -> s.contains(idx))) {
				commonPoints.add(bot);
			}
		});
		System.out.println(commonPoints.size());

//		long x = 0;
//		long y = 0;
//		long z = 0;
//		for (NanoBot b : commonPoints) {
//			Point p = b.getPos();
//			x += p.getX();
//			y += p.getY();
//			z += p.getZ();
//		}
//		Point mid = new Point(x / 985, y / 985, z / 985)
//				.move(-17_836_725, 11_875_000, 12_000_000)
//				.move(-420_000, -420_000, -420_000)
//				.move(0, 100_000, -200_000)
//				.move(0, 0, 76_292)
//				.move(-30_000, 30_000, 0)
//				.move(-236_000, 236_000, 26_000)
//				.move(0, 36_479, 0)
//				.move(-10_000, 10_000, -10_000);
		System.out.println(22698921 + 59279594 + 11772355);
		Point mid = new Point(22698921, 59279594, 11772355);
		System.out.println(numToStr(mid.getX()) + "\t" + numToStr(mid.getY()) + "\t" + numToStr(mid.getZ()));
		System.out.println(botsInRange(bots, mid).size());
		commonPoints.stream().filter(b -> b.getPos().getManhattanDistance(mid) > b.getR())
				.sorted(Comparator.comparing(b -> b.getR() - b.getPos().getManhattanDistance(mid)))
				.map(b -> b + "\t" + numToStr(b.getPos().getManhattanDistance(mid) - b.getR()))
				.forEach(System.out::println);
//		System.out.println("==============================");
//		commonPoints.stream().filter(b -> b.getPos().getManhattanDistance(mid) <= b.getR())
//				.filter(b -> b.getPos().getManhattanDistance(mid) + 644_999 > b.getR())
//				.sorted(Comparator.comparing(b -> b.getR() - b.getPos().getManhattanDistance(mid)))
//				.map(b -> b + "\t" + numToStr(b.getPos().getManhattanDistance(mid) - b.getR()))
//				.forEach(System.out::println);
		System.out.println(botsInRange(bots, mid).size());
		long rr = 1;

		int bestPointValue = 0;
		Point bestPoint = null;
//
		for (long xx = -rr ; xx <= rr ; xx++)
		{
//			System.out.println(xx);
			for (long yy = -rr + Math.abs(xx) ; yy <= rr - Math.abs(xx) ; yy++)
			{
				for (long zz = -rr + Math.abs(xx) + Math.abs(yy) ; zz <= rr - Math.abs(xx) - Math.abs(yy) ; zz++)
				{
//					long zz = -rr + Math.abs(xx) + Math.abs(yy);
					System.out.println(new Point(xx,yy,zz));
					Point pointToTest = new Point(mid.getX() + xx, mid.getY() + yy, mid.getZ() + zz);
					int testValue = botsInRange(bots, pointToTest).size();
					if ((testValue > bestPointValue) || ((testValue == bestPointValue) && (bestPoint == null))) 
					{
						bestPointValue = testValue;
//						System.out.println(bestPointValue);
						bestPoint = pointToTest;
					} else if (testValue == bestPointValue)
					{
						if (new Point(0,0,0).getManhattanDistance(pointToTest) < new Point(0,0,0).getManhattanDistance(bestPoint))
						{
							bestPointValue = testValue;
							bestPoint = pointToTest;
						}
					}
				}
			}
		}
		System.out.println(bestPointValue);
		System.out.println(bestPoint);

//		for (NanoBot bot : commonPoints)
//		{
//			for (NanoBot other : commonPoints)
//			{
//				if (bot.getPos().getManhattanDistance(other.getPos()) == bot.getR() + other.getR())
//				{
//					System.out.println(bot);
//					System.out.println(other);
//					System.out.println("=========");
//				}
//			}
//		}

		// pos=<(-12375334, 19041787, 14636248)>, r=78175955
//	    pos=<( 56291035, 63601362, -5724640)>, r=55410877
//		Point pos1 = new Point(-12375334, 19041787, 14636248);
//		Point pos2 = new Point(56291035, 63601362, -5724640);
//		int bestPointValue = 0;
//		Point bestPoint = null;
//		for (long r = 0 ; r <= 55410877 ; r++)
//		{
//			if (r % 100000 == 0)
//			{
//				System.out.println(r);
//			}
//			Point pointToTest = pos2.move(-r, 0, 55410877 - r);
//			if (pointToTest.getManhattanDistance(pos1) > 78175955)
//			{
//				continue;
//			}
//			int testValue = botsInRange(bots, pointToTest).size();
//			if ((testValue > bestPointValue) || ((testValue == bestPointValue) && (bestPoint == null))) 
//			{
//				bestPointValue = testValue;
//				bestPoint = pointToTest;
//			} else if (testValue == bestPointValue)
//			{
//				if (new Point(0,0,0).getManhattanDistance(pointToTest) < new Point(0,0,0).getManhattanDistance(bestPoint))
//				{
//					bestPointValue = testValue;
//					bestPoint = pointToTest;
//				}
//			}
//			pointToTest = pos1.move(0, r, r - 55410877);
//			testValue = botsInRange(bots, pointToTest).size();
//			if ((testValue > bestPointValue) || ((testValue == bestPointValue) && (bestPoint == null))) 
//			{
//				bestPointValue = testValue;
//				bestPoint = pointToTest;
//			} else if (testValue == bestPointValue)
//			{
//				if (new Point(0,0,0).getManhattanDistance(pointToTest) < new Point(0,0,0).getManhattanDistance(bestPoint))
//				{
//					bestPointValue = testValue;
//					bestPoint = pointToTest;
//				}
//			}
//		}
//		System.out.println(bestPointValue);
//		System.out.println(bestPoint);

//		long minX = commonPoints.stream().map(bot -> bot.getPos().getX() - bot.getR()).max(Long::compareTo).get();
//		long maxX = commonPoints.stream().map(bot -> bot.getPos().getX() + bot.getR()).min(Long::compareTo).get();
//		long minY = commonPoints.stream().map(bot -> bot.getPos().getY() - bot.getR()).max(Long::compareTo).get();
//		long maxY = commonPoints.stream().map(bot -> bot.getPos().getY() + bot.getR()).min(Long::compareTo).get();
//		long minZ = commonPoints.stream().map(bot -> bot.getPos().getZ() - bot.getR()).max(Long::compareTo).get();
//		long maxZ = commonPoints.stream().map(bot -> bot.getPos().getZ() + bot.getR()).min(Long::compareTo).get();
//		long cnt = 0;
//		for (long x = minX ; x <= maxX ; x++)
//		{
//			for (long y = minY ; y <= maxY ; y++)
//			{
//				cnt++;
//			}
//		}
//		System.out.println(cnt);

//		Point bestPoint = null;
//		int bestPointValue = 0;
//		for (int i = 0; i < bots.size(); i++) {
//			System.out.println(i);
//			for (int j = 0; j < bots.size(); j++) {
//				if (i == j) {
//					continue;
//				}
//				NanoBot edgeBot = bots.get(i);
//				NanoBot surfaceBot = bots.get(j);
//				if (edgeBot.getPos().getManhattanDistance(surfaceBot.getPos()) >= edgeBot.getR() + surfaceBot.getR()) {
//					continue;
//				}
//				Point topNode = edgeBot.getPos().move(0, 0, edgeBot.getR());
//				boolean topNodeInRange = pointInRange(topNode, surfaceBot);
//				Point bottomNode = edgeBot.getPos().move(0, 0, -edgeBot.getR());
//				boolean bottomNodeInRange = pointInRange(bottomNode, surfaceBot);
//				Point frontLeftNode = edgeBot.getPos().move(edgeBot.getR(), 0, 0);
//				boolean frontLeftNodeInRange = pointInRange(frontLeftNode, surfaceBot);
//				Point backLeftNode = edgeBot.getPos().move(-edgeBot.getR(), 0, 0);
//				boolean backLeftNodeInRange = pointInRange(backLeftNode, surfaceBot);
//				Point frontRightNode = edgeBot.getPos().move(0, edgeBot.getR(), 0);
//				boolean frontRightNodeInRange = pointInRange(frontRightNode, surfaceBot);
//				Point backRightNode = edgeBot.getPos().move(0, -edgeBot.getR(), 0);
//				boolean backRightNodeInRange = pointInRange(backRightNode, surfaceBot);
//				Set<Point> pointsToTest = new HashSet<>();
//				pointsToTest.add(topNode);
//				pointsToTest.add(bottomNode);
//				pointsToTest.add(frontLeftNode);
//				pointsToTest.add(frontRightNode);
//				pointsToTest.add(backLeftNode);
//				pointsToTest.add(backRightNode);
//				if (topNodeInRange ^ frontLeftNodeInRange) {
//					pointsToTest.add(lastInRange(surfaceBot, topNode, frontLeftNode));
//				}
//				if (topNodeInRange ^ frontRightNodeInRange) {
//					pointsToTest.add(lastInRange(surfaceBot, topNode, frontRightNode));
//				}
//				if (topNodeInRange ^ backLeftNodeInRange) {
//					pointsToTest.add(lastInRange(surfaceBot, topNode, backLeftNode));
//				}
//				if (topNodeInRange ^ backRightNodeInRange) {
//					pointsToTest.add(lastInRange(surfaceBot, topNode, backRightNode));
//				}
//				if (bottomNodeInRange ^ frontLeftNodeInRange) {
//					pointsToTest.add(lastInRange(surfaceBot, bottomNode, frontLeftNode));
//				}
//				if (bottomNodeInRange ^ frontRightNodeInRange) {
//					pointsToTest.add(lastInRange(surfaceBot, bottomNode, frontRightNode));
//				}
//				if (bottomNodeInRange ^ backLeftNodeInRange) {
//					pointsToTest.add(lastInRange(surfaceBot, bottomNode, backLeftNode));
//				}
//				if (bottomNodeInRange ^ backRightNodeInRange) {
//					pointsToTest.add(lastInRange(surfaceBot, bottomNode, backRightNode));
//				}
//				for (Point pointToTest : pointsToTest)
//				{
//					int testValue = botsInRange(bots, pointToTest).size();
//					if ((testValue > bestPointValue) || ((testValue == bestPointValue) && (bestPoint == null))) 
//					{
//						bestPointValue = testValue;
//						bestPoint = pointToTest;
//					} else if (testValue == bestPointValue)
//					{
//						if (new Point(0,0,0).getManhattanDistance(pointToTest) < new Point(0,0,0).getManhattanDistance(bestPoint))
//						{
//							bestPointValue = testValue;
//							bestPoint = pointToTest;
//						}
//					}
//				}
//			}
//		}
//		System.out.println(bestPoint + " with value " + bestPointValue);
//		System.out.println(bestPoint.getX() + bestPoint.getY() + bestPoint.getZ());
//		System.out.println(botsInRange(bots, bestPoint.move(1,0,0)).size());
//		System.out.println(botsInRange(bots, bestPoint.move(0,1,0)).size());
//		System.out.println(botsInRange(bots, bestPoint.move(0,0,1)).size());
//		System.out.println(botsInRange(bots, bestPoint.move(0,1,1)).size());
//		System.out.println(botsInRange(bots, bestPoint.move(1,0,1)).size());
//		System.out.println(botsInRange(bots, bestPoint.move(1,1,0)).size());
//		System.out.println(botsInRange(bots, bestPoint.move(1,1,1)).size());

//		Set<Point> botLocatinos = bots.stream().map(NanoBot::getPos).collect(Collectors.toSet());
//		long minX = Point.minX(botLocatinos);
//		long maxX = Point.maxX(botLocatinos);
//		long minY = Point.minY(botLocatinos);
//		long maxY = Point.maxY(botLocatinos);
//		long minZ = Point.minZ(botLocatinos);
//		long maxZ = Point.maxZ(botLocatinos);
//		Set<Area> areas = findAreas(new Point(minX, minY, minZ), new Point(maxX, maxY, maxZ), 10_000_000, bots);
//		System.out.println(areas.size());
//		areas = areas.stream().map(a -> findAreas(a.topLeft, a.bottomRight, 5_000_000, bots)).flatMap(Set::stream)
//				.collect(Collectors.toSet());
//		areas = filterAreas(areas);
//		System.out.println(areas.size());
//		areas = areas.stream().map(a -> findAreas(a.topLeft, a.bottomRight, 2_500_000, bots)).flatMap(Set::stream)
//				.collect(Collectors.toSet());
//		areas = filterAreas(areas);
//		System.out.println(areas.size());
//		areas = areas.stream().map(a -> findAreas(a.topLeft, a.bottomRight, 1_250_000, bots)).flatMap(Set::stream)
//				.collect(Collectors.toSet());
//		areas = filterAreas(areas);
//		System.out.println(areas.size());
//		areas = areas.stream().map(a -> findAreas(a.topLeft, a.bottomRight, 625_000, bots)).flatMap(Set::stream)
//				.collect(Collectors.toSet());
//		areas = filterAreas(areas);
//		System.out.println(areas.size());
//		areas = areas.stream().map(a -> findAreas(a.topLeft, a.bottomRight, 300_000, bots)).flatMap(Set::stream)
//				.collect(Collectors.toSet());
//		areas = filterAreas(areas);
//		System.out.println(areas.size());
	}

	private static String numToStr(long num) {
		String sgn = "";
		if (num < 0) {
			sgn = "-";
			num *= -1;
		}
		String str = "";
		do {
			int part = (int) (num % 1000);
			String pre = "";
			if (part < 100) {
				pre = "0";
			}
			if (part < 10) {
				pre += "0";
			}
			str = pre + part + "_" + str;
			num /= 1000;
		} while (num > 0);
		return sgn + str.substring(0, str.length() - 1).replaceAll("^0*", "");
	}

//	private static Set<Area> findAreas(Point from, Point to, long step, List<NanoBot> bots) {
//		Set<Area> areas = new HashSet<>();
//		for (long x = from.getX(); x <= to.getX(); x += step) {
////			System.out.println((x - from.getX()) * 100 / (to.getX() - from.getX()) + "%");
//			for (long y = from.getY(); y <= to.getY(); y += step) {
//				for (long z = from.getZ(); z <= to.getZ(); z += step) {
//					Area area = new Area();
//					areas.add(area);
//					area.topLeft = new Point(x, y, z);
//					area.bottomRight = new Point(x + step, y + step, z + step);
//					Set<NanoBot> topLeft = botsInRange(bots, new Point(x + step, y, z));
//					Set<NanoBot> bottomLeft = botsInRange(bots, new Point(x, y, z));
//					Set<NanoBot> topRight = botsInRange(bots, new Point(x + step, y + step, z));
//					Set<NanoBot> bottomRight = botsInRange(bots, new Point(x, y + step, z));
//					Set<NanoBot> topLeftBack = botsInRange(bots, new Point(x + step, y, z + step));
//					Set<NanoBot> bottomLeftBack = botsInRange(bots, new Point(x, y, z + step));
//					Set<NanoBot> topRightBack = botsInRange(bots, new Point(x + step, y + step, z + step));
//					Set<NanoBot> bottomRightBack = botsInRange(bots, new Point(x, y + step, z + step));
//					area.min = Math.max(
//							Math.max(Math.max(topLeft.size(), bottomLeft.size()),
//									Math.max(topRight.size(), bottomRight.size())),
//							Math.max(Math.max(topLeftBack.size(), bottomLeftBack.size()),
//									Math.max(topRightBack.size(), bottomRightBack.size())));
//					Set<NanoBot> maxSet = new HashSet<>();
//					maxSet.addAll(topLeft);
//					maxSet.addAll(bottomLeft);
//					maxSet.addAll(topRight);
//					maxSet.addAll(bottomRight);
//					maxSet.addAll(topLeftBack);
//					maxSet.addAll(bottomLeftBack);
//					maxSet.addAll(topRightBack);
//					maxSet.addAll(bottomRightBack);
//					area.max = maxSet.size();
//				}
//			}
//		}
//		int maxMax = 0;
//		Area maxArea = null;
//		for (Area a : areas) {
//			if (a.max > maxMax) {
//				maxArea = a;
//				maxMax = a.max;
//			}
//			if (a.max == maxMax && maxArea != null && a.min > maxArea.min) {
//				maxArea = a;
//				maxMax = a.max;
//			}
//		}
//		Area maxAreaFinal = maxArea;
//		return areas.stream().filter(a -> a.max > maxAreaFinal.min).collect(Collectors.toSet());
//	}
//
//	private static Point lastInRange(NanoBot bot, Point point1, Point point2) {
//		Point inRange = pointInRange(point1, bot) ? point1 : point2;
//		Point outOfRange = inRange.equals(point1) ? point2 : point1;
//		long x = inRange.getX() == outOfRange.getX() ? 0 : inRange.getX() > outOfRange.getX() ? -1 : 1;
//		long y = inRange.getY() == outOfRange.getY() ? 0 : inRange.getY() > outOfRange.getY() ? -1 : 1;
//		long z = inRange.getZ() == outOfRange.getZ() ? 0 : inRange.getZ() > outOfRange.getZ() ? -1 : 1;
//		Point stepOut = new Point(x, y, z);
//		Point previous = inRange;
//		Point mid = mid(inRange, outOfRange);
//		while (!mid.equals(previous)) {
//			previous = mid;
//			if (pointInRange(mid, bot)) {
//				inRange = mid;
//				mid = mid(mid, outOfRange);
//			} else {
//				outOfRange = mid;
//				mid = mid(mid, inRange);
//			}
//		}
//		while (!pointInRange(mid, bot)) {
//			mid = mid.move(stepOut.negate());
//		}
//		while (pointInRange(mid, bot)) {
//			mid = mid.move(stepOut);
//		}
//		return mid;
//	}

//	private static Point mid(Point p1, Point p2) {
//		long step = 0;
//		if (p1.getX() == p2.getX()) {
//			step = Math.abs(p1.getY() - p2.getY()) / 2;
//		} else {
//			step = Math.abs(p1.getX() - p2.getX()) / 2;
//		}
//		long x = p1.getX() > p2.getX() ? p1.getX() - step : p1.getX() + step;
//		x = p1.getX() == p2.getX() ? p1.getX() : x;
//		long y = p1.getY() > p2.getY() ? p1.getY() - step : p1.getY() + step;
//		y = p1.getY() == p2.getY() ? p1.getY() : y;
//		long z = p1.getZ() > p2.getZ() ? p1.getZ() - step : p1.getZ() + step;
//		z = p1.getZ() == p2.getZ() ? p1.getZ() : z;
//		return new Point(x, y, z);
//	}
//
//	private static Set<Area> filterAreas(Set<Area> input) {
//		int maxMax = 0;
//		Area maxArea = null;
//		for (Area a : input) {
//			if (a.max > maxMax) {
//				maxArea = a;
//				maxMax = a.max;
//			}
//			if (a.max == maxMax && maxArea != null && a.min > maxArea.min) {
//				maxArea = a;
//				maxMax = a.max;
//			}
//		}
//		Area maxAreaFinal = maxArea;
//		System.out.println(maxAreaFinal.min + " " + maxAreaFinal.max);
//		return input.stream().filter(a -> a.max > maxAreaFinal.min).filter(a -> a.max > 949)
//				.collect(Collectors.toSet());
//	}

	private static boolean pointInRange(Point p, NanoBot bot) {
		return p.getManhattanDistance(bot.getPos()) <= bot.getR();
	}

	private static Set<NanoBot> botsInRange(List<NanoBot> bots, Point point) {
		return bots.stream().filter(bot -> pointInRange(point, bot)).collect(Collectors.toSet());
//		return IN_RANGE.computeIfAbsent(point,
//				p -> bots.stream().filter(bot -> pointInRange(p, bot)).collect(Collectors.toSet()));
	}

	private static class NanoBot {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((pos == null) ? 0 : pos.hashCode());
			result = prime * result + r;
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
			NanoBot other = (NanoBot) obj;
			if (pos == null) {
				if (other.pos != null)
					return false;
			} else if (!pos.equals(other.pos))
				return false;
			if (r != other.r)
				return false;
			return true;
		}

		private final Point pos;
		private final int r;

		private NanoBot(Point p, int r) {
			pos = p;
			this.r = r;
		}

		public Point getPos() {
			return pos;
		}

		public int getR() {
			return r;
		}

		@Override
		public String toString() {
			return "pos=<" + numToStr(pos.getX()) + ",\t" + numToStr(pos.getY()) + ",\t" + numToStr(pos.getZ())
					+ ">,\tr=" + numToStr(r);
		}

		public static NanoBot fromString(String str) {
			Pattern p = Pattern.compile("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(-?\\d+)");
			Matcher m = p.matcher(str);
			if (!m.matches()) {
				throw new IllegalArgumentException(str);
			}
			return new NanoBot(
					new Point(Long.parseLong(m.group(1)), Long.parseLong(m.group(2)), Long.parseLong(m.group(3))),
					Integer.parseInt(m.group(4)));
		}
	}

//	private static class Area {
//		private Point topLeft;
//		private Point bottomRight;
//		private int min;
//		private int max;
//	}
}
