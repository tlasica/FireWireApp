package pl.tlasica.firewireapp.play;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Date;

import pl.tlasica.firewireapp.model.LevelPlay;


public class GameLoop implements Runnable {

    private final SurfaceHolder surfaceHolder;
    private boolean running = true;
    private final String TAG = "GAMELOOP";
    private final long frameDurationMs = 200;       // 10 frames per sec

    private final BoardDrawing boardDrawing;
    private final ConnectorSetDrawing connSetDrawing;

    public GameLoop(SurfaceHolder sfHolder) {
        surfaceHolder = sfHolder;
        boardDrawing = new BoardDrawing();
        connSetDrawing = new ConnectorSetDrawing();
    }

    @Override
    public void run() {
        Log.d(TAG, "GameLoop started");
        while (running) {
            long frameStartTime = new Date().getTime();
            processInput();
            doPhysics();
            drawGraphics();
            waitForNextFrame(frameStartTime);
        }
    }

    private long waitForNextFrame(long frameStartTime) {
        long nextFrameStartTime = new Date().getTime();
        long howLongWeTook = nextFrameStartTime - frameStartTime;
        long waitTime = frameDurationMs - howLongWeTook;
        if (waitTime > 0) {
            //Log.w(TAG, "Sleeping in frame for [ms]: " + waitTime);
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.w(TAG, "Not enough time for sleep in frame");
        }
        return nextFrameStartTime;
    }

    private void drawGraphics() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) {
            Log.w(TAG, "lockCanvas() returned null");
            return;
        }

        try {
            LevelPlay levelPlay = LevelPlay.current();
            synchronized (surfaceHolder) {
                boardDrawing.draw(canvas, levelPlay);
                connSetDrawing.draw(canvas, levelPlay);
            }
        }
        finally {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

    private void doPhysics() {

    }

    private void processInput() {

    }

    public void pleaseStop() {
        Log.i(TAG, "Requested GameLoop to stop");
        running = false;
    }
}
