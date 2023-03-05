package y2015.day19;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Day19 {

	public static void main(String[] args) {
		String medicine = "CRnSiRnCaPTiMgYCaPTiRnFArSiThFArCaSiThSiThPBCaCaSiRnSiRnTiTiMgArPBCaPMgYPTiRnFArFArCaSiRnBPMgArPRnCaPTiRnFArCaSiThCaCaFArPBCaCaPTiTiRnFArCaSiRnSiAlYSiThRnFArArCaSiRnBFArCaCaSiRnSiThCaCaCaFYCaPTiBCaSiThCaSiThPMgArSiRnCaPBFYCaCaFArCaCaCaCaSiThCaSiRnPRnFArPBSiThPRnFArSiRnMgArCaFYFArCaSiRnSiAlArTiTiTiTiTiTiTiRnPMgArPTiTiTiBSiRnSiAlArTiTiRnPMgArCaFYBPBPTiRnSiRnMgArSiThCaFArCaSiThFArPRnFArCaSiRnTiBSiThSiRnSiAlYCaFArPRnFArSiThCaFArCaCaSiThCaCaCaSiRnPRnCaFArFYPMgArCaPBCaPBSiRnFYPBCaFArCaSiAl";
		Molecule molecule = new Molecule();
		molecule.fromString(medicine);

//		List<Transformer> transformers = new ArrayList<>();
//		Input.parseLines("y2015/day19/day19.txt", Transformer::new).forEach(transformers::add);
//				.map(t -> t.canTransform(molecule)).flatMap(Set::stream).distinct().count());
		Molecule current = new Molecule();
		current.add(Atom.E);
//		System.out.println(createMedicine(current, molecule, transformers, 0));
		System.out.println(countSteps(molecule));
	}

	@SuppressWarnings("unused")
	private static int createMedicine(Molecule current, Molecule target, List<Transformer> transformers, int depth) {
		if (current.equals(target)) {
			System.out.println("Found: " + depth);
			return depth;
		}
//		if (depth >= 60) {
//			return -1;
//		}
		if (!current.canTransformTo(target)) {
			return -1;
		}
//		current.print();
		int min = Integer.MAX_VALUE;
		Set<Transformation> transformations = transformers.stream().map(t -> t.canTransform(current))
				.flatMap(Set::stream).collect(Collectors.toCollection(TreeSet::new));
		for (Transformation transformation : transformations) {
			transformation.transform();
			int transformationResult = createMedicine(current, target, transformers, depth + 1);
			if (transformationResult >= 0) {
				min = Math.min(min, transformationResult);
			}
			transformation.undo();
		}
		return min;
	}

	private static int countSteps(Molecule molecule) {
		int stepCount = 0;
		Molecule reduced = new Molecule();
		stepCount += simpleReduce(molecule, reduced);
		reduced.print();
		System.out.println(stepCount);

		molecule = reduced;
		reduced = new Molecule();
		stepCount += noYReduce(molecule, reduced);
		reduced.print();
		System.out.println(stepCount);

		molecule = reduced;
		reduced = new Molecule();
		stepCount += simpleReduce(molecule, reduced);
		reduced.print();
		System.out.println(stepCount);

		molecule = reduced;
		reduced = new Molecule();
		stepCount += noYReduce(molecule, reduced);
		reduced.print();
		System.out.println(stepCount);

		molecule = reduced;
		reduced = new Molecule();
		stepCount += simpleReduce(molecule, reduced);
		reduced.print();
		System.out.println(stepCount);

		molecule = reduced;
		reduced = new Molecule();
		stepCount += oneYReduce(molecule, reduced);
		reduced.print();
		System.out.println(stepCount);

		molecule = reduced;
		reduced = new Molecule();
		stepCount += simpleReduce(molecule, reduced);
		reduced.print();
		System.out.println(stepCount);

		molecule = reduced;
		reduced = new Molecule();
		stepCount += noYReduce(molecule, reduced);
		reduced.print();
		System.out.println(stepCount);

		molecule = reduced;
		reduced = new Molecule();
		stepCount += oneYReduce(molecule, reduced);
		reduced.print();
		System.out.println(stepCount);

		molecule = reduced;
		reduced = new Molecule();
		stepCount += simpleReduce(molecule, reduced);
		reduced.print();
		System.out.println(stepCount);

		return stepCount;
	}

	private static int simpleReduce(Molecule input, Molecule reduced) {
		int stepCount = 0;
		Atom previous = null;
		for (MoleculeNode node : input) {
			Atom current = node.getAtom();
			if (canReduce(current) && canReduce(previous)) {
				stepCount++;
			} else {
				reduced.add(current);
			}
			previous = current;
		}
		return stepCount;
	}

	private static int noYReduce(Molecule input, Molecule reduced) {
		int stepCount = 0;
		for (MoleculeNode node = input.getFirst(); node != null; node = node.getNext()) {
			reduced.add(node.getAtom());
			if (node.getNext() != null && node.getNext().getAtom() == Atom.RN
					&& node.getNext().getNext().getNext().getAtom() == Atom.AR) {
				stepCount++;
				node = node.getNext().getNext().getNext();
			}
		}
		return stepCount;
	}

	private static int oneYReduce(Molecule input, Molecule reduced) {
		int stepCount = 0;
		for (MoleculeNode node = input.getFirst(); node != null; node = node.getNext()) {
			reduced.add(node.getAtom());
			if (node.getNext() != null && node.getNext().getAtom() == Atom.RN
					&& node.getNext().getNext().getNext().getAtom() == Atom.Y
					&& node.getNext().getNext().getNext().getNext().getNext().getAtom() == Atom.AR) {
				stepCount++;
				node = node.getNext().getNext().getNext().getNext().getNext();
			}
		}
		return stepCount;
	}

	private static boolean canReduce(Atom atom) {
		return atom != null && atom != Atom.AR && atom != Atom.RN && atom != Atom.Y;
	}
}
