package pl.tlasica.firewireapp.play;

// TODO: add persistency

import android.util.Log;
import pl.tlasica.firewireapp.parser.BoardLoader;

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
        if (this.nextGameNo > BoardLoader.levelSizes[nextLevelNo]) {
            this.nextGameNo = 1;
            this.nextLevelNo++;
            Log.d("Player", "Level Up. Next level: " + String.valueOf(nextLevelNo));
        }
    }

    public void gameCancelled() {
        //TODO: ?
    }

    public static Player get() {
        return player;
    }

}
