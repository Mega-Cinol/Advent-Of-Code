package y2018.day1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import common.Input;

public class Day1 {

	public static void main(String[] args) {
		System.out.println(Input.parseLines("y2018/day1/day1.txt", Integer::valueOf).mapToInt(Integer::intValue).sum());
		List<Integer> changes = Input.parseLines("y2018/day1/day1.txt", Integer::valueOf).collect(Collectors.toList());
		int ptr = 0;
		int freq = 0;
		Set<Integer> knownFreq = new HashSet<>();
		while (true)
		{
			freq += changes.get(ptr++);
			ptr %= changes.size();
			if (!knownFreq.add(freq))
			{
				System.out.println(freq);
				break;
			}
		}
	}

}
