package y2022.day7;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Input;

public class Day7 {

	private static final Pattern CD_PATTERN = Pattern.compile("\\$ cd (.*)");
	private static final Pattern ITEM_PATTERN = Pattern.compile("(dir|\\d+) (.*)");
	private static final long MAX_SIZE = 100000;
	private static final long DISK_SIZE = 70000000;
	private static final long REQUIRED_SPACE = 30000000;

	public static void main(String[] args) {
		var input = Input.parseLines("y2022/day7/day7.txt").toList();
		var root = new Directory(null, "/");
		Directory current = root;
		for (String line : input) {
			if (line.equals("$ ls")) {
				continue;
			}
			Matcher cdMatcher = CD_PATTERN.matcher(line);
			if (cdMatcher.matches()) {
				var target = cdMatcher.group(1);
				if ("..".equals(target)) {
					current = current.getParent();
				} else if ("/".equals(target)) {
					current = root;
				} else {
					current = (Directory) current.getNodes().stream().filter(n -> n.getName().equals(target)).findAny()
							.get();
				}
				continue;
			}
			Matcher itemMatcher = ITEM_PATTERN.matcher(line);
			if (!itemMatcher.matches()) {
				throw new IllegalArgumentException(line);
			}
			if (itemMatcher.group(1).equals("dir")) {
				current.addNode(new Directory(current, itemMatcher.group(2)));
			} else {
				current.addNode(new File(current, itemMatcher.group(2), Long.parseLong(itemMatcher.group(1))));
			}
		}
		System.out.println(root.getPart1Size());
		var spaceToFree = REQUIRED_SPACE - (DISK_SIZE - root.getSize());
		System.out.println(root.getPart2DirSize(spaceToFree));
	}

	interface Node {
		Directory getParent();

		long getSize();

		String getName();

		long getPart1Size();

		long getPart2DirSize(long spaceToFree);
	}

	static class File implements Node {
		private final Directory parent;
		private final long size;
		private final String name;

		public File(Directory parent, String name, long size) {
			this.parent = parent;
			this.name = name;
			this.size = size;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public long getSize() {
			return size;
		}

		@Override
		public Directory getParent() {
			return parent;
		}

		@Override
		public long getPart1Size() {
			return 0;
		}

		@Override
		public long getPart2DirSize(long spaceToFree) {
			return 0;
		}
	}

	static class Directory implements Node {
		private final Directory parent;
		private final String name;

		private List<Node> subNodes = new ArrayList<>();

		public Directory(Directory parent, String name) {
			this.parent = parent;
			this.name = name;
		}

		public void addNode(Node node) {
			subNodes.add(node);
		}

		public List<Node> getNodes() {
			return subNodes;
		}

		@Override
		public long getSize() {
			return subNodes.stream().mapToLong(Node::getSize).sum();
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Directory getParent() {
			return parent;
		}

		@Override
		public long getPart1Size() {
			var size = getSize();
			size = size <= MAX_SIZE ? size : 0;
			return size + subNodes.stream().mapToLong(Node::getPart1Size).sum();
		}

		@Override
		public long getPart2DirSize(long spaceToFree) {
			var size = getSize();
			return getNodes().stream().mapToLong(n -> n.getPart2DirSize(spaceToFree)).filter(s -> s > spaceToFree).min()
					.orElse(size);
		}

	}
}
