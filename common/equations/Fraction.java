package common.equations;

public record Fraction(long top, long bottom) {
    public static Fraction parse(String fractionDesc) {
        var fractionParts = fractionDesc.split("/");
        var top = Long.parseLong(fractionParts[0]);
        var bottom = fractionParts.length > 1 ? Long.parseLong(fractionParts[1]) : 1;
        return new Fraction(top, bottom);
    }

    public Fraction add(Fraction other) {
        var newTop = top * other.bottom + other.top * bottom;
        var newBottom = bottom * other.bottom;
        return new Fraction(newTop, newBottom).normalize();
    }

    public Fraction add(long value) {
        var newTop = top  + value * bottom;
        return new Fraction(newTop, bottom).normalize();
    }

    public Fraction subtract(Fraction other) {
        var newTop = top * other.bottom - other.top * bottom;
        var newBottom = bottom * other.bottom;
        return new Fraction(newTop, newBottom).normalize();
    }

    public Fraction subtract(long value) {
        var newTop = top  - value * bottom;
        return new Fraction(newTop, bottom).normalize();
    }

    public Fraction multiply(Fraction other) {
        var newTop = top * other.top;
        var newBottom = bottom * other.bottom;
        return new Fraction(newTop, newBottom).normalize();
    }

    public Fraction multiply(long value) {
        var newTop = top * value;
        return new Fraction(newTop, bottom).normalize();
    }

    public Fraction divide(Fraction other) {
        var newTop = top * other.bottom;
        var newBottom = bottom * other.top;
        return new Fraction(newTop, newBottom).normalize();
    }

    public Fraction divide(long value) {
        var newBottom = bottom * value;
        return new Fraction(top, newBottom).normalize();
    }

    public Fraction power(long value) {
        var newTop = (long) Math.pow(top, value);
        var newBottom = (long) Math.pow(bottom, value);
        return new Fraction(newTop, newBottom).normalize();
    }

    public double toDouble() {
        return (double) top / (double) bottom;
    }

    @Override
    public String toString() {
        return bottom == 1 ? ("" + top) : (top + "/" + bottom);
    }

    public Fraction normalize() {
        var commonFactor = greatestCommonFactor();
        return new Fraction(top / commonFactor, bottom / commonFactor);
    }

    public boolean isEqual(Fraction other) {
        return subtract(other).isZero();
    }

    public boolean isEqual(Long other) {
        return subtract(other).isZero();
    }

    public boolean isZero() {
        return top == 0;
    }

    private long greatestCommonFactor() {
        var a = top;
        var b = bottom;
        while (b != 0) {
            var t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    public static void main(String[] args) {
        var fraction = Fraction.parse("2/4").normalize();
        fraction = fraction.add(new Fraction(1, 4));
        System.out.println(fraction);
        fraction = fraction.multiply(new Fraction(1, 4));
        System.out.println(fraction);
        fraction = fraction.subtract(new Fraction(1, 16));
        System.out.println(fraction.multiply(4).add(1));
        System.out.println(fraction.toDouble());
    }
}
