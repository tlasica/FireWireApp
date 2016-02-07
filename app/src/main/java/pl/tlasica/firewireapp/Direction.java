package pl.tlasica.firewireapp;

public class Direction {

    public static int vx(String dir) {
        if (dir.contains("E")) return +1;
        else if (dir.contains("W")) return -1;
        else return 0;
    }

    public static int vy(String dir) {
        if (dir.contains("N")) return -1;
        else if (dir.contains("S")) return +1;
        else return 0;
    }

}
