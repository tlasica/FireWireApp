package pl.tlasica.firewire.play;

import android.graphics.Point;
import pl.tlasica.firewire.MouseEvent;

public class ClickEvent extends MouseEvent{

    Point point;

    public ClickEvent(Point p) {
        point = p;
    }

}
