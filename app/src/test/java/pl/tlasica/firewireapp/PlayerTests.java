package pl.tlasica.firewireapp;

import org.junit.BeforeClass;
import org.junit.Test;

import pl.tlasica.firewireapp.parser.BoardLoader;
import pl.tlasica.firewireapp.play.LevelId;
import pl.tlasica.firewireapp.play.Player;

import static org.junit.Assert.*;

public class PlayerTests {

    @BeforeClass
    public static void initBoardLoader() {
        BoardLoader.levelSizes[1] = 5;
        BoardLoader.levelSizes[2] = 8;
    }

    @Test
    public void levelIsNotFinishedIfNotAllGamesSolved() {
        Player p = Player.get();
        p.clear();
        p.markSolved(LevelId.levelId(1,1));
        p.markSolved(LevelId.levelId(1,2));
        assertTrue(p.canPlayLevel(1));
        assertFalse(p.canPlayLevel(2));
        p.markSolved(LevelId.levelId(1,3));
        p.markSolved(LevelId.levelId(1,4));
        assertTrue(p.canPlayLevel(1));
        assertFalse(p.canPlayLevel(2));
    }

    @Test
    public void canPlayNotExistingLevelDoNotCrash() {
        assertFalse(Player.get().canPlayLevel(99));
    }

    @Test
    public void canPlayLevelIfAllPreviousFinished() {
        Player p = Player.get();
        p.clear();
        for(int i=1; i<=BoardLoader.levelSizes[1]; i++) p.markSolved(LevelId.levelId(1,i));
        assertTrue(p.canPlayLevel(2));
        assertFalse(p.canPlayLevel(3));
    }
}
