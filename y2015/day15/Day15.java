package y2015.day15;

import java.util.ArrayList;
import java.util.List;

public class Day15 {

	public static void main(String[] args) {
//		Sugar: capacity 3, durability 0, flavor 0, texture -3, calories 2
//		Sprinkles: capacity -3, durability 3, flavor 0, texture 0, calories 9
//		Candy: capacity -1, durability 0, flavor 4, texture 0, calories 1
//		Chocolate: capacity 0, durability 0, flavor -2, texture 2, calories 8
		List<Integer> sugar = new ArrayList<>(); // 24 => 72 0 0 -72 
		sugar.add(3);
		sugar.add(0);
		sugar.add(0);
		sugar.add(-3);
		sugar.add(2);
		List<Integer> sprinkles = new ArrayList<>(); // 15 => -45 45 0 0
		sprinkles.add(-3);
		sprinkles.add(3);
		sprinkles.add(0);
		sprinkles.add(0);
		sprinkles.add(9);
		List<Integer> candy = new ArrayList<>(); // 22 => -22 0 88 0
		candy.add(-1);
		candy.add(0);
		candy.add(4);
		candy.add(0);
		candy.add(1);
		List<Integer> chocolate = new ArrayList<>(); // 39 => 0 0 -78 78
		chocolate.add(0);
		chocolate.add(0);
		chocolate.add(-2);
		chocolate.add(2);
		chocolate.add(8);
		List<List<Integer>> ingredients = new ArrayList<>();
		ingredients.add(sugar);
		ingredients.add(sprinkles);
		ingredients.add(candy);
		ingredients.add(chocolate);

		System.out.println(score(24, 15, 22, ingredients));
		long best = 0;
		for (int first = 0 ; first <= 100 ; first++)
		{
			for (int second = 0; second <= 100 - first ; second++)
			{
				for (int third = 0 ; third <= 100 - first - second ; third++)
				{
					System.out.println(score(first, second, third, ingredients));
					best = Math.max(best, score(first, second, third, ingredients));
				}
			}
		}
		System.out.println(best);
	}

	private static long score(int first, int second, int third, List<List<Integer>> ingredients) {
		int capacity = ingredients.get(0).get(0) * first + ingredients.get(1).get(0) * second
				+ ingredients.get(2).get(0) * third + ingredients.get(3).get(0) * (100 - first - second - third);
		int dura = ingredients.get(0).get(1) * first + ingredients.get(1).get(1) * second
				+ ingredients.get(2).get(1) * third + ingredients.get(3).get(1) * (100 - first - second - third);
		int flav = ingredients.get(0).get(2) * first + ingredients.get(1).get(2) * second
				+ ingredients.get(2).get(2) * third + ingredients.get(3).get(2) * (100 - first - second - third);
		int tex = ingredients.get(0).get(3) * first + ingredients.get(1).get(3) * second
				+ ingredients.get(2).get(3) * third + ingredients.get(3).get(3) * (100 - first - second - third);
		int cal = ingredients.get(0).get(4) * first + ingredients.get(1).get(4) * second
				+ ingredients.get(2).get(4) * third + ingredients.get(3).get(4) * (100 - first - second - third);
		if (cal != 500) {
			return 0;
		}
		return Math.max(capacity, 0) * Math.max(dura, 0) * Math.max(flav, 0) * Math.max(tex, 0);
	}
}
