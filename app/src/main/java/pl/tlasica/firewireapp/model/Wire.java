package pl.tlasica.firewireapp.model;

public class Wire {

    public int a;
    public int b;

    public Wire(int a, int b) {
        this.a = Math.min(a, b);
        this.b = Math.max(a, b);
    }

    @Override
    public boolean equals(Object o) {
        Wire that = (Wire)o;
        return (this.a==that.a) && (this.b==that.b);
    }

    @Override
    public int hashCode() {
        return 101*a + b;
    }
}
