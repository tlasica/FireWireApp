package pl.tlasica.firewireapp.play;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Date;
import java.util.Queue;

import pl.tlasica.firewireapp.MouseEvent;
import pl.tlasica.firewireapp.engine.Solution;
import pl.tlasica.firewireapp.model.ConnectorType;
import pl.tlasica.firewireapp.model.LevelPlay;
import pl.tlasica.firewireapp.model.PlacedConnector;


public class GameLoop implements Runnable {

    private final SurfaceHolder         surfaceHolder;
    private boolean                     running = true;
    private boolean                     pause = false;
    private final String                TAG = "GameLoop";

    private Queue<MouseEvent>           eventQueue;

    private final BoardDrawing          boardDrawing;
    private final ConnectorSetDrawing   connSetDrawing;
    private final GameLoopStatistics    stats = new GameLoopStatistics();

    private ConnectorType movingConnType;
    private Point         movingConnPos;

    private long                lastLogTime;
    private Game                game;
    private Solution.GameStatus gameStatus = Solution.GameStatus.NOT_FINISHED;
    private int                 gameFinishedFrame = 0;    // 30 x 100ms = 3s

    public GameLoop(Game g, SurfaceHolder sfHolder, Queue<MouseEvent> eventQ) {
        game = g;
        eventQueue = eventQ;
        surfaceHolder = sfHolder;
        boardDrawing = new BoardDrawing();
        connSetDrawing = new ConnectorSetDrawing();
    }

    public void pause(boolean p) {
        this.pause = p;
    }

    @Override
    public void run() {
        Log.d(TAG, "GameLoop started");
        stats.start();
        while (running) {
            long frameStartTime = new Date().getTime();
            if (! pause ) {
                processInput();
                doPhysics();
                drawGraphics();
            }
            if (this.gameStatus == Solution.GameStatus.WIN) {
                this.gameFinishedFrame += 1;
                drawGraphics();
                if (this.gameFinishedFrame == 16) {  // has to be even to draw grilled creature as last
                    Log.d(TAG, "close game loop after game is finished");
                    this.gameSolved();
                }
            }
            waitForNextFrame(frameStartTime);
        }
        stats.stop();
    }

    private long waitForNextFrame(long frameStartTime) {
        long nextFrameStartTime = new Date().getTime();
        long howLongWeTook = nextFrameStartTime - frameStartTime;
        long frameDurationMs = 50;
        long waitTime = frameDurationMs - howLongWeTook;
        if (waitTime > 0) {
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
            long t0 = System.currentTimeMillis();
            synchronized (surfaceHolder) {
                boardDrawing.draw(canvas, levelPlay);
                connSetDrawing.draw(canvas, levelPlay);
                if (this.movingConnType != null) {
                    connSetDrawing.drawConnectorAtMouse(canvas, this.movingConnPos, this.movingConnType);
                }
                if (this.gameStatus == Solution.GameStatus.WIN) {
                    boardDrawing.drawGrilledCreature(canvas, levelPlay, this.gameFinishedFrame);
                }
            }
            long t1 = System.currentTimeMillis();
            logPerf("Drawing duration[ms]", t1-t0);
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
        boolean sthProcessed = false;
        while (!eventQueue.isEmpty()) {
            MouseEvent ev = eventQueue.poll();
            if (ev instanceof ClickEvent) {
                sthProcessed |= handleClick((ClickEvent)ev, play);
            }
            else if (ev instanceof SwipeEvent) {
                sthProcessed |= handleSwipe((SwipeEvent)ev, play);
            }
            else if (ev instanceof MouseMoveEvent) {
                handleMouseMove((MouseMoveEvent)ev, play);
            }
        }
        // if there was handled event
        if (sthProcessed) {
            game.noifyStateChange();
            Log.i(TAG, "Something processed, checking game status");
            this.gameStatus = Solution.solution(LevelPlay.current());
            switch (this.gameStatus) {
                case WIN:
                    // if is ok to just set status and pause
                    // main game loop will take care
                    this.pause(true);
                    Log.i("GAME", "Success. Win!");
                    stats.stop();
                    SoundPoolPlayer.get().electricshock();
                    break;
                case LOST:
                    Log.i("GAME", "Game Lost...");
                    stats.stop();
                    SoundPoolPlayer.get().playNo();
                    this.gameLost();
                    break;
            }
        }
    }

    private void gameSolved() {
        this.pleaseStop();
        game.notifyGameSolved();
    }

    private void gameLost() {
        this.pleaseStop();
        game.notifyGameLost();
    }

    public void pleaseStop() {
        Log.i(TAG, "Requested GameLoop to stop");
        running = false;
    }

    private boolean handleSwipe(SwipeEvent event, LevelPlay play) {
        this.movingConnType = null;
        int nodeFrom = boardDrawing.nodeNumber(event.from, play.board);
        int nodeTo = boardDrawing.nodeNumber(event.to, play.board);
        Log.d(TAG, "SwipeEvent nodeFrom:" + nodeFrom + ", nodeTo:" + nodeTo);
        if (nodeFrom >= 0) {
            if (nodeTo >= 0) {
                Log.d(TAG, "Try to move connector from " + nodeFrom + " to node " + nodeTo);
                boolean possible = play.tryMoveConnector(nodeFrom, nodeTo);
                if (possible) {
                    Log.d(TAG, "Connector moved from " + nodeFrom + " to node " + nodeTo);
                    play.moveConnector(nodeFrom, nodeTo);
                    SoundPoolPlayer.get().tick();
                    stats.incMove();
                    return true;
                }
                else {
                    Log.d(TAG, "This type cannot be moved to " + nodeTo);
                    SoundPoolPlayer.get().playNo();
                    return false;
                }
            }
            ConnectorType type = connSetDrawing.connAtMouse(event.to, play);
            if (type != null) {
                Log.d(TAG, "Try to remove connector from " + nodeFrom);
                boolean possible = play.tryRemoveConnector(nodeFrom);
                if (possible) {
                    Log.d(TAG, "Connector removed from " + nodeFrom);
                    play.removeConnector(nodeFrom, false);
                    stats.incRemove();
                    return true;
                }
            }
            return false;
        }
        if (nodeTo >= 0) {
            ConnectorType type = connSetDrawing.connAtMouse(event.from, play);
            if (type != null) {
                if (! play.hasAvaliableConnector(type)) {
                    Log.d(TAG, "No available connector of this type");
                    return false;
                }
                Log.d(TAG, "Try to place connector on " + nodeTo);
                int rotation = play.tryPlaceConnector(type, nodeTo, false);
                if (rotation >= 0) {
                    Log.d(TAG, "Type " + type + " placed on " + nodeTo);
                    play.placeConnector(type, nodeTo, rotation, false);
                    SoundPoolPlayer.get().tick();
                    stats.incPlace();
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
        this.movingConnType = null;
        int node = boardDrawing.nodeNumber(event.point, play.board);
        if (node >= 0) {
            Log.d(TAG, "ClickEvent on node " + node);
            int nextRotation = play.tryRotateConnector(node);
            if (nextRotation >= 0) {
                Log.d(TAG, "Rotation on " + node);
                play.rotateConnector(node, nextRotation);
                SoundPoolPlayer.get().tick();
                stats.incRotate();
                return true;
            }
        }
        return false;
    }

    private boolean handleMouseMove(MouseMoveEvent event, LevelPlay play) {
        // let's check if move from connector
        if (this.movingConnType != null) {
            this.movingConnPos = event.to;
            return true;
        }
        // in case it is placing connector
        ConnectorType type = connSetDrawing.connAtMouse(event.from, play);
        if (type != null && play.hasAvaliableConnector(type)) {
            this.movingConnType = type;
            this.movingConnPos = event.to;
            return true;
        }
        // in case it is moving connector / removing connector
        int node = boardDrawing.nodeNumber(event.from, play.board);
        if (node >= 0) {
            PlacedConnector conn = play.connectorAt(node);
            if (conn != null) {
                this.movingConnType = conn.type;
                this.movingConnPos = event.to;
                return true;
            }
        }
        return false;
    }

    private void logPerf(String metric, long value) {
        long ct = System.currentTimeMillis();
        long period = 5 * 1000;
        if (ct-lastLogTime > period) {
            lastLogTime = ct;
            Log.d("PERF", String.format("%s: %d", metric, value));
        }
    }

    public GameLoopStatistics stats() {
        return stats;
    }
}


class GameLoopStatistics {
    int numRotate = 0;
    int numMove = 0;
    int numPlace = 0;
    int numRemove = 0;
    private long startTimeMs;
    private long stopTimeMs;

    public GameLoopStatistics() {
    }

    public void start() {
        startTimeMs = System.currentTimeMillis();
        numRotate = 0;
        numMove = 0;
        numPlace = 0;
        numRemove = 0;
    }

    public void stop() {
        if (stopTimeMs == 0) stopTimeMs = System.currentTimeMillis();
    }

    public void incMove() {
        numMove++;
    }

    public void incRotate() {
        numRotate++;
    }

    public void incPlace() {
        numPlace++;
    }

    public void incRemove() {
        numRemove++;
    }

    public double durationSec() {
        return (stopTimeMs-startTimeMs) / 1000.0;
    }

}
