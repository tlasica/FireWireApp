package pl.tlasica.firewire;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import pl.tlasica.firewire.model.Board;
import pl.tlasica.firewire.model.LevelPlay;
import pl.tlasica.firewire.parser.BoardLoader;
import pl.tlasica.firewire.play.ConnectorBitmap;
import pl.tlasica.firewire.play.LevelId;
import pl.tlasica.firewire.play.LevelsActivity;
import pl.tlasica.firewire.play.Player;
import pl.tlasica.firewire.play.Settings;
import pl.tlasica.firewire.play.SoundPoolPlayer;
import pl.tlasica.firewire.play.TutorialActivity;

/**
 * fonts: http://www.1001fonts.com/free-fonts-for-commercial-use.html
 * icons: https://design.google.com/icons/#ic_exit_to_app
 */
public class MainActivity extends BasicActivity {

    @Override
    protected void onDestroy() {
        SoundPoolPlayer.destroy();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/chango_regular.ttf");
        TextView text = (TextView)findViewById(R.id.logo_text);
        text.setTypeface(tf);

        Settings settings = new Settings(this);
        Boolean soundOn = settings.sound();
        this.setSoundIcon(soundOn);
        SoundPoolPlayer.init(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "TODO: Share on FB";
                Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        ConnectorBitmap.initialize(getResources());

        try {
            BoardLoader.initLevelSizes(getAssets());
        } catch (IOException e) {
            Toast.makeText(this, "Ups. Loading levels failed.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            this.finish();
        }

        restoreSolvedLevels();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Toast.makeText(this, "ACTION: " + String.valueOf(id), Toast.LENGTH_LONG).show();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPlay(View view) {
        // load next level
        BoardLoader loader = new BoardLoader();
        try {
            int levelId = Player.get().firstUnfinishedLevelId();
            if (levelId == 0) {
                String msg = "Sorry to say but you already played all the games.";
                Toast.makeText(getBaseContext(), msg , Toast.LENGTH_LONG).show();
                return;
            }
            Board levelBoard = loader.load(levelId);
            LevelPlay.startLevel(levelBoard);
            Player.get().setCurrentLevelId(levelId);
            // start play activity
            Intent myIntent = new Intent(this, PlayActivity.class);
            //myIntent.putExtra("key", value); //Optional parameters
            startActivity(myIntent);
        } catch (IOException e) {
            String msg = String.format("Ups. Loading level failed on %s", loader.getLastFile());
            Toast.makeText(getBaseContext(), msg , Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            String msg = String.format("Ups. Loading level failed on %s\nError: %s", loader.getLastFile(), e.getMessage());
            Toast.makeText(getBaseContext(), msg , Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void showTutorial(View view) {
        Intent myIntent = new Intent(this, TutorialActivity.class);
        startActivity(myIntent);
    }

    public void switchSound(View view) {
        Settings s = new Settings(getApplicationContext());
        Boolean soundOn = s.switchSound();
        String msg = "Sound is " + (soundOn ? "ON" : "OFF");
        Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
        this.setSoundIcon(soundOn);
    }

    private void setSoundIcon(boolean on) {
        ImageView view = (ImageView)findViewById(R.id.button_switch_sound);
        if (on)
            view.setImageResource(R.drawable.ic_volume_up_black_36dp);
        else
            view.setImageResource(R.drawable.ic_volume_off_black_36dp);
    }

    public void exitGame(View view) {
        this.finish();
    }

    public void showLevels(View view) {
        Intent myIntent = new Intent(this, LevelsActivity.class);
        startActivity(myIntent);
    }

    private void restoreSolvedLevels() {
        Settings settings = new Settings(this);
        for(int level=1; level<=BoardLoader.NUM_LEVELS; level++) {
            for(int game=1; game<=BoardLoader.levelSizes[level]; game++) {
                int levelId = LevelId.levelId(level, game);
                boolean solved = settings.isLevelSolved(levelId);
                if (solved) Player.get().markSolved(levelId);
            }
        }
    }
}
