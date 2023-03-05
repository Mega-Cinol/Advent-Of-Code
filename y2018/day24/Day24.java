package y2018.day24;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day24 {
	private final static Pattern GROUP_PATTERN = Pattern.compile(
			"(\\d+) units each with (\\d+) hit points (\\((weak to ([a-z, ]*))?(; )?(immune to ([a-z, ]*))?\\) )?with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)");

	public static void main(String[] args) throws InterruptedException {
		List<String> input = Input.parseLines("y2018/day24/day24.txt").collect(Collectors.toList());
		Owner owner = Owner.IMMUNE_SYSTEM;
		List<Group> groups = new ArrayList<>();
		for (int i = 1; i < input.size(); i++) {
			if (input.get(i).isEmpty()) {
				i += 2;
				owner = Owner.INFECTION;
			}
			groups.add(new Group(input.get(i), owner));
		}
		while (groups.stream().filter(Group::isAlive).map(Group::getOwner).distinct().count() > 1) {
			Map<Group, Group> targets = new HashMap<>();
			groups.stream().filter(Group::isAlive).sorted(
					Comparator.comparing(Group::getEffectiveAttack).thenComparing(Group::getInitiative).reversed())
					.forEach(g -> addTarget(targets, g, groups));
			targets.keySet().stream().sorted(Comparator.comparing(Group::getInitiative).reversed())
					.forEach(g -> targets.get(g).attack(g));
		}
		System.out.println(groups.stream().filter(Group::isAlive).mapToInt(Group::getUnitCount).sum());
		int boost = 84;
		while (infectionWon(groups, boost++))
		{
			System.out.println(boost);
		}
		System.out.println(groups.stream().filter(Group::isAlive).mapToInt(Group::getUnitCount).sum());
	}

	private static boolean infectionWon(List<Group> groups, int boost) throws InterruptedException {
		groups.forEach(Group::reset);
		groups.stream().filter(g -> g.getOwner() == Owner.IMMUNE_SYSTEM).forEach(g -> g.setBopst(boost));
		while (groups.stream().filter(Group::isAlive).map(Group::getOwner).distinct().count() > 1) {
			Map<Group, Group> targets = new HashMap<>();
			groups.stream().filter(Group::isAlive).sorted(
					Comparator.comparing(Group::getEffectiveAttack).thenComparing(Group::getInitiative).reversed())
					.forEach(g -> addTarget(targets, g, groups));
			targets.keySet().stream().sorted(Comparator.comparing(Group::getInitiative).reversed())
					.forEach(g -> targets.get(g).attack(g));
		}
		System.out.println(groups.stream().filter(Group::isAlive).mapToInt(Group::getUnitCount).sum());
		return groups.stream().filter(Group::isAlive).map(Group::getOwner).findAny().get() == Owner.INFECTION;
	}

	private static void addTarget(Map<Group, Group> targets, Group attacker, List<Group> attacked) {
		Optional<Group> target = attacked.stream().filter(Group::isAlive)
				.filter(g -> g.getOwner() != attacker.getOwner()).filter(g -> !targets.values().contains(g))
				.filter(g -> g.damageFrom(attacker) > 0).max(Comparator.comparing((Group g) -> g.damageFrom(attacker))
						.thenComparing(Group::getEffectiveAttack).thenComparing(Group::getInitiative));
		target.ifPresent(t -> targets.put(attacker, t));
	}

	enum Owner {
		IMMUNE_SYSTEM, INFECTION;
	}

	private static class Group {
		private final Owner owner;
		private final int initialCount;
		private int unitsCount;
		private final int unitHp;
		private final Set<String> immunities = new HashSet<>();
		private final Set<String> weaknesses = new HashSet<>();
		private final int attackStrength;
		private final String attackType;
		private final int initiative;
		private int boost = 0;

		public Group(String description, Owner owner) {
			this.owner = owner;
			Matcher m = GROUP_PATTERN.matcher(description);
			if (!m.matches()) {
				throw new IllegalArgumentException(description);
			}
			unitsCount = Integer.parseInt(m.group(1));
			initialCount = unitsCount;
			unitHp = Integer.parseInt(m.group(2));
			if (m.group(5) != null) {
				Stream.of(m.group(5).split(", ")).forEach(weaknesses::add);
			}
			if (m.group(8) != null) {
				Stream.of(m.group(8).split(", ")).forEach(immunities::add);
			}
			attackStrength = Integer.parseInt(m.group(9));
			attackType = m.group(10);
			initiative = Integer.parseInt(m.group(11));
		}

		public void reset() {
			unitsCount = initialCount;
			boost = 0;
		}

		public void setBopst(int newBoost) {
			boost = newBoost;
		}

		public int getUnitCount() {
			return unitsCount;
		}

		public void attack(Group attacker) {
//			System.out.println("Attacker: " + attacker);
//			System.out.println("Defendet: " + this);
			unitsCount -= damageFrom(attacker) / unitHp;
			unitsCount = Math.max(unitsCount, 0);
		}

		public int damageFrom(Group attacker) {
			int multiplier = weaknesses.contains(attacker.attackType) ? 2
					: immunities.contains(attacker.attackType) ? 0 : 1;
			return attacker.getEffectiveAttack() * multiplier;
		}

		public boolean isAlive() {
			return unitsCount > 0;
		}

		public int getEffectiveAttack() {
			return unitsCount * (attackStrength + boost);
		}

		public int getInitiative() {
			return initiative;
		}

		public Owner getOwner() {
			return owner;
		}

		@Override
		public String toString() {
			String special = "";
			if (!immunities.isEmpty() || !weaknesses.isEmpty()) {
				special = " (";
				if (!weaknesses.isEmpty()) {
					special += "weak to " + weaknesses.stream().collect(Collectors.joining(", "));
				}
				if (!weaknesses.isEmpty() && !immunities.isEmpty()) {
					special += "; ";
				}
				if (!immunities.isEmpty()) {
					special += "immune to " + immunities.stream().collect(Collectors.joining(", "));
				}
				special += ")";
			}
			return owner + ": " + unitsCount + " units each with " + unitHp + " hit points" + special
					+ " with an attack that does " + attackStrength + " " + attackType + " damage at initiative "
					+ initiative;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + attackStrength;
			result = prime * result + ((attackType == null) ? 0 : attackType.hashCode());
			result = prime * result + ((immunities == null) ? 0 : immunities.hashCode());
			result = prime * result + initiative;
			result = prime * result + ((owner == null) ? 0 : owner.hashCode());
			result = prime * result + unitHp;
			result = prime * result + ((weaknesses == null) ? 0 : weaknesses.hashCode());
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
			Group other = (Group) obj;
			if (attackStrength != other.attackStrength)
				return false;
			if (attackType == null) {
				if (other.attackType != null)
					return false;
			} else if (!attackType.equals(other.attackType))
				return false;
			if (immunities == null) {
				if (other.immunities != null)
					return false;
			} else if (!immunities.equals(other.immunities))
				return false;
			if (initiative != other.initiative)
				return false;
			if (owner != other.owner)
				return false;
			if (unitHp != other.unitHp)
				return false;
			if (weaknesses == null) {
				if (other.weaknesses != null)
					return false;
			} else if (!weaknesses.equals(other.weaknesses))
				return false;
			return true;
		}
	}
}
