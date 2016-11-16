package me.foxaice.smartlight.utils;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public abstract class AnimationUtils {
    private AnimationUtils() {
    }

    public static Animation getChangeViewGroupHeightAnimation(ViewGroup viewGroup, float addedHeight) {
        return new ChangeViewGroupHeightAnimation(viewGroup, addedHeight);
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
