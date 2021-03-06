package pl.tlasica.firewire.play;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import pl.tlasica.firewire.BasicActivity;
import pl.tlasica.firewire.PlayActivity;
import pl.tlasica.firewire.R;
import pl.tlasica.firewire.model.Board;
import pl.tlasica.firewire.model.LevelPlay;
import pl.tlasica.firewire.parser.BoardLoader;

// gird layput not expanded:
// https://gist.github.com/sakurabird/6868765

//TODO: Expand Grid inside ScrollView

public class LevelsActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        buildContent();
    }

    public void onClose(View view) {
        finish();
    }

    private void buildContent() {
        for(int level=1; level<=BoardLoader.NUM_LEVELS; level++) {
            addLevelTitle(level);
            addLevelGames(level);
        }
    }

    private void addLevelTitle(int level) {
        LinearLayout layout = (LinearLayout)findViewById(R.id.levels_vertical_view);
        TextView text = new TextView(this);
        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        text.setText("Level " + String.valueOf(level));
        text.setTextColor(getResources().getColor(R.color.myGreen));
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        //TODO: Font
        layout.addView(text);
    }

    private LinearLayout row() {
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(10, 10, 10, 10);
        return row;
    }

    @Override
    protected void onResume() {
        super.onResume();
        for(int level=1; level<=BoardLoader.NUM_LEVELS; level++) {
            for(int game=1; game<=BoardLoader.levelSizes[level]; game++) {
                int levelId = LevelId.levelId(level, game);
                ImageView image = (ImageView) findViewById(levelId);
                this.setImageForLevel(image);
            }
        }
    }

    private void addLevelGames(int level) {
        int numColumns = 5;
        LinearLayout layout = (LinearLayout)findViewById(R.id.levels_vertical_view);
        // calculate image size and create layout params
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int imageSize = metrics.widthPixels / 7; // TODO: change to 1/7 of screen width
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        layoutParams.setMargins(10, 5, 10, 5);
        // add 1st row
        LinearLayout row = this.row();
        layout.addView(row);
        // is level permitted
        for(int game=1; game<=BoardLoader.levelSizes[level]; game++) {
            Log.d("LEVEL", "adding game " + String.valueOf(game));
            ImageView image = new ImageView(this);
            image.setPadding(6, 6, 6, 6);
            image.setAdjustViewBounds(true);
            int levelId = LevelId.levelId(level, game);
            image.setId(levelId);
//            this.setImageForLevel(image);
            row.addView(image, layoutParams);
            if (game % numColumns == 0) {
                row = this.row();
                layout.addView(row);
            }
        }
    }

    private void setImageForLevel(ImageView image) {
        int levelId = image.getId();
        int levelNo = LevelId.level(levelId);
        boolean canPlayLevel = Player.get().canPlayLevel(levelNo);

        if (canPlayLevel) {
            image.setBackgroundResource(R.drawable.level_image_view);
            if (Player.get().isSolved(levelId))
                image.setImageResource(R.drawable.level_solved);
            else
                image.setImageResource(R.drawable.level_not_solved);
        }
        else {
            image.setImageResource(R.drawable.level_not_available);
            image.setBackgroundResource(R.drawable.level_image_view_not_available);
        }

        if (canPlayLevel) {
            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int levelId = v.getId();
                    BoardLoader loader = new BoardLoader();
                    try {
                        Board level = loader.load(levelId);
                        Player.get().setCurrentLevelId(levelId);
                        LevelPlay.startLevel(level);
                        Intent myIntent = new Intent(getBaseContext(), PlayActivity.class);
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
            });
        }
    }

}

