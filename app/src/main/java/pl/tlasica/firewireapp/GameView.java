package pl.tlasica.firewireapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// FIXME: optimize drawing of the background as it will not change
// FIXME: drawing parameters (x->rx, rad) are function of view size, maybe also max coord?
// FIXME: może warto wydzielić klasę "Geometry", ktora coord <-> real coord

/**
 * Game View prints current game status using 2D shapes
 */
public class GameView extends View {

    private Map<String, Paint> paints = new HashMap<String, Paint>();

    private Board board = BoardFactory.standard();

    private final int colorWire = Color.argb(255, 219, 219, 219);
    private final int colorConn = Color.argb(255, 112, 173, 71);
    private final int colorConnLine = Color.argb(255, 255, 230, 0);


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(int n: board.nodes) {
            int x = Coord.x(n);
            int y = Coord.y(n);
            drawNode(canvas, x, y);
        }

        for(Wire w: board.wires) {
            drawWire(canvas, w.a, w.b);
        }

        drawConnector(canvas, 12, "S,E");
    }

    private void drawNode(Canvas canvas, int cx, int cy) {
        canvas.drawCircle(rx(cx), ry(cy), radius(), nodePaint());
    }

    private void drawWire(Canvas canvas, int from, int to) {
        int px = Coord.x(from), py = Coord.y(from);
        int qx = Coord.x(to), qy = Coord.y(to);
        int pxr = rx(px) + wireSpacer(px, qx);
        int qxr = rx(qx) + wireSpacer(qx, px);
        int pyr = ry(py) + wireSpacer(py, qy);
        int qyr = ry(qy) + wireSpacer(qy, py);
        canvas.drawLine(pxr, pyr, qxr, qyr, wirePaint());
    }

    private void drawConnector(Canvas canvas, int at, String type) {
        int cx = rx(Coord.x(at));
        int cy = ry(Coord.y(at));
        // draw connector circle
        canvas.drawCircle(cx, cy, radius(), connectorCirclePaint());
        // draw connector lines
        for(String dir : type.split(",")) {
            int vx = Direction.vx(dir);
            int vy = Direction.vy(dir);
            int dx = (cx-vx*5) + vx * radius() * 3;
            int dy = (cy-vy*5) + vy * radius() * 3;
            canvas.drawLine(cx, cy, dx, dy, connectorLinePaint());
        }
    }

    private int wireSpacer(int p, int q) {
        int spacer = 5;
        if (q>p) return radius() - spacer;
        if (p>q) return -(radius() - spacer);
        return 0;
    }


    private Paint nodePaint() {
        String key = "node";
        if (!paints.containsKey(key)) {
            Paint paint = createLinePaint(cellSize() / 8, colorWire);
            paints.put(key, paint);
        }
        return paints.get(key);
    }

    private Paint wirePaint() {
        String key = "wire";
        if (!paints.containsKey(key)) {
            Paint paint = createLinePaint(cellSize() / 10, colorWire);
            paints.put(key, paint);
        }
        return paints.get(key);
    }

    private Paint connectorCirclePaint() {
        String key = "connector";
        if (!paints.containsKey(key)) {
            Paint paint = createLinePaint(cellSize() / 8, colorConn);
            paints.put(key, paint);
        }
        return paints.get(key);
    }

    private Paint connectorLinePaint() {
        String key = "connectorline";
        if (!paints.containsKey(key)) {
            Paint paint = createLinePaint(cellSize() / 10, colorConnLine);
            paints.put(key, paint);
        }
        return paints.get(key);
    }

    private Paint createLinePaint(int width, int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        return paint;
    }

    private int rx(int x) { return x*cellSize() + cellSize()/2; }

    private int ry(int y) {
        return y*cellSize() + cellSize()/2;
    }

    private int radius() {
        return cellSize()/9;
    }

    private int cellSize() {
        return Math.round(getWidth() / 7);
    }

    // Use Color.parseColor to define HTML colors
    //paint.setColor(Color.parseColor("#CD5C5C"));

}
