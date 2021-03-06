package pl.tlasica.firewire;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;

import pl.tlasica.firewire.model.Board;
import pl.tlasica.firewire.model.LevelPlay;
import pl.tlasica.firewire.parser.BoardLoader;
import pl.tlasica.firewire.play.Game;
import pl.tlasica.firewire.play.LevelId;
import pl.tlasica.firewire.play.Player;


//http://stackoverflow.com/questions/6645537/how-to-detect-the-swipe-left-or-right-in-android
//https://developer.android.com/guide/topics/ui/dialogs.html

public class PlayActivity extends BasicActivity {
    private TextView    mTimeView;
    private CountDownTimer timer;
    private String      addUnitId = "ca-app-pub-6316552100242193/6643259663";
    InterstitialAd      mInterstitialAd;
    Game                game;

    private static int  playNextCounter = 0;
    private static int  showAddCounterBarrier = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        // request new add
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(addUnitId);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d("Ad", "Ad closed.");
                requestNewInterstitial();
                playLevel();
            }
        });

        requestNewInterstitial();
        // set the full screen mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // change title to level title
        TextView titleText = (TextView)findViewById(R.id.level_title);
        titleText.setText(LevelPlay.current().board.title);
        mTimeView = (TextView)findViewById(R.id.level_time);
        // start countdown timer to stop game and update time
        timer = startTimer();
        // start game
        game = new Game(this);
        this.setGameInGameView();
    }


    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        Log.i("PLAYACT", "onResume()");
        super.onResume();
        fullScreenMode();
        this.game = new Game(this);
        this.setGameInGameView();
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

    private CountDownTimer startTimer() {
        final long limitMs = 15 * 60 * 1000;
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(limitMs, 1000) {
            public void onTick(long millisUntilFinished) {
                long durSec = (limitMs - millisUntilFinished) /1000;
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
        builder.setIcon(R.drawable.shockcircle);
        builder.setTitle("Stop game");
        builder.setMessage("Do you want to stop this game and exit?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
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

    public void showRestartGameDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.shockcircle);
        builder.setTitle("Restart game");
        builder.setMessage("Do you want to restart this game?");
        builder.setPositiveButton("YES, RESTART", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doRestartGame();
            } });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                fullScreenMode();
            } });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void doRestartGame() {
        this.startTimer();
        fullScreenMode();
        game().restart();
    }

    private Game game() {
        return this.game;
    }

    public void nextLevel() {
        int currLevel = Player.get().currentLevelId();
        int nextLevel = BoardLoader.nextLevelId(currLevel);
        if (nextLevel > 0) {
            int level = LevelId.level(nextLevel);
            boolean canPlayLevel = Player.get().canPlayLevel(level);
            if (!canPlayLevel) {
                Log.i("", "taking fist unfinished as next instead of 1st in next level");
                nextLevel = Player.get().firstUnfinishedLevelId();
            }
            Player.get().setCurrentLevelId(nextLevel);
            boolean shouldShowAdd = this.checkIfShowAdd(nextLevel) && mInterstitialAd.isLoaded();
            if (shouldShowAdd) {
                Log.d("Ad", "Ad is loaded - showing");
                mInterstitialAd.show();
            } else {
                Log.d("Ad", "Ad is NOT loaded");
                new AppRater(this).tryRate();
                playLevel();
            }
        }
        else {
            Log.i("", "all levels solved, game finished!");
            this.showEndOfLevels();
        }
    }

    /**
     * We want to show Ad after 3..4..5 playNext runs and only in level 2+
     */
    private static boolean checkIfShowAdd(int nextLevel) {
        // do not show if level == 1
        if (nextLevel == 0) return false;
        if (LevelId.level(nextLevel) < 2) return false;
        // show after 3., then 7th (3+4), then 12. (3+4+5) run
        playNextCounter++;
        if (playNextCounter == showAddCounterBarrier) {
            playNextCounter = 0;
            if (showAddCounterBarrier<5) showAddCounterBarrier++; // 3..4..5
            return true;
        }
        return false;
    }

    //TODO: implement a nice dialog for this with score etc, share on FB
    private void showEndOfLevels() {
        final AppRater appRater = new AppRater(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game completed!");
        builder.setIcon(R.drawable.shockcircle);
        builder.setMessage("Awesome job!\n\nYou have solved all the puzzles we have at the moment.\n\nStay tuned, we are working on new challenges!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!appRater.isDoNotShowAgain()) appRater.showRateDialog();
                fullScreenMode();
            } });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void repeatLevel() {
        playLevel();
    }

    private void playLevel() {
        game().stop();
        fullScreenMode();
        Board level = null;
        BoardLoader loader = new BoardLoader();
        try {
            level = loader.load(Player.get().currentLevelId());
        } catch (IOException e) {
            Toast.makeText(this, "Ups. Loading level failed on " + loader.getLastFile(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            this.finish();
        }
        LevelPlay.startLevel(level);
        timer = startTimer();
        TextView titleText = (TextView)findViewById(R.id.level_title);
        titleText.setText(LevelPlay.current().board.title);
        game = new Game(this);
        this.setGameInGameView();
        game().start();
    }

    private void setGameInGameView() {
        GameView gameView = (GameView)findViewById(R.id.game_view);
        if (gameView != null) {
            gameView.setGame(game);
        }
    }
}
