package pl.tlasica.firewireapp.play;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import pl.tlasica.firewireapp.BasicActivity;
import pl.tlasica.firewireapp.R;
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
        LinearLayout row = this.row();
        layout.addView(row);
        int imageSize = 160; // TODO: change to 1/7 of screen width
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        layoutParams.setMargins(5, 5, 5, 5);
        for(int game=1; game<=BoardLoader.levelSizes[level]; game++) {
            Log.d("LEVEL", "adding game " + String.valueOf(game));
            ImageView image = new ImageView(row.getContext());
            image.setPadding(6, 6, 6, 6);
            image.setBackgroundResource(R.drawable.level_image_view);
            image.setImageResource(R.drawable.shockcircle);
            image.setAdjustViewBounds(true);
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




class LevelAdapter extends BaseAdapter {
    private Context mContext;
    private int     levelNo;

    public LevelAdapter(Context c, int level) {
        mContext = c;
        levelNo = level;
    }

    @Override
    public int getCount() {
        int count = BoardLoader.levelSizes[levelNo];
        Log.d("LEVELS", "Level " + String.valueOf(levelNo) + " count is " + String.valueOf(count));
        return count;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("LEVELS", "Level " + String.valueOf(levelNo) + " get view for " + String.valueOf(position));
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            imageView.setBackgroundResource(R.drawable.level_image_view);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(R.drawable.shockcircle);
        return imageView;
    }
}