package pl.tlasica.firewireapp;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import pl.tlasica.firewireapp.model.LevelPlay;
import pl.tlasica.firewireapp.play.Game;
import pl.tlasica.firewireapp.play.SoundPoolPlayer;


//http://stackoverflow.com/questions/6645537/how-to-detect-the-swipe-left-or-right-in-android

public class PlayActivity extends BasicActivity {
    private View        mContentView;
    private View        mGameView;
    private TextView    mTimeView;
    private CountDownTimer timer;
    private long timeLimitMs;

//    private final Runnable mHidePart2Runnable = new Runnable() {
//        @SuppressLint("InlinedApi")
//        @Override
//        public void run() {
//            // Delayed removal of status and navigation bar
//
//            // Note that some of these constants are new as of API 16 (Jelly Bean)
//            // and API 19 (KitKat). It is safe to use them, as they are inlined
//            // at compile-time and do nothing on earlier devices.
//            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//                    //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mGameView = findViewById(R.id.game_view);
        // set the full screen mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // change title to level title
        TextView titleText = (TextView)findViewById(R.id.level_title);
        titleText.setText(LevelPlay.current().board.title);
        mTimeView = (TextView)findViewById(R.id.level_time);
        // start countdown timer to stop game and update time
        timeLimitMs = 5 * 60 * 1000;
        timer = startTimer(timeLimitMs);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullScreenMode();
    }

    private void fullScreenMode() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            Log.d("", "Hiding action bar");
            actionBar.hide();
        }

        View view = getWindow().getDecorView();
        //int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        flags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        flags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        view.setSystemUiVisibility(flags);
    }

    private CountDownTimer startTimer(long limitMs) {
        timer = new CountDownTimer(limitMs, 1000) {
            public void onTick(long millisUntilFinished) {
                long durSec = (timeLimitMs - millisUntilFinished) /1000;
                long min = durSec / 60;
                long sec = durSec % 60;
                mTimeView.setText(String.format("%02d:%02d", min, sec));
            }
            public void onFinish() {
                mTimeView.setText("GAME OVER");
            }
        }.start();
        return timer;
    }

    public void closeGame(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Stop this game?");
        builder.setPositiveButton("STOP", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                fullScreenMode();
                finish();
            } });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    fullScreenMode();
                } });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void restartGame(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Restart this game?");
        builder.setPositiveButton("RESTART", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                fullScreenMode();
                game().restart();
            } });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                fullScreenMode();
            } });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Game game() {
        GameView gameView = (GameView)findViewById(R.id.game_view);
        Game game = gameView.getGame();
        return game;
    }
}
