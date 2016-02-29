package pl.tlasica.firewireapp.play;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Date;
import java.util.Queue;

import pl.tlasica.firewireapp.MouseEvent;
import pl.tlasica.firewireapp.model.ConnectorType;
import pl.tlasica.firewireapp.model.LevelPlay;


public class GameLoop implements Runnable {

    private final SurfaceHolder surfaceHolder;
    private boolean running = true;
    private final String TAG = "GAMELOOP";
    private final long frameDurationMs = 200;       // 10 frames per sec

    private Queue<MouseEvent>   eventQueue;

    private final BoardDrawing boardDrawing;
    private final ConnectorSetDrawing connSetDrawing;

    public GameLoop(SurfaceHolder sfHolder, Queue<MouseEvent> eventQ) {
        eventQueue = eventQ;
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
        // not needed at the moment
    }

    private void processInput() {
        LevelPlay play = LevelPlay.current();
        while (eventQueue.isEmpty() == false) {
            MouseEvent ev = eventQueue.poll();
            if (ev instanceof ClickEvent) {
                handleClick((ClickEvent)ev, play);
            }
            else if (ev instanceof SwipeEvent) {
                handleSwipe((SwipeEvent)ev, play);
            }
        }
    }

    public void pleaseStop() {
        Log.i(TAG, "Requested GameLoop to stop");
        running = false;
    }


    private boolean handleSwipe(SwipeEvent event, LevelPlay play) {
        Log.d(TAG, "SwipeEvent");
        int nodeFrom = boardDrawing.nodeNumber(event.from, play.board);
        int nodeTo = boardDrawing.nodeNumber(event.to, play.board);
        Log.d(TAG, "nodeFrom:" + nodeFrom + ", nodeTo:" + nodeTo);
        if (nodeFrom >= 0) {
            if (nodeTo >= 0) {
                Log.d(TAG, "Try to move connector from " + nodeFrom + " to node " + nodeTo);
                // try move from node A to node B
                return true;
            }
            ConnectorType type = connSetDrawing.connAtMouse(event.to, play);
            if (type != null) {
                Log.d(TAG, "Try to remove connector from " + nodeFrom);
                boolean possible = play.tryRemoveConnector(nodeFrom);
                if (possible) {
                    Log.d(TAG, "Connector removed from " + nodeFrom);
                    play.removeConnector(nodeFrom);
                    return true;
                }
            }
            return false;
        }
        if (nodeTo >= 0) {
            ConnectorType type = connSetDrawing.connAtMouse(event.from, play);
            if (type != null) {
                Log.d(TAG, "Try to place connector on " + nodeTo);
                int rotation = play.tryPlaceConnector(type, nodeTo);
                if (rotation >= 0) {
                    Log.d(TAG, "Type " + type + " placed on " + nodeTo);
                    play.placeConnector(type, nodeTo, rotation);
                    SoundPoolPlayer.get().tick();
                    return true;
                }
                else {
                    Log.d(TAG, "This type cannot be placed on " + nodeTo);
                    SoundPoolPlayer.get().playNo();
                    return false;
                }
            }
        }
        return false;
    }

    private boolean handleClick(ClickEvent event, LevelPlay play) {
        int node = boardDrawing.nodeNumber(event.point, play.board);
        if (node >= 0) {
            Log.d(TAG, "ClickEvent on node " + node);
            int nextRotation = play.tryRotateConnector(node);
            if (nextRotation >= 0) {
                Log.d(TAG, "Rotation on " + node);
                play.rotateConnector(node, nextRotation);
                SoundPoolPlayer.get().tick();
                return true;
            }
        }
        return false;
    }

}
