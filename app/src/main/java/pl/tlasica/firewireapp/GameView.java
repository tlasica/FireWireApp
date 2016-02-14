package pl.tlasica.firewireapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.model.LevelFactory;
import pl.tlasica.firewireapp.model.IntCoord;
import pl.tlasica.firewireapp.model.Direction;
import pl.tlasica.firewireapp.model.Wire;
import pl.tlasica.firewireapp.play.Game;

// FIXME: optimize drawing of the background as it will not change
// FIXME: drawing parameters (x->rx, rad) are function of view size, maybe also max coord?
// FIXME: może warto wydzielić klasę "Geometry", ktora coord <-> real coord

/**
 * Game View prints current game status using 2D shapes
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Game game;

    private Map<String, Paint> paints = new HashMap<String, Paint>();
    private Board board = LevelFactory.standard();
    private AvailableConnectorPainter availableConnectorPainter;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("SURFACE", "Created");
        this.game = new Game(holder);
        game.start();
//        Canvas canvas = holder.lockCanvas();
//        drawGame(canvas);
//        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("SURFACE", "Changed with w:"+width+" and h:"+height);
        // TODO: how to handle this???
//        Canvas canvas = holder.lockCanvas();
//        drawGame(canvas);
//        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("SURFACE", "Destroyed");
        if (game != null) {
            game.stop();
            game = null;
        }

    }







    protected void drawGame(Canvas canvas) {

        canvas.drawColor(Color.parseColor("#009240"));

        for(int n: board.nodes) {
            int x = IntCoord.x(n);
            int y = IntCoord.y(n);
            drawNode(canvas, x, y);
        }

        for(Wire w: board.wires) {
            drawWire(canvas, w.a, w.b);
        }

        drawConnector(canvas, 12, "S,E");

        if (availableConnectorPainter == null) {
            availableConnectorPainter = new AvailableConnectorPainter(this);
        }
        availableConnectorPainter.draw(canvas, 0, "N,W", 3);
        availableConnectorPainter.draw(canvas, 1, "E,W", 2);

    }

    private void drawNode(Canvas canvas, int cx, int cy) {
        canvas.drawCircle(rx(cx), ry(cy), radius(), nodePaint());
    }

    private void drawWire(Canvas canvas, int from, int to) {
        int px = IntCoord.x(from), py = IntCoord.y(from);
        int qx = IntCoord.x(to), qy = IntCoord.y(to);
        int pxr = rx(px) + wireSpacer(px, qx);
        int qxr = rx(qx) + wireSpacer(qx, px);
        int pyr = ry(py) + wireSpacer(py, qy);
        int qyr = ry(qy) + wireSpacer(qy, py);
        canvas.drawLine(pxr, pyr, qxr, qyr, wireSidePaint());
        canvas.drawLine(pxr, pyr, qxr, qyr, wirePaint());
    }

    private void drawConnector(Canvas canvas, int at, String type) {
        int cx = rx(IntCoord.x(at));
        int cy = ry(IntCoord.y(at));
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
        int spacer = 4;
        if (q>p) return radius() + spacer;
        if (p>q) return -(radius() + spacer);
        return 0;
    }


    private Paint nodePaint() {
        String key = "node";
        if (!paints.containsKey(key)) {
            int color = Color.argb(255, 219, 219, 219);
            Paint paint = PaintFactory.strokePaint(cellSize() / 8, color);
            paints.put(key, paint);
        }
        return paints.get(key);
    }

    private Paint wirePaint() {
        String key = "wire";
        if (!paints.containsKey(key)) {
            int width = cellSize()/10 - 2;
            int color = Color.argb(255, 219, 219, 219);
            Paint paint = PaintFactory.strokePaint(width, color);
            paints.put(key, paint);
        }
        return paints.get(key);
    }

    private Paint wireSidePaint() {
        String key = "wireside";
        if (!paints.containsKey(key)) {
            int width = cellSize()/10 + 2;
            Paint paint = PaintFactory.strokePaint(width, Color.argb(255, 156, 192, 171));
            paints.put(key, paint);
        }
        return paints.get(key);
    }

    private Paint connectorCirclePaint() {
        String key = "connector";
        if (!paints.containsKey(key)) {
            int color = Color.argb(255, 112, 173, 71);
            Paint paint = PaintFactory.strokePaint(cellSize() / 8, color);
            paints.put(key, paint);
        }
        return paints.get(key);
    }

    private Paint connectorLinePaint() {
        String key = "connectorline";
        if (!paints.containsKey(key)) {
            int color = Color.argb(255, 255, 230, 0);
            Paint paint = PaintFactory.strokePaint(cellSize() / 10, color);
            paints.put(key, paint);
        }
        return paints.get(key);
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

}

/**
 * Can draw available connector based on the dimensions of it's parent
 * Draws frame, connector shape and number of connectors available
 * Connectors with count==0 should be visible disabled
 */
class AvailableConnectorPainter {

    int frameWidth;
    int frameHeight;
    float offsetX;
    float offsetY;
    Paint framePaint;

    Map<String, Drawable> images = new HashMap<>();


    public AvailableConnectorPainter(View owner) {
        frameWidth = owner.getWidth() / 6;
        frameHeight = frameWidth;
        offsetX = Math.round(frameWidth * 0.25);
        offsetY = Math.round(owner.getHeight() - 1.25 * frameHeight);
        Log.d("OFF:X", String.valueOf(offsetX));
        Log.d("OFF:Y", String.valueOf(offsetY));
        initPaints();

        Resources res = owner.getContext().getResources();
        images.put("180", res.getDrawable(R.drawable.connector180));
        images.put("90", res.getDrawable(R.drawable.connector90));
    }

    public void draw(Canvas canvas, int index, String type, int count) {
        drawFrame(canvas, index);
        drawConnector(canvas, index, type);
        drawCount(canvas, index, count);
    }

    private void drawFrame(Canvas canvas, int index) {
        canvas.drawRoundRect(frameRect(index), 3, 3, framePaint);
    }

    private void drawCount(Canvas canvas, int index, int count) {
        // TODO: draw number of remaining connectors
    }

    private void drawConnector(Canvas canvas, int index, String type) {
        //TODO: draw shape from drawables
    }

    private float left(int index) { return offsetX + index * (frameWidth + offsetX); }

    private RectF frameRect(int index) {
        return new RectF(left(index), offsetY, left(index)+frameWidth, offsetY+frameHeight);
    }

    private void initPaints() {
        framePaint = PaintFactory.strokePaint(3, Color.argb(255, 219, 219, 219));
    }
}


class PaintFactory {



    public static Paint strokePaint(int width, int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        return paint;
    }


}