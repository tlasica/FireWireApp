package pl.tlasica.firewireapp.play;

import android.graphics.Point;
import pl.tlasica.firewireapp.MouseEvent;

public class SwipeEvent extends MouseEvent {
    Point from;
    Point to;

    public SwipeEvent(Point f, Point t) {
        from = f;
        to = t;
    }
}
