package pl.tlasica.firewire.play;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Map;

import pl.tlasica.firewire.model.ConnectorType;
import pl.tlasica.firewire.model.LevelPlay;

public class ConnectorSetDrawing extends CanvasDrawing {

    int frameWidth;
    int frameHeight;
    float offsetX;
    float offsetY;

    final int countColor = Color.parseColor("#FFA500");
    final int frameColor = Color.argb(255, 219, 219, 219);

    Paint framePaint = strokePaint(3, frameColor);
    Paint bmpPaint = new Paint();
    Paint textPaint;    // will be calculated later

    @Override
    void prepareDrawing(Canvas canvas) {
        super.prepareDrawing(canvas);
        frameWidth = canvas.getWidth() / 6;
        frameHeight = frameWidth;
        offsetX = Math.round(frameWidth * 0.25);
        offsetY = Math.round(canvas.getHeight() - 1.25 * frameHeight);
    }

    public void draw(Canvas canvas, LevelPlay play) {
        prepareDrawing(canvas);
        int index = 0;
        for(Map.Entry<ConnectorType, Integer> e: play.availableConnectors.entrySet()) {
            ConnectorType type = e.getKey();
            int count = e.getValue();
            drawConnector(canvas, index, type, count);
            index ++;
        }
    }

    public void drawConnectorAtMouse(Canvas canvas, Point mouse, ConnectorType connectorType) {
        float size = this.cellSize / 2.7f;
        RectF dst = new RectF(mouse.x-size, mouse.y-size, mouse.x+size, mouse.y+size);
        clipRect(canvas);
        this.drawConnectorInRect(canvas, dst, connectorType);
    }

    public ConnectorType connAtMouse(Point mouse, LevelPlay play) {
        int index = 0;
        for(Map.Entry<ConnectorType, Integer> e: play.availableConnectors.entrySet()) {
            ConnectorType type = e.getKey();
            RectF rect = bitmapRect(index);
            if (rect.contains(mouse.x, mouse.y)) return type;
            index++;
        }
        return null;
    }

    void drawConnector(Canvas canvas, int index, ConnectorType type, int count) {
        drawFrame(canvas, index);
        drawConnectorByIndex(canvas, index, type);
        drawCount(canvas, index, count);
    }

    void drawFrame(Canvas canvas, int index) {
        int rad = frameWidth / 8;
        canvas.drawRoundRect(frameRect(index), rad, rad, framePaint());
    }

    void drawCount(Canvas canvas, int index, int count) {
        RectF rect = textRect(index);
        Paint paint = textPaint();
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format("%d",count), rect.right, rect.bottom, paint);
    }

    void drawConnectorByIndex(Canvas canvas, int index, ConnectorType type) {
        this.drawConnectorInRect(canvas, bitmapRect(index), type);
    }

    void drawConnectorInRect(Canvas canvas, RectF dst, ConnectorType type) {
        Bitmap bmp = ConnectorBitmap.choiceBitmap(type);
        if (bmp == null) {
            Log.e("", "No bitmap for" + type);
            return;
        }
        Rect rectSrc = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        canvas.drawBitmap(bmp, rectSrc, dst, bmpPaint);
    }

    float left(int index) {
        return offsetX + index * (frameWidth + offsetX);
    }

    RectF bitmapRect(int index) {
        float l = left(index) + 10;
        float t = offsetY + 0.25f * frameHeight;
        float r = left(index) + 0.75f * frameWidth;
        float b = offsetY + frameHeight - 10;
        return new RectF(l, t, r, b);
    }

    RectF textRect(int index) {
        float l = left(index) + 0.70f * frameWidth;
        float r = left(index) + frameWidth - 10;
        float t = offsetY + 5;
        float b = offsetY + 0.33f * frameHeight;
        return new RectF(l, t, r, b);
    }

    RectF frameRect(int index) {
        return new RectF(left(index), offsetY, left(index)+frameWidth, offsetY+frameHeight);
    }

    Paint framePaint() {
        if (framePaint == null) {
            int width = 3;
            int color = Color.argb(255, 219, 219, 219);
            framePaint = strokePaint(width, color);

            bmpPaint = new Paint();
        }
        return framePaint;
    }

    Paint textPaint() {
        if (textPaint == null) {
            RectF rect = textRect(0);
            int size = determineMaxTextSize("0", 0.75f * (rect.right - rect.left));
            int color = countColor;
            textPaint = new Paint();
            textPaint.setColor(color);
            textPaint.setTextSize(size);
            textPaint.setAntiAlias(true);
            textPaint.setTextAlign(Paint.Align.RIGHT);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        }
        return textPaint;
    }

    private int determineMaxTextSize(String str, float maxWidth)
    {
        int size = 0;
        Paint paint = new Paint();
        do {
            paint.setTextSize(++ size);
        } while(paint.measureText(str) < maxWidth);

        Log.d("", "Connector count font size: " + size);
        return size-4;
    }

}
