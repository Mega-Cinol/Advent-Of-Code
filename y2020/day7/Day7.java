package y2020.day7;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;

public class Day7 {
	private final static Pattern BAG_NAME_PATTERN = Pattern.compile("^(?<name>[a-z ]*) bags contain");
	private final static Pattern BAG_CONTENT_PATTERN = Pattern
			.compile("((?<amount>[1-9]) (?<name>[a-z ]*)) bags?[,\\.]+");

	public static void main(String[] args) {
		Map<String, Entry> graph = new HashMap<>();
		Input.parseLines("y2020/day7/day7.txt", Entry::new, entry -> graph.put(entry.getName(), entry));
		graph.values().forEach(entry -> entry.getContent()
				.forEach(content -> graph.get(content.getName()).addParent(entry.getName())));
		Set<String> parents = new HashSet<>();
		findParents(graph, "shiny gold", parents);
		System.out.println(parents.size());
		System.out.println(countInside(graph, "shiny gold"));
	}

	private static void findParents(Map<String, Entry> graph, String name, Set<String> parents) {
		if (parents.contains(name)) {
			return;
		}
		parents.add(name);
		graph.get(name).getParents().stream().forEach(parent -> findParents(graph, parent, parents));
	}

	private static long countInside(Map<String, Entry> graph, String name)
	{
		long count = 1;
		for (Content content : graph.get(name).getContent())
		{
			count += countInside(graph, content.getName()) * content.getAmount();
		}
		return count;
	}

	private static class Entry {
		private final Set<Content> content = new HashSet<>();
		private final Set<String> parents = new HashSet<>();
		private final String name;

		public Entry(String description) {
			Matcher nameMatcher = BAG_NAME_PATTERN.matcher(description);
			if (!nameMatcher.find()) {
				throw new IllegalArgumentException("Invalid description: " + description);
			}
			name = nameMatcher.group("name");

			Matcher contentMatcher = BAG_CONTENT_PATTERN.matcher(description);
			while (contentMatcher.find()) {
				content.add(Content.of(contentMatcher.group("name"), Integer.parseInt(contentMatcher.group("amount"))));
			}
		}

		public String getName() {
			return name;
		}

		public Set<Content> getContent() {
			return content;
		}

		public Set<String> getParents() {
			return parents;
		}

		public void addParent(String parent) {
			parents.add(parent);
		}
	}

	private static class Content {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + amount;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			Content other = (Content) obj;
			if (amount != other.amount)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		public String getName() {
			return name;
		}

		public int getAmount() {
			return amount;
		}

		private final String name;
		private final int amount;

		private Content(String name, int amount) {
			this.name = name;
			this.amount = amount;
		}

		public static Content of(String name, int amount) {
			return new Content(name, amount);
		}
	}
}
