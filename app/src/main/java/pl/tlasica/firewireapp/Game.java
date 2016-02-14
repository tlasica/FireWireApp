package pl.tlasica.firewireapp;

import android.util.Log;

/**
 * Class to start / stop game loop thread
 */
public class Game {

    private Thread      thread;
    private GameLoop    gameLoop;

    public void start() {
        Log.i("", "Game started");
        gameLoop = new GameLoop();
        thread = new Thread(gameLoop);
        thread.start();
    }

    public void stop() {
        gameLoop.pleaseStop();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
