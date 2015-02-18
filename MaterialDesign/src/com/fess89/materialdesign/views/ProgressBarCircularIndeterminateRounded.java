package com.fess89.materialdesign.views;

import android.content.Context;
import android.content.res.TypedArray;
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
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;

import com.gc.materialdesign.R;

public class ProgressBarCircularIndeterminateRounded extends CustomView {

    private static final String TAG = ProgressBarCircularIndeterminateRounded.class.getSimpleName();

    private static final int DEFAULT_BACKGROUND_CIRCLE_COLOR = Color.parseColor("#1E88E5");

    private int mBackgroundCircleColor = DEFAULT_BACKGROUND_CIRCLE_COLOR;

    private static final int DEFAULT_FOREGROUND_CIRCLE_COLOR = Color.parseColor("#FF0000");

    private int mForegroundCircleColor = DEFAULT_FOREGROUND_CIRCLE_COLOR;

    private static final int DEFAULT_OUTER_CIRCLE_COLOR = Color.parseColor("#00FF00");

    private int mOuterCircleColor = DEFAULT_OUTER_CIRCLE_COLOR;

    private final Paint mBackgroundCirclePaint = new Paint();

    private final Paint mForegroundCirclePaint = new Paint();

    private final Paint mOuterCirclePaint = new Paint();

    private final Path arcPath = new Path();

    private int mainCircleThickness;

    private int outerCircleThickness;

    private int maxBackgroundCircleWidth = 270;

    private int minBackgroundCircleWidth = 0;

    private int maxForegroundCircleWidth = 180;

    private int minForegroundCircleWidth = 0;

    private boolean backgroundCircleFinished = false;

    private final Paint transparentPaint;

    private int mBackgroundCircleWidth = 0;

    private int mForegroundCircleWidth = 0;

    private int distanceBetweenCircles = 5;

    private final int startOffset = 90;

    private int width;

    private int height;

    public ProgressBarCircularIndeterminateRounded(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);

        transparentPaint = new Paint();
        transparentPaint.setAntiAlias(true);
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        BackgroundCircleAnimation backgroundCircleAnimation = new BackgroundCircleAnimation(1500);
        backgroundCircleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                backgroundCircleFinished = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ForegroundCircleAnimation foregroundCircleAnimation = new ForegroundCircleAnimation(1500);
        foregroundCircleAnimation.setStartOffset(backgroundCircleAnimation.getDuration());

        //MyRotateAnimation rotateAnimation = new MyRotateAnimation(0, 360, 1500);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(backgroundCircleAnimation);
        animationSet.addAnimation(foregroundCircleAnimation);
        //animationSet.addAnimation(rotateAnimation);

        this.setAnimation(animationSet);
        startAnimation(animationSet);
    }

    // Set attributes of XML to View
    protected void setAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomAttributes);
            try {
                maxBackgroundCircleWidth = typedArray.getInteger(R.styleable.CustomAttributes_maxArcAngle, 270);
                maxBackgroundCircleWidth %= 360;

                // TODO
                //maxForegroundCircleWidth = typedArray.getInteger(R.styleable.CustomAttributes_minArcAngle, 15);
                //maxForegroundCircleWidth %= 360;

                mainCircleThickness = typedArray.getDimensionPixelSize(R.styleable.CustomAttributes_thickness, 7);
                outerCircleThickness = typedArray.getDimensionPixelSize(R.styleable.CustomAttributes_outerThickness, 5);

                mBackgroundCircleColor = typedArray.getColor(R.styleable.CustomAttributes_arcColor, DEFAULT_BACKGROUND_CIRCLE_COLOR);
            } catch (Exception e) {
                Log.e(TAG, "Cannot load attributes");
            } finally {
                typedArray.recycle();
            }
        }

        mBackgroundCirclePaint.setStyle(Paint.Style.FILL);
        mBackgroundCirclePaint.setAntiAlias(true);
        mBackgroundCirclePaint.setColor(this.mBackgroundCircleColor);

        mForegroundCirclePaint.setStyle(Paint.Style.FILL);
        mForegroundCirclePaint.setAntiAlias(true);
        mForegroundCirclePaint.setColor(this.mForegroundCircleColor);

        mOuterCirclePaint.setStyle(Paint.Style.STROKE);
        mOuterCirclePaint.setAntiAlias(true);
        mOuterCirclePaint.setColor(this.mOuterCircleColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(bitmap);

        if (backgroundCircleFinished) {
            mBackgroundCircleWidth = maxBackgroundCircleWidth;
        }

        drawOuterCircle(temp);

        drawBackgroundCircle(temp);

        drawForegroundCircle(temp);

        drawInnerCircle(temp);

        canvas.drawBitmap(bitmap, 0, 0, new Paint());
    }

    private void drawForegroundCircle(Canvas canvas) {
        drawMainArc(canvas, mForegroundCircleWidth, mForegroundCirclePaint);
    }

    private Canvas drawBackgroundCircle(Canvas canvas) {
        drawMainArc(canvas, mBackgroundCircleWidth, mBackgroundCirclePaint);
        return canvas;
    }

    private void drawInnerCircle(Canvas canvas) {
        canvas.drawCircle(width/2, height/2, width/2 - mainCircleThickness, transparentPaint);
    }

    private void drawOuterCircle(Canvas canvas) {
        canvas.drawCircle(width/2, height/2, width/2, mOuterCirclePaint);
    }

    private void drawMainArc(Canvas canvas, float arcWidth, Paint arcPaint) {
        if (arcWidth <= 0) {
            return;
        }

        arcPath.reset();

        int halfWidth = width / 2;
        int halfHeight = height / 2;

        int radius = halfWidth - mainCircleThickness / 2;

        arcPath.arcTo(new RectF(0, 0, width, height), startOffset, arcWidth);
        arcPath.arcTo(new RectF(mainCircleThickness,
                                mainCircleThickness,
                                width - mainCircleThickness,
                                height - mainCircleThickness),
                                startOffset + arcWidth, -arcWidth);

        Point startPoint = calculatePointOnArc(halfWidth, halfHeight, radius, startOffset);
        arcPath.addCircle(startPoint.x, startPoint.y, mainCircleThickness / 2, Path.Direction.CW);

        Point endPoint = calculatePointOnArc(halfWidth, halfHeight, radius, startOffset + arcWidth);
        arcPath.addCircle(endPoint.x, endPoint.y, mainCircleThickness / 2, Path.Direction.CW);

        arcPath.close();
        canvas.drawPath(arcPath, arcPaint);
    }

    // this is to calculate the end points of the arc
    private Point calculatePointOnArc(int centerX, int centerY, int circleRadius, float endAngle) {
        double endAngleRadian = endAngle * (Math.PI / 180);

        int x = (int) Math.round((centerX + circleRadius * Math.cos(endAngleRadian)));
        int y = (int) Math.round((centerY + circleRadius * Math.sin(endAngleRadian)));

        return new Point(x, y);
    }

    // Set color of background
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        if (isEnabled()) {
            beforeBackground = mBackgroundCircleColor;
        }
        this.mBackgroundCircleColor = color;
    }

    public class BackgroundCircleAnimation extends Animation {

        public BackgroundCircleAnimation(int duration) {
            setInterpolator(new LinearInterpolator());
            setRepeatCount(0);
            setFillAfter(true);
            setDuration(duration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            mBackgroundCircleWidth = (int) (interpolatedTime * maxBackgroundCircleWidth) + minBackgroundCircleWidth;
            ProgressBarCircularIndeterminateRounded.this.invalidate();
        }
    }

    public class ForegroundCircleAnimation extends Animation {

        public ForegroundCircleAnimation(int duration) {
            setInterpolator(new AccelerateDecelerateInterpolator());
            setFillAfter(true);
            setRepeatCount(0);
            setDuration(duration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            mForegroundCircleWidth = (int) (interpolatedTime * maxForegroundCircleWidth) + minForegroundCircleWidth;
            ProgressBarCircularIndeterminateRounded.this.invalidate();
        }
    }

    public class MyRotateAnimation extends RotateAnimation {

        public MyRotateAnimation(float fromDegrees, float toDegrees, int duration) {
            super(fromDegrees, toDegrees,
                    ProgressBarCircularIndeterminateRounded.this.getWidth() / 2,
                    ProgressBarCircularIndeterminateRounded.this.getHeight() / 2);

            setDuration(duration);
            setFillAfter(true);
            setRepeatMode(Animation.RESTART);
            setRepeatCount(Animation.INFINITE);
            setInterpolator(new LinearInterpolator());
        }
    }
}
