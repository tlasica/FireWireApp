package pl.tlasica.firewireapp.play;

import android.graphics.Point;
import pl.tlasica.firewireapp.MouseEvent;

public class MouseMoveEvent extends MouseEvent{
    Point from;
    Point to;

    public MouseMoveEvent(Point f, Point t) {
        from = f;
        to = t;
    }
}
