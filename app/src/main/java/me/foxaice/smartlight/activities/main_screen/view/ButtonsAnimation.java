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
import me.foxaice.smartlight.utils.AnimationUtils;
import me.foxaice.smartlight.utils.ViewUtils;

import static me.foxaice.smartlight.utils.AnimationUtils.AnimationListenerAdapter;

abstract class ButtonsAnimation {
    View newView;
    View oldView;
    View otherView;
    View modeContent;
    View settingsContent;
    Drawable oldViewDrawable;
    Drawable newViewDrawable;
    Animation startAnimation;
    MainScreenActivity activity;
    String modeTag;

    private ButtonsAnimation(MainScreenActivity activity, View modeContent, View settingsContent, View newView, View oldView, View otherView) {
        this.modeContent = modeContent;
        this.settingsContent = settingsContent;
        this.newView = newView;
        this.oldView = oldView;
        this.otherView = otherView;
        this.activity = activity;
        modeTag = ButtonsAnimation.getModeTagFromModeButton(newView);
        Drawable[] drawables = ButtonsAnimation.getDrawables(newView, oldView);
        newViewDrawable = drawables[0];
        oldViewDrawable = drawables[1];
    }

    private static Drawable[] getDrawables(View postView, View preView) {
        Context context = postView.getContext();
        int postId = postView.getId();
        int preId = preView.getId();
        return new Drawable[]{
                ContextCompat.getDrawable(context,
                        postId == R.id.activity_main_screen_image_bulb_mode ?
                                R.drawable.button_mode_bulb_disabled : postId == R.id.activity_main_screen_image_music_mode ?
                                R.drawable.button_mode_music_disabled : R.drawable.button_mode_disco_disabled),
                ContextCompat.getDrawable(context,
                        preId == R.id.activity_main_screen_image_bulb_mode ?
                                R.drawable.button_mode_bulb_enabled : preId == R.id.activity_main_screen_image_music_mode ?
                                R.drawable.button_mode_music_enabled : R.drawable.button_mode_disco_enabled)
        };
    }

    private static String getModeTagFromModeButton(View modeButtonView) {
        int id = modeButtonView.getId();
        return id == R.id.activity_main_screen_image_bulb_mode ?
                BulbModeFragment.TAG : id == R.id.activity_main_screen_image_music_mode ?
                MusicModeFragment.TAG : DiscoModeFragment.TAG;
    }

    private static void setModeFragmentOnActivity(MainScreenActivity activity, String modeTag) {
        activity.setModeFragmentByTag(modeTag);
    }

    void startAnimation() {
        newView.startAnimation(startAnimation);
    }

    static class PreLollipop extends ButtonsAnimation {
        PreLollipop(MainScreenActivity activity, View modeContent, View settingsContent, View newView, View oldView, View otherView) {
            super(activity, modeContent, settingsContent, newView, oldView, otherView);
            prepareAnimations();
        }

        private void prepareAnimations() {
            Animation downScalePreView =
                    new ScaleAnimation(1, 0, 1, 0, oldView.getWidth() / 2, oldView.getHeight() / 2);
            Animation upScalePreView =
                    new ScaleAnimation(0, 1, 0, 1, oldView.getWidth() / 2, oldView.getHeight() / 2);
            Animation firstUpScaleCurView =
                    new ScaleAnimation(1, 2, 1, 2, newView.getWidth() / 2, newView.getHeight() / 2);
            Animation downScaleCurView =
                    new ScaleAnimation(2, 0, 2, 0, newView.getWidth() / 2, newView.getHeight() / 2);
            Animation secondUpScaleCurView =
                    new ScaleAnimation(0, 1, 0, 1, newView.getWidth() / 2, newView.getHeight() / 2);
            Animation downScaleModeContent =
                    new ScaleAnimation(1, 0, 1, 0, modeContent.getWidth() / 2, modeContent.getHeight() / 2);
            Animation translateModeContent = new TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0
            );

            AnimationUtils.setDuration(300,
                    downScalePreView, upScalePreView, firstUpScaleCurView, downScaleCurView, downScaleModeContent);
            AnimationUtils.setDuration(1000,
                    secondUpScaleCurView, translateModeContent);

            AnimationUtils.setInterpolator(new AnticipateInterpolator(),
                    downScaleModeContent);
            AnimationUtils.setInterpolator(new OvershootInterpolator(),
                    downScalePreView, upScalePreView, firstUpScaleCurView, downScaleCurView, secondUpScaleCurView, translateModeContent);

            setAnimationListeners(
                    firstUpScaleCurView,
                    downScalePreView,
                    downScaleCurView,
                    upScalePreView,
                    downScaleModeContent,
                    secondUpScaleCurView,
                    translateModeContent
            );

            startAnimation = firstUpScaleCurView;
        }

        /* Sequence of Animations
         *  *start----->end*
         *
         * an1------------->an1
         *   |                |
         *   |                an3------------------->an3
         *   |                  |                      |
         *   |                  an5------------>an5    |
         *   |                                         |
         *   |                                         an6----------->an6
         *   |                                         |
         *   |                                         an7----------->an7
         *   an2----------------->an2
         *                          |
         *                          an4------------>an4
         */
        private void setAnimationListeners(final Animation an1, final Animation an2, final Animation an3, final Animation an4, final Animation an5, final Animation an6, final Animation an7) {
            an1.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ViewUtils.setEnabled(false, newView, oldView, otherView);
                    ViewUtils.bringToFront(newView, oldView, otherView, settingsContent);
                    oldView.startAnimation(an2);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    newView.startAnimation(an3);
                }
            });
            an2.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    oldView.startAnimation(an4);
                }
            });
            an3.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    modeContent.startAnimation(an5);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    newView.startAnimation(an6);
                }
            });
            an4.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ((ImageView) oldView).setImageDrawable(oldViewDrawable);
                }
            });
            an6.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ButtonsAnimation.setModeFragmentOnActivity(activity, modeTag);
                    ((ImageView) newView).setImageDrawable(newViewDrawable);
                    modeContent.startAnimation(an7);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ViewUtils.setEnabled(false, newView);
                    ViewUtils.setEnabled(true, oldView, otherView);
                }
            });
            an7.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ViewUtils.bringToFront(newView, modeContent, oldView, otherView);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static class PostLollipop extends ButtonsAnimation {
        private View mContentView;
        private View mWaveBackground;
        private int mWaveColor;
        private int mFinalColor;

        PostLollipop(MainScreenActivity activity, View waveBackground, View contentView, View modeContent, View settingsContent, View newView, View oldView, View otherView) {
            super(activity, modeContent, settingsContent, newView, oldView, otherView);
            mContentView = contentView;
            mWaveBackground = waveBackground;
            mFinalColor = ContextCompat.getColor(activity, R.color.backgroundActivity);
            mWaveColor = ContextCompat.getColor(activity,
                    modeTag.equals(BulbModeFragment.TAG) ?
                            R.color.backgroundBulbButton : modeTag.equals(MusicModeFragment.TAG) ?
                            R.color.backgroundMusicButton : R.color.backgroundDiscoButton
            );
            prepareAnimations();
        }

        private void prepareAnimations() {
            int[] coords = new int[]{0, 0};
            newView.getLocationInWindow(coords);
            int revealX = coords[0] + newView.getWidth() / 2;
            int revealY = (int) (coords[1] - newView.getHeight() * 1.4f);
            int radius = (int) (Math.max(mWaveBackground.getHeight(), mWaveBackground.getWidth()) * 1.1);

            final Animator firstAnim =
                    ViewAnimationUtils.createCircularReveal(mWaveBackground, revealX, revealY, newView.getWidth() / 2, radius);
            final Animator secondAnim =
                    ViewAnimationUtils.createCircularReveal(mContentView, revealX, revealY, newView.getWidth() / 2, radius);
            final Animation firstScaleUp =
                    new ScaleAnimation(1, 2, 1, 2, newView.getPivotX(), newView.getPivotY());
            final Animation firstScaleDown =
                    new ScaleAnimation(1, 0.5f, 1, 0.5f, newView.getPivotX(), newView.getPivotY());
            final Animation secondScaleDown =
                    new ScaleAnimation(1, 0.1f, 1, 0.1f, newView.getPivotX(), newView.getPivotY());
            final Animation secondScaleUp =
                    new ScaleAnimation(0.1f, 1, 0.1f, 1, newView.getPivotX(), newView.getPivotY());

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(firstScaleUp);
            animationSet.addAnimation(firstScaleDown);

            firstScaleDown.setStartOffset(100);

            AnimationUtils.setDuration(100, firstScaleUp, secondScaleUp);
            AnimationUtils.setDuration(200, firstScaleDown);
            AnimationUtils.setDuration(250, secondScaleDown);
            AnimationUtils.setDuration(300, firstAnim);
            AnimationUtils.setDuration(400, secondAnim);

            AnimationUtils.setInterpolator(new OvershootInterpolator(), animationSet, secondScaleDown, secondScaleUp);
            AnimationUtils.setInterpolator(new AccelerateInterpolator(), secondAnim);

            setAnimationListeners(
                    firstAnim,
                    secondAnim,
                    animationSet,
                    secondScaleDown,
                    secondScaleUp
            );

            startAnimation = animationSet;
        }

        /* Sequence of Animations
         *  *start----->end*
         *
         * animation1-->animation1
         *                       |
         *                       animator1-->animator1
         *                                           |
         *                                           animation2-->animation2
         *                                                    |
         *                                                    animation3-->animation3
         *                                                                          |
         *                                                                          animator2-->animator2
         */
        private void setAnimationListeners(final Animator animator1, final Animator animator2, Animation animation1, final Animation animation2, final Animation animation3) {
            animation1.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ViewUtils.bringToFront(newView);
                    ViewUtils.setEnabled(false, newView, oldView, otherView);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animator1.start();
                }
            });
            animator1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ViewUtils.setEnabled(false, newView, oldView, otherView);
                    mWaveBackground.setBackgroundColor(mWaveColor);
                    ViewUtils.bringToFront(mWaveBackground, newView, settingsContent);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    newView.startAnimation(animation2);
                }
            });
            animation2.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    newView.startAnimation(animation3);
                }
            });
            animation3.setAnimationListener(new AnimationListenerAdapter() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ((ImageView) newView).setImageDrawable(newViewDrawable);
                    ((ImageView) oldView).setImageDrawable(oldViewDrawable);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animator2.start();
                }
            });
            animator2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ((View) mContentView.getParent()).setBackgroundColor(mWaveColor);
                    mWaveBackground.setBackgroundColor(mFinalColor);
                    ViewUtils.bringToFront(newView, oldView, otherView, modeContent, settingsContent);
                    ButtonsAnimation.setModeFragmentOnActivity(activity, modeTag);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ViewUtils.setEnabled(false, newView);
                    ViewUtils.setEnabled(true, oldView, otherView);
                    mContentView.setBackgroundColor(mFinalColor);
                }
            });
        }
    }
}
