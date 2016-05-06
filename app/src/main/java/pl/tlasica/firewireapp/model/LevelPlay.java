package pl.tlasica.firewireapp.model;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes current play on some level, including all information presented on the Play screen.
 * Includes board for the level, name of the level and current status:
 * placed and available connectors.
 *
 * It also servers as a singleton with current() and startLevel() methods
 */
//TODO: connectors placed as part of the board need special approach?
public class LevelPlay {

    public Board                            board = null;
    private static LevelPlay                currentPlay = null;

    public Map<ConnectorType, Integer>     availableConnectors;
    public Map<Integer, PlacedConnector>   placedConnectors;


    public static LevelPlay current() {
        return currentPlay;
    }

    public static void startLevel(Board b) {
        LevelPlay play = new LevelPlay(b);
        currentPlay = play;
    }

    public static void restart() {
        if (current() != null) {
            startLevel(current().board);
        }
    }

    private LevelPlay(Board b) {
        this.board = b;
        prepareConnectors(b);
    }

    private void prepareConnectors(Board b) {
        availableConnectors = new HashMap<>(b.connectors);
        placedConnectors = new HashMap<>();
    }

    /**
     * place connector of given type at given position
     * - removes from available list
     * - placed on a board with first available rotation
     */
    public void placeConnector(ConnectorType type, int position, int rotation) {
        PlacedConnector placedConn = new PlacedConnector(type, rotation);
        int curr = availableConnectors.get(type);
        availableConnectors.put(type, curr-1);
        placedConnectors.put(position, placedConn);
    }

    public int tryPlaceConnector(ConnectorType type, int position) {
        // if this node is free
        if (!board.isFree(position)) {
            Log.d("", "This position is not a free slot: " + position);
            return -1;
        }
        // if position is not already occupied
        if (connectorAt(position) != null) {
            Log.d("", "This position is already occupied by connector: " + position);
            return -1;
        }
        // if we have connectors of this type
        Integer connLeft = availableConnectors.get(type);
        if (connLeft==null || connLeft==0) {
            Log.d("", "No more connectors of this type left: " + type);
            return -1;
        }
        int[] possibleRotations = board.possibleRotations(type, position);
        if (possibleRotations != null) {
            return possibleRotations[0];
        }
        else {
            Log.d("", "Not possible to place this type of connector at " + position);
            return -1;
        }
    }

    public int tryRotateConnector(int position) {
        PlacedConnector conn = connectorAt(position);
        if (conn == null) {
            Log.d("", "Not possible to rotate non-existing connector at " + position);
            return -1;
        }
        int[] possibleRotations = board.possibleRotations(conn.type, position);
        if (possibleRotations.length <= 1) {
            Log.d("", "No other rotations at position " + position);
            return -1;
        }
        int nextRotation = 0;
        for(int i=0; i<possibleRotations.length; ++i) {
            int d = possibleRotations[i];
            if (d == conn.rotation) {
                nextRotation = (i+1) % possibleRotations.length;
                break;
            }
        }
        return possibleRotations[nextRotation];
    }

    public void rotateConnector(int position, int rotation) {
        PlacedConnector placedConn = connectorAt(position);
        placedConn.rotation = rotation;
    }

    public boolean tryRemoveConnector(int position) {
        return placedConnectors.containsKey(position);
    }

    public void removeConnector(int position) {
        PlacedConnector conn = connectorAt(position);
        int count = 1 + availableConnectors.get(conn.type);
        availableConnectors.put(conn.type, count);
        placedConnectors.remove(position);
    }

    public PlacedConnector connectorAt(int pos) {
        return placedConnectors.get(pos);
    }
}
