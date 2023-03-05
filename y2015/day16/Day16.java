package y2015.day16;

import java.util.HashMap;
import java.util.Map;

import common.Input;

public class Day16 {

	public static void main(String[] args) {
		Input.parseLines("y2015/day16/day16.txt", Day16::readAunt).filter(Day16::validAunt).findFirst().ifPresent(System.out::println);
	}

	private static boolean validAunt(Map<String, Integer> aunt)
	{
		boolean valid = true;
		valid &= aunt.get("children") == null || aunt.get("children") == 3;
		valid &= aunt.get("cats") == null || aunt.get("cats") > 7;
		valid &= aunt.get("samoyeds") == null || aunt.get("samoyeds") == 2;
		valid &= aunt.get("pomeranians") == null || aunt.get("pomeranians") < 3;
		valid &= aunt.get("akitas") == null || aunt.get("akitas") == 0;
		valid &= aunt.get("vizslas") == null || aunt.get("vizslas") == 0;
		valid &= aunt.get("goldfish") == null || aunt.get("goldfish") < 5;
		valid &= aunt.get("trees") == null || aunt.get("trees") > 3;
		valid &= aunt.get("cars") == null || aunt.get("cars") == 2;
		valid &= aunt.get("perfumes") == null || aunt.get("perfumes") == 1;
		return valid;
	}
	private static Map<String, Integer> readAunt(String aunt)
	{
		Map<String, Integer> auntProperties = new HashMap<String, Integer>();
		int auntId = Integer.parseInt(aunt.substring(4, aunt.indexOf(":")));
		auntProperties.put("Aunt", auntId);
		String auntDesc = aunt.substring(aunt.indexOf(":") + 1);
		for (String property : auntDesc.split(","))
		{
			String propName = property.substring(1, property.indexOf(":"));
			int propValue = Integer.parseInt(property.substring(property.indexOf(":") + 2, property.length()));
			auntProperties.put(propName, propValue);
		}
		return auntProperties;
	}
}
