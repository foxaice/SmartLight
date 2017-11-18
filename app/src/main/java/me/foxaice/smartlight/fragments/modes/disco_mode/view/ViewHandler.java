package me.foxaice.smartlight.fragments.modes.disco_mode.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringDef;
import android.view.View;

import java.lang.ref.WeakReference;

class ViewHandler extends Handler {
    private static final int START_DRAWING = 0x0001;
    private static final int KEEP_DRAWING = 0x0002;
    private static final int STOP_DRAWING = 0x0003;
    private final WeakReference<DiscoModeFragment> wrFragment;
    private final int[] mColors;
    private int mCurColor = Color.parseColor(Colors.RED);
    private int mCurColorIndex = 0;
    private int mPrevColor = Color.TRANSPARENT;
    private int mAlpha;
    private int mHeight;
    private int mWidth;
    private float mPercent;

    ViewHandler(DiscoModeFragment fragment) {
        wrFragment = new WeakReference<>(fragment);
        mColors = new int[]{
                Color.parseColor(Colors.RED),
                Color.parseColor(Colors.PINK),
                Color.parseColor(Colors.PURPLE),
                Color.parseColor(Colors.INDIGO),
                Color.parseColor(Colors.TEAL),
                Color.parseColor(Colors.LIGHT_GREEN),
                Color.parseColor(Colors.YELLOW),
                Color.parseColor(Colors.ORANGE),
                Color.parseColor(Colors.BROWN)
        };
    }

    @Override
    public void handleMessage(Message msg) {
        DiscoModeFragment fragment = wrFragment.get();
        if (fragment != null) {
            if (msg.what == KEEP_DRAWING) {
                mAlpha = mAlpha + 10;
                if (mAlpha >= 254) mAlpha = 254;
                ViewHandler.setBackground(fragment.mVinylImage, drawBackgroundVinyl(mWidth, mHeight, mAlpha, mCurColor, mPrevColor));
                if (mAlpha == 254) {
                    this.sendEmptyMessage(STOP_DRAWING);
                } else {
                    this.sendEmptyMessageDelayed(KEEP_DRAWING, 10);
                }
            } else if (msg.what == START_DRAWING) {
                this.removeMessages(KEEP_DRAWING);
                mHeight = fragment.mVinylImage.getMeasuredHeight();
                mWidth = fragment.mVinylImage.getMeasuredWidth();
                int minSide = mHeight > mWidth ? mHeight : mWidth;
                mHeight = minSide;
                mWidth = minSide;
                mPercent = minSide / 100f;
                mAlpha = 0;
                changeColors();
                this.sendEmptyMessageDelayed(KEEP_DRAWING, 10);
            } else if (msg.what == STOP_DRAWING) {
                this.removeMessages(KEEP_DRAWING);
                mAlpha = 255;
                ViewHandler.setBackground(fragment.mVinylImage, drawBackgroundVinyl(mWidth, mHeight, mAlpha, mCurColor, mPrevColor));
            }
        }
    }

    void sendStartDrawingMessage() {
        sendEmptyMessage(START_DRAWING);
    }

    void sendStopDrawingMessage() {
        sendEmptyMessage(STOP_DRAWING);
    }

    void removeAllCallbacksAndMessages() {
        removeCallbacksAndMessages(null);
    }

    private void changeColors() {
        mPrevColor = mCurColor;
        mCurColorIndex += 1;
        if (mCurColorIndex >= mColors.length) {
            mCurColorIndex = 0;
        }
        mCurColor = nextColors(mCurColorIndex);
    }

    private int nextColors(int index) {
        return mColors[index];
    }

    private Bitmap drawBackgroundVinyl(int width, int height, int alpha, int curColor, int prevColor) {
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        paint.setStrokeWidth(mPercent * 14f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(255);
        paint.setColor(prevColor);
        canvas.drawCircle(height / 2f, width / 2f, mPercent * 11.5f, paint);
        paint.setColor(curColor);
        paint.setAlpha(alpha);
        canvas.drawCircle(height / 2f, width / 2f, mPercent * 11.5f, paint);
        return bm;
    }

    private static void setBackground(View view, Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(view.getResources(), bitmap);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    @StringDef({
            Colors.RED, Colors.PINK,
            Colors.PURPLE, Colors.INDIGO,
            Colors.TEAL, Colors.LIGHT_GREEN,
            Colors.YELLOW, Colors.ORANGE,
            Colors.BROWN})
    private @interface Colors {
        String RED = "#F44336";
        String PINK = "#E91E63";
        String PURPLE = "#9C27B0";
        String INDIGO = "#3F51B5";
        String TEAL = "#009688";
        String LIGHT_GREEN = "#8BC34A";
        String YELLOW = "#FFEB3B";
        String ORANGE = "#FF9800";
        String BROWN = "#795548";
    }
}
