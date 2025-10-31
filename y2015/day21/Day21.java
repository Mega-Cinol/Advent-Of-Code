package y2015.day21;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

import common.Combinations;

public class Day21 {

	public static void main(String[] args) {
		Set<Item> weapons = new HashSet<>();
		weapons.add(new Item(4, 0, 8));
		weapons.add(new Item(5, 0, 10));
		weapons.add(new Item(6, 0, 25));
		weapons.add(new Item(7, 0, 40));
		weapons.add(new Item(8, 0, 74));

		Set<Item> armors = new HashSet<>();
		armors.add(new Item(0, 0, 0));
		armors.add(new Item(0, 1, 13));
		armors.add(new Item(0, 2, 31));
		armors.add(new Item(0, 3, 53));
		armors.add(new Item(0, 4, 75));
		armors.add(new Item(0, 5, 102));

		Set<Item> rings = new HashSet<>();
		rings.add(new Item(0, 0, 0));
		rings.add(new Item(0, 0, 0));
		rings.add(new Item(1, 0, 25));
		rings.add(new Item(2, 0, 50));
		rings.add(new Item(3, 0, 100));
		rings.add(new Item(0, 1, 20));
		rings.add(new Item(0, 2, 40));
		rings.add(new Item(0, 3, 80));

		Combinations.cartesian(weapons, armors, rings, rings)
				.filter(equipment -> equipment.get(2) != equipment.get(3))
				.sorted((e1, e2) -> getStat(e1, Item::getCost) - getStat(e2, Item::getCost))
				.filter(Day21::canWin)
				.findFirst()
				.ifPresent(System.out::println);
		Combinations.cartesian(weapons, armors, rings, rings)
				.filter(equipment -> equipment.get(2) != equipment.get(3))
				.sorted((e1, e2) -> getStat(e2, Item::getCost) - getStat(e1, Item::getCost))
				.filter(Day21::cantWin)
				.findFirst()
				.ifPresent(System.out::println);
	}

	private static int getStat(List<Item> equipment, ToIntFunction<Item> statExtractor)
	{
		return equipment.stream().mapToInt(statExtractor).sum();
	}

	private static boolean cantWin(List<Item> equipment)
	{
		return !canWin(equipment);
	}

	private static boolean canWin(List<Item> equipment)
	{
		int attack = getStat(equipment, Item::getAttack);
		int defence = getStat(equipment, Item::getDefence);

		int turnsToWin = 100 / Math.max(attack - 2, 1);
		int turnsToLoose = 100 / Math.max(8 - defence, 1);
		return turnsToWin <= turnsToLoose;
	}
	private static class Item
	{
		private final int attack;
		private final int defence;
		private final int cost;

		public Item(int attack,int defence,int cost)
		{
			this.attack = attack;
			this.defence = defence;
			this.cost = cost;
		}

		public int getAttack() {
			return attack;
		}

		public int getDefence() {
			return defence;
		}

		public int getCost() {
			return cost;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + attack;
			result = prime * result + cost;
			result = prime * result + defence;
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
			Item other = (Item) obj;
			if (attack != other.attack)
				return false;
			if (cost != other.cost)
				return false;
			if (defence != other.defence)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Item [attack=" + attack + ", defence=" + defence + ", cost=" + cost + "]";
		}
	}
}
