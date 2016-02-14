package pl.tlasica.firewireapp.model;

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

    private LevelPlay(Board b) {
        this.board = b;
        prepareConnectors(b);
    }

    private void prepareConnectors(Board b) {
        availableConnectors = new HashMap<>(b.connectors);
        placedConnectors = new HashMap<>();
    }
}
