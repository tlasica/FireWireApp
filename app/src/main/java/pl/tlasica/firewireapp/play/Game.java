package pl.tlasica.firewireapp.play;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import pl.tlasica.firewireapp.MouseEvent;
import pl.tlasica.firewireapp.PlayActivity;
import pl.tlasica.firewireapp.engine.Solution;
import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.model.LevelPlay;


/**
 * Class to start / stop game loop thread
 * And to be notified by GameLoop
 */
public class Game {

    private Thread    thread = null;
    private GameLoop  gameLoop = null;
    private Handler         msgHandler;
    private PlayActivity    playActivity;

    private static final int STATE_CHANGED = 997;
    private static final int GAME_SOLVED = 998;
    private static final int GAME_LOST = 999;

    private Queue<MouseEvent> eventsQueue;
    private SurfaceHolder surfaceHolder;

    public Game(PlayActivity activity) {
        playActivity = activity;
        msgHandler = createMessageHandler();
    }

    private Handler createMessageHandler() {
        // Defines a Handler object that's attached to the UI thread
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                AlertDialog.Builder builder;
                switch (inputMessage.what) {
                    case STATE_CHANGED:
                        Log.d("GAME", "game loop msg: change");
                        break;
                    case GAME_LOST:
                        // TODO: show alert what now: restart, stop
                        // TODO: maybe a better idea would be to pass this message to
                        Log.d("GAME", "game loop msg: lost");
                        builder = new AlertDialog.Builder(playActivity);
                        builder.setMessage("Game lost:\npower wired but not fired.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                playActivity.finish();
                            }
                        });
                        builder.create().show();
                        break;
                    case GAME_SOLVED:
                        Log.d("GAME", "game loop msg: solved");
                        Player.get().gameFinishedWithSuccess();
                        builder = new AlertDialog.Builder(playActivity);
                        builder.setMessage("Good job!");
                        builder.setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                playActivity.finish();
                            }
                        });
                        builder.setNegativeButton("MENU", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                playActivity.finish();
                            }
                        });
                        builder.create().show();
                        break;
                }
            }
        };
        return handler;
    }

    public void noifyStateChange() {
        Message completeMessage = msgHandler.obtainMessage(STATE_CHANGED);
        completeMessage.sendToTarget();
    }

    public void notifyGameSolved() {
        Message completeMessage = msgHandler.obtainMessage(GAME_SOLVED);
        completeMessage.sendToTarget();
    }

    public void notifyGameLost() {
        Message completeMessage = msgHandler.obtainMessage(GAME_LOST);
        completeMessage.sendToTarget();
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

    public void restart() {
        gameLoop.pause(true);
        LevelPlay.restart();
        gameLoop.pause(false);
    }

    public void create(SurfaceHolder surfaceHolder, Queue<MouseEvent> eventsQueue) {
        gameLoop = new GameLoop(this, surfaceHolder, eventsQueue);
        thread = new Thread(gameLoop);
    }

}
