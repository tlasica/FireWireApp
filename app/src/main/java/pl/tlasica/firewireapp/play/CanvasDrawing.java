package pl.tlasica.firewireapp.play;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
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
        nodeRadius = cellSize / 10.0f;
        wireWidth = nodeRadius / 2.2f;
    }

    float canvasX(int boardX) {
        return boardX * cellSize + cellSize/2.0f;
    }

    float canvasY(int boardY) {
        return boardY * cellSize + cellSize/2.0f;
    }

    RectF getBoardRect(Canvas canvas) {
        float margin = 10.0f;
        RectF rect = new RectF(margin, margin, canvas.getWidth()-margin, canvas.getHeight()-margin);
        return rect;
    }

    void clipRect(Canvas canvas) {
        RectF boardRect = getBoardRect(canvas);
        float safety = 6.0f;    // to not draw moving connectors on rounded edges as they are nore refreshed
        RectF safeRect = new RectF(boardRect.left+safety, boardRect.top+safety, boardRect.right-safety, boardRect.bottom-safety);
        canvas.clipRect(safeRect);
    }

}
