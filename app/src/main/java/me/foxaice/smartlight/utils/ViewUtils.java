package me.foxaice.smartlight.utils;

import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public final class ViewUtils {
    private ViewUtils() {}

    public static void setEnabled(boolean isEnable, View... views) {
        for (View item : views) {
            item.setEnabled(isEnable);
        }
    }

    public static void bringToFront(View... views) {
        for (View item : views) {
            item.bringToFront();
        }
    }

    public static void setImageDrawable(View view, @DrawableRes int id) {
        ((ImageView) view).setImageDrawable(ContextCompat.getDrawable(view.getContext(), id));
    }

    public static void setVisibility(int visibility, View... views) {
        for (View item : views) {
            item.setVisibility(visibility);
        }
    }

    public static void setAlpha(float alpha, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setAlpha(alpha);
        } else {
            Animation alphaAnim = new AlphaAnimation(alpha, alpha);
            alphaAnim.setDuration(0);
            alphaAnim.setFillAfter(true);
            view.startAnimation(alphaAnim);
        }
    }

    public static float getX(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return view.getX();
        } else {
            return view.getLeft();
        }
    }

    public static float getY(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return view.getY();
        } else {
            return view.getTop();
        }
    }

    public static float getPivotX(View view) {
        return view.getWidth() / 2.f;
    }

    public static float getPivotY(View view) {
        return view.getHeight() / 2.f;
    }
}
