package pl.tlasica.firewireapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

// FIXME: optimize drawing of the background as it will not change
// FIXME: drawing parameters (x->rx, rad) are function of view size, maybe also max coord?
// FIXME: może warto wydzielić klasę "Geometry", ktora coord <-> real coord

/**
 * Game View prints current game status using 2D shapes
 */
public class GameView extends View {

    private Paint basicPaint;
    private List<Coord> nodes = null;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(Coord n: nodes()) {
            drawNode(canvas, n.x, n.y);
        }
        drawWire(canvas, 0, 0, 1, 0);
        drawWire(canvas, 0, 0, 0, 1);
        drawWire(canvas, 1, 0, 2, 2);
        drawWire(canvas, 0, 1, 2, 2);
    }

    // TODO: create paints when the view is created and use them later

    private void drawNode(Canvas canvas, int cx, int cy) {
        canvas.drawCircle(rx(cx), ry(cy), radius(), basicPaint());
    }

    private void drawWire(Canvas canvas, int px, int py, int qx, int qy) {
        int pxr = rx(px) + wireSpacer(px, qx);
        int qxr = rx(qx) + wireSpacer(qx, px);
        int pyr = ry(py) + wireSpacer(py, qy);
        int qyr = ry(qy) + wireSpacer(qy, py);
        canvas.drawLine(pxr, pyr, qxr, qyr, basicPaint());
    }

    private int wireSpacer(int p, int q) {
        if (q>p) return radius()-3;
        if (p>q) return -(radius()-3);
        return 0;
    }

    private Paint basicPaint() {
        if (this.basicPaint == null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.LTGRAY);
            paint.setStrokeWidth(10);
            this.basicPaint = paint;
        }
        return this.basicPaint;
    }

    public List<Coord> nodes() {
        if (nodes == null) {
            List<Coord> c = new ArrayList<Coord>();
            for (int x = 0; x < 6; ++x)
                for (int y = 0; y < 6; ++y)
                    c.add(new Coord(x, y));
            nodes = c;
        }
        return nodes;
    }


    private int rx(int x) {
        return 100 + x * 150;
    }

    private int ry(int y) {
        return 100 + y * 150;
    }

    private int radius() {
        return 18;
    }

    // Use Color.parseColor to define HTML colors
    //paint.setColor(Color.parseColor("#CD5C5C"));

}
