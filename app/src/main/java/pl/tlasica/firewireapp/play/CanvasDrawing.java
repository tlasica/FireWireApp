package pl.tlasica.firewireapp.play;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class CanvasDrawing {

    float cellSize;
    float nodeRadius;
    float wireWidth;
    float cellsInRow = 6.0f;

    Paint strokePaint(int width, int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        return paint;
    }

    Paint fillPaint(int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setDither(true);
        paint.setColor(color);
        return paint;
    }

    void prepareDrawing(Canvas canvas) {
        cellSize = Math.round(canvas.getWidth() / cellsInRow);
        nodeRadius = cellSize / 7.0f;
        wireWidth = nodeRadius / 1.3f;
        String msg = String.format("prepareDrawing: cellSize=%f nodeRad=%f wireWidth=%f",
                cellSize, nodeRadius, wireWidth);
        Log.d("DIM", msg);
    }

    float canvasX(int boardX) {
        return boardX * cellSize + cellSize/2.0f;
    }

    float canvasY(int boardY) {
        return boardY * cellSize + cellSize/2.0f;
    }

}
