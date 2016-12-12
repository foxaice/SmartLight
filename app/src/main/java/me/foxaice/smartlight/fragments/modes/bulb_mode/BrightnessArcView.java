package me.foxaice.smartlight.fragments.modes.bulb_mode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;

import me.foxaice.smartlight.R;

public class BrightnessArcView extends android.support.v7.widget.AppCompatImageView {

    private Bitmap mBitmapBorder;
    private Bitmap mBitmapMask;
    private Paint mPaintMask;
    private Paint mPaintColor;
    private Paint mPaintBorder;
    private RectF mArcRect;
    private float mAngle = 50;
    private int mWidth;
    private int mHeight;
    private Rect mFrameRect;

    public BrightnessArcView(Context context) {
        this(context, null);
    }

    public BrightnessArcView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mWidth = getResources().getDimensionPixelSize(R.dimen.brightness_arc_width);
        mHeight = getResources().getDimensionPixelSize(R.dimen.brightness_arc_height);
        int targetRadius = getResources().getDimensionPixelSize(R.dimen.brightness_arc_target_size);

        mBitmapBorder = getBitmapFromVectorDrawable(getContext(), R.drawable.bulb_mode_image_brightness_arc);
        mBitmapMask = getBitmapFromVectorDrawable(getContext(), R.drawable.bulb_mode_image_brightness_arc_mask);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBitmapMask = mBitmapMask.extractAlpha();
        }
        mFrameRect = new Rect(0, 0, mWidth, mHeight);

        mArcRect = new RectF(targetRadius, targetRadius, mWidth - targetRadius, mWidth - targetRadius);

        mPaintMask = new Paint();
        mPaintColor = new Paint();
        mPaintBorder = new Paint();
        mPaintMask.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaintColor.setStrokeWidth(targetRadius * 2);
        mPaintColor.setStyle(Paint.Style.STROKE);
        mPaintColor.setColor(ContextCompat.getColor(context, R.color.backgroundBrightnessArc));
        mPaintBorder.setColor(Color.BLACK);
    }

    public BrightnessArcView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int saveFlags = Canvas.ALL_SAVE_FLAG;
            canvas.saveLayer(0, 0, mWidth, mHeight, null, saveFlags);
        } else {
            canvas.saveLayer(0, 0, mWidth, mHeight, null);
        }
        canvas.drawArc(mArcRect, 130f, mAngle + 12, false, mPaintColor);
        canvas.drawBitmap(mBitmapMask, null, mFrameRect, mPaintMask);
        canvas.drawBitmap(mBitmapBorder, null, mFrameRect, null);
        canvas.restore();
    }

    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(mWidth,
                mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}