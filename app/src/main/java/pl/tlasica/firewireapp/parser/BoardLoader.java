package pl.tlasica.firewireapp.parser;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.play.LevelId;

/**
 * Translates level# and game# to Board object,
 * by reading from assets and parsing.
 */
public class BoardLoader {

    public static final int NUM_LEVELS = 3;
    public static final int[] levelSizes = new int[NUM_LEVELS+1];

    private AssetManager    assets;
    private String          lastFile;   // last file used for loading eg. for error reporting

    public BoardLoader(AssetManager assets) {
        this.assets = assets;
    }

    public Board load(int levelId) throws IOException {
        int levelNo = LevelId.level(levelId);
        int gameNo = LevelId.game(levelId);
        lastFile = fileName(levelNo, gameNo);
        List<String> lines = loadLines(this.assets, lastFile);
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

    private int numGamesInLevel(int levelNo) throws IOException {
        String levelPath = String.format("levels/%02d", levelNo);
        String[] games = this.assets.list(levelPath);
        return games.length;
    }

    public final void initLevelSizes() throws IOException {
        for(int l=1; l<=NUM_LEVELS; l++) {
            levelSizes[l] = numGamesInLevel(l);
            Log.d("GameLoader", String.format("Level %02d: %d games", l, levelSizes[l]));
        }
    }

    public static int nextLevelId(int levelId) {
        int levelNo = LevelId.level(levelId);
        int gameNo = LevelId.game(levelId);
        if (gameNo > levelSizes[levelNo])
            return LevelId.levelId(levelNo+1, 1);
        else
            return LevelId.levelId(levelNo, gameNo+1);
    }

}
