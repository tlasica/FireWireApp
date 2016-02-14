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

    public static void initialize(Resources res) {
        resources = res;
        for(ConnectorType type: ConnectorType.values()){
            Bitmap bmp = create(type);
            bitmaps.put(type, bmp);
        }
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
            case I_SHAPE: return R.drawable.connector_i_shape;
            case L_SHAPE: return R.drawable.connector_l_shape;
            default: return 0;
        }
    }
}
