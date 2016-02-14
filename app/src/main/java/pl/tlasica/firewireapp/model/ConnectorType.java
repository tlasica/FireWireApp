package pl.tlasica.firewireapp.model;

public enum ConnectorType {
    I_SHAPE(0),    // 180 deg N-S
    L_SHAPE(1),    // 90 deg N-W
    T_SHAPE(2),    // 90 deg T -shaped W-N-E
    X_SHAPE(3),    // 90 deg W, N, E ,S
    FULL(99); // connects every possible direction

    public int index;

    ConnectorType(int idx) {
        this.index = idx;
    }

}
