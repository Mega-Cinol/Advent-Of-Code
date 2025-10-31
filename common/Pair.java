package common;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class Pair<T> {
	private final T first;
	private final T second;

	private Pair(T first, T second)
	{
		this.first = first;
		this.second = second;
	}

	public static <T> Pair<T> of(T first, T second)
	{
		return new Pair<T>(first, second);
	}

	public <U> Pair<U> map(Function<T, U> mapper) {
		return new Pair<U>(mapper.apply(first), mapper.apply(second));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result += ((second == null) ? 0 : second.hashCode());
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
		@SuppressWarnings("unchecked")
		Pair<T> other = (Pair<T>) obj;
		Set<T> mySet = new HashSet<>();
		Set<T> otherSet = new HashSet<>();
		mySet.add(first);
		mySet.add(second);
		otherSet.add(other.first);
		otherSet.add(other.second);
		return mySet.equals(otherSet);
	}
}
