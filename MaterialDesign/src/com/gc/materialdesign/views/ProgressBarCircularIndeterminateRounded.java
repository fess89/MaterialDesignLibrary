package com.gc.materialdesign.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.gc.materialdesign.utils.Utils;

public class ProgressBarCircularIndeterminateRounded extends CustomView {

    private static final String ANDROIDXML = "http://schemas.android.com/apk/res/android";

    private static final int DEFAULT_COLOR = Color.parseColor("#1E88E5");

    private int backgroundColor = DEFAULT_COLOR;

    private Paint arcPaint;

    private final Path arcPath = new Path();

    private final int thicknessDp = 10;

    private final int thicknessPx;

    private final int increment = 2;

    private final int maxArcAngle = 180;

    private final int minArcAngle = 15;

    private final Paint transparentPaint;

    int arcWidth = 1;

    int startAngle = 0;

    float rotateAngle = 0;

    int limit = 0;

    public ProgressBarCircularIndeterminateRounded(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);

        transparentPaint = new Paint();
        transparentPaint.setAntiAlias(true);
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        thicknessPx = Utils.dpToPx(thicknessDp, context.getResources());
    }

    // Set atributtes of XML to View
    protected void setAttributes(AttributeSet attrs) {

        //Set background Color
        // Color by resource
        int backgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
        if (backgroundColor != -1) {
            setBackgroundColor(getResources().getColor(backgroundColor));
        } else {
            // Color by hexadecimal
            int background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1) {
                setBackgroundColor(background);
            }
            else {
                setBackgroundColor(DEFAULT_COLOR);
            }
        }

        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setColor(this.backgroundColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSecondAnimation(canvas);
        invalidate();
    }

    /**
     * Draw second animation of view
     *
     * @param canvas - the canvas to draw on.
     */
    private void drawSecondAnimation(Canvas canvas) {
        rotateCanvas(canvas);

        Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = drawOuterCircle(bitmap);
        drawTransparentCircle(temp);

        canvas.drawBitmap(bitmap, 0, 0, new Paint());
    }

    private Canvas drawOuterCircle(Bitmap bitmap) {
        Canvas temp = new Canvas(bitmap);
        drawArc(temp, startAngle, arcWidth, arcPaint);
        return temp;
    }

    private void rotateCanvas(Canvas canvas) {
        if (startAngle == limit) {
            arcWidth += increment;
        }
        if ( (arcWidth >= maxArcAngle) || (startAngle > limit) ) {
            startAngle += increment;
            arcWidth -= increment;
        }
        if (startAngle > limit + maxArcAngle - minArcAngle) {
            limit = startAngle;
            startAngle = limit;
            arcWidth = minArcAngle;
        }
        rotateAngle += increment;
        canvas.rotate(rotateAngle, getWidth() / 2, getHeight() / 2);
    }

    private void drawTransparentCircle(Canvas temp) {
        temp.drawCircle(getWidth()/2, getHeight()/2,
                (getWidth()/2) - Utils.dpToPx(thicknessDp, getResources()),
                transparentPaint);
    }

    private void drawArc(Canvas canvas, float startAngle, float sweepDegrees, Paint arcPaint) {

        if (sweepDegrees <= 0) {
            return;
        }

        arcPath.reset();

        int width = getWidth();
        int height = getHeight();

        int halfWidth = width/2;
        int halfHeight = height/2;

        int radius = halfWidth - thicknessPx / 2;

        arcPath.arcTo(new RectF(0, 0, width, height), startAngle, sweepDegrees);
        arcPath.arcTo(new RectF(thicknessPx, thicknessPx, width - thicknessPx, height - thicknessPx),
                startAngle + sweepDegrees, -sweepDegrees);

        Point startPoint = calculatePointOnArc(halfWidth, halfHeight, radius, startAngle);
        arcPath.addCircle(startPoint.x, startPoint.y, thicknessPx / 2, Path.Direction.CW);

        Point endPoint = calculatePointOnArc(halfWidth, halfHeight, radius, startAngle + sweepDegrees);
        arcPath.addCircle(endPoint.x, endPoint.y, thicknessPx / 2, Path.Direction.CW);

        arcPath.close();
        canvas.drawPath(arcPath, arcPaint);
    }

    // this is to calculate the end points of the arc
    private Point calculatePointOnArc(int centerX, int centerY, int circleRadius, float endAngle)
    {
        double endAngleRadian = endAngle * (Math.PI / 180);

        int x = (int) Math.round((centerX + circleRadius * Math.cos(endAngleRadian)));
        int y = (int) Math.round((centerY + circleRadius * Math.sin(endAngleRadian)));

        return new Point(x, y);
    }

    // Set color of background
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        if (isEnabled()) {
            beforeBackground = backgroundColor;
        }
        this.backgroundColor = color;
    }
}
