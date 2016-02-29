package pl.tlasica.firewireapp.play;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import java.util.Map;

import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.model.Direction;
import pl.tlasica.firewireapp.model.IntCoord;
import pl.tlasica.firewireapp.model.LevelPlay;
import pl.tlasica.firewireapp.model.PlacedConnector;
import pl.tlasica.firewireapp.model.Wire;

public class BoardDrawing extends CanvasDrawing {

    private int backgroundColor = Color.parseColor("#009240");

    private Paint nodePaint;
    private Paint wirePaint;
    private Paint wireSidePaint;
    private Paint connPaint;
    private Paint connLinePaint;

    public void draw(Canvas canvas, LevelPlay play) {
        prepareDrawing(canvas);
        canvas.drawColor(backgroundColor);
        drawWires(canvas, play.board);
        drawNodes(canvas, play.board);
        drawConnectors(canvas, play.placedConnectors);
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

    void drawConnectors(Canvas canvas, Map<Integer, PlacedConnector> placedConnectors) {
        for(Map.Entry<Integer, PlacedConnector> item: placedConnectors.entrySet()) {
            int pos = item.getKey();
            PlacedConnector conn = item.getValue();
            drawConnector(canvas, pos, conn);
        }
    }

    void drawNodes(Canvas canvas, Board board) {
        for(int n: board.nodes) {
            int x = canvasX(IntCoord.x(n));
            int y = canvasY(IntCoord.y(n));
            canvas.drawCircle(x, y, nodeRadius, nodePaint());
        }
    }

    void drawWires(Canvas canvas, Board board) {
        for(Wire w: board.wires) {
            int px = IntCoord.x(w.a);
            int py = IntCoord.y(w.a);
            int qx = IntCoord.x(w.b);
            int qy = IntCoord.y(w.b);
            int pxr = canvasX(px) + wireSpacer(px, qx);
            int qxr = canvasX(qx) + wireSpacer(qx, px);
            int pyr = canvasY(py) + wireSpacer(py, qy);
            int qyr = canvasY(qy) + wireSpacer(qy, py);
            canvas.drawLine(pxr, pyr, qxr, qyr, wireSidePaint());
            canvas.drawLine(pxr, pyr, qxr, qyr, wirePaint());
        }
    }

    void drawConnector(Canvas canvas, int at, PlacedConnector conn) {
        drawRectangle(canvas, at, rectFillPaint());
        drawRectangle(canvas, at, rectPaint());

        int cx = canvasX(IntCoord.x(at));
        int cy = canvasY(IntCoord.y(at));
        // draw connector circle

        //canvas.drawCircle(cx, cy, nodeRadius, connPaint());
        // draw connector lines
        for(int d: conn.type.directions(conn.rotation)) {
            int vx = Direction.vx(d);
            int vy = Direction.vy(d);
            int dx = (cx-vx*5) + vx * nodeRadius * 3;
            int dy = (cy-vy*5) + vy * nodeRadius * 3;

            float w = (cellSize / 10 ) / 2 ;

            if (vy == 0) {
                canvas.drawLine(cx+vx*nodeRadius, cy - w, cx + vx*3*nodeRadius, cy - w, rectPaint());
                canvas.drawLine(cx+vx*nodeRadius, cy + w, cx + vx*3*nodeRadius, cy + w, rectPaint());
            }
            else if (vx == 0) {
                canvas.drawLine(cx - w, cy + vy * nodeRadius, cx - w, cy + vy * 3 *nodeRadius, rectPaint());
                canvas.drawLine(cx + w, cy + vy * nodeRadius, cx + w, cy + vy * 3 * nodeRadius, rectPaint());

            }
            else {
                canvas.drawLine(cx, cy, dx, dy, connLinePaint());
            }


        }
    }

    int wireSpacer(int p, int q) {
        int spacer = 1;
        if (q>p) return nodeRadius + spacer;
        else if (p>q) return -(nodeRadius + spacer);
        else return 0;
    }

    Paint nodePaint() {
        if (nodePaint == null) {
            int color = Color.argb(255, 191, 193, 194);
            nodePaint = strokePaint(cellSize/8, color);
        }
        return nodePaint;
    }

    Paint wirePaint() {
        if (wirePaint == null) {
            int width = cellSize/10 - 2;
            int color = Color.argb(255, 191, 193, 194);
            wirePaint = strokePaint(width, color);
        }
        return wirePaint;
    }

    private Paint wireSidePaint() {
        if (wireSidePaint == null) {
            int width = cellSize/10 + 2;
            int color = Color.argb(255, 172, 172, 172);
            wireSidePaint = strokePaint(width, color);
        }
        return wireSidePaint;
    }

    private Paint connPaint() {
        if (connPaint == null) {
            int width = cellSize/8;
            int color = Color.argb(255, 112, 173, 71);
            connPaint = strokePaint(width, color);
        }
        return connPaint;
    }

    private Paint connLinePaint() {
        if (connLinePaint == null) {
            int width = cellSize/10;
            int color = Color.argb(255, 255, 230, 0);
            connLinePaint = strokePaint(width, color);
        }
        return connLinePaint;
    }

    private Paint rectPaint;
    private Paint rectFillPaint;

    private Paint rectPaint() {
        if (rectPaint == null) {
            int width = cellSize / 10 / 2;
            int color = Color.argb(255,90, 92, 94);
            rectPaint = strokePaint(width, color);
            rectPaint.setAntiAlias(false);
        }
        return rectPaint;
    }

    private Paint rectFillPaint() {
        if (rectFillPaint == null) {
            int width = 1;
            int color = Color.argb(255, 220, 220, 220);
            rectFillPaint = strokePaint(width, color);
            rectFillPaint.setStyle(Paint.Style.FILL);
        }
        return rectFillPaint;
    }

    private void drawRectangle(Canvas canvas, int n, Paint paint) {
        int x = canvasX( IntCoord.x(n) );
        int y = canvasY(IntCoord.y(n));
        RectF rect = new RectF(x-2*nodeRadius, y-2*nodeRadius, x+2*nodeRadius, y+2*nodeRadius);
        canvas.drawRoundRect(rect, nodeRadius, nodeRadius, paint);
    }
}
