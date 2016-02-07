package pl.tlasica.firewireapp;

/**
 * Created by tomek on 2/6/16.
 */
public class BoardFactory {

    public static Board standard() {
        Board b = new Board();
        for (int x = 0; x < 6; ++x) {
            for (int y = 0; y < 6; ++y) {
                b.with(Coord.i(x, y), Coord.i(x + 1, y));
                b.with(Coord.i(x, y), Coord.i(x, y + 1));
            }
        }
        b.with(50, 61);
        b.with(21, 32);
        b.with(21, 12);
        b.with(12, 23);
        b.with(22, 32);
        b.with(22, 33);
        b.with(35, 24);
        b.with(33, 24);
        return b;
    }
        /*
        MATRIX(7,7)
                -(3,2)~(4,2)
        -(4,3)~(5,4)
        -(1,2)~(2,2)
        (3,3)~(4,4)
        (3,5)~(4,4)
        (1,1)~(2,2)
        (3,2)~(4,3)
        (3,3)~(3,5)
        (3,2)~(4,1)
        RED(2,2)
        PLUS(0,0)
        MINUS(5,5)
        (2,2)
        -(5,1)~(6,1)
        -(5,1)~(4,1)
        -(5,1)~(5,0)
        -(5,1)~(5,2)
        -(4,2)~(5,1)
        -(4,2)~(4,1)
        -(4,2)~(5,2)
        (4,2)~(5,1)
        (4,2)~(5,3)
        -(3,2)~(4,2)
        -(4,3)~(5,3)
        -(4,3)~(5,2)
        -(4,3)~(3,3)
        -(4,3)~(4,4)
        -(3,2)~(4,3)
    }
    */
}
