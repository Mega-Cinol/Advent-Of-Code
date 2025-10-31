package y2017.day13;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.Input;

public class Day13 {

	public static void main(String[] args) {
		Set<Scanner> scanners = Input.parseLines("y2017/day13/day13.txt", Scanner::fromString).collect(Collectors.toSet());
		boolean safe = false;
		int delay = 0;
		while (!safe)
		{
			int delayCpy = delay;
			safe = scanners.stream().noneMatch(sc -> sc.locationAtTime(delayCpy + sc.depth) == 0);
			delay++;
		}
		System.out.println(delay - 1);
	}

	private static class Scanner
	{
		public int depth;
		public int range;

		private int locationAtTime(int time)
		{
			int wholeTrip = range * 2 - 2;
			int offset = time % wholeTrip;
			if (offset >= range)
			{
				offset %= range;
				return range - offset - 2;
			} else {
				return offset;
			}
		}
		public static Scanner fromString(String desc)
		{
			Pattern p = Pattern.compile("(\\d+): (\\d+)");
			Matcher m = p.matcher(desc);
			System.out.println(m.matches());
			int depth = Integer.parseInt(m.group(1));
			int range = Integer.parseInt(m.group(2));
			Scanner sc = new Scanner();
			sc.depth = depth;
			sc.range = range;
			return sc;
		}
	}
}
