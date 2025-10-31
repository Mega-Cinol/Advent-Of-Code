package y2023.day5;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;

public class Day5 {

	public static void main(String[] args) {
		var input = Input.parseLines("y2023/day5/day5.txt").toList();

		Converter.Builder converterBuilder = null;
		var converters = new HashMap<String, Converter>();
		for (var line : input.subList(2, input.size() - 1)) {
			if (converterBuilder == null) {
				converterBuilder = new Converter.Builder();
				converterBuilder.addHeader(line);
				continue;
			}
			if (line.isBlank()) {
				var newConverter = converterBuilder.build();
				converters.put(newConverter.sourceType(), newConverter);
				converterBuilder = null;
				continue;
			}
			converterBuilder.addConverter(line);
		}
		var newConverter = converterBuilder.build();
		converters.put(newConverter.sourceType(), newConverter);

		// part 1
		var initItems = getInitItems(input.get(0));
		System.out.println(initItems.stream().map(item -> mapToType(item, "location", converters))
				.mapToLong(Item::value).min().getAsLong());

		// part 2
		var initRangeItems = getInitRanges(input.get(0));
		System.out.println(initRangeItems.stream().map(item -> mapToType(item, "location", converters)).flatMap(Set::stream)
				.mapToLong(RangedItem::minValue).min().getAsLong());
	}

	private static Item mapToType(Item item, String targetType, Map<String, Converter> converters) {
		var mappedItem = item;
		while (!mappedItem.type().equals(targetType)) {
			var converter = converters.get(mappedItem.type());
			if (converter == null) {
				throw new IllegalArgumentException("Can't find converter for %s, known types: %s"
						.formatted(mappedItem.type(), converters.keySet()));
			}
			mappedItem = converter.convert(mappedItem);
		}
		return mappedItem;
	}

	private static Set<RangedItem> mapToType(RangedItem item, String targetType, Map<String, Converter> converters) {
		var mappedItems = new HashSet<RangedItem>();
		var toMap = new HashSet<RangedItem>();
		toMap.add(item);
		while (!toMap.isEmpty()) {
			var next = new HashSet<RangedItem>();
			toMap.forEach(itemToMap -> {
				var converter = converters.get(itemToMap.type());
				if (converter == null) {
					throw new IllegalArgumentException("Can't find converter for %s, known types: %s"
							.formatted(itemToMap.type(), converters.keySet()));
				}
				var mapped = converter.convertRange(itemToMap);
				mapped.stream().filter(mappedRange -> mappedRange.type().equals(targetType)).forEach(mappedItems::add);
				mapped.stream().filter(mappedRange -> !mappedRange.type().equals(targetType)).forEach(next::add);
			});
			toMap.clear();
			toMap.addAll(next);
		}
		return mappedItems;
	}

	private static Set<Item> getInitItems(String itemsDefinition) {
		var itemPattern = Pattern.compile("\\d+");
		var itemMatcher = itemPattern.matcher(itemsDefinition);
		var items = new HashSet<Item>();
		while (itemMatcher.find()) {
			items.add(new Item("seed", Long.parseLong(itemMatcher.group())));
		}
		return items;
	}

	private static Set<RangedItem> getInitRanges(String itemsDefinition) {
		var itemPattern = Pattern.compile("(\\d+)\\s*(\\d+)");
		var itemMatcher = itemPattern.matcher(itemsDefinition);
		var items = new HashSet<RangedItem>();
		while (itemMatcher.find()) {
			items.add(new RangedItem("seed", Long.parseLong(itemMatcher.group(1)),
					Long.parseLong(itemMatcher.group(1)) + Long.parseLong(itemMatcher.group(2))));
		}
		return items;
	}

	private static record Item(String type, long value) {
	}

	private static record Range(long minValue, long maxValue) {
		public boolean overlapping(long otherMinValue, long otherMaxValue) {
			return (minValue <= otherMinValue && maxValue >= otherMinValue)
					|| (maxValue >= otherMaxValue && minValue <= otherMaxValue);
		}

		public boolean contained(long otherMinValue, long otherMaxValue) {
			return (minValue >= otherMinValue && maxValue <= otherMaxValue);
		}

		public boolean containing(long otherMinValue, long otherMaxValue) {
			return (minValue <= otherMinValue && maxValue >= otherMaxValue);
		}

		public Set<Range> splitByRange(long otherMinValue, long otherMaxValue) {
			if (containing(otherMinValue, otherMaxValue)) {
				var resultSet = new HashSet<Range>();
				resultSet.add(new Range(otherMinValue, otherMaxValue));
				if (minValue < otherMinValue) {
					resultSet.add(new Range(minValue, otherMinValue - 1));
				}
				if (maxValue > otherMaxValue) {
					resultSet.add(new Range(otherMaxValue + 1, maxValue));
				}
				return resultSet;
			}
			if (contained(otherMinValue, otherMaxValue) || !overlapping(otherMinValue, otherMaxValue)) {
				return Set.of(this);
			}
			if (minValue < otherMinValue) {
				return Set.of(new Range(minValue, otherMinValue - 1), new Range(otherMinValue, maxValue));
			} else {
				return Set.of(new Range(minValue, otherMaxValue), new Range(otherMaxValue + 1, maxValue));
			}
		}
	}

	private static record RangedItem(String type, long minValue, long maxValue) {
	}

	private static record Converter(String sourceType, String targetType, SortedSet<RangeConverter> converters) {

		public boolean canConvert(Item item) {
			return item.type().equals(sourceType);
		}

		public boolean canConvert(RangedItem item) {
			return item.type().equals(sourceType);
		}

		public Item convert(Item item) {
			if (!canConvert(item)) {
				throw new IllegalArgumentException(item.toString());
			}
			var newValue = converters.stream().filter(converter -> converter.inRange(item.value()))
					.mapToLong(converter -> converter.convert(item.value())).findAny().orElse(item.value());
			return new Item(targetType, newValue);
		}

		public Set<RangedItem> convertRange(RangedItem item) {
			if (!canConvert(item)) {
				throw new IllegalArgumentException(item.toString());
			}
			var convertedValues = new HashSet<Range>();
			var unconvertedValues = new HashSet<Range>();
			unconvertedValues.add(new Range(item.minValue(), item.maxValue()));
			converters.forEach(converter -> {
				var overlapping = unconvertedValues.stream()
						.filter(range -> range.overlapping(converter.lowest(), converter.highest()) || range.contained(converter.lowest(), converter.highest()))
						.collect(Collectors.toSet());
				unconvertedValues.removeAll(overlapping);
				overlapping.forEach(overlappingRange -> {
					var split = overlappingRange.splitByRange(converter.lowest(), converter.highest());
					split.forEach(splitRange -> {
						if (splitRange.contained(converter.lowest(), converter.highest())) {
							convertedValues.add(converter.convertRange(splitRange));
						} else {
							unconvertedValues.add(splitRange);
						}
					});
				});
			});
			convertedValues.addAll(unconvertedValues);

			return convertedValues.stream().map(value -> new RangedItem(targetType, value.minValue(), value.maxValue()))
					.collect(Collectors.toSet());
		}

		public static class Builder {
			private String sourceType;
			private String targetType;
			private SortedSet<RangeConverter> converters = new TreeSet<>();

			public Builder addHeader(String header) {
				var headerPattern = Pattern.compile("([a-z]+)-to-([a-z]+) map:");
				var headerMatcher = headerPattern.matcher(header);
				if (!headerMatcher.matches()) {
					throw new IllegalArgumentException(header);
				}
				sourceType = headerMatcher.group(1);
				targetType = headerMatcher.group(2);
				return this;
			}

			public Builder addConverter(String converterDefinition) {
				converters.add(RangeConverter.from(converterDefinition));
				return this;
			}

			public Converter build() {
				return new Converter(sourceType, targetType, converters);
			}
		}
	}

	private static record RangeConverter(long lowest, long highest, long offset) implements Comparable<RangeConverter> {
		public static RangeConverter from(String converterDefinition) {
			var converterPattern = Pattern.compile("(\\d+) (\\d+) (\\d+)");
			var converterMatcher = converterPattern.matcher(converterDefinition);
			if (!converterMatcher.find()) {
				throw new IllegalArgumentException(converterDefinition);
			}
			var targetStart = Long.parseLong(converterMatcher.group(1));
			var sourceStart = Long.parseLong(converterMatcher.group(2));
			var rangeSize = Long.parseLong(converterMatcher.group(3));
			return new RangeConverter(sourceStart, sourceStart + rangeSize - 1, targetStart - sourceStart);
		}

		public long convert(long value) {
			return value + offset;
		}

		public Range convertRange(Range range) {
			if (lowest > range.minValue || highest < range.maxValue) {
				throw new IllegalArgumentException(
						"Can't convert range %s, allowed values: %d - %d".formatted(range.toString(), lowest, highest));
			}
			return new Range(convert(range.minValue), convert(range.maxValue));
		}

		public boolean inRange(long value) {
			return value >= lowest && value <= highest;
		}

		@Override
		public int compareTo(RangeConverter o) {
			return lowest > o.lowest() ? 1 : lowest < o.lowest ? -1 : 0;
		}
	}
}
