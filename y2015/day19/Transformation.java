package y2015.day19;

public class Transformation implements Comparable<Transformation>{
	private final MoleculeNode source;
	private final Molecule target;
	private final Molecule updatedMolecule;
	private final int targetSize;

	public Transformation(MoleculeNode source, Molecule target,Molecule updatedMolecule, int targetSize) {
		this.source = source;
		this.target = target;
		target.getFirst().setPrevious(source.getPrevious());
		target.getLast().setNext(source.getNext());
		this.updatedMolecule = updatedMolecule;
		this.targetSize = targetSize;
	}

	public void transform() {
		if (source.getPrevious() != null) {
			source.getPrevious().setNext(target.getFirst());
		} else
		{
			updatedMolecule.setFirst(target.getFirst());
		}
		if (source.getNext() != null) {
			source.getNext().setPrevious(target.getLast());
		} else {
			updatedMolecule.setLast(target.getLast());
		}
	}

	public void undo()
	{
		if (target.getFirst().getPrevious() != null) {
			target.getFirst().getPrevious().setNext(source);
		} else {
			updatedMolecule.setFirst(source);
		}
		if (target.getLast().getNext() != null) {
			target.getLast().getNext().setPrevious(source);
		} else {
			updatedMolecule.setLast(source);
		}
	}

	@Override
	public int compareTo(Transformation o) {
		return o.targetSize - targetSize;
	}
}
