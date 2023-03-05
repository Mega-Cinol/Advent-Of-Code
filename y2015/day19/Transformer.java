package y2015.day19;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Transformer {
	private final Atom src;
	private final Molecule target = new Molecule();
	private final int targetSize;

	public Transformer(String rule) {
		Pattern rulePattern = Pattern.compile("([a-zA-Z]+) => ([a-zA-Z]*)");
		Matcher ruleMatcher = rulePattern.matcher(rule);
		ruleMatcher.matches();
		src = Atom.valueOf(ruleMatcher.group(1).toUpperCase());
		target.fromString(ruleMatcher.group(2));
		targetSize = target.size();
	}

	public Set<Transformation> canTransform(Molecule input) {
		Set<Transformation> possibleResults = new TreeSet<>();
		MoleculeNode lastIndexOf = input.find(src);
		while (lastIndexOf != null) {
			possibleResults.add(new Transformation(lastIndexOf, target.copy(), input, targetSize));
			lastIndexOf = input.find(src, lastIndexOf);
		}
		return possibleResults;
	}
}