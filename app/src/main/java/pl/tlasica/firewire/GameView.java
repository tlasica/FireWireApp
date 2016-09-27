package pl.tlasica.firewire;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import pl.tlasica.firewire.play.Game;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Game        game;
    Queue<MouseEvent>   events = new ConcurrentLinkedQueue<>();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        new ViewMotionDetector(this, events);
    }

    public void setGame(Game g) {
        this.game = g;
        if (this.getHolder() != null) {
            this.game.create(this.getHolder(), this.events);
        }
    }

    public Game getGame() {
        return game;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("SURFACE", "Created");
        if (game != null) {
            game.create(holder, events);
            game.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("SURFACE", "Changed with w:"+width+" and h:"+height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("SURFACE", "Destroyed");
        if (game != null) {
            game.stop();
            game = null;
        }
    }
}
