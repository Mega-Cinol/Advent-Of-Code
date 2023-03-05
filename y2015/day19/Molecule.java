package y2015.day19;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Molecule implements Iterable<MoleculeNode> {
	private MoleculeNode first;
	private MoleculeNode last;

	public void setFirst(MoleculeNode first) {
		this.first = first;
	}

	public void setLast(MoleculeNode last) {
		this.last = last;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		MoleculeNode current = first;
		do {
			result = prime * result + current.getAtom().hashCode();
			current = current.getNext();
		} while (current != last);
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
		Molecule other = (Molecule) obj;
		MoleculeNode current = first;
		MoleculeNode otherCurrent = other.first;
		do {
			if (current.getAtom() != otherCurrent.getAtom()) {
				return false;
			}
			current = current.getNext();
			otherCurrent = otherCurrent.getNext();
		} while (current != last && otherCurrent != other.last);
		return current == last && otherCurrent == other.last;
	}

	public MoleculeNode getFirst() {
		return first;
	}

	public MoleculeNode getLast() {
		return last;
	}

	public boolean canTransformTo(Molecule other) {
		if (size() > other.size())
		{
			return false;
		}
		List<Atom> toAtoms = new ArrayList<>();

		for (MoleculeNode current : other) {
			if (current.getAtom().isFixed()) {
				toAtoms.add(current.getAtom());
			}
		}

		int targetIndex = 0;
		boolean nonFinalFound = false;
		for (MoleculeNode current : this) {
			if (current.getAtom().isFixed()) {
				if (!nonFinalFound && current.getAtom() != toAtoms.get(targetIndex)) {
					return false;
				}
				while (toAtoms.get(targetIndex) != current.getAtom()) {
					targetIndex++;
					if (targetIndex == toAtoms.size()) {
						return false;
					}
				}
				nonFinalFound = false;
			} else {
				nonFinalFound = true;
			}
		}
		if (!nonFinalFound && targetIndex < toAtoms.size() - 1) {
			return false;
		}
		return true;
	}

	public MoleculeNode find(Atom pattern, MoleculeNode current) {
		if (current == null) {
			current = first;
		} else {
			current = current.getNext();
		}
		if (current == null)
		{
			return null;
		}
		do {
			if (current.getAtom() == pattern) {
				return current;
			}
			current = current.getNext();
		} while ((current != last) && (current != null));
		if ((current != null) && (current.getAtom() == pattern))
		{
			return current;
		}
		return null;
	}

	public MoleculeNode find(Atom pattern) {
		return find(pattern, null);
	}

	public void mutate(MoleculeNode index, int amount, Molecule dst) {
		MoleculeNode current = index;
		for (int i = 0; i < amount; i++) {
			current = current.getNext();
		}
		current.setPrevious(dst.last);
		dst.last.setNext(current);
		dst.first.setPrevious(index.getPrevious());
		if (index.getPrevious() != null) {
			index.getPrevious().setNext(dst.first);
		}
	}

	public void fromString(String formula) {
		String atom = "" + formula.charAt(0);
		MoleculeNode current = first;
		for (int i = 1; i < formula.length(); i++) {
			if (formula.charAt(i) >= 'a' && formula.charAt(i) <= 'z') {
				atom += formula.charAt(i);
			} else {
				Atom enumAtom = Atom.valueOf(atom.toUpperCase());
				MoleculeNode next = new MoleculeNode(enumAtom);
				if (current != null) {
					next.setPrevious(current);
					current.setNext(next);
				} else {
					first = next;
				}
				current = next;
				atom = "" + formula.charAt(i);
			}
		}
		Atom enumAtom = Atom.valueOf(atom.toUpperCase());
		MoleculeNode next = new MoleculeNode(enumAtom);
		if (current != null) {
			next.setPrevious(current);
			current.setNext(next);
		} else {
			first = next;
		}
		last = next;
	}

	public int size() {
		int size = 0;
		for (MoleculeNode current = first; current != null; current = current.getNext()) {
			size++;
		}
		return size;
	}

	public void print() {
		int size = 0;
		for (MoleculeNode node : this) {
			System.out.print(node.getAtom());
			size++;
		}
		System.out.println();
		System.out.println("Size: " + size);
	}

	@Override
	public Iterator<MoleculeNode> iterator() {
		return new Iterator<MoleculeNode>() {
			private MoleculeNode current = first;

			@Override
			public boolean hasNext() {
				return current != null;
			}

			@Override
			public MoleculeNode next() {
				MoleculeNode result = current;
				current = current.getNext();
				return result;
			}
		};
	}

	public void add(Atom atom) {
		if (last == null) {
			first = new MoleculeNode(atom);
			last = first;
		} else {
			last.connectToBefore(new MoleculeNode(atom));
			last = last.getNext();
		}
	}

	public Molecule copy() {
		Molecule result = new Molecule();
		for (MoleculeNode nodes : this) {
			result.add(nodes.getAtom());
		}
		return result;
	}
}
