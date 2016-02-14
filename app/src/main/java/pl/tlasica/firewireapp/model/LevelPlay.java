package pl.tlasica.firewireapp.model;

/**
 * Describes current play on some level, including all information presented on the Play screen.
 * Includes board for the level, name of the level and current status:
 * placed and available connectors.
 *
 * It also servers as a singleton with current() and startLevel() methods
 */
public class LevelPlay {

    public Board                board = null;
    private static LevelPlay    currentPlay = null;

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
        //TODO: copy connectors from board description to list of available connectors
    }
}
