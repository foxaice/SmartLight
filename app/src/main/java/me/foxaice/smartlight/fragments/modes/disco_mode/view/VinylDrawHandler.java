package me.foxaice.smartlight.fragments.modes.disco_mode.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;

class VinylDrawHandler extends Handler {
    static final int START_DRAWING = 0x0001;
    static final int STOP_DRAWING = 0x0003;
    private static final int KEEP_DRAWING = 0x0002;
    private static final String RED = "#F44336";
    private static final String PINK = "#E91E63";
    private static final String PURPLE = "#9C27B0";
    private static final String INDIGO = "#3F51B5";
    private static final String TEAL = "#009688";
    private static final String LIGHT_GREEN = "#8BC34A";
    private static final String YELLOW = "#FFEB3B";
    private static final String ORANGE = "#FF9800";
    private static final String BROWN = "#795548";
    private final DiscoModeFragment fragment;
    private final int[] colors;
    private int curColor = Color.parseColor(RED);
    private int curColorIndex = 0;
    private int prevColor = Color.TRANSPARENT;
    private int alpha;
    private int height;
    private int width;
    private float percent;

    VinylDrawHandler(DiscoModeFragment fragment) {
        this.fragment = fragment;
        colors = new int[]{
                Color.parseColor(RED),
                Color.parseColor(PINK),
                Color.parseColor(PURPLE),
                Color.parseColor(INDIGO),
                Color.parseColor(TEAL),
                Color.parseColor(LIGHT_GREEN),
                Color.parseColor(YELLOW),
                Color.parseColor(ORANGE),
                Color.parseColor(BROWN)
        };
    }

    private void setBackground(View view, Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(fragment.getResources(), bitmap);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == KEEP_DRAWING) {
            alpha = alpha + 10;
            if (alpha >= 254) alpha = 254;
            setBackground(fragment.mVinylImage, drawBackgroundVinyl(width, height, alpha, curColor, prevColor));
            if (alpha == 254) {
                this.sendEmptyMessage(STOP_DRAWING);
            } else {
                this.sendEmptyMessageDelayed(KEEP_DRAWING, 10);
            }
        } else if (msg.what == START_DRAWING) {
            this.removeMessages(KEEP_DRAWING);
            height = fragment.mVinylImage.getMeasuredHeight();
            width = fragment.mVinylImage.getMeasuredWidth();
            int minSide = height > width ? height : width;
            height = minSide;
            width = minSide;
            percent = minSide / 100f;
            alpha = 0;
            changeColors();
            this.sendEmptyMessageDelayed(KEEP_DRAWING, 10);
        } else if (msg.what == STOP_DRAWING) {
            this.removeMessages(KEEP_DRAWING);
            alpha = 255;
            setBackground(fragment.mVinylImage, drawBackgroundVinyl(width, height, alpha, curColor, prevColor));
        }
    }

    private void changeColors() {
        prevColor = curColor;
        curColorIndex += 1;
        if (curColorIndex >= colors.length) {
            curColorIndex = 0;
        }
        curColor = nextColors(curColorIndex);
    }

    private int nextColors(int index) {
        return colors[index];
    }

    private Bitmap drawBackgroundVinyl(int width, int height, int alpha, int curColor, int prevColor) {
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        paint.setStrokeWidth(percent * 14f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(255);
        paint.setColor(prevColor);
        canvas.drawCircle(height / 2f, width / 2f, percent * 11.5f, paint);
        paint.setColor(curColor);
        paint.setAlpha(alpha);
        canvas.drawCircle(height / 2f, width / 2f, percent * 11.5f, paint);
        return bm;
    }
}
