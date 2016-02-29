package pl.tlasica.firewireapp.play;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Map;

import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.model.IntCoord;
import pl.tlasica.firewireapp.model.LevelPlay;
import pl.tlasica.firewireapp.model.PlacedConnector;
import pl.tlasica.firewireapp.model.Wire;

public class BoardDrawing extends CanvasDrawing {

    private int backgroundColor = Color.WHITE;
    private int wireColor = Color.argb(255, 191, 193, 194);
    private int connectionColor = Color.parseColor("#ff2600");

    private Paint nodePaint;
    private Paint wirePaint;
    private Paint connectionPaint;
    private Paint bmpPaint = new Paint();


    public void draw(Canvas canvas, LevelPlay play) {
        prepareDrawing(canvas);
        canvas.drawColor(backgroundColor);
        drawWires(canvas, play.board);
        drawNodes(canvas, play.board);
        drawConnectors(canvas, play.board, play.placedConnectors);
        drawSpecial(canvas, play.board, play.board.plus, ConnectorBitmap.plusBitmap);
        drawSpecial(canvas, play.board, play.board.minus, ConnectorBitmap.minusBitmap);
        drawSpecial(canvas, play.board, play.board.target, ConnectorBitmap.targetBitmap);
    }

    public int nodeNumber(Point mouse, Board board) {
        for(int n: board.nodes) {
            int x = canvasX(IntCoord.x(n));
            int y = canvasY(IntCoord.y(n));
            int dist = cellSize / 3;
            boolean xFit = (mouse.x>=x-dist && mouse.x<=x+dist);
            boolean yFit = (mouse.y>=y-dist && mouse.y<=y+dist);
            if (xFit && yFit) {
                return n;
            }
        }
        return -1;
    }

    void drawConnectors(Canvas canvas, Board board, Map<Integer, PlacedConnector> placedConnectors) {
        for(Map.Entry<Integer, PlacedConnector> item: placedConnectors.entrySet()) {
            int pos = item.getKey();
            PlacedConnector conn = item.getValue();
            drawConnector(canvas, board, pos, conn);
        }
    }

    void drawNodes(Canvas canvas, Board board) {
        // intentionally empty
    }

    void drawWires(Canvas canvas, Board board) {
        for(Wire w: board.wires) {
            drawWire(canvas, w, wirePaint());
        }
    }

    void drawWire(Canvas canvas, Wire wire, Paint paint) {
        int px = IntCoord.x(wire.a);
        int py = IntCoord.y(wire.a);
        int qx = IntCoord.x(wire.b);
        int qy = IntCoord.y(wire.b);
        int pxr = canvasX(px) + wireSpacer(px, qx);
        int qxr = canvasX(qx) + wireSpacer(qx, px);
        int pyr = canvasY(py) + wireSpacer(py, qy);
        int qyr = canvasY(qy) + wireSpacer(qy, py);
        canvas.drawLine(pxr, pyr, qxr, qyr, paint);
    }

    void drawConnector(Canvas canvas, Board board, int at, PlacedConnector conn) {
        // draw all connected wires as "connected"
        for (Wire w: board.connectedWires(at, conn.type, conn.rotation)) {
            drawWire(canvas, w, connectedPaint());
        }
        // get connector icon
        Bitmap bmp = ConnectorBitmap.get(conn.type);
        assert bmp != null;

        // draw connector bitmap
        drawBitmapAtNode(canvas, at, bmp);
    }

    void drawSpecial(Canvas canvas, Board board, int at, Bitmap bmp) {
        // draw all connected wires as "connected"
        for (Wire w: board.adj(at)) {
            drawWire(canvas, w, connectedPaint());
        }
        // draw icon
        drawBitmapAtNode(canvas, at, bmp);
    }

    void drawBitmapAtNode(Canvas canvas, int at, Bitmap bmp) {
        int cx = canvasX(IntCoord.x(at));
        int cy = canvasY(IntCoord.y(at));
        float size = this.cellSize / 2.0f;
        RectF targetRect = new RectF(cx-size/2, cy-size/2, cx+size/2, cy+size/2);
        Rect rectSrc = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        canvas.drawBitmap(bmp, rectSrc, targetRect, bmpPaint);
    }

    int wireSpacer(int p, int q) {
        int spacer = 1;
        if (q>p) return nodeRadius + spacer;
        else if (p>q) return -(nodeRadius + spacer);
        else return 0;
    }

    Paint wirePaint() {
        if (wirePaint == null) {
            int width = wireWidth();
            int color = Color.argb(255, 191, 193, 194);
            wirePaint = strokePaint(width, color);
        }
        return wirePaint;
    }

    int wireWidth() {
        return cellSize/20;
    }

    private Paint connectedPaint() {
        if (connectionPaint == null) {
            int width =wireWidth();
            int color = connectionColor;
            connectionPaint = strokePaint(width, color);
        }
        return connectionPaint;
    }

}
