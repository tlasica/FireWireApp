package pl.tlasica.firewire.play;

import android.graphics.Point;

public class SwipeEvent extends MouseMoveEvent {
    public SwipeEvent(Point f, Point t) {
        super(f, t);
    }
}
