package pl.tlasica.firewireapp;

public class Coord {

    public static int x(int i) {
        return i / 10;
    }

    public static int y(int i) {
        return i % 10;
    }

    public static int i(int x, int y) {
        return x*10 + y;
    }
}
