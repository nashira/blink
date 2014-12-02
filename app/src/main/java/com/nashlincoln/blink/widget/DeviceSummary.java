package com.nashlincoln.blink.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;

import com.nashlincoln.blink.R;

/**
 * Created by nash on 11/30/14.
 */
public class DeviceSummary extends TextView {
    private Paint mBackgroundPaint;
    private Paint mLevelPaint;
    private int mOnColor;
    private int mOffColor;
    private RectF mArcRect;
    private boolean isOn;
    private float level;

    public DeviceSummary(Context context) {
        super(context);
        init();
    }

    public DeviceSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DeviceSummary(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DeviceSummary(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        int textColor = getResources().getColor(R.color.white);
        int levelColor = getResources().getColor(R.color.accent);
        float strokeWidth = getResources().getDimension(R.dimen.device_summary_stroke);

        mOnColor = getResources().getColor(R.color.primary);
        mOffColor = getResources().getColor(R.color.gray_400);
        mArcRect = new RectF(0, 0, 0, 0);
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mLevelPaint = new Paint();
        mLevelPaint.setAntiAlias(true);
        mLevelPaint.setColor(levelColor);
        mLevelPaint.setStrokeWidth(strokeWidth);
        mLevelPaint.setStyle(Paint.Style.STROKE);
        setTextColor(textColor);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float half = mLevelPaint.getStrokeWidth() / 2;
        mArcRect.top = half;
        mArcRect.left = half;
        mArcRect.bottom = getHeight() - half;
        mArcRect.right = getWidth() - half;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        mBackgroundPaint.setColor(isOn ? mOnColor : mOffColor);
        canvas.drawOval(mArcRect, mBackgroundPaint);
        canvas.drawArc(mArcRect, 270, -360 * level, false, mLevelPaint);
        super.onDraw(canvas);
    }

    public void setOn(boolean isOn) {
        this.isOn = isOn;
        invalidate();
    }

    public void setLevel(float level) {
        this.level = level;
        invalidate();
    }
}
