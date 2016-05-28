package pl.tlasica.firewireapp.parser;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import pl.tlasica.firewireapp.model.Board;

/**
 * Translates level# and game# to Board object,
 * by reading from assets and parsing.
 */
public class BoardLoader {

    private AssetManager assets;
    private String lastFile;

    public BoardLoader(AssetManager assets) {
        this.assets = assets;
    }

    public Board load(int levelNo, int gameNo) throws IOException {
        lastFile = fileName(levelNo, gameNo);
        List<String> lines = loadLines(this.assets, lastFile);
        TextParser parser = new TextParser();
        return parser.parse(lines);
    }

    public String getLastFile() {
        return lastFile;
    }

    private String fileName(int levelNo, int gameNo) {
        return String.format("levels/%02d/%02d.txt", levelNo, gameNo);
    }

    public static List<String> loadLines(AssetManager assets, String fileName) throws IOException {
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
}
