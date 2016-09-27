package pl.tlasica.firewire.parser;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import pl.tlasica.firewire.model.Board;
import pl.tlasica.firewire.play.LevelId;

/**
 * Translates level# and game# to Board object,
 * by reading from assets and parsing.
 */
public class BoardLoader {

    public static final int         NUM_LEVELS = 5;
    public static final int[]       levelSizes = new int[NUM_LEVELS+1];
    private static AssetManager     assets;

    private String                  lastFile;   // last file used for loading eg. for error reporting

    public Board load(int levelId) throws IOException {
        int levelNo = LevelId.level(levelId);
        int gameNo = LevelId.game(levelId);
        lastFile = fileName(levelNo, gameNo);
        List<String> lines = loadLines(assets, lastFile);
        TextParser parser = new TextParser();
        Board board = parser.parse(lines);
        if (board.title==null) {
            board.title = String.format("Level %02d:%02d", levelNo, gameNo);
        }
        return board;
    }

    public String getLastFile() {
        return lastFile;
    }

    private String fileName(int levelNo, int gameNo) {
        return String.format("levels/%02d/%02d.txt", levelNo, gameNo);
    }

    private static List<String> loadLines(AssetManager assets, String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(assets.open(fileName)));
        List<String> lines = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            if (!line.startsWith("#")) lines.add(line);
            line = reader.readLine();
        }
        return lines;
    }

    private static final int numGamesInLevel(int levelNo) throws IOException {
        String levelPath = String.format("levels/%02d", levelNo);
        String[] games = assets.list(levelPath);
        return games.length;
    }

    public final static void initLevelSizes(AssetManager assets) throws IOException {
        BoardLoader.assets = assets;
        for(int l=1; l<=NUM_LEVELS; l++) {
            levelSizes[l] = numGamesInLevel(l);
            Log.d("GameLoader", String.format("Level %02d: %d games", l, levelSizes[l]));
        }
    }

    /**
     * Return next level id or 0 if all games are finished and nothing left
     */
    public static int nextLevelId(int levelId) {
        int levelNo = LevelId.level(levelId);
        int gameNo = LevelId.game(levelId);
        if (gameNo >= levelSizes[levelNo])
            return levelNo < NUM_LEVELS ? LevelId.levelId(levelNo+1, 1) : 0;
        else
            return LevelId.levelId(levelNo, gameNo+1);
    }

}
