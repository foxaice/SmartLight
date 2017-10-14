package me.foxaice.smartlight.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

public final class AnimationUtils {
    private AnimationUtils() {
    }

    public static Animation getChangeViewGroupHeightAnimation(ViewGroup viewGroup, float addedHeight) {
        return new ChangeViewGroupHeightAnimation(viewGroup, addedHeight);
    }

    public static void setInterpolator(Interpolator interpolator, Animation... anims) {
        for (Animation item : anims) {
            item.setInterpolator(interpolator);
        }
    }

    public static void setDuration(long duration, Animation... anims) {
        for (Animation item : anims) {
            item.setDuration(duration);
        }
    }

    public static void setEnabledViews(boolean isEnable, View... views) {
        for (View item : views) {
            item.setEnabled(isEnable);
        }
    }

    public static void bringToFrontViews(View... views) {
        for (View item : views) {
            item.bringToFront();
        }
    }

    public static class AnimationListenerAdapter implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {}

        @Override
        public void onAnimationEnd(Animation animation) {}

        @Override
        public void onAnimationRepeat(Animation animation) {}
    }

    private static class ChangeViewGroupHeightAnimation extends Animation {
        private ViewGroup mViewGroup;
        private float mAddedHeight;
        private int mInitHeight;

        ChangeViewGroupHeightAnimation(ViewGroup viewGroup, float addedHeight) {
            super();
            mViewGroup = viewGroup;
            mAddedHeight = addedHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            mViewGroup.getLayoutParams().height = mInitHeight + (int) (mAddedHeight * interpolatedTime);
            mViewGroup.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mInitHeight = height;
        }
        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}
