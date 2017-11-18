package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.utils.AnimationUtils;
import me.foxaice.smartlight.utils.ViewUtils;

final class ButtonsAnimation {
    private ButtonsAnimation() {}

    static void animateSuccessfulConnection(Activity activity,
                                            String connectionInfoState,
                                            TextView stateText, View changeNameButton,
                                            View changePasswordOrDisconnectButton,
                                            View connectToAnotherNetworkButton) {
        long animationTime = 1_000;
        Interpolator interpolator = new DecelerateInterpolator();
        SuccessConnection.animateStateInfo(stateText, connectionInfoState, new AccelerateDecelerateInterpolator(), animationTime);
        SuccessConnection.animateButton(activity, changeNameButton, interpolator, 0, animationTime);
        SuccessConnection.animateButton(activity, changePasswordOrDisconnectButton, interpolator, 100, animationTime);
        SuccessConnection.animateButton(activity, connectToAnotherNetworkButton, interpolator, 200, animationTime);
    }

    static void animateFailedConnection(Activity activity,
                                        View failedToConnect, View nameController,
                                        View controllerImage, View portText,
                                        View ipText, View macText, View stateText) {
        float deltaX = 500f;
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        ViewUtils.setAlpha(0f, failedToConnect);
        FailedConnection.animateNameController(nameController, interpolator, 1400);
        FailedConnection.animateControllerImage(activity, controllerImage, new AnticipateOvershootInterpolator(), 600);
        FailedConnection.animateControllerInfo(macText, deltaX, 0f, interpolator, 150, 600);
        FailedConnection.animateControllerInfo(ipText, deltaX, 0f, interpolator, 150, 600);
        FailedConnection.animateControllerInfo(portText, deltaX, 0f, interpolator, 150, 600);
        FailedConnection.animateControllerInfo(stateText, 0f, 300f, new AccelerateInterpolator(), 0, 600);
        FailedConnection.animateFailedToConnect(failedToConnect, interpolator, 600, 1000);
    }

    private static int getScreenWidth(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point p = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(p);
            return p.x;
        } else {
            return activity.findViewById(R.id.toolbar_settings).getMeasuredWidth();
        }
    }

    private static int getScreenHeight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point p = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(p);
            return p.y;
        } else {
            return activity.findViewById(R.id.toolbar_settings).getMeasuredHeight();
        }
    }

    private static class SuccessConnection {
        private static void animateButton(Activity activity, View view, Interpolator interpolator, long starDelay, long duration) {
            float screenHeight = ButtonsAnimation.getScreenHeight(activity);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                view.setAlpha(0f);
                view.setY(screenHeight);
                view.animate()
                        .translationY(0f)
                        .alpha(1.f)
                        .setInterpolator(interpolator)
                        .setDuration(duration)
                        .setStartDelay(starDelay)
                        .start();
            } else {
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(new AlphaAnimation(0f, 1.f));
                set.addAnimation(new TranslateAnimation(0, 0, screenHeight, 0f));
                set.setFillAfter(true);
                set.setInterpolator(interpolator);
                set.setDuration(duration);
                view.startAnimation(set);
            }
        }


        private static void animateStateInfo(final TextView view, final String connectionInfoState, final Interpolator interpolator, final long duration) {
            final AnimationSet translateAndFadeIn = new AnimationSet(true);
            translateAndFadeIn.addAnimation(new TranslateAnimation(-view.getMeasuredWidth(), 0f, 0f, 0f));
            translateAndFadeIn.addAnimation(new AlphaAnimation(0f, 1.f));
            translateAndFadeIn.setInterpolator(interpolator);
            translateAndFadeIn.setDuration(duration / 2);
            translateAndFadeIn.setFillAfter(true);
            AlphaAnimation fadeOut = new AlphaAnimation(1.f, 0f);
            fadeOut.setAnimationListener(new AnimationUtils.AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setText(connectionInfoState);
                    view.startAnimation(translateAndFadeIn);
                }
            });
            fadeOut.setDuration(duration / 2);
            fadeOut.setInterpolator(interpolator);
            view.startAnimation(fadeOut);
        }
    }

    private static class FailedConnection {
        private static void animateNameController(View view, Interpolator interpolator, long duration) {
            float offsetScale = 0.1f;
            float translationY = 10f;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                view.animate()
                        .scaleXBy(offsetScale).scaleYBy(offsetScale)
                        .translationY(translationY)
                        .setInterpolator(interpolator)
                        .setDuration(duration)
                        .start();
            } else {
                float x = ViewUtils.getX(view);
                float y = ViewUtils.getY(view);
                float yDelta = y + translationY;
                float pivotX = view.getMeasuredWidth() / 2.f;
                float pivotY = view.getMeasuredHeight() / 2.f;
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(new TranslateAnimation(x, x, y, yDelta));
                set.addAnimation(new ScaleAnimation(1.f, 1.f + offsetScale,
                        1.f, 1.f + offsetScale,
                        pivotX, pivotY));
                set.setInterpolator(interpolator);
                set.setDuration(duration);
                set.setFillAfter(true);
                view.startAnimation(set);
            }
        }

        private static void animateControllerImage(Activity activity, View view, Interpolator interpolator, long duration) {
            int screenWidth = ButtonsAnimation.getScreenWidth(activity);
            float xDelta = screenWidth / 2.f - ViewUtils.getPivotX(view);
            float deltaScale = 1.35f;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                view.animate()
                        .translationX(xDelta)
                        .scaleX(deltaScale).scaleY(deltaScale)
                        .setInterpolator(interpolator)
                        .setDuration(duration).start();
            } else {
                float pivotX = ViewUtils.getPivotX(view);
                float pivotY = ViewUtils.getPivotY(view);
                float x = ViewUtils.getX(view);
                float y = ViewUtils.getY(view);
                float yDelta = y - pivotY;
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(new ScaleAnimation(1.f, deltaScale, 1.f, deltaScale, pivotX, pivotY));
                set.addAnimation(new TranslateAnimation(x, xDelta, yDelta, yDelta));
                set.setInterpolator(interpolator);
                set.setDuration(duration);
                set.setFillAfter(true);
                view.startAnimation(set);
            }
        }

        private static void animateControllerInfo(View view, float translationByX, float translationByY, Interpolator interpolator, long delay, long duration) {
            float alpha = 0f;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                view.animate()
                        .translationXBy(translationByX)
                        .translationYBy(translationByY)
                        .alpha(alpha)
                        .setInterpolator(interpolator)
                        .setStartDelay(delay)
                        .setDuration(duration)
                        .start();
            } else {
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(new AlphaAnimation(1.f, alpha));
                set.addAnimation(new TranslateAnimation(0f, translationByX, 0f, translationByY));
                set.setInterpolator(interpolator);
                set.setDuration(duration);
                set.setStartOffset(delay);
                set.setFillAfter(true);
                view.startAnimation(set);
            }
        }

        private static void animateFailedToConnect(View view, Interpolator interpolator, long startDelay, long duration) {
            float alpha = 1.f;
            float offsetScale = 0.1f;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                view.animate()
                        .scaleXBy(offsetScale).scaleYBy(offsetScale)
                        .alpha(alpha)
                        .setStartDelay(startDelay)
                        .setInterpolator(interpolator)
                        .setDuration(duration)
                        .start();
            } else {
                float pivotX = ViewUtils.getPivotX(view);
                float pivotY = ViewUtils.getPivotY(view);
                AnimationSet set = new AnimationSet(true);
                set.addAnimation(new AlphaAnimation(0f, alpha));
                set.addAnimation(new ScaleAnimation(1.f, 1.f + offsetScale,
                        1.f, 1.f + offsetScale,
                        pivotX, pivotY));
                set.setStartTime(startDelay);
                set.setInterpolator(interpolator);
                set.setDuration(duration);
                set.setFillAfter(true);
                view.startAnimation(set);
            }
        }
    }
}
