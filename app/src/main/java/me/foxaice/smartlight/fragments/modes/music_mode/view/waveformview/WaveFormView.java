package me.foxaice.smartlight.fragments.modes.music_mode.view.waveformview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;

public class WaveFormView extends AppCompatImageView {
    private static final int STROKE_WIDTH_DP = 2;
    private static final int PADDING_DP = 16;
    private Paint mStrokePaint;
    private Paint mFillPaint;
    private Paint mWaveFormPaint;
    private int mStrokeWidthPx;
    private float mLeftCanvasCoord;
    private float mTopCanvasCoord;
    private float mBottomCanvasCoord;
    private float mRightCanvasCoord;
    private float mDensity;
    private RectF mRectF;
    private int mMax = Short.MIN_VALUE;
    @IMusicInfo.ViewType
    private int mViewType;
    private double[] mAudioBuffer;

    public WaveFormView(Context context) {
        super(context);
        init();
    }

    public WaveFormView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveFormView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDensity = getResources().getDisplayMetrics().density;
        mStrokeWidthPx = (int) (mDensity * STROKE_WIDTH_DP);
        mStrokePaint = new Paint();
        mStrokePaint.setColor(Color.BLACK);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mStrokeWidthPx);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        mWaveFormPaint = new Paint();
        mWaveFormPaint.setStyle(Paint.Style.STROKE);
        mWaveFormPaint.setStrokeWidth(mStrokeWidthPx);
        mFillPaint = new Paint();
        mFillPaint.setColor(ContextCompat.getColor(getContext(), R.color.toolbar));
        mFillPaint.setStyle(Paint.Style.FILL);
        mRectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mLeftCanvasCoord = PADDING_DP * mDensity;
        mTopCanvasCoord = 0;
        mRightCanvasCoord = width - PADDING_DP * mDensity;
        mBottomCanvasCoord = height - PADDING_DP * mDensity;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRectF.set(mLeftCanvasCoord, mTopCanvasCoord, mRightCanvasCoord, mBottomCanvasCoord);
        canvas.drawRect(mRectF, mFillPaint);
        float halfStrokeWidth = mStrokeWidthPx / 2f;
        mRectF.set(mLeftCanvasCoord + halfStrokeWidth, mTopCanvasCoord + halfStrokeWidth, mRightCanvasCoord - halfStrokeWidth, mBottomCanvasCoord - halfStrokeWidth);
        canvas.drawRect(mRectF, mStrokePaint);
        if (mAudioBuffer != null) {
            float[] points = getPoints();
            if (mViewType == IMusicInfo.ViewType.WAVEFORM) {
                canvas.drawPoints(points, mWaveFormPaint);
            } else if (mViewType == IMusicInfo.ViewType.FREQUENCIES) {
                canvas.drawLines(points, mWaveFormPaint);
            }
            mAudioBuffer = null;
        }
    }

    public void setAudioBuffer(double[] audioBuffer) {
        mAudioBuffer = audioBuffer;
        postInvalidate();
    }

    public void setAudioBufferColor(String color) {
        mWaveFormPaint.setColor(Color.parseColor(color));
    }

    public void setMax(double max) {
        mMax = (int) max;
    }

    public void setViewType(@IMusicInfo.ViewType int type) {
        mViewType = type;
    }

    private float[] getPoints() {
        float[] points = new float[mViewType == IMusicInfo.ViewType.WAVEFORM
                ? mAudioBuffer.length * 2
                : mAudioBuffer.length * 4];
        float height = mMax;
        float xPercent = (mRightCanvasCoord - mLeftCanvasCoord - mStrokeWidthPx * 2) * 1.0f / mAudioBuffer.length;
        float yPercent = (mBottomCanvasCoord - mStrokeWidthPx * 2) / height;
        for (int i = 0; i < mAudioBuffer.length; i++) {
            float temp = (float) (mAudioBuffer[i] * yPercent);
            if (mViewType == IMusicInfo.ViewType.WAVEFORM) {
                if (Math.abs(temp) > mBottomCanvasCoord / 2 - mStrokeWidthPx) {
                    temp = temp < 0
                            ? -(mBottomCanvasCoord / 2 - mStrokeWidthPx)
                            : (mBottomCanvasCoord / 2 - mStrokeWidthPx);
                }
                points[2 * i] = mLeftCanvasCoord + mStrokeWidthPx + i * xPercent;
                points[2 * i + 1] = mBottomCanvasCoord / 2 - temp;
            } else if (mViewType == IMusicInfo.ViewType.FREQUENCIES) {
                if (temp > mBottomCanvasCoord - 2 * mStrokeWidthPx) {
                    temp = mBottomCanvasCoord - 2 * mStrokeWidthPx;
                }
                points[4 * i] = mLeftCanvasCoord + mStrokeWidthPx + i * xPercent;
                points[4 * i + 1] = mBottomCanvasCoord - mStrokeWidthPx;
                points[4 * i + 2] = mLeftCanvasCoord + mStrokeWidthPx + i * xPercent;
                points[4 * i + 3] = mBottomCanvasCoord - mStrokeWidthPx - temp;
            }
        }
        return points;
    }
}
