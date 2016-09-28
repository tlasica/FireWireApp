package pl.tlasica.firewire;

import android.app.Application;
import android.content.res.AssetManager;
import android.test.ApplicationTestCase;

import java.io.IOException;

import pl.tlasica.firewire.model.Board;
import pl.tlasica.firewire.parser.BoardLoader;
import pl.tlasica.firewire.play.LevelId;
import pl.tlasica.firewire.play.Player;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        createApplication();
        AssetManager assets = this.getApplication().getAssets();
        BoardLoader.initLevelSizes(assets);
    }

    public void testBoardLoaderInitLevels() throws IOException {
        for(int l=1; l<=BoardLoader.NUM_LEVELS; l++) {
            assertTrue(BoardLoader.levelSizes[l] > 0);
        }
    }

    public void testBoardLoaderEachGame() throws IOException {
        BoardLoader loader = new BoardLoader();
        for(int l=1; l<=BoardLoader.NUM_LEVELS; l++) {
            int gamesInLevel = BoardLoader.levelSizes[l];
            for(int game=1; game<gamesInLevel; game++) {
                int levelId = LevelId.levelId(l, game);
                Board b = loader.load(levelId);
                assertNotNull(b.title);
            }
        }
    }

}