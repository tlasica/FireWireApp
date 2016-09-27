package pl.tlasica.firewire.play;

/**
 * LevelId number = 100 * level + game
 * So we use int to uniquely identify game level
 * rationale: easier to keep history, pass parameters etc.
 * convention: level:1.., game:1..
 */
public class LevelId {

    public static int level(int levelId) {
        return levelId / 100;
    }

    public static int game(int levelId) {
        return levelId % 100;
    }

    public static int levelId(int level, int game) {
        return 100 * level + game;
    }
}
