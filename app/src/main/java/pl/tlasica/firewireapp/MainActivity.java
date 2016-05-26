package pl.tlasica.firewireapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.model.LevelPlay;
import pl.tlasica.firewireapp.parser.BoardLoader;
import pl.tlasica.firewireapp.play.ConnectorBitmap;
import pl.tlasica.firewireapp.play.SoundPoolPlayer;

public class MainActivity extends BasicActivity {

    private int currentLevel = 1;
    private int currentGame = 0;

    @Override
    protected void onDestroy() {
        SoundPoolPlayer.destroy();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        setButtonFontBoldItalic(R.id.button_play);
        setButtonFontBoldItalic(R.id.button_rank);
        setTextFont(R.id.logo_text);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        SoundPoolPlayer.init(this);
        ConnectorBitmap.initialize(getResources());
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
            int randomGame = 1 + new Random().nextInt(9);
            Board level = loader.load(currentLevel, randomGame);
            LevelPlay.startLevel(level);
            currentGame++;
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
}
