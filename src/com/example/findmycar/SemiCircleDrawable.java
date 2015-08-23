package com.example.findmycar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class SemiCircleDrawable extends Drawable {

	private Paint paint;
	private RectF rectF;
	private int color;
	private Direction angle;

	final String LOG_TAG = "myLogs";

	public enum Direction {
		LEFT, RIGHT, TOP, BOTTOM
	}

	public SemiCircleDrawable(int c) {
		this(c, Direction.LEFT);
	}

	public SemiCircleDrawable(int color, Direction angle) {
		this.color = color;
		this.angle = angle;
		paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		rectF = new RectF();
	}

	public int getColor() {
		return color;
	}

	/**
	 * A 32bit color not a color resources.
	 * 
	 * @param color
	 */
	public void setColor(int color) {
		this.color = color;
		paint.setColor(color);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		Log.d(LOG_TAG, "1");
		Rect bounds = getBounds();

		if (angle == Direction.RIGHT || angle == Direction.LEFT) {
			canvas.scale(1, 1);

		}

		rectF.set(bounds);
		canvas.drawRoundRect(rectF, 90, 360, paint);
	}

	@Override
	public void setAlpha(int alpha) {
		// Has no effect
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}

	@Override
	public int getOpacity() {
		return 0;
	}

}
