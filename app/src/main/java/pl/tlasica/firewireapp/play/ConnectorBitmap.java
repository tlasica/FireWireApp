package pl.tlasica.firewireapp.play;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

import pl.tlasica.firewireapp.R;
import pl.tlasica.firewireapp.model.ConnectorType;

public class ConnectorBitmap {

    static Resources resources;
    static Map<ConnectorType, Bitmap> bitmaps= new HashMap<>();
    static public Bitmap plusBitmap;
    static public Bitmap minusBitmap;
    static public Bitmap targetBitmap;

    public static void initialize(Resources res) {
        resources = res;
        for(ConnectorType type: ConnectorType.values()){
            Bitmap bmp = create(type);
            bitmaps.put(type, bmp);
        }
        plusBitmap = BitmapFactory.decodeResource(res, R.drawable.source_plus);
        minusBitmap = BitmapFactory.decodeResource(res, R.drawable.source_minus);
        targetBitmap = BitmapFactory.decodeResource(res, R.drawable.human);
    }

    public static Bitmap get(ConnectorType type) {
        return bitmaps.get(type);
    }

    static Bitmap create(ConnectorType type) {
        int resId = resourceId(type);
        if (resId > 0) {
            Bitmap bmp = BitmapFactory.decodeResource(resources, resId);
            return bmp;
        }
        else return null;
    }

    static int resourceId(ConnectorType type) {
        switch (type) {
            case I_SHAPE: return R.drawable.connector_e_180;
            case L_SHAPE: return R.drawable.connector_e_180;
            case X_SHAPE: return R.drawable.connector_e_180;
            default: return R.drawable.connector_e_180;
        }
    }
}
