package me.foxaice.smartlight.fragments.controllers_screen.controller_management.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


public class CheckMarkProgressView extends View {
    private static final float SCALE_NORMAL = 0.6f;
    private static final int FRAMES_PER_SECONDS = 60;
    private static final int RING_DRAWING = 0x0001;
    private static final int RING_DRAWING_REVERSE = 0x0002;
    private static final int CIRCLE_DRAWING = 0x0003;
    private static final int CIRCLE_DRAWN = 0x0004;
    private int mState = 0x0000;
    private Paint mCirclePaint = new Paint();
    private Paint mCheckMarkPaint = new Paint();
    private RectF mOval = new RectF();
    private Path mPath = new Path();
    private int mSize;
    private float mPercent;
    private float mRadius;
    private float mAngle;
    private float mScale;
    private float mRotateAngle;
    private float mMoveCheckMark;
    private boolean mIsChecked;
    private boolean mIsScaling;
    private boolean mIsMaxScaled;
    private boolean mIsCheckMarkDrawing;
    private boolean mIsDone;
    private Runnable mActionAfterCheck;

    public CheckMarkProgressView(Context context) {
        super(context);
    }

    public CheckMarkProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckMarkProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startAnimation() {
        if (mState == 0x0000) {
            mState = RING_DRAWING;
            mIsChecked = false;
            mIsScaling = false;
            mIsMaxScaled = false;
            mIsDone = false;
            mIsCheckMarkDrawing = false;
            init();
            postInvalidate();
        }
    }

    public void onCheck() {
        if (!mIsChecked) {
            this.mIsChecked = true;
        }
    }

    public void setActionAfterCheck(Runnable action) {
        mActionAfterCheck = action;
    }

    private void init() {
        mAngle = 0;
        mScale = SCALE_NORMAL;
        mRotateAngle = 0;
        mRadius = mSize / 2f;
        mMoveCheckMark = 0;
        mPath.reset();
        mCirclePaint.setColor(Color.parseColor("#007e94"));
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mPercent * 6);
        mCirclePaint.setAntiAlias(true);
        mCheckMarkPaint.setColor(Color.WHITE);
        mCheckMarkPaint.setStyle(Paint.Style.STROKE);
        mCheckMarkPaint.setAntiAlias(true);
        mCheckMarkPaint.setStrokeWidth(mPercent * 3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mSize = Math.min(getMeasuredHeight(), getMeasuredWidth());
        mPercent = mSize / 100f;
        mRadius = mSize / 2f;
        mOval.set(0, 0, mSize, mSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsScaling) {
            if (!mIsMaxScaled) {
                mScale += 0.02f;
                if (mScale >= SCALE_NORMAL + 0.2f) mIsMaxScaled = true;
            } else {
                mScale -= 0.02f;
                if (mScale <= SCALE_NORMAL) {
                    mIsScaling = false;
                    mScale = SCALE_NORMAL;
                }
            }
        }
        scaleCanvas(canvas);

        switch (mState) {
            case RING_DRAWING: {
                rotateCanvas(canvas);
                ringDrawing(canvas);
                break;
            }
            case RING_DRAWING_REVERSE: {
                rotateCanvas(canvas);
                ringDrawingReverse(canvas);
                break;
            }
            case CIRCLE_DRAWING: {
                circleDrawing(canvas);
                break;
            }
            case CIRCLE_DRAWN: {
                circleDrawn(canvas);
            }
        }
        if (!mIsDone) postInvalidateDelayed(1000 / FRAMES_PER_SECONDS);
        else {
            float x = mPercent * 27.5f;
            float y = mPercent * 52.5f;
            float x1 = x + 15 * mPercent;
            float y1 = y + 15 * mPercent;
            float temp = 31.75f * mPercent;
            canvas.drawCircle(mSize / 2, mSize / 2, mRadius, mCirclePaint);
            canvas.drawLine(x, y, x1 + mPercent, y1 + mPercent, mCheckMarkPaint);
            canvas.drawLine(x1, y1, x1 + temp, y1 - temp, mCheckMarkPaint);
            if (mActionAfterCheck != null) {
                this.post(mActionAfterCheck);
                mActionAfterCheck = null;
            }
        }
    }

    private void circleDrawn(Canvas canvas) {
        canvas.drawPath(mPath, mCirclePaint);
        if (mIsCheckMarkDrawing) {
            mMoveCheckMark += mPercent * 2;
            float x = mPercent * 27.5f;
            float y = mPercent * 52.5f;
            float x1 = x + 15 * mPercent;
            float y1 = y + 15 * mPercent;
            if (mMoveCheckMark <= 15 * mPercent) {
                canvas.drawLine(x, y, x + mMoveCheckMark, y + mMoveCheckMark, mCheckMarkPaint);
            } else if (mMoveCheckMark <= 46 * mPercent) {
                float temp = mMoveCheckMark - 15 * mPercent;
                canvas.drawLine(x, y, x1 + mPercent, y1 + mPercent, mCheckMarkPaint);
                canvas.drawLine(x1, y1, x1 + temp, y1 - temp, mCheckMarkPaint);
            } else {
                float temp = 31.75f * mPercent;
                canvas.drawLine(x, y, x1 + mPercent, y1 + mPercent, mCheckMarkPaint);
                canvas.drawLine(x1, y1, x1 + temp, y1 - temp, mCheckMarkPaint);
                mIsDone = true;
                mState = 0x0000;
            }
        }
    }

    private void circleDrawing(Canvas canvas) {
        mPath.reset();
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPath.setFillType(Path.FillType.EVEN_ODD);
        mPath.addCircle(mSize / 2f, mSize / 2f, mSize / 2f, Path.Direction.CW);

        if (mRadius < 20) {
            mIsScaling = true;
            mIsCheckMarkDrawing = true;
        }

        if (mRadius > 0) {
            mPath.addCircle(mSize / 2f, mSize / 2f, mRadius -= 10, Path.Direction.CCW);
        } else {
            mState = CIRCLE_DRAWN;
        }
        circleDrawn(canvas);
    }

    private void ringDrawing(Canvas canvas) {
        mPath.arcTo(mOval, mAngle += 10, 10, true);
        canvas.drawPath(mPath, mCirclePaint);
        if (mAngle >= 360) {
            mAngle = 360;
            if (!mIsChecked) {
                mState = RING_DRAWING_REVERSE;
            } else {
                mState = CIRCLE_DRAWING;
            }
        }
    }

    private void ringDrawingReverse(Canvas canvas) {
        mPath.rewind();
        mPath.arcTo(mOval, 360 - mAngle, mAngle -= 10, true);
        canvas.drawPath(mPath, mCirclePaint);
        if (mAngle <= 0) {
            mAngle = 0;
            mState = RING_DRAWING;
        }
    }

    private void rotateCanvas(Canvas canvas) {
        canvas.rotate(mRotateAngle++, mSize / 2f, mSize / 2f);
    }

    private void scaleCanvas(Canvas canvas) {
        canvas.scale(mScale, mScale, mSize / 2f, mSize / 2f);
    }
}
