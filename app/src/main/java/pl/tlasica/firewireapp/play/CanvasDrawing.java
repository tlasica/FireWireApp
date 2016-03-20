package pl.tlasica.firewireapp.play;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class CanvasDrawing {

    float cellSize;
    float nodeRadius;
    float wireWidth;

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
        cellSize = Math.round(canvas.getWidth() / 7);
        nodeRadius = cellSize / 7.0f;
        wireWidth = nodeRadius / 1.3f;
        Log.d("DIM", "prepareDrawing: cellSize=" + String.valueOf(cellSize));
        Log.d("DIM", "prepareDrawing: nodeRadius=" + String.valueOf(nodeRadius));
        Log.d("DIM", "prepareDrawing: wireWidth=" + String.valueOf(wireWidth));
    }

    float canvasX(int boardX) {
        return boardX * cellSize + cellSize/2.0f;
    }

    float canvasY(int boardY) {
        return boardY * cellSize + cellSize/2.0f;
    }

}
