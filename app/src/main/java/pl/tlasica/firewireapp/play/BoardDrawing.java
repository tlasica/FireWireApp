package pl.tlasica.firewireapp.play;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.util.Log;

import pl.tlasica.firewireapp.model.Board;
import pl.tlasica.firewireapp.model.ConnectorType;
import pl.tlasica.firewireapp.model.DefinedConnector;
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
    private Paint connectionPaint;
    private Paint bmpPaint = new Paint();

    private Random random = new Random(System.currentTimeMillis());
    private Map<Integer, Integer> diagonalWires = new HashMap<>();

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

    private Canvas boardCanvas;
    private Bitmap boardBitmap;
    private long   lastDrawnGeneration = 0;

    private int full_wire_size = 8; // full wire consists of 8 parts
    private int conn_wire_size = 4; // we do not print last part


    public void draw(Canvas canvas, LevelPlay play) {
        this.cellsInRow = play.board.xSize();
        prepareDrawing(canvas);

        if (this.boardCanvas == null) {
            // create canvas
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            boardBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), conf);
            boardCanvas = new Canvas(boardBitmap);
        }

        if (play.generation != this.lastDrawnGeneration) {
            Log.d("DRAW", "Drawing background for generation: " + String.valueOf(play.generation));
            // draw the board on canvas
            drawBoard(boardCanvas, play.board);
            drawWires(boardCanvas, play.board);
            drawNodes(boardCanvas, play);
            this.lastDrawnGeneration = play.generation;
        }

        canvas.drawBitmap(boardBitmap, 0, 0, bmpPaint);

        drawConnectors(canvas, play.board, play.placedConnectors);
        drawDefinedConnections(canvas, play.board);
        drawSpecial(canvas, play.board, play.board.plus, ConnectorBitmap.plusBitmap);
        drawSpecial(canvas, play.board, play.board.minus, ConnectorBitmap.minusBitmap);
        drawSpecial(canvas, play.board, play.board.target, ConnectorBitmap.targetBitmap);
    }

    private void drawBoard(Canvas canvas, Board board) {
        RectF rect = new RectF(10, 10, canvas.getWidth()-10, canvas.getHeight()-10);
        float rad = this.nodeRadius * 0.6f;
        canvas.drawRoundRect(rect, rad, rad, this.boardPaint);
    }

    public int nodeNumber(Point mouse, Board board) {
        for (int n : board.nodes) {
            float x = canvasX(IntCoord.x(n));
            float y = canvasY(IntCoord.y(n));
            float dist = cellSize / 2.25f;
            boolean xFit = (mouse.x >= x - dist && mouse.x <= x + dist);
            boolean yFit = (mouse.y >= y - dist && mouse.y <= y + dist);
            if (xFit && yFit) {
                return n;
            }
        }
        return -1;
    }

    void drawConnectors(Canvas canvas, Board board, Map<Integer, PlacedConnector> placedConnectors) {
        for (Map.Entry<Integer, PlacedConnector> item : placedConnectors.entrySet()) {
            int pos = item.getKey();
            PlacedConnector conn = item.getValue();
            drawConnector(canvas, board, pos, conn);
        }
    }

    void drawDefinedConnections(Canvas canvas, Board board) {
        for (Map.Entry<Integer, DefinedConnector> item : board.definedConnectors.entrySet()) {
            int pos = item.getKey();
            DefinedConnector conn = item.getValue();
            drawDefinedConnection(canvas, board, pos, conn);
        }
    }

    void drawDefinedConnection(Canvas canvas, Board board, int pos, DefinedConnector conn) {
        // draw all connected wires as "connected"
        List<Integer> connNodes = board.connectedNodes(pos, conn);
        drawConnectedWires(canvas, pos, connNodes, conn_wire_size);
        // get connector icon
        if (!board.isSpecial(pos)) {
            Bitmap bmp = ConnectorBitmap.freeBitmap(ConnectorType.DEFINED);
            assert bmp != null;
            drawBitmapAtNode(canvas, pos, bmp);
        }
    }

    void drawNodes(Canvas canvas, LevelPlay play) {
        for (Integer n : play.board.nodes) {
            if (play.isFree(n)) {
                drawNode(canvas, n, true);
            }
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
        for (Wire w : board.wires) {
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
        boolean horizontal = (ax == bx);
        boolean vertical = (by == ay);
        if (horizontal || vertical) {
            canvas.drawLine(ax, ay, ax + length * xStep, ay + length * yStep, paint);
        } else {
            float space = 2.0f;
            float bendRad = 0.40f * wireWidth;
            // draw part 1-2/8
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(ax + space * xStep, ay + space * yStep, bendRad, paint);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(ax, ay, ax + space * xStep, ay + space * yStep, paint);
            // draw line |_ or it's part
            drawDiagonal(canvas, a, b, paint, length);
            // draw part 7-8/8 only if applicable
            if (length == full_wire_size) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(bx - space * xStep, by - space * yStep, bendRad, paint);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawLine(bx - space * xStep, by - space * yStep, bx, by, paint);
            }
        }
    }

    private void drawDiagonal(Canvas canvas, int a, int b, Paint paint, int length) {
        float ax = canvasX(IntCoord.x(a));
        float ay = canvasY(IntCoord.y(a));
        float bx = canvasX(IntCoord.x(b));
        float by = canvasY(IntCoord.y(b));
        float xStep = (bx - ax) / full_wire_size;
        float yStep = (by - ay) / full_wire_size;
        float space = 2.0f;
        float bendRad = 0.40f * wireWidth;
        int shape = getDiagonalWireShape(a, b);
        float minX = Math.min(ax + space * xStep, bx - space * xStep);
        float maxX = Math.max(ax + space * xStep, bx - space * xStep);
        float minY = Math.min(ay + space * yStep, by - space * yStep);
        float maxY = Math.max(ay + space * yStep, by - space * yStep);
        float dx = (maxX + minX) / 2.0f;
        float dy = (maxY + minY) / 2.0f;
        if (shape < 0) {
            dx = minX;
            dy = maxY;
        }
        if (shape > 0) {
            dx = maxX;
            dy = minY;
        }
        // draw part 1 of the line (ax,bx)->(dx,dy)
        if (length > 3) {
            canvas.drawLine(ax + space * xStep, ay + space * yStep, dx, dy, paint);
        }
        // draw part 2 of the line (dx,dy)->(bx,by)
        if (length > 4) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(dx, dy, bendRad, paint);
            paint.setStyle(Paint.Style.STROKE);
            // draw line right
            canvas.drawLine(dx, dy, bx - space * xStep, by - space * yStep, paint);
        }
    }

    void drawConnector(Canvas canvas, Board board, int at, PlacedConnector conn) {
        // draw all connected wires as "connected"
        List<Integer> connNodes = board.connectedNodes(at, conn);
        drawConnectedWires(canvas, at, connNodes, conn_wire_size);
        // get connector icon
        Bitmap bmp = ConnectorBitmap.freeBitmap(conn.type);
        assert bmp != null;
        // draw connector bitmap
        drawBitmapAtNode(canvas, at, bmp);
    }

    void drawConnectedWires(Canvas canvas, int at, List<Integer> toNodes, int size) {
        for (Integer n : toNodes) {
            drawWire(canvas, at, n, connectedPaint(), size);
        }
    }

    void drawSpecial(Canvas canvas, Board board, int at, Bitmap bmp) {
        // we expected defined conn for special fields if they are connected
        drawBitmapAtNode(canvas, at, bmp);
    }

    void drawBitmapAtNode(Canvas canvas, int at, Bitmap bmp) {
        float cx = canvasX(IntCoord.x(at));
        float cy = canvasY(IntCoord.y(at));
        float size = this.cellSize / 2.7f;
        RectF targetRect = new RectF(cx - size / 2, cy - size / 2, cx + size / 2, cy + size / 2);
        Rect rectSrc = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        canvas.drawBitmap(bmp, rectSrc, targetRect, bmpPaint);
    }

    private Paint connectedPaint() {
        if (connectionPaint == null) {
            int width = (int) wireWidth;
            int color = connectionColor;
            connectionPaint = strokePaint(width, color);
        }
        return connectionPaint;
    }

    /**
     * Returns diagonal shape between 2 nodes, has to be deterministic
     * -1
     * 0 : normal diagonal
     * 1 :
     */
    private Integer getDiagonalWireShape(int node0, int node1) {
        int minNode = java.lang.Math.min(node0, node1);
        int maxNode = java.lang.Math.max(node0, node1);
        int key = 100 * minNode + maxNode;
        Integer shape = diagonalWires.get(key);
        if (shape == null) {
            shape = random.nextInt(3) - 1; // -1, 0, 1
            diagonalWires.put(key, shape);
        }
        return shape;
    }

}
