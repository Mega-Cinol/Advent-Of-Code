package y2015.day22;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22 {

	public static void main(String[] args) {
		Stream.of(Spell.values()).mapToInt(spell -> { System.out.println(spell.name());return nextTurn(new Battle(), spell, 0, 0, new ArrayList<>());}).min().ifPresent(System.out::println);
	}

	private static int nextTurn(Battle battle, Spell spell, int currentCost, int depth, List<Spell> spells) {
		if (currentCost > 1700)
		{
			return -1;
		}
		if (!battle.spellAllowed(spell))
		{
			return -1;
		}
		currentCost += spell.getCost();
		spells.add(spell);
		Battle next = battle.turn(spell);
		if (next.isWon()) {
			if (currentCost == 1295)
			{
				System.out.println(spells);
			}
			return currentCost;
		}
		if (!next.isOngoing()) {
			return -1;
		}
		int minCost = Integer.MAX_VALUE;
		boolean found = false;
		for (Spell nextSpell : Spell.values()) {
			int nextResult = nextTurn(next, nextSpell, currentCost, depth + 1, new ArrayList<>(spells));
			if (nextResult < 0) {
				continue;
			}
			found = true;
			minCost = Math.min(minCost, nextResult);
		}
		return found ? minCost : -1;
	}

	private static enum Spell {
		MAGIC_MISSLE(() -> new Effect(1, magicMissle(), empty(), "MAGIC_MISSLE"), 53), DRAIN(() -> new Effect(1, drain(), empty(), "DRAIN"), 73),
		SHIELD(() -> new Effect(6, shield(), shieldWearOff(), "SHIELD"), 113),
		POISON(() -> new Effect(6, poison(), empty(), "POISON"), 173), RECHARGE(() -> new Effect(5, recharge(), empty(), "RECHARGE"), 229);

		private final Supplier<Effect> effect;
		private final int cost;

		Spell(Supplier<Effect> effect, int cost) {
			this.effect = effect;
			this.cost = cost;
		}

		public Effect getEffect() {
			return effect.get();
		}

		public int getCost() {
			return cost;
		}

		private static BiConsumer<Character, Character> empty() {
			return (player, boss) -> {
			};
		}

		private static BiConsumer<Character, Character> magicMissle() {
			return (player, boss) -> {
				boss.magicalDamage(4);
			};
		}

		private static BiConsumer<Character, Character> drain() {
			return (player, boss) -> {
				player.heal(2);
				boss.magicalDamage(2);
			};
		}

		private static BiConsumer<Character, Character> shield() {
			return (player, boss) -> {
				player.setDefence(7);
			};
		}

		private static BiConsumer<Character, Character> shieldWearOff() {
			return (player, boss) -> {
				player.setDefence(0);
			};
		}

		private static BiConsumer<Character, Character> poison() {
			return (player, boss) -> {
				boss.magicalDamage(3);
			};
		}

		private static BiConsumer<Character, Character> recharge() {
			return (player, boss) -> {
				player.generateMana(101);
			};
		}
	}

	private static class Battle {
		private final Character player;
		private final Character boss;
		private final Set<Effect> effects = new HashSet<>();

		private Battle(Character player, Character boss, Set<Effect> effects) {
			this.player = player;
			this.boss = boss;
			this.effects.addAll(effects);
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

		public boolean spellAllowed(Spell spell)
		{
			return effects.stream().map(Effect::getSpell).noneMatch(s -> s == spell);
		}
		public Battle turn(Spell spell) {
			Set<Effect> newEffects = effects.stream().map(Effect::copy).collect(Collectors.toCollection(HashSet::new));
			Character newPlayer = player.copy();
			Character newBoss = boss.copy();
			newPlayer.magicalDamage(1);
			if (!newPlayer.isAlive())
			{
				newPlayer.magicalDamage(200);
			}
			newEffects.forEach(e -> e.apply(newPlayer, newBoss));
			newPlayer.spendMana(spell.getCost());
			if (newPlayer.getMana() < 0)
			{
				newPlayer.spendMana(101);
			}
			newEffects.add(spell.getEffect());

			newEffects.forEach(e -> e.apply(newPlayer, newBoss));
			newEffects.stream().filter(e -> e.turnsLeft() == 0).forEach(e -> e.wearOff(newPlayer, newBoss));
			newEffects.removeIf(e -> e.turnsLeft() == 0);
			if (newBoss.isAlive()) {
				newPlayer.physicalAttack(newBoss.getAttack());
			}
			return new Battle(newPlayer, newBoss, newEffects);
		}
	}

	private static class Effect {
		private int turns;
		private final BiConsumer<Character, Character> effect;
		private final BiConsumer<Character, Character> wearOff;
		private final Spell spell;

		public Effect(int turns, BiConsumer<Character, Character> effect, BiConsumer<Character, Character> wearOff, String spell) {
			this.turns = turns;
			this.effect = effect;
			this.wearOff = wearOff;
			this.spell = Spell.valueOf(spell);
		}

		public int turnsLeft() {
			return turns;
		}

		public void apply(Character player, Character boss) {
			if (turns > 0) {
				turns--;
				effect.accept(player, boss);
			}
		}

		public void wearOff(Character player, Character boss) {
			wearOff.accept(player, boss);
		}
		
		public Spell getSpell()
		{
			return spell;
		}

		public Effect copy() {
			return new Effect(turns, effect, wearOff, spell.name());
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
