package pl.tlasica.firewireapp.model;


public enum ConnectorType {
    I_SHAPE,    // 180 deg N-S
    L_SHAPE,    // 90 deg N-W
    T_SHAPE,    // 90 deg T -shaped W-N-E
    X_SHAPE,    // 90 deg W, N, E ,S
    V_SHAPE,    // 45 deg e. N,NW
    C_SHAPE,    // 135 deg
    FULL,       // connects every possible direction
    DEFINED;    // pre-defined on board description, with specified degs

    /**
     * get array of degrees connected by the connector type with given rotation
     */
    public int[] directions(int rotation) {
        switch(this) {
            case I_SHAPE: return rotate(iDirs, rotation);
            case L_SHAPE: return rotate(lDirs, rotation);
            case X_SHAPE: return rotate(xDirs, rotation);
            case T_SHAPE: return rotate(tDirs, rotation);
            case C_SHAPE: return rotate(cDirs, rotation);
            case V_SHAPE: return rotate(vDirs, rotation);
            case FULL: return fullDirs;
            case DEFINED: return null;
            default: return null;
        }
    }

    private static int[] rotate(int[] base, int angle) {
        int[] ret = base.clone();
        for(int i=0; i<ret.length; ++i) {
            int d = ret[i];
            ret[i] = (d + angle) % 360;
        }
        return ret;
    }

    private final static int[] iDirs = {0,180};    // |
    private final static int[] lDirs = {0,90};     // |_
    private final static int[] tDirs = {0,90,180}; // |-
    private final static int[] xDirs = {0,90,180,270}; // |-
    private final static int[] vDirs = {0,45};
    private final static int[] cDirs = {0,135};
    private final static int[] fullDirs = {0,45,90,135,180,225,270,315,360};
}
