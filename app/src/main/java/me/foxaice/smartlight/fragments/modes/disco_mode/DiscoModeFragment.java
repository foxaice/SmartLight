package me.foxaice.smartlight.fragments.modes.disco_mode;

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
import me.foxaice.smartlight.fragments.modes.disco_mode.view.IDiscoModeView;

public class DiscoModeFragment extends ModeBaseView implements IDiscoModeView {
    public static final String TAG = "DISCO_MODE_FRAGMENT";
    private IDiscoModePresenter mDiscoModePresenter = new DiscoModePresenter();
    private ImageView mSpeedUpTextImage;
    private ImageView mSpeedUpIconImage;
    private ImageView mSpeedDownTextImage;
    private ImageView mSpeedDownIconImage;
    private ImageView mNextModeTextImage;
    private ImageView mNextModeIconImage;
    private ImageView mVinylImage;
    private Handler mVinylDrawerHandler;

    private Animation[] mAnimationsOfVinylImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disco_mode, container, false);
        mAnimationsOfVinylImage = new Animation[]{
                AnimationUtils.loadAnimation(getContext(), R.anim.vinyl_speed_up),
                AnimationUtils.loadAnimation(getContext(), R.anim.vinyl_next_mode),
                AnimationUtils.loadAnimation(getContext(), R.anim.vinyl_speed_down)
        };

        mSpeedUpTextImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_text_speed_up);
        mSpeedUpIconImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_icon_speed_up);
        mSpeedDownTextImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_text_speed_down);
        mSpeedDownIconImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_icon_speed_down);
        mNextModeTextImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_text_next_mode);
        mNextModeIconImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_icon_next_mode);
        mVinylImage = (ImageView) view.findViewById(R.id.fragment_disco_mode_image_vinyl);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDiscoModePresenter.attachView(this);
    }


    @Override
    public void onChangedControllerSettings() {

    }

    @Override
    public void spinVinylImage(@Action int action) {

    }

    @Override
    public void setPressedSpeedUpButton(boolean isPressed) {

    }

    @Override
    public void setPressedSpeedDownButton(boolean isPressed) {

    }

    @Override
    public void setPressedNextModeButton(boolean isPressed) {

    }

    private static class VinylDrawHandler extends Handler {
        private static final int START_DRAWING = 0x0001;
        private static final int KEEP_DRAWING = 0x0002;
        private static final int STOP_DRAWING = 0x0003;
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
        private int minSide;
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
