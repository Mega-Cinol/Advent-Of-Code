package y2015.day22;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22Part2 {

	public static void main(String[] args) {
		System.out.println(nextTurn(new Battle(), 0, 0, new ArrayList<>()));
	}

	private static int nextTurn(Battle battle, int currentCost, int depth, List<Spell> spells) {
		if (battle == null || currentCost > 1700)
		{
			return Integer.MAX_VALUE;
		}
		if (battle.isWon())
		{
			return currentCost;
		}
		if (!battle.isOngoing()) {
			return Integer.MAX_VALUE;
		}
		int minCost = Integer.MAX_VALUE;
		for (Spell nextSpell : battle.allowedSpells()) {
			List<Spell> nextSpellsList = new ArrayList<>(spells);
			nextSpellsList.add(nextSpell);
			int nextResult = nextTurn(battle.turn(nextSpell), currentCost + nextSpell.getCost(), depth + 1, nextSpellsList);
			minCost = Math.min(minCost, nextResult);
		}
		return minCost;
	}

	private static enum Spell {
		MAGIC_MISSLE(53), DRAIN(73),
		SHIELD(113),
		POISON(173), RECHARGE(229);

		private final int cost;

		Spell(int cost) {
			this.cost = cost;
		}

		public int getCost() {
			return cost;
		}
	}

	private static class Battle {
		private final Character player;
		private final Character boss;
		private int shieldTurnsLeft = 0;
		private int poisonTurnsLeft = 0;
		private int rechargeTurnsLeft = 0;

		private Battle(Character player, Character boss, int shieldTimer, int poisonTimer, int rechargeTimer) {
			this.player = player;
			this.boss = boss;
			shieldTurnsLeft = shieldTimer;
			poisonTurnsLeft = poisonTimer;
			rechargeTurnsLeft = rechargeTimer;
		}

		@SuppressWarnings("unused")
		public void print() {
			System.out.println("Player's HP: " + player.getHp() + " player's mana: " + player.getMana() + " boss' HP: " + boss.getHp());
		}

		public Battle() {
			player = new Character(50, 0, 0, 500);
			boss = new Character(55, 8, 0, 0);
		}

		public boolean isWon() {
			return player.isAlive() && player.getMana() >= 0 && !boss.isAlive();
		}

		public boolean isOngoing() {
			return player.isAlive() && player.getMana() >= 0 && boss.isAlive();
		}

		public Set<Spell> allowedSpells()
		{
			Set<Spell> allowed = Stream.of(Spell.values()).collect(Collectors.toCollection(HashSet::new));
			if (shieldTurnsLeft > 1)
			{
				allowed.remove(Spell.SHIELD);
			}
			if (rechargeTurnsLeft > 1)
			{
				allowed.remove(Spell.RECHARGE);
			}
			if (poisonTurnsLeft > 1)
			{
				allowed.remove(Spell.POISON);
			}
			return allowed;
		}

		public Battle turn(Spell spell) {
			int poisonTimer = poisonTurnsLeft;
			int shieldTimer = shieldTurnsLeft;
			int rechargeTimer = rechargeTurnsLeft;

			Character newPlayer = player.copy();
			Character newBoss = boss.copy();

			// Players turn
			newPlayer.magicalDamage(1);
			if (!newPlayer.isAlive())
			{
				return null;
			}
			if (poisonTimer > 0)
			{
				poisonTimer--;
				newBoss.magicalDamage(3);
			}
			if (shieldTimer > 0)
			{
				shieldTimer--;
				newPlayer.setDefence(7);
			} else {
				newPlayer.setDefence(0);
			}
			if (rechargeTimer > 0)
			{
				rechargeTimer--;
				newPlayer.generateMana(101);
			}
			newPlayer.spendMana(spell.getCost());
			if (newPlayer.getMana() < 0)
			{
				return null;
			}
			switch (spell) {
			case MAGIC_MISSLE:
				newBoss.magicalDamage(4);
				break;
			case DRAIN:
				newBoss.magicalDamage(2);
				newPlayer.heal(2);
				break;
			case SHIELD:
				shieldTimer = 6;
				break;
			case POISON:
				poisonTimer = 6;
				break;
			case RECHARGE:
				rechargeTimer = 5;
				break;
			}

			// Boss turn
//			newPlayer.magicalDamage(1);
//			if (!newPlayer.isAlive())
//			{
//				return null;
//			}
			if (poisonTimer > 0)
			{
				poisonTimer--;
				newBoss.magicalDamage(3);
			}
			if (shieldTimer > 0)
			{
				shieldTimer--;
				newPlayer.setDefence(7);
			} else {
				newPlayer.setDefence(0);
			}
			if (rechargeTimer > 0)
			{
				rechargeTimer--;
				newPlayer.generateMana(101);
			}
			if (newBoss.isAlive()) {
				newPlayer.physicalAttack(newBoss.getAttack());
			}
			return new Battle(newPlayer, newBoss, shieldTimer, poisonTimer, rechargeTimer);
		}
	}

	private static class Character {
		private int hp;
		private int attack;
		private int defence;
		private int mana;

		public Character(int hp, int attack, int defence, int mana) {
			this.hp = hp;
			this.attack = attack;
			this.defence = defence;
			this.mana = mana;
		}

		public Character copy() {
			return new Character(hp, attack, defence, mana);
		}

		public int getHp() {
			return hp;
		}

		public boolean isAlive() {
			return getHp() > 0;
		}

		public void heal(int hp) {
			this.hp += hp;
		}

		public void magicalDamage(int hp) {
			this.hp -= hp;
		}

		public int getAttack() {
			return attack;
		}

		public void physicalAttack(int attack) {
			hp -= Math.max(1, attack - getDefence());
		}

		public int getDefence() {
			return defence;
		}

		public void setDefence(int defence) {
			this.defence = defence;
		}

		public int getMana() {
			return mana;
		}

		public void spendMana(int mana) {
			this.mana -= mana;
		}

		public void generateMana(int mana) {
			this.mana += mana;
		}
	}
}
