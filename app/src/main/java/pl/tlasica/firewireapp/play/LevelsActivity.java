package pl.tlasica.firewireapp.play;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import pl.tlasica.firewireapp.BasicActivity;
import pl.tlasica.firewireapp.PlayActivity;
import pl.tlasica.firewireapp.R;
import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.model.LevelPlay;
import pl.tlasica.firewireapp.parser.BoardLoader;

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
        boolean canPlayLevel = Player.get().canPlayLevel(level);
        for(int game=1; game<=BoardLoader.levelSizes[level]; game++) {
            Log.d("LEVEL", "adding game " + String.valueOf(game));
            ImageView image = new ImageView(row.getContext());
            image.setPadding(6, 6, 6, 6);
            image.setAdjustViewBounds(true);
            int levelId = LevelId.levelId(level, game);
            image.setId(levelId);
            if (canPlayLevel) {
                image.setBackgroundResource(R.drawable.level_image_view);
                if (Player.get().isSolved(levelId))
                    image.setImageResource(R.drawable.level_solved);
                else
                    image.setImageResource(R.drawable.level_not_solved);
                image.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int levelId = v.getId();
                        BoardLoader loader = new BoardLoader(getAssets());
                        try {
                            Board level = loader.load(levelId);
                            Player.get().setLevel(levelId);
                            LevelPlay.startLevel(level);
                            Intent myIntent = new Intent(getBaseContext(), PlayActivity.class);
                            startActivity(myIntent);
                        } catch (IOException e) {
                            Toast.makeText(getBaseContext(), "Ups. Loading level failed on " + loader.getLastFile(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
            else {
                image.setImageResource(R.drawable.level_not_available);
                image.setBackgroundResource(R.drawable.level_image_view_not_available);
            }
            row.addView(image, layoutParams);
            Space space = new Space(this);
            row.addView(space);
            if (game % numColumns == 0) {
                row = this.row();
                layout.addView(row);
            }
        }
    }
}

