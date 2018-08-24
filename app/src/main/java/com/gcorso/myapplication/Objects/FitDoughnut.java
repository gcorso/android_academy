package com.gcorso.myapplication.Objects;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

import com.gcorso.myapplication.R;

// from https://github.com/tehmantra/FitDoughnut

public class FitDoughnut extends ViewGroup {

    private static final long ANIM_DURATION_DEFAULT = 500;
    private static final long ANIM_DURATION_LONG = 2000;


    private FitDoughnutView fitDoughnutView;

    private Paint paintPrimary;
    private Paint paintSecondary;
    private Paint paintTextPrimary;
    private Paint paintTextSecondary;

    private final RectF oval = new RectF();
    private float width;

    private float textSizePrimary;
    private float textSizeSecondary;

    private int colorPrimary;
    private int colorSecondary;
    private int colorTextPrimary;
    private int colorTextSecondary;

    private ObjectAnimator headAnimator;
    private ObjectAnimator tailAnimator;

    // Percent
    // stored internally as float between 0..360 (degrees)
    // exposed through setter and getter as 0..100 (percent)
    // overflow with modulo allows for continuous looping animations
    private float percentDeg;
    public float getPercent() { return (percentDeg/ 360.f) * 100.f; }
    public void setPercent(float percent) { percentDeg = ((percent % 100)/ 100.f) * 360.f; }
    private final Property<FitDoughnut, Float> percentProperty = new Property<FitDoughnut, Float>(Float.class, "Percent") {
        @Override public Float get(FitDoughnut fd) { return fd.getPercent(); }
        @Override public void set(FitDoughnut fd, Float value) { fd.setPercent(value); }
    };


    // OriginAngle
    // used for animations
    // range 0..360
    // overflow loops with modulo
    // offset by 270deg i.e. 0deg is 12 o'clock, 90deg is 3 o'clock
    private float originAngle = 0;
    private float getOriginAngle() { return (originAngle + 270) % 360; }
    private void setOriginAngle(Float value) { originAngle = (value % 360); }
    private final Property<FitDoughnut, Float> originAngleProperty = new Property<FitDoughnut, Float>(Float.class, "OriginAngle") {
        @Override public Float get(FitDoughnut fd) { return fd.getOriginAngle(); }
        @Override public void set(FitDoughnut fd, Float value) { fd.setOriginAngle(value); }
    };


    // invalidate the view so it is redrawn when animator changes a property
    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override public void onAnimationUpdate(ValueAnimator valueAnimator) { fitDoughnutView.invalidate(); }
    };



    public FitDoughnut(Context ctx) {
        super(ctx);
        init();
    }

    public FitDoughnut(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);

        TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.FitDoughnut, 0, 0);

        // attempt to get any values from the xml
        try {
            colorPrimary = a.getColor(R.styleable.FitDoughnut_fdColorPrimary, Color.rgb(225, 140, 80));
            colorSecondary = a.getColor(R.styleable.FitDoughnut_fdColorSecondary, Color.rgb(200,200,200));
        } finally {
            a.recycle();
        }

        init();
    }



    private void init() {

        fitDoughnutView = new FitDoughnutView(getContext());
        addView(fitDoughnutView);

        textSizePrimary = 10.f;
        textSizeSecondary = 10.f;

        colorTextPrimary = Color.BLACK;
        colorTextSecondary = Color.BLACK;

        // setup animator
        headAnimator = new ObjectAnimator();
        headAnimator.setTarget(this);
        headAnimator.setProperty(percentProperty);
        headAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        headAnimator.addUpdateListener(animatorUpdateListener);

        // tail animator
        // animates the 'end' of the ring.. for loading anim
        tailAnimator = new ObjectAnimator();
        tailAnimator.setTarget(this);
        tailAnimator.setProperty(originAngleProperty);
        tailAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        tailAnimator.addUpdateListener(animatorUpdateListener);
        tailAnimator.setFloatValues(0, 1080);
        tailAnimator.setDuration(FitDoughnut.ANIM_DURATION_LONG);
        tailAnimator.setRepeatCount(Animation.INFINITE);
        tailAnimator.setStartDelay(100);

        // setup paint
        paintPrimary = new Paint();
        paintPrimary.setAntiAlias(true);
        paintPrimary.setColor(colorPrimary);
        paintPrimary.setStyle(Paint.Style.STROKE);
        paintPrimary.setStrokeCap(Paint.Cap.ROUND);

        paintSecondary = new Paint();
        paintSecondary.setAntiAlias(true);
        paintSecondary.setColor(colorSecondary);
        paintSecondary.setStyle(Paint.Style.STROKE);

        paintTextPrimary = new TextPaint();
        paintTextPrimary.setAntiAlias(true);
        paintTextPrimary.setColor(colorTextPrimary);
        paintTextPrimary.setStyle(Paint.Style.STROKE);

        paintTextSecondary = new TextPaint();
        paintTextSecondary.setAntiAlias(true);
        paintTextSecondary.setColor(colorTextSecondary);
        paintTextSecondary.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {
        // do nothing
        // don't call super.onLayout() -- this would cause a layout pass on the children
        // children will lay out in onSizeChanged()
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // account for padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        // figure out how big the doughnut can be
        float ww = (float) w - xpad;
        float hh = (float) h - ypad;
        float diameter = Math.min(ww, hh);

        oval.set(0.f, 0.f, diameter, diameter);
        oval.offsetTo(getPaddingLeft(), getPaddingTop());

        // set stroke width..
        width = diameter / 15.f;
        paintPrimary.setStrokeWidth(width);
        paintSecondary.setStrokeWidth(width);

        // lay out the child view that actually draws the doughnut
        fitDoughnutView.layout((int) oval.left, (int) oval.top, (int) oval.right, (int) oval.bottom);
    }


    //region Animations

    private void animateRing(float from, float to, long duration) {
        headAnimator.setFloatValues(from, to);
        headAnimator.setDuration(duration);
        headAnimator.start();
    }



    public void animateSetPercent(float percent) {
        float old = getPercent();
        setPercent(percent);
        animateRing(old, getPercent(), FitDoughnut.ANIM_DURATION_DEFAULT);

    }

    /**
     * gracefully stop the "indeterminate loading" animation and reset to the initial point
     */
    public void stopAnimateLoading() {
        stopAnimateLoading(.1f);
    }

    /**
     * gracefully stop the "indeterminate loading" animation and animate to a new {@code percent}
     * @param percent the new percent to animate to once current animation has finished
     */
    public void stopAnimateLoading(final float percent) {
        if (headAnimator.isRunning() && tailAnimator.isRunning()) {
            // remove any listeners we may have previously attached
            headAnimator.removeAllListeners();
            tailAnimator.removeAllListeners();

            // gracefully let the animation finish it's current cycle, then kill it and remove this listener
            tailAnimator.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) {}
                @Override public void onAnimationEnd(Animator animation) {}
                @Override public void onAnimationCancel(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {
                    animation.end();
                    animation.removeListener(this);
                    setOriginAngle(0.f);
                }
            });

            headAnimator.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) {}
                @Override public void onAnimationEnd(Animator animation) {}
                @Override public void onAnimationCancel(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {
                    animation.end();
                    animation.removeListener(this);
                    // reset the head animator values we changed when starting the loading animation
                    headAnimator.setRepeatCount(0);
                    headAnimator.setDuration(FitDoughnut.ANIM_DURATION_DEFAULT);

                    animateSetPercent(percent);
                }
            });

        }
    }

    /**
     * start the "indeterminate loading" animation
     */
    public void startAnimateLoading() {
        headAnimator.setFloatValues(.1f, 66.f, .1f);
        headAnimator.setDuration(FitDoughnut.ANIM_DURATION_LONG);
        headAnimator.setRepeatCount(Animation.INFINITE);

        headAnimator.start();
        tailAnimator.start();
    }



    //endregion


    //region FitDoughnutView

    /**
     * FitDoughnutView class
     */
    class FitDoughnutView extends View {

        private final RectF fdvOval = new RectF();

        public FitDoughnutView(Context ctx) {
            super(ctx);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            //canvas.drawText("PAINT TEXT PRIMARY", oval.left, oval.top, paintTextPrimary);

            // draw the background doughnut
            canvas.drawArc(fdvOval, 0, 360, false, paintSecondary);

            // draw the main ring on top
            canvas.drawArc(fdvOval, getOriginAngle(), percentDeg, false, paintPrimary);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            fdvOval.set(width, width /*[SIC]*/, w - width, h - width);
        }
    }

    //endregion
}