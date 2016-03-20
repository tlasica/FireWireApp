package pl.tlasica.firewireapp.play;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.List;
import java.util.Map;

import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.model.IntCoord;
import pl.tlasica.firewireapp.model.LevelPlay;
import pl.tlasica.firewireapp.model.PlacedConnector;
import pl.tlasica.firewireapp.model.Wire;

public class BoardDrawing extends CanvasDrawing {

    private int boardColor = Color.parseColor("#325132");
    private int wireColor = Color.parseColor("#55B746");
    private int connectionColor = Color.parseColor("#E7C03F");

    private Paint nodePaint = fillPaint(wireColor);
    private Paint boardPaint = fillPaint(boardColor);
    private Paint wirePaint;
    private Paint connectionPaint;
    private Paint bmpPaint = new Paint();

    private Paint[] wirePaints = {
            strokePaint(10, Color.parseColor("#2A5528")),
            strokePaint(10, Color.parseColor("#488341")),
            strokePaint(10, Color.parseColor("#55B746"))
    };

    private Paint[] connectorFillPaints = {
            fillPaint(Color.parseColor("#C6A436")),
            fillPaint(Color.parseColor("#E7C03F")),
            fillPaint(Color.parseColor("#FADC6A")),
            fillPaint(Color.parseColor("#FFF29D"))
    };


    private int full_wire_size = 8; // full wire consists of 8 parts
    private int conn_wire_size = 6; // we do not print last part

    public void draw(Canvas canvas, LevelPlay play) {
        prepareDrawing(canvas);
        canvas.drawColor(boardColor);
        drawWires(canvas, play.board);
        drawNodes(canvas, play.board);
        drawConnectors(canvas, play.board, play.placedConnectors);
        drawSpecial(canvas, play.board, play.board.plus, ConnectorBitmap.plusBitmap);
        drawSpecial(canvas, play.board, play.board.minus, ConnectorBitmap.minusBitmap);
        drawSpecial(canvas, play.board, play.board.target, ConnectorBitmap.targetBitmap);
    }

    public int nodeNumber(Point mouse, Board board) {
        for(int n: board.nodes) {
            float x = canvasX(IntCoord.x(n));
            float y = canvasY(IntCoord.y(n));
            float dist = cellSize / 3;
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
        for(Integer n: board.nodes) {
            drawNode(canvas, n, true);
        }
    }

    void drawNode(Canvas canvas, int node, boolean large) {
        float radius = large ? this.nodeRadius : this.nodeRadius * 0.66f;
        float cx = canvasX(IntCoord.x(node));
        float cy = canvasY(IntCoord.y(node));
        canvas.drawCircle(cx, cy, radius, nodePaint);
        float holeRadius = wireWidth > 6 ? wireWidth / 3.0f : 2.0f;
        canvas.drawCircle(cx, cy, holeRadius, boardPaint);
    }

    void drawWires(Canvas canvas, Board board) {
        float border = wireWidth * 0.25f;
        wirePaints[0].setStrokeWidth(wireWidth + border);
        wirePaints[1].setStrokeWidth(wireWidth);
        wirePaints[2].setStrokeWidth(wireWidth - border);
        for(Wire w: board.wires) {
            drawWire(canvas, w.a, w.b, wirePaints[0], full_wire_size);
            drawWire(canvas, w.a, w.b, wirePaints[1], full_wire_size);
            drawWire(canvas, w.a, w.b, wirePaints[2], full_wire_size);
        }
    }

    void drawWire(Canvas canvas, int a, int b, Paint paint, int length) {
        float ax = canvasX(IntCoord.x(a));
        float ay = canvasY(IntCoord.y(a));
        float bx = canvasX(IntCoord.x(b));
        float by = canvasY(IntCoord.y(b));
        float xStep = (bx - ax) / full_wire_size;
        float yStep = (by - ay) / full_wire_size;
        boolean horizontal = (ax==bx);
        boolean vertical = (by==ay);
        if (horizontal || vertical) {
            canvas.drawLine(ax, ay, ax + length * xStep, ay + length * yStep, paint);
        }
        else {
            // draw part 1-2/8
            float space = 1.5f;
            float bendRad = 0.40f * wireWidth;
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(ax + space * xStep, ay + space * yStep, bendRad, paint);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(ax, ay, ax+space*xStep, ay+space*yStep, paint);
            // draw part 7-8/8 only if applicable
            if (length==full_wire_size) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(bx - space*xStep, by - space*yStep, bendRad, paint);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawLine(bx - space*xStep, by - space * yStep, bx, by, paint);
            }
            // draw circle where it bends
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(ax + space*xStep, by - space*yStep, bendRad, paint);
            paint.setStyle(Paint.Style.STROKE);
            // draw line down
            canvas.drawLine(ax+space*xStep, ay+space*yStep, ax+space*xStep, by-space*yStep, paint);
            // draw line right
            canvas.drawLine(ax+space*xStep, by-space*yStep, bx-space*xStep, by-space*yStep, paint);

        }
    }

    void drawConnector(Canvas canvas, Board board, int at, PlacedConnector conn) {
        // draw all connected wires as "connected"
        List<Integer> connNodes = board.connectedNodes(at, conn.type, conn.rotation);
        drawConnectedWires(canvas, at, connNodes );
        // get connector icon
        Bitmap bmp = ConnectorBitmap.get(conn.type);
        assert bmp != null;
        // draw connector bitmap
        drawBitmapAtNode(canvas, at, bmp);
    }

    void drawConnectedWires(Canvas canvas, int at, List<Integer> toNodes) {
        for(Integer n: toNodes) {
            drawWire(canvas, at, n, connectedPaint(), conn_wire_size);
        }
    }

    void drawSpecial(Canvas canvas, Board board, int at, Bitmap bmp) {
        drawConnectedWires(canvas, at, board.adjNodes(at));
        drawBitmapAtNode(canvas, at, bmp);
    }

    void drawBitmapAtNode(Canvas canvas, int at, Bitmap bmp) {
        float cx = canvasX(IntCoord.x(at));
        float cy = canvasY(IntCoord.y(at));
        float size = this.cellSize / 2.0f;
        RectF targetRect = new RectF(cx-size/2, cy-size/2, cx+size/2, cy+size/2);
        Rect rectSrc = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        canvas.drawBitmap(bmp, rectSrc, targetRect, bmpPaint);
    }

    private Paint connectedPaint() {
        if (connectionPaint == null) {
            int width = (int)wireWidth;
            int color = connectionColor;
            connectionPaint = strokePaint(width, color);
        }
        return connectionPaint;
    }

}
