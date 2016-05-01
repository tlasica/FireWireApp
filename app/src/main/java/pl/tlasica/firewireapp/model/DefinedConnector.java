package pl.tlasica.firewireapp.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Defined connector, to be placed on board with specified directions
 */
public class DefinedConnector extends PlacedConnector {

    public static final DefinedConnector FULL = new DefinedConnector("N,NE,E,SE,S,SW,W,NW");

    public static final DefinedConnector fromDirs(String dirs) {
        return new DefinedConnector(dirs);
    }

    private DefinedConnector(String dirs) {
        super(ConnectorType.DEFINED, 0);
        this.definedDirections = parse(dirs);
    }

    private Set<Integer> parse(String dirs) {
        Set<Integer> ret = new HashSet<>();
        for(String d: dirs.split(",")) {
            int deg = Direction.degForDir(d);
            ret.add(deg);
        }
        return ret;
    }

    private Set<Integer> definedDirections = null;

    public int[] definedDirections() {
        int[] ret = new int[this.definedDirections.size()];
        int index = 0;
        for(int v: definedDirections) {
            ret[index++] = v;
        }
        return ret;
    }


}