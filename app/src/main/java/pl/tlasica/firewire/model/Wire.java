package pl.tlasica.firewire.model;

public class Wire {

    public int a;
    public int b;

    public Wire(int a, int b) {
        this.a = Math.min(a, b);
        this.b = Math.max(a, b);
    }

    public int dirFrom(int p) {
        int q = (a==p) ? b : a;
        int dx = IntCoord.x(q) - IntCoord.x(p);
        int dy = IntCoord.y(q) - IntCoord.y(p);
        if (dx==0) {
            if (dy < 0) return 0;   // N
            else return 180;        // S
        }
        if (dx < 0) {
            if (dy < 0) return 315; // NW
            if (dy > 0) return 225; // SW
            else return 270;        // W
        }
        else {
            if (dy < 0) return 45;  // NE
            if (dy > 0) return 135; // SE
            else return 90;         // E
        }
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
