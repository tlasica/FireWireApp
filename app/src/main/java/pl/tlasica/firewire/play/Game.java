package pl.tlasica.firewire.play;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Queue;

import pl.tlasica.firewire.MouseEvent;
import pl.tlasica.firewire.PlayActivity;
import pl.tlasica.firewire.R;
import pl.tlasica.firewire.model.LevelPlay;


/**
 * Class to start / stop game loop thread
 * And to be notified by GameLoop
 */
public class Game {

    private Thread          thread = null;
    private GameLoop        gameLoop = null;
    private Handler         msgHandler;
    private PlayActivity    playActivity;

    private static final int STATE_CHANGED = 997;
    private static final int GAME_SOLVED = 998;
    private static final int GAME_LOST = 999;

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
                        Log.d("GAME", "game loop msg: lost");
                        builder = new AlertDialog.Builder(playActivity);
                        builder.setIcon(R.drawable.shockcircle);
                        builder.setTitle("Game Over");
                        builder.setMessage("Game lost.\nPower wired but not fired :-(");
                        builder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                playActivity.finish();
                            }
                        });
                        builder.setPositiveButton("REPLAY", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                playActivity.repeatLevel();
                            } });

                        builder.create().show();
                        break;
                    case GAME_SOLVED:
                        Log.d("GAME", "game loop msg: solved");
                        // save to game history etc.
                        Player.get().gameFinishedWithSuccess(); // to save this fact
                        // save the fact game is solved
                        Settings settings = new Settings(playActivity);
                        int levelId = Player.get().currentLevelId();
                        settings.storeLevelSolved(levelId);
                        // show level completed dialog
                        LevelCompletedDialogFragment dialogFragment = new LevelCompletedDialogFragment();
                        dialogFragment.gameStats = gameLoop.stats();
                        dialogFragment.show(playActivity.getFragmentManager(), "levelCompleted");
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
        Log.i("Game", "start()");
        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }
    }

    public void stop() {
        gameLoop.pleaseStop();
        try {
            thread.join();
            Log.i("Game", "stop() completed. thread joined." );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void restart() {
        gameLoop.pause(true);
        LevelPlay.restart();
        gameLoop.restart();
        gameLoop.pause(false);
    }

    public void create(SurfaceHolder surfaceHolder, Queue<MouseEvent> eventsQueue) {
        Log.i("Game", "create()");
        if (gameLoop == null) {
            Log.i("Game", "gameLoop was null -> starting.");
            gameLoop = new GameLoop(this, surfaceHolder, eventsQueue);
            thread = new Thread(gameLoop);
        }
        else {
            Log.i("Game", "gameLoop was not null -> updating");

        }
    }

}
