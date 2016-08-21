package pl.tlasica.firewireapp.play;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import pl.tlasica.firewireapp.R;
import pl.tlasica.firewireapp.parser.BoardLoader;

// gird layput not expanded:
// https://gist.github.com/sakurabird/6868765

//TODO: Expand Grid inside ScrollView

public class LevelsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        GridView gridview1 = (GridView) findViewById(R.id.gridview_level_1);
        gridview1.setAdapter(new LevelAdapter(this, 1));

        GridView gridview2 = (GridView) findViewById(R.id.gridview_level_2);
        gridview2.setAdapter(new LevelAdapter(this, 2));

        GridView gridview3 = (GridView) findViewById(R.id.gridview_level_3);
        gridview3.setAdapter(new LevelAdapter(this, 3));

    }

    public void onClose(View view) {
        finish();
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