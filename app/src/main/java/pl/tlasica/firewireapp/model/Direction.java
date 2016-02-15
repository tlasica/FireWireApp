package pl.tlasica.firewireapp.model;

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

    public static int vx(int dir) {
        if (dir==45 || dir==90 || dir==135) return +1;
        if (dir==225 || dir==270 || dir==315) return -1;
        else return 0;
    }

    public static int vy(int dir) {
        if (dir==135 || dir==180 || dir==225) return +1;
        if (dir==315 || dir==0 || dir==45) return -1;
        else return 0;
    }

}
