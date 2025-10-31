package y2015.day19;

public enum Atom {
	E(false), H(false), N(false), O(false),
	AL(false), TH(false), F(false), B(false), CA(false), TI(false), P(false), SI(false), MG(false),
	RN(true), AR(true), Y(true), C(true);

	private final boolean fixed;

	Atom(boolean fixed) {
		this.fixed = fixed;
	}

	public boolean isFixed() {
		return fixed;
	}

	@Override
	public String toString()
	{
		String name = name();
		return name.substring(0, 1) + name.substring(1).toLowerCase();
	}
}
