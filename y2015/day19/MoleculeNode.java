package y2015.day19;

import y2015.day19.MoleculeNode;

public class MoleculeNode {
	private final Atom atom;
	private MoleculeNode previous;
	private MoleculeNode next;

	public MoleculeNode(Atom atom) {
		this.atom = atom;
	}

	public MoleculeNode getPrevious() {
		return previous;
	}

	public void setPrevious(MoleculeNode previous) {
		this.previous = previous;
	}

	public MoleculeNode getNext() {
		return next;
	}

	public void setNext(MoleculeNode next) {
		this.next = next;
	}

	public Atom getAtom() {
		return atom;
	}

	public void connectToAfter(MoleculeNode previous) {
		setPrevious(previous);
		if (previous != null) {
			previous.setNext(this);
		}
	}
	public void connectToBefore(MoleculeNode next) {
		setNext(next);
		if (next != null) {
			next.setPrevious(this);
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atom == null) ? 0 : atom.hashCode());
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
		MoleculeNode other = (MoleculeNode) obj;
		if (atom != other.atom)
			return false;
		return true;
	}
}
