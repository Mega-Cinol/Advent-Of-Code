package y2020.day21;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import common.Input;

public class Day21 {

	public static void main(String[] args) {
		Map<String, Set<String>> allergensMap = new HashMap<>();
		Queue<String> ingridientsOccurences = new ArrayDeque<>();

		Input.parseLines("y2020/day21/day21.txt", Food::new).peek(food -> fillInAllergensMap(allergensMap, food))
				.forEach(food -> ingridientsOccurences.addAll(food.ingredients));
		Set<String> allergensIngridients = allergensMap.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
		System.out.println(ingridientsOccurences.stream().filter(i -> !allergensIngridients.contains(i)).count());
		Map<String, String> allergensMap2 = new HashMap<>();
		allergensMap.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getValue().size())).forEach(e -> {
			e.getValue().removeAll(allergensMap2.values());
			allergensMap2.put(e.getKey(), e.getValue().stream().findAny().get());
		});
		System.out.println(allergensMap2.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).map(Map.Entry::getValue).collect(Collectors.joining(",")));
	}

	private static void fillInAllergensMap(Map<String, Set<String>> allergensMap, Food food) {
		food.allergens.forEach(allergen -> allergensMap.merge(allergen, food.ingredients, (i1, i2) -> {
			Set<String> result = new HashSet<>(i1);
			result.retainAll(i2);
			return result;
		}));
	}

	private static class Food {
		private final Set<String> ingredients = new HashSet<>();
		private final Set<String> allergens = new HashSet<>();

		public Food(String description) {
			String[] descriptionSplit = description.split("\\(");
			Stream.of(descriptionSplit[0].split(" ")).filter(str -> !str.isEmpty()).forEach(ingredients::add);
			Stream.of(descriptionSplit[1].substring("contains ".length()).replace(")", "").split(", "))
					.filter(str -> !str.isEmpty()).forEach(allergens::add);
		}
	}
}
