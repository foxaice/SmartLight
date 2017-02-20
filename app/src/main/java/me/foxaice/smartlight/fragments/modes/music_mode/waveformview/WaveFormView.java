package me.foxaice.smartlight.fragments.modes.music_mode.waveformview;

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
        mFillPaint = new Paint();
        mFillPaint.setColor(ContextCompat.getColor(getContext(), R.color.toolbar));
        mFillPaint.setStyle(Paint.Style.FILL);
        mRectF = new RectF();
    }
}
