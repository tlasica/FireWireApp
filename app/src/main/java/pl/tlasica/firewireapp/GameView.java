package pl.tlasica.firewireapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Game View prints current game status using 2D shapes
 */
public class GameView extends View {

    private Paint basicPaint;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        super.onDraw(canvas);
        int x = getWidth();
        int y = getHeight();
        int radius;
        radius = x / 10;
        canvas.drawCircle(100, 100, radius, basicPaint());
    }

    // TODO: create paints when the view is created and use them later

    private Paint basicPaint() {
        if (this.basicPaint == null) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.LTGRAY);
            paint.setStrokeWidth(10);
            this.basicPaint = paint;
        }
        return this.basicPaint;
    }

    // Use Color.parseColor to define HTML colors
    //paint.setColor(Color.parseColor("#CD5C5C"));

}
