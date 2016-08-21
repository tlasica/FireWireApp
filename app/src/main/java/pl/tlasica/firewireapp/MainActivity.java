package pl.tlasica.firewireapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.model.LevelPlay;
import pl.tlasica.firewireapp.parser.BoardLoader;
import pl.tlasica.firewireapp.play.ConnectorBitmap;
import pl.tlasica.firewireapp.play.LevelId;
import pl.tlasica.firewireapp.play.LevelsActivity;
import pl.tlasica.firewireapp.play.Player;
import pl.tlasica.firewireapp.play.Settings;
import pl.tlasica.firewireapp.play.SoundPoolPlayer;
import pl.tlasica.firewireapp.play.TutorialActivity;

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

        int nextLevel = settings.nextLevelId();
        Player.get().setLevel(nextLevel);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = "TODO: Share on FB";
                Toast.makeText(view.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        SoundPoolPlayer.init(this);

        ConnectorBitmap.initialize(getResources());

        try {
            new BoardLoader(getAssets()).initLevelSizes();
        } catch (IOException e) {
            Toast.makeText(this, "Ups. Loading levels failed.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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
        BoardLoader loader = new BoardLoader(getAssets());
        try {
            Board level = loader.load(Player.get().currentLevelId());
            LevelPlay.startLevel(level);
            // start play activity
            Intent myIntent = new Intent(this, PlayActivity.class);
            //myIntent.putExtra("key", value); //Optional parameters
            startActivity(myIntent);
        } catch (IOException e) {
            Toast.makeText(this, "Ups. Loading level failed on " + loader.getLastFile(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Ups. Loading level failed on " + e.getMessage(), Toast.LENGTH_LONG).show();
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
}
