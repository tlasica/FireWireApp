package pl.tlasica.firewire;

import android.app.Application;
import android.content.res.AssetManager;
import android.test.ApplicationTestCase;

import java.io.IOException;

import pl.tlasica.firewire.parser.BoardLoader;

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
    }

    public void testBoardLoaderInitLevels() throws IOException {
        AssetManager assets = this.getApplication().getAssets();
        BoardLoader.initLevelSizes(assets);
        for(int l=1; l<=BoardLoader.NUM_LEVELS; l++) {
            assertTrue(BoardLoader.levelSizes[l] > 0);
        }
    }
}