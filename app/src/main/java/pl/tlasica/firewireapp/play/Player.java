package pl.tlasica.firewireapp.play;

// TODO: add persistency

import android.util.Log;
import pl.tlasica.firewireapp.parser.BoardLoader;

/**
 * Holds information about current level to play
 */
public class Player {

    private static final Player player = new Player();

    private int currLevelId = LevelId.levelId(1, 1);

    public int currentLevelId() {
        return currLevelId;
    }

    public void setNextLevel() {
        currLevelId = BoardLoader.nextLevelId(currentLevelId());
    }

    // mark current game as finished with success
    public void gameFinishedWithSuccess() {
        //TODO: save the fact that game is solved
        //TODO: save game statistics
        //TODO: update points per level
        Log.d("Player", "Level finished: " + String.valueOf(currentLevelId()));
    }

    public void gameCancelled() {
        //TODO: ?
    }

    public static Player get() {
        return player;
    }

}
