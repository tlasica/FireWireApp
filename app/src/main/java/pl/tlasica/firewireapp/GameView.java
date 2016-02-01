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

    private Paint nodePaint;
    private Paint wirePaint;
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
        for(int x=0; x<5; x++) {
            for (int y = 0; y < 5; y++) {
                drawWire(canvas, x, y, x+1, y);
                drawWire(canvas, x, y, x, y+1);
            }
        }
        drawWire(canvas, 5, 5, 4, 5);
        drawWire(canvas, 5, 5, 5, 4);
    }

    // TODO: create paints when the view is created and use them later

    private void drawNode(Canvas canvas, int cx, int cy) {
        canvas.drawCircle(rx(cx), ry(cy), radius(), nodePaint());
    }

    private void drawWire(Canvas canvas, int px, int py, int qx, int qy) {
        int pxr = rx(px) + wireSpacer(px, qx);
        int qxr = rx(qx) + wireSpacer(qx, px);
        int pyr = ry(py) + wireSpacer(py, qy);
        int qyr = ry(qy) + wireSpacer(qy, py);
        canvas.drawLine(pxr, pyr, qxr, qyr, wirePaint());
    }

    private int wireSpacer(int p, int q) {
        if (q>p) return radius()-3;
        if (p>q) return -(radius()-3);
        return 0;
    }

    private Paint nodePaint() {
        if (this.nodePaint == null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.DKGRAY);
            paint.setStrokeWidth(cellSize()/12);
            this.nodePaint = paint;
        }
        return this.nodePaint;
    }

    private Paint wirePaint() {
        if (this.wirePaint == null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.DKGRAY);
            paint.setStrokeWidth(cellSize()/10);
            this.wirePaint = paint;
        }
        return this.wirePaint;
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
        return x*cellSize() + cellSize()/2;
    }

    private int ry(int y) {
        return y*cellSize() + cellSize()/2;
    }

    private int radius() {
        return cellSize()/6;
    }

    private int cellSize() {
        return Math.round(getWidth() / 7);
    }

    // Use Color.parseColor to define HTML colors
    //paint.setColor(Color.parseColor("#CD5C5C"));

}
