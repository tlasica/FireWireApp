package pl.tlasica.firewire.play;

import android.graphics.Point;
import pl.tlasica.firewire.MouseEvent;

public class MouseMoveEvent extends MouseEvent{
    Point from;
    Point to;

    public MouseMoveEvent(Point f, Point t) {
        from = f;
        to = t;
    }
}
