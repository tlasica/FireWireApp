package pl.tlasica.firewireapp.play;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import pl.tlasica.firewireapp.model.ConnectorType;
import pl.tlasica.firewireapp.model.LevelPlay;

public class ConnectorSetDrawing extends CanvasDrawing {

    int frameWidth;
    int frameHeight;
    float offsetX;
    float offsetY;

    Paint framePaint;
    Paint bmpPaint;
    Paint textPaint;

    Map<String, Drawable> images = new HashMap<>();

    @Override
    void prepareDrawing(Canvas canvas) {
        super.prepareDrawing(canvas);
        frameWidth = canvas.getWidth() / 6;
        frameHeight = frameWidth;
        offsetX = Math.round(frameWidth * 0.25);
        offsetY = Math.round(canvas.getHeight() - 1.25 * frameHeight);
    }

    public void draw(Canvas canvas, LevelPlay play) {
        //TODO: draw all available connectors
        prepareDrawing(canvas);
        int index = 0;
        for(Map.Entry<ConnectorType, Integer> e: play.availableConnectors.entrySet()) {
            ConnectorType type = e.getKey();
            int count = e.getValue();
            drawConnector(canvas, index, type, count);
            index ++;
        }
    }

    public void drawConnector(Canvas canvas, int index, ConnectorType type, int count) {
        drawFrame(canvas, index);
        drawConnector(canvas, index, type);
        drawCount(canvas, index, count);
    }

    void drawFrame(Canvas canvas, int index) {
        canvas.drawRoundRect(frameRect(index), 3, 3, framePaint());
    }

    void drawCount(Canvas canvas, int index, int count) {
        RectF rect = textRect(index);
        canvas.drawText(String.valueOf(count), rect.left, rect.bottom, textPaint());
    }

    void drawConnector(Canvas canvas, int index, ConnectorType type) {
        Bitmap bmp = ConnectorBitmap.get(type);
        if (bmp == null) {
            Log.e("", "No bitmap for" + type);
            return;
        }
        Rect rectSrc = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        canvas.drawBitmap(bmp, rectSrc, bitmapRect(index), bmpPaint);
    }

    float left(int index) {
        return offsetX + index * (frameWidth + offsetX);
    }

    RectF bitmapRect(int index) {
        float l = left(index) + 10;
        float t = (float) (offsetY + 0.25 * frameHeight);
        float r = (float) (left(index) + 0.75 * frameWidth);
        float b = offsetY + frameHeight - 10;
        return new RectF(l, t, r, b);
    }

    RectF textRect(int index) {
        float l = (float) (left(index) + 0.75 * frameWidth);
        float r = left(index) + frameWidth - 5;
        float t = (float) (offsetY + 5 );
        float b = (float) (offsetY + 0.25 * frameHeight);
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
            int size = determineMaxTextSize("0", rect.right - rect.left);
            int color = Color.argb(255, 219, 219, 219);
            textPaint = new Paint();
            textPaint.setColor(color);
            textPaint.setTextSize(size);
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
        return size-2;
    }

}
