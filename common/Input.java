package common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class Input {
	public static Stream<String> parseLines(String path) {
		return parseLines(path, Function.identity());
	}

	public static <T> void parseLines(String path, Function<String, T> converter, Consumer<T> consumer) {
		parseLines(path, converter).forEach(consumer);
	}

	public static <T> Stream<T> parseLines(String path, Function<String, T> converter) {
		try {
			return Files.lines(Paths.get(path)).map(converter);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static <T> Map<Point, T> parseGrid(String path, Function<Character, T> converter) {
		var input = parseLines(path).toList();
		var grid = new HashMap<Point, T>();
		for (int y = 0 ; y < input.size() ; y++) {
			for (int x = 0 ; x < input.get(y).length() ; x++) {
				grid.put(new Point(x, y), converter.apply(input.get(y).charAt(x)));
			}
		}
		return grid;
	}
}
