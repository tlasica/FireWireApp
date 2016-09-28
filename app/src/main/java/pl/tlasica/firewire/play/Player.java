package pl.tlasica.firewire.play;

import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import pl.tlasica.firewire.parser.BoardLoader;

/**
 * Holds information about current level to play
 */
public class Player {

    private static final Player player = new Player();

    private int currLevelId = LevelId.levelId(1, 1);
    private Set<Integer> levelsSolved = new HashSet<>();

    /**
     * CurrentLevelId is currently playing (not next)
     */
    public int currentLevelId() {
        return currLevelId;
    }

    public void setCurrentLevelId(int levelId) {
        currLevelId = levelId;
    }

    public int setNextLevel() {
        currLevelId = BoardLoader.nextLevelId(currentLevelId());
        return currLevelId;
    }

    /**
     * Return first unfinished levelId or 0 if all levels are finished (solved)
     */
    public int firstUnfinishedLevelId() {
        for (int level = 1; level <= BoardLoader.NUM_LEVELS; level++) {
            for (int game = 1; game <= BoardLoader.levelSizes[level]; game++) {
                int levelId = LevelId.levelId(level, game);
                if (!levelsSolved.contains(levelId)) return levelId;
            }
        }
        return 0; // all tasks played, end of the game
    }

    // mark current game as finished with success
    // TODO: save game statistics
    // TODO: update points per level
    public void gameFinishedWithSuccess() {
        markSolved(currentLevelId());
        Log.d("Player", "Level finished: " + String.valueOf(currentLevelId()));
    }

    public void gameCancelled() {
        Log.d("Player", "Level cancelled: " + String.valueOf(currentLevelId()));
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
        int maxLevel = Math.min(level, BoardLoader.NUM_LEVELS);
        for (int l = 1; solved && l < maxLevel; l++) {
            solved = allGamesSolvedInLevel(l);
        }
        return solved;
    }

    private boolean allGamesSolvedInLevel(int level) {
        boolean solved = true;
        int maxGame = BoardLoader.levelSizes[level];
        for (int game = 1; solved && game <= maxGame ; game++) {
            int levelId = LevelId.levelId(level, game);
            solved = levelsSolved.contains(levelId);
        }
        return solved;
    }

    public void clear() {
        this.currLevelId = LevelId.levelId(1, 1);
        this.levelsSolved.clear();
    }

}
