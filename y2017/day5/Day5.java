package y2017.day5;

import java.util.List;
import java.util.stream.Collectors;

import common.Input;

public class Day5 {

	public static void main(String[] args) {
		List<Integer> instructions = Input.parseLines("y2017/day5/day5.txt", Integer::parseInt).collect(Collectors.toList());
		int ip = 0;
		int count = 0;
		while (ip < instructions.size() && ip >= 0)
		{
			count++;
			int oldIp = ip;
			ip += instructions.get(ip);
			int newValue = instructions.get(oldIp);
			if (newValue >= 3)
			{
				newValue--;
			}
			else
			{
				newValue++;
			}
			instructions.set(oldIp, newValue);
		}
		System.out.println(count);
	}

}
