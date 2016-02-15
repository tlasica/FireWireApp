package pl.tlasica.firewireapp.play;

import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Queue;

import pl.tlasica.firewireapp.MouseEvent;


/**
 * Class to start / stop game loop thread
 */
public class Game {

    private final Thread    thread;
    private final GameLoop  gameLoop;
    private SurfaceHolder   surfaceHolder;

    public Game(SurfaceHolder holder, Queue<MouseEvent> events) {
        surfaceHolder = holder;
        gameLoop = new GameLoop(surfaceHolder, events);
        thread = new Thread(gameLoop);
    }

    public void start() {
        Log.i("", "Game started");
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
