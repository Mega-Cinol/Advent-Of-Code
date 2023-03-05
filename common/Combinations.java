package common;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Combinations {
	public static <T> Stream<Deque<T>> permutations(Collection<T> elements)
	{
		Stream.Builder<Deque<T>> streamBuilder = Stream.builder();
		if (elements.size() <= 1)
		{
			streamBuilder.add(new ArrayDeque<T>(elements));
		}
		else
		{
			for (T element : elements)
			{
				Set<T> subset = new HashSet<>(elements);
				subset.remove(element);
				permutations(subset).peek(queue -> queue.add(element)).forEach(streamBuilder);
			}
		}
		return streamBuilder.build();
	}

	@SafeVarargs
	public static <T> Stream<List<T>> cartesian(Set<T>... possibleValues)
	{
		return cartesian(Stream.of(possibleValues).collect(Collectors.toList()));
	}

	public static <T> Stream<List<T>> cartesian(List<Set<T>> possibleValues)
	{
		Set<List<T>> result = new HashSet<>();
		result.add(new ArrayList<>());
		for (Set<T> dimValues : possibleValues) {
			Set<List<T>> newResult = new HashSet<>();
			for (T possibleValue : dimValues)
			{
				result.stream().map(e -> {
					List<T> copy = new ArrayList<>(e);
					copy.add(possibleValue);
					return copy;
				}).forEach(newResult::add);
				
			}
			result = newResult;
		}
		return result.stream();
	}

	public static <T> Stream<Set<T>> subSets(Collection<T> items, int size)
	{
		if (size == 1)
		{
			return items.stream().map(item -> {
				Set<T> subset = new HashSet<>();
				subset.add(item);
				return subset;
			});
		} else
		{
			Set<Set<T>> newSubsets = new HashSet<>();
			for (T item : items)
			{
				Set<T> withoutItem = new HashSet<>(items);
				withoutItem.remove(item);
				subSets(withoutItem, size - 1).peek(subset -> subset.add(item)).forEach(newSubsets::add);
			}
			return newSubsets.stream().distinct();
		}
	}
	public static void main(String[]  args)
	{
		Set<Integer> test = new HashSet<>();
		test.add(0);
		test.add(1);
		test.add(2);
		test.add(3);
		System.out.println("------------------------------------");
		permutations(test).distinct().forEach(System.out::println);
		System.out.println(permutations(test).distinct().count());

		Set<Integer> x = new HashSet<>();
		x.add(0);
		x.add(1);
		Set<Integer> y = new HashSet<>();
		y.add(1);
		y.add(2);
		Set<Integer> z = new HashSet<>();
		z.add(3);
		z.add(4);
		System.out.println(cartesian(x, y, z));

		System.out.println("------------------------------------");
		subSets(test, 2).forEach(System.out::println);
	}
}
