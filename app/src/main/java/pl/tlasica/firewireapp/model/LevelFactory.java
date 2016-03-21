package pl.tlasica.firewireapp.model;

public class LevelFactory {

    public static Board standard() {
        Board b = new Board();
        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                b.with(IntCoord.i(x, y), IntCoord.i(x + 1, y));
                b.with(IntCoord.i(x, y), IntCoord.i(x, y + 1));
            }
        }

        b.with(21, 32);
        b.with(21, 12);
        b.with(12, 23);
        b.with(23, 32);
        b.remove(12, 22);
        b.with(22, 33);
        b.with(35, 24);
        b.with(33, 24);
        b.with(33, 44);
        b.with(35, 44);
        b.with(11, 22);
        b.with(32, 43);
        b.with(33, 35);
        b.with(32, 41);

        b.target = 22;
        b.plus = 0;
        b.minus = 55;

        b.connectors.put(ConnectorType.I_SHAPE, 2);
        b.connectors.put(ConnectorType.L_SHAPE, 5);
        b.connectors.put(ConnectorType.X_SHAPE, 1);

        return b;
    }
}
