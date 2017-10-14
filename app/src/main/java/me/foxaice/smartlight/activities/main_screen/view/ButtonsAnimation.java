package me.foxaice.smartlight.activities.main_screen.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.modes.bulb_mode.view.BulbModeFragment;
import me.foxaice.smartlight.fragments.modes.disco_mode.view.DiscoModeFragment;
import me.foxaice.smartlight.fragments.modes.music_mode.view.MusicModeFragment;

import static me.foxaice.smartlight.utils.AnimationUtils.AnimationListenerAdapter;
import static me.foxaice.smartlight.utils.AnimationUtils.bringToFrontViews;
import static me.foxaice.smartlight.utils.AnimationUtils.setDuration;
import static me.foxaice.smartlight.utils.AnimationUtils.setEnabledViews;
import static me.foxaice.smartlight.utils.AnimationUtils.setInterpolator;

class ButtonsAnimation {
    private ButtonsAnimation() {
    }

    private static Drawable[] getDrawables(View postView, View preView) {
        Context context = postView.getContext();
        int postViewId = postView.getId();
        int preViewId = preView.getId();
        return new Drawable[]{
                ContextCompat.getDrawable(context,
                        postViewId == R.id.activity_main_screen_image_bulb_mode
                                ? R.drawable.button_mode_bulb_disabled
                                : postViewId == R.id.activity_main_screen_image_music_mode
                                ? R.drawable.button_mode_music_disabled
                                : R.drawable.button_mode_disco_disabled),
                ContextCompat.getDrawable(context,
                        preViewId == R.id.activity_main_screen_image_bulb_mode
                                ? R.drawable.button_mode_bulb_enabled
                                : preViewId == R.id.activity_main_screen_image_music_mode
                                ? R.drawable.button_mode_music_enabled
                                : R.drawable.button_mode_disco_enabled)
        };
    }

    static class PreLollipop {
        private View mNewView;
        private View mOldView;
        private View mModeContent;
        private View mOtherView;
        private View mSettingsContent;
        private Drawable mNewViewDrawable;
        private Drawable mOldViewDrawable;
        private Animation mStartAnimation;
        private MainScreenActivity mActivity;

        PreLollipop(MainScreenActivity activity, View modeContent, View settingsContent, View newView, View oldView, View otherView) {
            mModeContent = modeContent;
            mSettingsContent = settingsContent;
            mNewView = newView;
            mOldView = oldView;
            mOtherView = otherView;
            mActivity = activity;
            Drawable[] drawables = getDrawables(mNewView, mOldView);
            mNewViewDrawable = drawables[0];
            mOldViewDrawable = drawables[1];
            prepareAnimations();
        }

        private void prepareAnimations() {
            final String tag = mNewView.getId() == R.id.activity_main_screen_image_bulb_mode
                    ? BulbModeFragment.TAG
                    : mNewView.getId() == R.id.activity_main_screen_image_music_mode
                    ? MusicModeFragment.TAG
                    : DiscoModeFragment.TAG;
            final Animation downScalePreView = new ScaleAnimation(1, 0, 1, 0, mOldView.getWidth() / 2, mOldView.getHeight() / 2);
            final Animation upScalePreView = new ScaleAnimation(0, 1, 0, 1, mOldView.getWidth() / 2, mOldView.getHeight() / 2);
            final Animation firstUpScaleCurView = new ScaleAnimation(1, 2, 1, 2, mNewView.getWidth() / 2, mNewView.getHeight() / 2);
            final Animation downScaleCurView = new ScaleAnimation(2, 0, 2, 0, mNewView.getWidth() / 2, mNewView.getHeight() / 2);
            final Animation secondUpScaleCurView = new ScaleAnimation(0, 1, 0, 1, mNewView.getWidth() / 2, mNewView.getHeight() / 2);
            final Animation downScaleModeContent = new ScaleAnimation(1, 0, 1, 0, mModeContent.getWidth() / 2, mModeContent.getHeight() / 2);
            final Animation translateModeContent = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0
            );

            mStartAnimation = firstUpScaleCurView;

            setDuration(300, downScalePreView, upScalePreView, firstUpScaleCurView, downScaleCurView, downScaleModeContent);
            setDuration(1000, secondUpScaleCurView, translateModeContent);

            setInterpolator(new OvershootInterpolator(), downScalePreView, upScalePreView, firstUpScaleCurView, downScaleCurView, secondUpScaleCurView, translateModeContent);
            setInterpolator(new AnticipateInterpolator(), downScaleModeContent);

            //Animation Listeners
            downScalePreView.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mOldView.startAnimation(upScalePreView);
                }
            });
            upScalePreView.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ((ImageView) mOldView).setImageDrawable(mOldViewDrawable);
                }
            });
            firstUpScaleCurView.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    setEnabledViews(false, mNewView, mOldView, mOtherView);
                    bringToFrontViews(mNewView, mOldView, mOtherView, mSettingsContent);
                    mOldView.startAnimation(downScalePreView);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mNewView.startAnimation(downScaleCurView);
                }
            });
            downScaleCurView.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mModeContent.startAnimation(downScaleModeContent);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mNewView.startAnimation(secondUpScaleCurView);
                }
            });
            secondUpScaleCurView.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mActivity.setModeFragmentByTag(tag);
                    ((ImageView) mNewView).setImageDrawable(mNewViewDrawable);
                    mModeContent.startAnimation(translateModeContent);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setEnabledViews(false, mNewView);
                    setEnabledViews(true, mOldView, mOtherView);
                }
            });
        }

        void startAnimation() {
            mNewView.startAnimation(mStartAnimation);
        }
    }

    static class PostLollipop {
        private View mNewView;
        private View mOldView;
        private View mContentView;
        private View mOtherView;
        private View mSettingsContent;
        private View mWaveBackground;
        private Drawable mNewViewDrawable;
        private Drawable mOldViewDrawable;
        private Animation mStartAnimation;
        private View mModeContent;
        private MainScreenActivity mActivity;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        PostLollipop(MainScreenActivity activity, View waveBackground, View contentView, View modeContent, View settingsContent, View newView, View oldView, View otherView) {
            mContentView = contentView;
            mSettingsContent = settingsContent;
            mWaveBackground = waveBackground;
            mNewView = newView;
            mOldView = oldView;
            mOtherView = otherView;
            mActivity = activity;
            mModeContent = modeContent;
            Drawable[] drawables = getDrawables(mNewView, mOldView);
            mNewViewDrawable = drawables[0];
            mOldViewDrawable = drawables[1];
            prepareAnimations();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void prepareAnimations() {
            final String tag = mNewView.getId() == R.id.activity_main_screen_image_bulb_mode
                    ? BulbModeFragment.TAG
                    : mNewView.getId() == R.id.activity_main_screen_image_music_mode
                    ? MusicModeFragment.TAG
                    : DiscoModeFragment.TAG;

            final int waveColor = ContextCompat.getColor(mActivity,
                    tag.equals(BulbModeFragment.TAG) ?
                            R.color.backgroundBulbButton : tag.equals(MusicModeFragment.TAG) ?
                            R.color.backgroundMusicButton : R.color.backgroundDiscoButton
            );
            final int finalColor = ContextCompat.getColor(mActivity, R.color.backgroundActivity);

            int[] coords = new int[]{0, 0};
            mNewView.getLocationInWindow(coords);

            int revealX = coords[0] + mNewView.getWidth() / 2;
            int revealY = (int) (coords[1] - mNewView.getHeight() * 1.4f);
            int radius = (int) (Math.max(mWaveBackground.getHeight(), mWaveBackground.getWidth()) * 1.1);

            final Animator firstAnim = ViewAnimationUtils.createCircularReveal(mWaveBackground, revealX, revealY, mNewView.getWidth() / 2, radius);
            final Animator secondAnim = ViewAnimationUtils.createCircularReveal(mContentView, revealX, revealY, mNewView.getWidth() / 2, radius);
            Animation scaleUp = new ScaleAnimation(1, 2, 1, 2, mNewView.getPivotX(), mNewView.getPivotY());
            Animation scaleDown = new ScaleAnimation(1, 0.5f, 1, 0.5f, mNewView.getPivotX(), mNewView.getPivotY());
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(scaleUp);
            animationSet.addAnimation(scaleDown);

            scaleDown.setStartOffset(100);
            firstAnim.setDuration(300);
            setDuration(200, scaleDown);
            setDuration(100, scaleUp);
            setInterpolator(new OvershootInterpolator(), animationSet);

            mStartAnimation = animationSet;

            //Animation Listeners
            firstAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setEnabledViews(false, mNewView, mOldView, mOtherView);
                    mWaveBackground.setBackgroundColor(waveColor);
                    bringToFrontViews(mWaveBackground, mNewView, mSettingsContent);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    Animation scaleDown = new ScaleAnimation(1, 0.1f, 1, 0.1f, mNewView.getPivotX(), mNewView.getPivotY());
                    final Animation scaleUp = new ScaleAnimation(0.1f, 1, 0.1f, 1, mNewView.getPivotX(), mNewView.getPivotY());
                    setDuration(250, scaleDown);
                    setDuration(100, scaleUp);
                    setInterpolator(new OvershootInterpolator(), scaleDown, scaleUp);

                    scaleUp.setAnimationListener(new AnimationListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            ((ImageView) mNewView).setImageDrawable(mNewViewDrawable);
                            ((ImageView) mOldView).setImageDrawable(mOldViewDrawable);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            secondAnim.setDuration(400);
                            secondAnim.setInterpolator(new AccelerateInterpolator());
                            secondAnim.start();
                        }
                    });

                    scaleDown.setAnimationListener(new AnimationListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mNewView.startAnimation(scaleUp);
                        }
                    });

                    mNewView.startAnimation(scaleDown);
                }
            });

            secondAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ((View) mContentView.getParent()).setBackgroundColor(waveColor);
                    mWaveBackground.setBackgroundColor(finalColor);
                    bringToFrontViews(mNewView, mOldView, mOtherView, mModeContent, mSettingsContent);
                    mActivity.setModeFragmentByTag(tag);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setEnabledViews(false, mNewView);
                    setEnabledViews(true, mOldView, mOtherView);
                    mContentView.setBackgroundColor(finalColor);
                }
            });

            animationSet.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    bringToFrontViews(mNewView);
                    setEnabledViews(false, mNewView, mOldView, mOtherView);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    firstAnim.start();
                }
            });
        }

        void startAnimation() {
            mNewView.startAnimation(mStartAnimation);
        }
    }
}
