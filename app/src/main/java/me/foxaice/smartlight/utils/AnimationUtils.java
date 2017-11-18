package me.foxaice.smartlight.utils;

import android.animation.Animator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

public final class AnimationUtils {
    private AnimationUtils() {}

    public static Animation getChangeViewGroupHeightAnimation(ViewGroup viewGroup, float addedHeight) {
        return new ChangeViewGroupHeightAnimation(viewGroup, addedHeight);
    }

    public static void setInterpolator(Interpolator interpolator, Animation... anims) {
        for (Animation item : anims) {
            item.setInterpolator(interpolator);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static void setInterpolator(Interpolator interpolator, Animator... animators) {
        for (Animator item : animators) {
            item.setInterpolator(interpolator);
        }
    }

    public static void setDuration(long duration, Animation... anims) {
        for (Animation item : anims) {
            item.setDuration(duration);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static void setDuration(long duration, Animator... animators) {
        for (Animator item : animators) {
            item.setDuration(duration);
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

        private ChangeViewGroupHeightAnimation(ViewGroup viewGroup, float addedHeight) {
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
