package me.foxaice.smartlight.fragments.modes.disco_mode.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.modes.ModeBaseView;
import me.foxaice.smartlight.fragments.modes.disco_mode.presenter.DiscoModePresenter;
import me.foxaice.smartlight.fragments.modes.disco_mode.presenter.IDiscoModePresenter;

import static me.foxaice.smartlight.utils.AnimationUtils.AnimationListenerAdapter;

public class DiscoModeFragment extends ModeBaseView implements IDiscoModeView {
    public static final String TAG = "DISCO_MODE_FRAGMENT";
    ImageView mVinylImage;
    private ImageView mSpeedUpTextImage;
    private ImageView mSpeedUpIconImage;
    private ImageView mSpeedDownTextImage;
    private ImageView mSpeedDownIconImage;
    private ImageView mNextModeTextImage;
    private ImageView mNextModeIconImage;
    private Handler mVinylDrawerHandler = new VinylDrawHandler(this);
    private IDiscoModePresenter mDiscoModePresenter = new DiscoModePresenter();
    private Animation[] mAnimationsOfVinylImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disco_mode, container, false);
        loadAnimations();
        initViews(view);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDiscoModePresenter.attachView(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mVinylDrawerHandler.removeCallbacksAndMessages(null);
        mDiscoModePresenter.stopExecutorService();
        mDiscoModePresenter.detachView();
    }

    @Override
    public void spinVinylImage(@Action int mode) {
        if (mode == Action.NEXT_MODE) {
            mVinylImage.startAnimation(mAnimationsOfVinylImage[1]);
        } else if (mode == Action.SPEED_UP) {
            mVinylImage.startAnimation(mAnimationsOfVinylImage[0]);
        } else if (mode == Action.SPEED_DOWN) {
            mVinylImage.startAnimation(mAnimationsOfVinylImage[2]);
        }
    }

    @Override
    public void setPressedSpeedUpButton(boolean isPressed) {
        mSpeedUpIconImage.setPressed(isPressed);
        mSpeedUpTextImage.setPressed(isPressed);
    }

    @Override
    public void setPressedSpeedDownButton(boolean isPressed) {
        mSpeedDownIconImage.setPressed(isPressed);
        mSpeedDownTextImage.setPressed(isPressed);
    }

    @Override
    public void setPressedNextModeButton(boolean isPressed) {
        mNextModeIconImage.setPressed(isPressed);
        mNextModeTextImage.setPressed(isPressed);
    }

    @Override
    public void onChangedControllerSettings() {
        mDiscoModePresenter.updateControllerSettings();
    }

    private void loadAnimations() {
        mAnimationsOfVinylImage = new Animation[]{
                AnimationUtils.loadAnimation(getContext(), R.anim.vinyl_speed_up),
                AnimationUtils.loadAnimation(getContext(), R.anim.vinyl_next_mode),
                AnimationUtils.loadAnimation(getContext(), R.anim.vinyl_speed_down)
        };
        mAnimationsOfVinylImage[1].setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationStart(Animation animation) {
                mVinylDrawerHandler.sendEmptyMessage(VinylDrawHandler.START_DRAWING);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mVinylDrawerHandler.sendEmptyMessage(VinylDrawHandler.STOP_DRAWING);
            }
        });
    }

    private void initViews(View root) {
        mSpeedUpTextImage = (ImageView) root.findViewById(R.id.fragment_disco_mode_image_text_speed_up);
        mSpeedUpIconImage = (ImageView) root.findViewById(R.id.fragment_disco_mode_image_icon_speed_up);
        mSpeedDownTextImage = (ImageView) root.findViewById(R.id.fragment_disco_mode_image_text_speed_down);
        mSpeedDownIconImage = (ImageView) root.findViewById(R.id.fragment_disco_mode_image_icon_speed_down);
        mNextModeTextImage = (ImageView) root.findViewById(R.id.fragment_disco_mode_image_text_next_mode);
        mNextModeIconImage = (ImageView) root.findViewById(R.id.fragment_disco_mode_image_icon_next_mode);
        mVinylImage = (ImageView) root.findViewById(R.id.fragment_disco_mode_image_vinyl);
    }

    private void setListeners() {
        DiscoButtonListener discoButtonListener = new DiscoButtonListener();
        mSpeedUpTextImage.setOnTouchListener(discoButtonListener);
        mSpeedUpIconImage.setOnTouchListener(discoButtonListener);
        mSpeedDownTextImage.setOnTouchListener(discoButtonListener);
        mSpeedDownIconImage.setOnTouchListener(discoButtonListener);
        mNextModeTextImage.setOnTouchListener(discoButtonListener);
        mNextModeIconImage.setOnTouchListener(discoButtonListener);
    }

    private class DiscoButtonListener implements View.OnTouchListener {
        @Override
        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(View v, MotionEvent event) {
            @IDiscoModePresenter.Events int action;
            try {
                action = getAction(event);
            } catch (IllegalArgumentException e) {
                return false;
            }
            if (v.getId() == mSpeedUpTextImage.getId() || v.getId() == mSpeedUpIconImage.getId()) {
                mDiscoModePresenter.onTouchSpeedUpButton(action);
            } else if (v.getId() == mSpeedDownTextImage.getId() || v.getId() == mSpeedDownIconImage.getId()) {
                mDiscoModePresenter.onTouchSpeedDownButton(action);
            } else {
                mDiscoModePresenter.onTouchNextModeButton(action);
            }
            return true;
        }

        private int getAction(MotionEvent e) throws IllegalArgumentException {
            int action = e.getAction();
            if (MotionEvent.ACTION_UP == action) {
                return IDiscoModePresenter.Events.ACTION_UP;
            } else if (MotionEvent.ACTION_DOWN == action) {
                return IDiscoModePresenter.Events.ACTION_DOWN;
            } else if (MotionEvent.ACTION_MOVE == action) {
                return IDiscoModePresenter.Events.ACTION_MOVE;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}
