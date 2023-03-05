package y2017.day20;

import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;

public class Day20 {

	public static void main(String[] args) {
//		List<Long> mins = Input.parseLines("y2017/day20/day20.txt", Day20::particleToDistance)
//				.collect(Collectors.toList());
//		long min = Long.MAX_VALUE;
//		int minI = -1;
//		System.out.println(mins);
//		for (int i = 0; i < mins.size(); i++) {
//			if (mins.get(i) < min) {
//				min = mins.get(i);
//				minI = i;
//				System.out.println(minI + " = " + min);
//			}
//		}
//		System.out.println(minI);
//		System.out.println(min);
		List<Particle> ps = Input.parseLines("y2017/day20/day20.txt", Particle::new).collect(Collectors.toList());
		for (int step = 0; step < 10000; step++) {
			NavigableSet<Integer> idxToRemove = new TreeSet<>();
			for (int i = ps.size() - 1; i >= 0; i--) {
				for (int j = i - 1; j >= 0; j--) {
					if (ps.get(i).collide(ps.get(j))) {
						idxToRemove.add(i);
						idxToRemove.add(j);
					}
				}
			}
			if (!idxToRemove.isEmpty()) {
				Integer idx = idxToRemove.last();
				while (idx != null) {
					ps.remove(idx.intValue());
					idx = idxToRemove.lower(idx);
				}
			}
			for (int i = ps.size() - 1; i >= 0; i--) {
				ps.get(i).step();
			}
			System.out.println(ps.size());
		}
	}

	private static class Particle {
		private long x;
		private long y;
		private long z;
		private long vx;
		private long vy;
		private long vz;
		private long ax;
		private long ay;
		private long az;

		public Particle(String desc) {
			Pattern p = Pattern.compile(
					"p=<(-?\\d+),(-?\\d+),(-?\\d+)>, v=<(-?\\d+),(-?\\d+),(-?\\d+)>, a=<(-?\\d+),(-?\\d+),(-?\\d+)>");
			Matcher m = p.matcher(desc);
			m.matches();
			x = Integer.parseInt(m.group(1));
			y = Integer.parseInt(m.group(2));
			z = Integer.parseInt(m.group(3));
			vx = Integer.parseInt(m.group(4));
			vy = Integer.parseInt(m.group(5));
			vz = Integer.parseInt(m.group(6));
			ax = Integer.parseInt(m.group(7));
			ay = Integer.parseInt(m.group(8));
			az = Integer.parseInt(m.group(9));
		}

//		public long dist() {
//			return Math.abs(x) + Math.abs(y) + Math.abs(z);
//		}

		public boolean collide(Particle other) {
			return x == other.x && y == other.y && z == other.z;
		}

		public void step() {
			vx += ax;
			vy += ay;
			vz += az;
			x += vx;
			y += vy;
			z += vz;
		}
	}

//	private static long particleToDistance(String desc) {
//		Pattern p = Pattern.compile(
//				"p=<(-?\\d+),(-?\\d+),(-?\\d+)>, v=<(-?\\d+),(-?\\d+),(-?\\d+)>, a=<(-?\\d+),(-?\\d+),(-?\\d+)>");
//		Matcher m = p.matcher(desc);
//		m.matches();
//
//		// p0 + v0 * t + a * t * ( t + 1) / 2 = 0
//		// 2p0 + 2tv0 + at(t + 1) = 0
//		// at^2 + t(2v0 + a) + 2p0 = 0
//		// d = (2v0 + a)^2 - 8ap0
////		Map<Range, BiNominal> sum = new HashMap<>();
////		for (int dim = 0; dim < 3; dim++) {
////			int p0 = Integer.parseInt(m.group(1 + dim));
////			int v0 = Integer.parseInt(m.group(4 + dim));
////			int acc = Integer.parseInt(m.group(7 + dim));
////
////			int a = acc;
////			int b = 2 * v0 + acc;
////			int c = 2 * p0;
////			BiNominal dimBin = new BiNominal();
////			dimBin.a = a;
////			dimBin.b = b;
////			dimBin.c = c;
////			if (sum.isEmpty()) {
////				sum.putAll(dimBin.abs());
////			} else {
////				Map<Range, BiNominal> tmp = new HashMap<Day20.Range, Day20.BiNominal>(sum);
////				sum.clear();
////				sum.putAll(BiNominal.sum(tmp, dimBin.abs()));
////			}
////		}
////		System.out.println(BiNominal.getMin(sum));
////		List<Integer> fromBin = IntStream.range(0, 1001).map(x -> BiNominal.valuOfRanged(sum, x)).mapToObj(Integer::new)
////				.collect(Collectors.toList());
////		System.out.println(fromBin);
//		long x = Integer.parseInt(m.group(1));
//		long y = Integer.parseInt(m.group(2));
//		long z = Integer.parseInt(m.group(3));
//		long vx = Integer.parseInt(m.group(4));
//		long vy = Integer.parseInt(m.group(5));
//		long vz = Integer.parseInt(m.group(6));
//		long ax = Integer.parseInt(m.group(7));
//		long ay = Integer.parseInt(m.group(8));
//		long az = Integer.parseInt(m.group(9));
//		long minD = Long.MAX_VALUE;
//		for (int i = 0; i < 1001; i++) {
//			long d = Math.abs(x) + Math.abs(y) + Math.abs(z);
//			if (d < minD) {
//				minD = d;
//			}
//			vx += ax;
//			vy += ay;
//			vz += az;
//			x += vx;
//			y += vy;
//			z += vz;
//		}
//		return minD;
////		System.out.println(oldWay);
////		for (int i = 0 ; i < fromBin.size() ; i++)
////		{
////			if (!fromBin.get(i).equals(oldWay.get(i)))
////			{
////				System.out.println(i);
////			}
////		}
////		return BiNominal.getMin(sum);
//	}

//	private static class Range {
//		public Integer min;
//		public Integer max;
//
//		@Override
//		public String toString() {
//			StringBuilder sb = new StringBuilder("(");
//			if (min == null) {
//				sb.append("-inf");
//			} else {
//				sb.append(min);
//			}
//			sb.append(";");
//			if (max == null) {
//				sb.append("inf");
//			} else {
//				sb.append(max);
//			}
//			sb.append(")");
//			return sb.toString();
//		}
//	}

//	private static class BiNominal {
//		public int a;
//		public int b;
//		public int c;
//
//		public int valueOf(int x) {
//			return (a * x * x + b * x + c) / 2;
//		}
//
//		@Override
//		public String toString() {
//			StringBuilder sb = new StringBuilder("y = ");
//			sb.append(a);
//			sb.append("x^2 ");
//			if (b > 0) {
//				sb.append(" + " + b + "x");
//			} else if (b < 0) {
//				sb.append(" - " + Math.abs(b) + "x");
//			}
//			if (c > 0) {
//				sb.append(" + " + c);
//			} else if (c < 0) {
//				sb.append(" - " + Math.abs(c));
//			}
//			return sb.toString();
//		}
//
//		public Map<Range, BiNominal> abs() {
//			Map<Range, BiNominal> abs = new HashMap<>();
//			if (a != 0) {
//				int delta = b * b - 4 * a * c;
//				if (delta <= 0) {
//					if ((c > 0) || (a + b + c > 0)) {
//						abs.put(new Range(), this);
//					} else {
//						BiNominal reversed = new BiNominal();
//						reversed.a = -a;
//						reversed.b = -b;
//						reversed.c = -c;
//						abs.put(new Range(), reversed);
//					}
//				} else {
//					int x0 = (int) (-b - Math.sqrt(delta)) / (2 * a);
//					int x1 = (int) (-b + Math.sqrt(delta)) / (2 * a);
//
//					int lowerX = Math.min(x0, x1);
//					int beforeLX = valueOf(lowerX - 1);
//					int atX = valueOf(lowerX);
//					int firstRangeMax = lowerX - 1;
//					if (beforeLX * atX >= 0) {
//						firstRangeMax = lowerX;
//					}
//					int higherX = Math.max(x0, x1);
//					int beforeHX = valueOf(higherX - 1);
//					atX = valueOf(higherX);
//					int secondRangeMax = higherX - 1;
//					if (beforeHX * atX >= 0) {
//						secondRangeMax = higherX;
//					}
//					BiNominal reversed = new BiNominal();
//					reversed.a = -a;
//					reversed.b = -b;
//					reversed.c = -c;
//					Range first = new Range();
//					first.max = firstRangeMax;
//					Range second = new Range();
//					second.min = firstRangeMax + 1;
//					second.max = secondRangeMax;
//					Range third = new Range();
//					third.min = secondRangeMax + 1;
//					if (beforeLX > 0) {
//						abs.put(first, this);
//						abs.put(second, reversed);
//						abs.put(third, this);
//					} else {
//						abs.put(first, reversed);
//						abs.put(second, this);
//						abs.put(third, reversed);
//					}
//				}
//			} else if (b != 0) {
//				int x0 = -1 * c / b;
//				int beforeX = valueOf(x0 - 1);
//				int atX = valueOf(x0);
//				int rangeMax = x0 - 1;
//				if (beforeX * atX >= 0) {
//					rangeMax = x0;
//				}
//				BiNominal reversed = new BiNominal();
//				reversed.a = -a;
//				reversed.b = -b;
//				reversed.c = -c;
//				Range low = new Range();
//				low.max = rangeMax;
//				Range high = new Range();
//				high.min = rangeMax + 1;
//				if (beforeX < 0) {
//					abs.put(low, reversed);
//					abs.put(high, this);
//				} else {
//					abs.put(low, this);
//					abs.put(high, reversed);
//				}
//			} else {
//				Range r = new Range();
//				BiNominal bi = new BiNominal();
//				bi.a = 0;
//				bi.b = 0;
//				if (c >= 0) {
//					bi.c = c;
//				} else {
//					bi.c = -c;
//				}
//				abs.put(r, bi);
//			}
//			return abs;
//		}
//
//		public static Map<Range, BiNominal> sum(Map<Range, BiNominal> first, Map<Range, BiNominal> second) {
//			Map<Range, BiNominal> sum = new HashMap<>();
//			Range firstRange = first.keySet().stream().filter(r -> r.min == null).findAny().get();
//			Range secondRange = second.keySet().stream().filter(r -> r.min == null).findAny().get();
//			Integer currentMin = null;
//			while (firstRange.max != null || secondRange.max != null) {
//				BiNominal firstBi = first.get(firstRange);
//				BiNominal secondBi = second.get(secondRange);
//				BiNominal sumInRange = new BiNominal();
//				sumInRange.a = firstBi.a + secondBi.a;
//				sumInRange.b = firstBi.b + secondBi.b;
//				sumInRange.c = firstBi.c + secondBi.c;
//				if (lessThan(firstRange.max, secondRange.max)) {
//					Range newRange = new Range();
//					newRange.min = currentMin;
//					newRange.max = firstRange.max;
//					sum.put(newRange, sumInRange);
//					firstRange = first.keySet().stream().filter(r -> {
//						return r.min != null && r.min == newRange.max + 1;
//					}).findAny().get();
//					currentMin = firstRange.min;
//				} else if (lessThan(secondRange.max, firstRange.max)) {
//					Range newRange = new Range();
//					newRange.min = currentMin;
//					newRange.max = secondRange.max;
//					sum.put(newRange, sumInRange);
//					secondRange = second.keySet().stream().filter(r -> r.min != null && r.min == newRange.max + 1)
//							.findAny().get();
//					currentMin = secondRange.min;
//				} else {
//					Range newRange = new Range();
//					newRange.min = currentMin;
//					newRange.max = secondRange.max;
//					sum.put(newRange, sumInRange);
//					firstRange = first.keySet().stream().filter(r -> r.min != null && r.min == newRange.max + 1)
//							.findAny().get();
//					secondRange = second.keySet().stream().filter(r -> r.min != null && r.min == newRange.max + 1)
//							.findAny().get();
//					currentMin = secondRange.min;
//				}
//			}
//			BiNominal firstBi = first.get(firstRange);
//			BiNominal secondBi = second.get(secondRange);
//			BiNominal sumInRange = new BiNominal();
//			sumInRange.a = firstBi.a + secondBi.a;
//			sumInRange.b = firstBi.b + secondBi.b;
//			sumInRange.c = firstBi.c + secondBi.c;
//			Range newRange = new Range();
//			newRange.min = currentMin;
//			newRange.max = null;
//			sum.put(newRange, sumInRange);
//
//			return sum;
//		}
//
//		private static boolean lessThan(Integer left, Integer right) {
//			if (left == null) {
//				return false;
//			}
//			if (right == null) {
//				return true;
//			}
//			return left < right;
//		}
//
//		public static int valuOfRanged(Map<Range, BiNominal> ranges, int x) {
//			return ranges.entrySet().stream().filter(r -> r.getKey().max == null || r.getKey().max >= x)
//					.filter(r -> r.getKey().min == null || r.getKey().min <= x).findAny()
//					.map(r -> r.getValue().valueOf(x)).get();
//		}
//
//		public static int getMin(Map<Range, BiNominal> ranges) {
//			List<Integer> suspects = new ArrayList<>();
//			ranges.keySet().stream().map(r -> r.min).filter(m -> m != null).forEach(suspects::add);
//			ranges.keySet().stream().map(r -> r.max).filter(m -> m != null).forEach(suspects::add);
//			for (Map.Entry<Range, BiNominal> entry : ranges.entrySet()) {
//				BiNominal nom = entry.getValue();
//				if (nom.a != 0) {
//					int ex = -1 * nom.b / nom.a / 2;
//					Range r = entry.getKey();
//					if (r.max == null || r.max > ex) {
//						if (r.min == null || r.min < ex) {
//							suspects.add(ex);
//						}
//					}
//				}
//			}
//			return suspects.stream().filter(x -> x > 0).mapToInt(x -> BiNominal.valuOfRanged(ranges, x)).min()
//					.getAsInt();
//		}
//	}
}
