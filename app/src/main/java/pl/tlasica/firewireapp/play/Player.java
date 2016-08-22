package pl.tlasica.firewireapp.play;

// TODO: add persistency

import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import pl.tlasica.firewireapp.parser.BoardLoader;

/**
 * Holds information about current level to play
 */
public class Player {

    private static final Player player = new Player();

    private int currLevelId = LevelId.levelId(1, 1);
    private Set<Integer> levelsSolved = new HashSet<>();

    public int currentLevelId() {
        return currLevelId;
    }

    public void setLevel(int levelId) {
        currLevelId=levelId;
    }

    public void setNextLevel() {
        currLevelId = BoardLoader.nextLevelId(currentLevelId());
    }

    // mark current game as finished with success
    public void gameFinishedWithSuccess() {
        // mark current level as solved
        markSolved(currentLevelId());
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

    public boolean isSolved(int levelId) {
        return levelsSolved.contains(levelId);
    }

    public void markSolved(int levelId) {
        levelsSolved.add(levelId);
    }

    public boolean canPlayLevel(int level) {
        // Player can play level if all games in previous level were solved
        boolean solved = true;
        for(int l=1; solved && l<level; l++) {
            solved = allGamesSolvedInLevel(l);
        }
        return solved;
    }

    private boolean allGamesSolvedInLevel(int level) {
        boolean solved = true;
        for(int game=1; solved && game<BoardLoader.levelSizes[level]; game++) {
            int levelId = LevelId.levelId(level, game);
            solved = levelsSolved.contains(levelId);
        }
        return solved;
    }

}
