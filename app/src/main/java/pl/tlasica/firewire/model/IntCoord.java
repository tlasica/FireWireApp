package pl.tlasica.firewire.model;

/**
 * Helper class to translate integer to (x,y) coordinates
 */
public class IntCoord {

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
