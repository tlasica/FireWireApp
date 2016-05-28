package pl.tlasica.firewireapp.play;

// TODO: add persistency

/**
 * Holds information about current level to play, history etc.
 */
public class Player {

    private static final Player player = new Player();

    private int nextGameNo = 1;
    private int nextLevelNo = 1;

    public int nextLevel() {
        return nextLevelNo;
    }

    public int nextGame() {
        return nextGameNo;
    }

    // mark current game as finished with success
    public void gameFinishedWithSuccess() {
        this.nextGameNo++;
        //TODO: move to next level
    }

    public void gameCancelled() {
        //TODO: ?
    }

    public static Player get() {
        return player;
    }

}
