package common;

import java.util.HashSet;
import java.util.Set;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

public class PointRange3D {
	private final Point from;
	private final Point to;

	public PointRange3D(Point from, Point to) {
		this.from = new Point(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()),
				Math.min(from.getZ(), to.getZ()));
		this.to = new Point(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()),
				Math.max(from.getZ(), to.getZ()));
	}

	public long pointCount() {
		long xSize = Math.abs(from.getX() - to.getX()) + 1;
		long ySize = Math.abs(from.getY() - to.getY()) + 1;
		long zSize = Math.abs(from.getZ() - to.getZ()) + 1;
		return xSize * ySize * zSize;
	}

	public boolean contains(Point point) {
		boolean containsX = Math.max(from.getX(), to.getX()) >= point.getX()
				&& Math.min(from.getX(), to.getX()) <= point.getX();
		boolean containsY = Math.max(from.getY(), to.getY()) >= point.getY()
				&& Math.min(from.getY(), to.getY()) <= point.getY();
		boolean containsZ = Math.max(from.getZ(), to.getZ()) >= point.getZ()
				&& Math.min(from.getZ(), to.getZ()) <= point.getZ();
		return containsX && containsY && containsZ;
	}

	public Stream<Point> getEdges() {
		return Stream.of(new Point[] { from, new Point(to.getX(), from.getY(), from.getZ()),
				new Point(from.getX(), to.getY(), from.getZ()), new Point(to.getX(), to.getY(), from.getZ()),
				new Point(to.getX(), from.getY(), to.getZ()), new Point(from.getX(), to.getY(), to.getZ()),
				new Point(from.getX(), from.getY(), to.getZ()), to });
	}

	private enum Relation {
		SEPARATE, OVERLAPPING_LOW, OVERLAPPING_HIGH, CONTAINING, CONTAINED
	}

	private Relation getRelation(PointRange3D other, ToLongFunction<Point> axisSelector) {
		long myLow = axisSelector.applyAsLong(from);
		long myHigh = axisSelector.applyAsLong(to);
		long otherLow = axisSelector.applyAsLong(other.from);
		long otherHigh = axisSelector.applyAsLong(other.to);
		if (myLow > otherHigh || myHigh < otherLow) {
			return Relation.SEPARATE;
		} else if (myLow > otherLow && otherHigh >= myLow && myHigh > otherHigh) {
			return Relation.OVERLAPPING_LOW;
		} else if (myLow < otherLow && myHigh >= otherLow && myHigh < otherHigh) {
			return Relation.OVERLAPPING_HIGH;
		} else if (myLow <= otherLow && myHigh >= otherHigh) {
			return Relation.CONTAINING;
		} else if (otherLow <= myLow && otherHigh >= myHigh) {
			return Relation.CONTAINED;
		}
		throw new IllegalStateException(
				"myLow: " + myLow + " myHigh: " + myHigh + " otherLow: " + otherLow + " otherHigh: " + otherHigh);
	}

	public boolean coliding(PointRange3D other)
	{
		Relation xRelation = getRelation(other, Point::getX);
		Relation yRelation = getRelation(other, Point::getY);
		Relation zRelation = getRelation(other, Point::getZ);
		return xRelation != Relation.SEPARATE && yRelation != Relation.SEPARATE && zRelation != Relation.SEPARATE;
	}

	public Stream<PointRange3D> add(PointRange3D other) {
		Relation xRelation = getRelation(other, Point::getX);
		Relation yRelation = getRelation(other, Point::getY);
		Relation zRelation = getRelation(other, Point::getZ);
		if (xRelation == Relation.SEPARATE || yRelation == Relation.SEPARATE || zRelation == Relation.SEPARATE) {
			return Stream.of(this, other);
		}
		if (xRelation == Relation.CONTAINED && yRelation == Relation.CONTAINED && zRelation == Relation.CONTAINED) {
			return Stream.of(other);
		} else if (xRelation == Relation.CONTAINING && yRelation == Relation.CONTAINING && zRelation == Relation.CONTAINING) {
			return Stream.of(this);
		}
		else {
			Set<PointRange3D> result = new HashSet<>();
			// top
			if (to.getZ() < other.to.getZ()) {
				result.add(new PointRange3D(new Point(other.from.getX(), other.from.getY(), to.getZ() + 1),
						new Point(other.to.getX(), other.to.getY(), other.to.getZ())));
			}
			// bottom
			if (from.getZ() > other.from.getZ()) {
				result.add(new PointRange3D(new Point(other.from.getX(), other.from.getY(), from.getZ() - 1),
						new Point(other.to.getX(), other.to.getY(), other.from.getZ())));
			}

			// front
			if (to.getY() < other.to.getY()) {
				result.add(new PointRange3D(
						new Point(other.from.getX(), to.getY() + 1, Math.max(from.getZ(), other.from.getZ())),
						new Point(other.to.getX(), other.to.getY(), Math.min(to.getZ(), other.to.getZ()))));
			}
			// back
			if (from.getY() > other.from.getY()) {
				result.add(
						new PointRange3D(new Point(other.from.getX(), from.getY() - 1, Math.max(from.getZ(), other.from.getZ())),
								new Point(other.to.getX(), other.from.getY(), Math.min(to.getZ(), other.to.getZ()))));
			}

			// left
			if (to.getX() < other.to.getX()) {
				result.add(new PointRange3D(
						new Point(to.getX() + 1, Math.max(from.getY(), other.from.getY()),
								Math.max(from.getZ(), other.from.getZ())),
						new Point(other.to.getX(), Math.min(to.getY(), other.to.getY()),
								Math.min(to.getZ(), other.to.getZ()))));
			}
			// right
			if (from.getX() > other.from.getX()) {
				result.add(new PointRange3D(
						new Point(other.from.getX(), Math.max(from.getY(), other.from.getY()),
								Math.max(from.getZ(), other.from.getZ())),
						new Point(from.getX() - 1, Math.min(to.getY(), other.to.getY()),
								Math.min(to.getZ(), other.to.getZ()))));
			}
			result.add(this);
			return result.stream();
		}
	}

	public Stream<PointRange3D> remove(PointRange3D other) {
		Relation xRelation = getRelation(other, Point::getX);
		Relation yRelation = getRelation(other, Point::getY);
		Relation zRelation = getRelation(other, Point::getZ);
		if (xRelation == Relation.SEPARATE || yRelation == Relation.SEPARATE || zRelation == Relation.SEPARATE) {
			return Stream.of(this);
		}
		if (xRelation == Relation.CONTAINED && yRelation == Relation.CONTAINED && zRelation == Relation.CONTAINED) {
			return Stream.empty();
		} else {
			Set<PointRange3D> result = new HashSet<>();
			// top
			if (to.getZ() > other.to.getZ()) {
				result.add(new PointRange3D(new Point(from.getX(), from.getY(), other.to.getZ() + 1),
						new Point(to.getX(), to.getY(), to.getZ())));
			}
			// bottom
			if (from.getZ() < other.from.getZ()) {
				result.add(new PointRange3D(new Point(from.getX(), from.getY(), from.getZ()),
						new Point(to.getX(), to.getY(), other.from.getZ() - 1)));
			}

			// front
			if (to.getY() > other.to.getY()) {
				result.add(new PointRange3D(
						new Point(from.getX(), other.to.getY() + 1, Math.max(from.getZ(), other.from.getZ())),
						new Point(to.getX(), to.getY(), Math.min(to.getZ(), other.to.getZ()))));
			}
			// back
			if (from.getY() < other.from.getY()) {
				result.add(
						new PointRange3D(new Point(from.getX(), from.getY(), Math.max(from.getZ(), other.from.getZ())),
								new Point(to.getX(), other.from.getY() - 1, Math.min(to.getZ(), other.to.getZ()))));
			}

			// left
			if (to.getX() > other.to.getX()) {
				result.add(new PointRange3D(
						new Point(other.to.getX() + 1, Math.max(from.getY(), other.from.getY()),
								Math.max(from.getZ(), other.from.getZ())),
						new Point(to.getX(), Math.min(to.getY(), other.to.getY()),
								Math.min(to.getZ(), other.to.getZ()))));
			}
			// right
			if (from.getX() < other.from.getX()) {
				result.add(new PointRange3D(
						new Point(from.getX(), Math.max(from.getY(), other.from.getY()),
								Math.max(from.getZ(), other.from.getZ())),
						new Point(other.from.getX() - 1, Math.min(to.getY(), other.to.getY()),
								Math.min(to.getZ(), other.to.getZ()))));
			}
			return result.stream();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		PointRange3D other = (PointRange3D) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "From: " + from + " to: " + to;
	}
}
