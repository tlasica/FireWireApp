package pl.tlasica.firewireapp.play;

import android.graphics.Canvas;
import android.graphics.Paint;

public class CanvasDrawing {

    int cellSize;
    int nodeRadius;

    Paint strokePaint(int width, int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        return paint;
    }

    void prepareDrawing(Canvas canvas) {
        cellSize = Math.round(canvas.getWidth() / 7);
        nodeRadius = cellSize / 9;
    }

    int canvasX(int boardX) {
        return boardX * cellSize + cellSize/2;
    }

    int canvasY(int boardY) {
        return boardY * cellSize + cellSize/2;
    }

}
