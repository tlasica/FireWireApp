package pl.tlasica.firewire;

import android.graphics.Point;
import android.util.Log;
        import android.view.MotionEvent;
        import android.view.View;

import java.util.Queue;

import pl.tlasica.firewire.play.ClickEvent;
import pl.tlasica.firewire.play.MouseMoveEvent;
import pl.tlasica.firewire.play.SwipeEvent;

/**
 * http://stackoverflow.com/questions/6645537/how-to-detect-the-swipe-left-or-right-in-android
 */
public class ViewMotionDetector implements View.OnTouchListener{

    private float downX, downY;
    private Point down;
    private View view;
    private Queue<MouseEvent> eventQueue;

    public ViewMotionDetector(View v, Queue<MouseEvent> queue){
        this.view = v;
        this.eventQueue = queue;
        view.setOnTouchListener(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN: {
                Log.d("EVENT", event.toString());
                downX = event.getX();
                downY = event.getY();
                this.down = new Point((int)downX, (int)downY);
                return true;
            }
            case MotionEvent.ACTION_UP: {
                Log.d("EVENT", event.toString());
                float upX = event.getX();
                float upY = event.getY();

                // Can be swipe event
                double dist = distance(downX, downY, upX, upY);
                if (isSwipeDistance(dist)) {
                    onSwipeEvent(downX, downY, upX, upY);
                    return true;
                }
                else {
                    onClick(downX, downY);
                    return true;
                }
            }
            case MotionEvent.ACTION_MOVE: {
                Point curr = new Point((int)event.getX(),(int)event.getY());
                eventQueue.add(new MouseMoveEvent(this.down, curr));
            }
            default:
                return false;
        }
    }

    private void onClick(float downX, float downY) {
        int x = (int) downX;
        int y = (int) downY;
        String msg = String.format("SHORT CLICK at (%d, %d)", x, y);
        Log.d("MOTION", msg);
        eventQueue.add(new ClickEvent(new Point(x, y)));
    }

    private boolean isSwipeDistance(double dist) {
        return dist >= 50.0;
    }

    private boolean isClickDistance(double dist) {
        return  dist <= 10.0;
    }

    private void onSwipeEvent(float downX, float downY, float upX, float upY) {
        String msg = String.format("SWIPE from (%f, %f) to (%f, %f)", downX, downY, upX, upY);
        Log.d("MOTION", msg);
        Point down = new Point((int)downX, (int)downY);
        Point up = new Point((int)upX, (int)upY);
        eventQueue.add(new SwipeEvent(down, up));
    }


    private double distance(float downX, float downY, float upX, float upY) {
        float dx = upX - downX;
        float dy = upY - downY;
        return Math.sqrt(dx*dx + dy*dy);
    }

}