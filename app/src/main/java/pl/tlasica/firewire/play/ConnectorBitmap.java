package pl.tlasica.firewire.play;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

import pl.tlasica.firewire.R;
import pl.tlasica.firewire.model.ConnectorType;

public class ConnectorBitmap {

    static Resources resources;
    static Map<ConnectorType, Bitmap> freeBitmaps= new HashMap<>();
    static Map<ConnectorType, Bitmap> choiceBitmaps= new HashMap<>();
    static public Bitmap plusBitmap;
    static public Bitmap minusBitmap;
    static public Bitmap targetBitmap;
    public static Bitmap targetConnectedBitmap;

    public static void initialize(Resources res) {
        resources = res;
        for(ConnectorType type: ConnectorType.values()){
            choiceBitmaps.put(type, create(choiceResId(type)));
            freeBitmaps.put(type, create(freeResId(type)));
        }
        plusBitmap = BitmapFactory.decodeResource(res, R.drawable.battery_blue);
        minusBitmap = BitmapFactory.decodeResource(res, R.drawable.battery_blue);
        targetBitmap = BitmapFactory.decodeResource(res, R.drawable.shockcircle_off);
        targetConnectedBitmap = BitmapFactory.decodeResource(res, R.drawable.shockcircle_grilled);
    }

    // get bitmap without any connections
    public static Bitmap freeBitmap(ConnectorType type) {
        return freeBitmaps.get(type);
    }

    // get bitmap describing placed connector
    public static Bitmap choiceBitmap(ConnectorType type) {
        return choiceBitmaps.get(type);
    }

    static Bitmap create(int resId) {
        if (resId > 0) {
            Bitmap bmp = BitmapFactory.decodeResource(resources, resId);
            return bmp;
        }
        else return null;
    }

    static int choiceResId(ConnectorType type) {
        switch (type) {
            case I_SHAPE: return R.drawable.connector_e_180_placed;
            case L_SHAPE: return R.drawable.connector_e_90_placed;
            case X_SHAPE: return R.drawable.connector_e_x_placed;
            default: return R.drawable.connector;
        }
    }

    static int freeResId(ConnectorType type) {
        switch (type) {
            case DEFINED: return R.drawable.connection;
            default: return R.drawable.connector;
        }
    }
}
