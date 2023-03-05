package y2017.day16;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import common.Input;

public class Day16 {

	public static void main(String[] args) {
		DanceList list = new DanceList();
		List<String> commands = Stream.of(Input.parseLines("y2017/day16/day16.txt").findFirst().get().split(",")).collect(Collectors.toList());
		System.out.println("0 - " + list);
		for (int i = 0 ; i < 9 ; i++)
		{
			commands.forEach(cmd -> execute(cmd, list));
			System.out.println((i + 1) + " - " + list);
		}
		List<Integer> mapping = new ArrayList<>();
		IntStream.range(0, 16).forEach(mapping::add);
		int idx = 0;
		for (Character c : list)
		{
			mapping.set(c - 'a', idx++);
		}
		for (int i = 9 ; i < 90 ; i++)
		{
			commands.forEach(cmd -> execute(cmd, list));
			System.out.println((i + 1) + " - " + list);
		}
		DanceList list3 = new DanceList();
		System.out.println("============");
		System.out.println("0 - " + list3);
		for (int i = 0 ; i < 90 ; i++)
		{
			commands.forEach(cmd -> execute2(cmd, list3));
			System.out.println((i + 1) + " - " + list3);
		}

		List<Integer> after9 = mapping;
		List<Integer> after18 = combine(after9, after9);
		List<Integer> after36 = combine(after18, after18);
		List<Integer> after72 = combine(after36, after36);
		List<Integer> after90 = combine(after72, after18);
		System.out.println("============");
		System.out.println("9  - " + print(after9));
		System.out.println("18 - " + print(after18));
		System.out.println("36 - " + print(after36));
		System.out.println("72 - " + print(after72));
		System.out.println("90 - " + print(after90));
	}

	private static String print(List<Integer> list)
	{
		return list.stream().map(i -> 'a' + i).map(c -> "" + (char) c.intValue()).collect(Collectors.joining());
	}

	private static List<Integer> combine(List<Integer> first, List<Integer> second)
	{
		List<Integer> combined = new ArrayList<>();
		IntStream.range(0, 16).forEach(combined::add);
		for (int i = 0 ; i < 16 ; i++)
		{
			combined.set(i, second.get(first.get(i)));
		}
		return combined;
	}
	private static void execute(String command, DanceList list)
	{
		if (command.startsWith("s"))
		{
			int value = Integer.parseInt(command.substring(1));
			list.spin(value);
		} else if (command.startsWith("x")) {
			int i1 = Integer.parseInt(command.substring(1, command.indexOf('/')));
			int i2 = Integer.parseInt(command.substring(command.indexOf('/') + 1));
			list.swap(i1, i2);
		} else {
			list.swap(command.charAt(1), command.charAt(3));
		}
	}
	private static void execute2(String command, DanceList list)
	{
		if (command.startsWith("s"))
		{
			int value = Integer.parseInt(command.substring(1));
			list.spin(value);
		} else if (command.startsWith("x")) {
			int i1 = Integer.parseInt(command.substring(1, command.indexOf('/')));
			int i2 = Integer.parseInt(command.substring(command.indexOf('/') + 1));
			list.swap(i1, i2);
		}
	}
	private static class DanceList implements List<Character>
	{
		private final ArrayList<Character> backendList = new ArrayList<>();
		private int offset = 0;

		public DanceList()
		{
			for (char c = 'a' ; c <= 'p' ; c++)
			{
				backendList.add(c);
			}
		}

		public void spin(int x)
		{
			offset -= x;
			offset += 16;
			offset %= 16;
		}

		public void swap(int i1, int i2)
		{
			int from = (i1 + offset) % 16;
			int to = (i2 + offset) % 16;
			Character tmp = backendList.get(from);
			backendList.set(from, backendList.get(to));
			backendList.set(to, tmp);
		}

		public void swap(char c1, char c2)
		{
			int from = backendList.indexOf(c1);
			int to = backendList.indexOf(c2);
			Character tmp = backendList.get(from);
			backendList.set(from, backendList.get(to));
			backendList.set(to, tmp);
		}

		@Override
		public int size() {
			return 16;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public boolean contains(Object o) {
			return o instanceof Character && ((Character) o).charValue() >= 'a' && ((Character) o).charValue() <= 'p';
		}

		@Override
		public Iterator<Character> iterator() {
			return new Iterator<Character>() {
				private int pos = offset;
				private int steps = 0;

				@Override
				public boolean hasNext() {
					return steps < 16;
				}

				@Override
				public Character next() {
					Character item = backendList.get(pos);
					pos++;
					pos %= 16;
					steps++;
					return item;
				}
			};
		}

		@Override
		public Object[] toArray() {
			Character[] array = new Character[16];
			return toArray(array);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T[] toArray(T[] a) {
			int ptr = 0;
			for (Character c : this)
			{
				a[ptr++] = (T) c;
			}
			return a;
		}

		@Override
		public boolean add(Character e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return c.stream().allMatch(this::contains);
		}

		@Override
		public boolean addAll(Collection<? extends Character> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int index, Collection<? extends Character> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Character get(int index) {
			return backendList.get((index + offset) % 16);
		}

		@Override
		public Character set(int index, Character element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int index, Character element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Character remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(Object o) {
			int idx = 0;
			for (Character c : this)
			{
				if (c.equals(o))
				{
					return idx;
				}
				idx++;
			}
			return -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			for (int i = offset - 1, steps = 0 ; steps < 16 ; steps++, i = (i + 15) % 16)
			{
				if (backendList.get(i).equals(o))
				{
					return i;
				}
			}
			return -1;
		}

		@Override
		public ListIterator<Character> listIterator() {
			return backendList.listIterator(offset);
		}

		@Override
		public ListIterator<Character> listIterator(int index) {
			return backendList.listIterator((offset + index) % 16);
		}

		@Override
		public List<Character> subList(int fromIndex, int toIndex) {
			List<Character> result = new ArrayList<>();
			for (int i = (offset + fromIndex) % 16, steps = 0 ; steps < toIndex - fromIndex ; steps++, i = (i + 1) % 16)
			{
				result.add(backendList.get(i));
			}
			return result;
		}

		@Override
		public String toString()
		{
			String result = "";
			for (Character c : this)
			{
				result += c;
			}
			return result;
		}
	}
}
