package y2021.day22;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;
import common.Point;
import common.PointRange3D;

public class Day22 {

	public static void main(String[] args) {
		// Part 1
		Set<PointRange3D> reactorParts = new HashSet<>();
		Input.parseLines("y2021/day22/day22.txt", Function.identity(), s -> updateReactorPart1(reactorParts, s));
		System.out.println(reactorParts.stream().mapToLong(PointRange3D::pointCount).sum());
		reactorParts.clear();
		Input.parseLines("y2021/day22/day22.txt", Function.identity(), s -> updateReactorPart2(reactorParts, s));
		System.out.println(reactorParts.stream().mapToLong(PointRange3D::pointCount).sum());
	}

	private static void updateReactorPart1(Set<PointRange3D> reactor, String command) {
		String pointsDescription = command.split(" ")[1];
		Pattern p = Pattern
				.compile("x=(-?[0-9]*)\\.\\.(-?[0-9]*),y=(-?[0-9]*)\\.\\.(-?[0-9]*),z=(-?[0-9]*)\\.\\.(-?[0-9]*)");
		Matcher m = p.matcher(pointsDescription);
		if (!m.matches()) {
			throw new IllegalArgumentException(command);
		}
		long xFrom = Long.parseLong(m.group(1));
		long xTo = Long.parseLong(m.group(2));
		long yFrom = Long.parseLong(m.group(3));
		long yTo = Long.parseLong(m.group(4));
		long zFrom = Long.parseLong(m.group(5));
		long zTo = Long.parseLong(m.group(6));
		if (xFrom > 50 || xTo < -50 || yFrom > 50 || yTo < -50 || zFrom > 50 || zTo < -50) {
			return;
		}
		xFrom = Math.max(xFrom, -50);
		yFrom = Math.max(yFrom, -50);
		zFrom = Math.max(zFrom, -50);
		xTo = Math.min(xTo, 50);
		yTo = Math.min(yTo, 50);
		zTo = Math.min(zTo, 50);
		PointRange3D newRange = new PointRange3D(new Point(xFrom, yFrom, zFrom), new Point(xTo, yTo, zTo));
		if (reactor.isEmpty() && command.startsWith("on ")) {
			reactor.add(newRange);
			return;
		}
		Set<PointRange3D> updatedReactor = new HashSet<>();
		if (command.startsWith("on ")) {
			reactor.stream().flatMap(r -> r.add(newRange)).forEach(updatedReactor::add);
			while (removeColision(updatedReactor));
		} else {
			reactor.stream().flatMap(r -> r.remove(newRange)).forEach(updatedReactor::add);
		}
		reactor.clear();
		reactor.addAll(updatedReactor);
	}

	private static void updateReactorPart2(Set<PointRange3D> reactor, String command) {
		String pointsDescription = command.split(" ")[1];
		Pattern p = Pattern
				.compile("x=(-?[0-9]*)\\.\\.(-?[0-9]*),y=(-?[0-9]*)\\.\\.(-?[0-9]*),z=(-?[0-9]*)\\.\\.(-?[0-9]*)");
		Matcher m = p.matcher(pointsDescription);
		if (!m.matches()) {
			throw new IllegalArgumentException(command);
		}
		long xFrom = Long.parseLong(m.group(1));
		long xTo = Long.parseLong(m.group(2));
		long yFrom = Long.parseLong(m.group(3));
		long yTo = Long.parseLong(m.group(4));
		long zFrom = Long.parseLong(m.group(5));
		long zTo = Long.parseLong(m.group(6));
		PointRange3D newRange = new PointRange3D(new Point(xFrom, yFrom, zFrom), new Point(xTo, yTo, zTo));
		if (reactor.isEmpty() && command.startsWith("on ")) {
			reactor.add(newRange);
			return;
		}
		Set<PointRange3D> updatedReactor = new HashSet<>();
		if (command.startsWith("on ")) {
			reactor.stream().flatMap(r -> r.add(newRange)).forEach(updatedReactor::add);
			Set<PointRange3D> colliding = updatedReactor.stream().filter(r -> r.coliding(newRange)).collect(Collectors.toSet());
			updatedReactor.removeAll(colliding);
			while (removeColision(colliding));
			updatedReactor.addAll(colliding);
		} else {
			reactor.stream().flatMap(r -> r.remove(newRange)).forEach(updatedReactor::add);
		}
		reactor.clear();
		reactor.addAll(updatedReactor);
	}

	private static boolean removeColision(Set<PointRange3D> reactorParts) {
		PointRange3D firstFound = null;
		PointRange3D secondFound = null;
		outer: for (PointRange3D first : reactorParts) {
			for (PointRange3D second : reactorParts) {
				if (!first.equals(second) && first.coliding(second)) {
					firstFound = first;
					secondFound = second;
					break outer;
				}
			}
		}
		if (firstFound != null && secondFound != null) {
			reactorParts.remove(firstFound);
			reactorParts.remove(secondFound);
			firstFound.add(secondFound).forEach(reactorParts::add);
			return true;
		} else {
			return false;
		}
	}
}
