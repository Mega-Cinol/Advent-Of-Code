package y2015.day17;

import java.util.ArrayList;
import java.util.List;

public class Day17 {

	public static void main(String[] args) {
		List<Integer> containers = new ArrayList<>();
		containers.add(43);
		containers.add(3);
		containers.add(4);
		containers.add(10);
		containers.add(21);
		containers.add(44);
		containers.add(4);
		containers.add(6);
		containers.add(47);
		containers.add(41);
		containers.add(34);
		containers.add(17);
		containers.add(17);
		containers.add(44);
		containers.add(36);
		containers.add(31);
		containers.add(46);
		containers.add(9);
		containers.add(27);
		containers.add(38);

		int combinationsCount = 0;
		int minAmount = containers.size();
		int minAmountCount = 0;
		for (long i = 0 ; i < Math.pow(2, containers.size()) ; i++)
		{
			int sum = 0;
			int amount = 0;
			for (int idx = 0 ; idx < containers.size() ; idx++)
			{
				if ((i & (1 << idx)) != 0)
				{
					sum += containers.get(idx);
					amount++;
				}
			}
			if (sum == 150)
			{
				combinationsCount++;
				if (amount == minAmount)
				{
					minAmountCount++;
				}
				else if (amount < minAmount)
				{
					minAmount = amount;
					minAmountCount = 1;
				}
			}
		}
		System.out.println(combinationsCount);
		System.out.println(minAmountCount);
	}

}
