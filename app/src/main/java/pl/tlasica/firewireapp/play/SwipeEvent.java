package pl.tlasica.firewireapp.play;

import android.graphics.Point;
import pl.tlasica.firewireapp.MouseEvent;

public class SwipeEvent extends MouseMoveEvent {
    public SwipeEvent(Point f, Point t) {
        super(f, t);
    }
}
