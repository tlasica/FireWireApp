package pl.tlasica.firewireapp.play;

import android.graphics.Point;
import pl.tlasica.firewireapp.MouseEvent;

public class ClickEvent extends MouseEvent{

    Point point;

    public ClickEvent(Point p) {
        point = p;
    }

}
