package pl.tlasica.firewireapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import pl.tlasica.firewireapp.model.LevelFactory;
import pl.tlasica.firewireapp.model.LevelPlay;
import pl.tlasica.firewireapp.play.Game;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Game game;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LevelPlay.startLevel(LevelFactory.standard());
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("SURFACE", "Created");
        this.game = new Game(holder);
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
