package common.equations;

public record Variable(String name, long exp) {
	public Variable(String name) {
		this(name, 1);
	}

	@Override
	public String toString() {
		if (exp == 1) {
			return name;
		}
		return name + "^" + exp;
	}
}
