package pl.tlasica.firewireapp.model;

public class LevelFactory {

    public static Board standard() {
        Board b = new Board();
        for (int x = 0; x < 6; ++x) {
            for (int y = 0; y < 6; ++y) {
                b.with(IntCoord.i(x, y), IntCoord.i(x + 1, y));
                b.with(IntCoord.i(x, y), IntCoord.i(x, y + 1));
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
        b.remove(32, 42);
        b.remove(43, 54);
        b.remove(12, 22);
        b.with(33, 44);
        b.with(35, 44);
        b.with(11, 22);
        b.with(32, 43);
        b.with(43, 43);
        b.with(33, 35);
        b.with(32, 41);
        b.remove(51, 61);
        b.remove(51, 41);
        b.remove(51, 50);
        b.remove(51, 52);
        b.remove(42, 51);
        b.remove(42, 41);
        b.remove(42, 52);

        b.plus = 0;
        b.minus = 55;
        b.target = 22;

        b.connectors.put(ConnectorType.I_SHAPE, 5);
        b.connectors.put(ConnectorType.L_SHAPE, 4);
        b.connectors.put(ConnectorType.X_SHAPE, 1);

        return b;
    }
        /*
        (2,2)
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
