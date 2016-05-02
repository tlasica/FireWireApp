package pl.tlasica.firewireapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import pl.tlasica.firewireapp.play.Game;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Game game;
    Queue<MouseEvent> events = new ConcurrentLinkedQueue<>();

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        new ViewMotionDetector(this, events);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("SURFACE", "Created");
        this.game = new Game(holder, events);
        game.start();
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
