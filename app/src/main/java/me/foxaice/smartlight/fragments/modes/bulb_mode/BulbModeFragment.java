package me.foxaice.smartlight.fragments.modes.bulb_mode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.modes.ModeBaseView;
import me.foxaice.smartlight.fragments.modes.bulb_mode.presenter.IBulbModePresenter;
import me.foxaice.smartlight.fragments.modes.bulb_mode.view.IBulbModeView;

public class BulbModeFragment extends ModeBaseView implements IBulbModeView {
    public static final String TAG = "BULB_MODE_FRAGMENT";
    private static final String EXTRA_ANGLE = "EXTRA_ANGLE";
    private static final String EXTRA_COLOR_TARGET_COORDS = "COLOR_COORDS";
    private static final String EXTRA_BRIGHTNESS_TARGET_COORDS = "BRIGHTNESS_COORDS";
    private float mAngle = 50;
    private float[] mColorTargetCoords;
    private float[] mBrightnessTargetCoords;
    private ImageView mPowerButtonImage;
    private ImageView mRGBCircleImage;
    private ImageView mRGBTargetImage;
    private ImageView mBrightnessTargetImage;
    private int mDensityDpi;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bulb_mode, container, false);

        mDensityDpi = getResources().getDisplayMetrics().densityDpi;
        mPowerButtonImage = (ImageView) view.findViewById(R.id.fragment_bulb_mode_image_power_button);
        mRGBCircleImage = (ImageView) view.findViewById(R.id.fragment_bulb_mode_image_rgb_circle);
        mRGBTargetImage = (ImageView) view.findViewById(R.id.fragment_bulb_mode_image_rgb_target_circle);
        mBrightnessTargetImage = (ImageView) view.findViewById(R.id.fragment_bulb_mode_image_brightness_target_arc);
        View whiteColorButton = view.findViewById(R.id.fragment_bulb_mode_image_white_color_button);

        return null;
    }

    @Override
    public void onChangedControllerSettings() {

    }

    @Override
    public void drawBrightnessArcBackground(float angle) {

    }

    @Override
    public void showBulbGroupStateMessage(String group, boolean isPowerOn, boolean isAllGroup) {

    }

    @Override
    public void setBrightnessArcTargetCoords(float x, float y) {

    }

    @Override
    public void setColorCircleTargetCoords(float x, float y) {

    }

    @Override
    public int getScreenDpi() {
        return 0;
    }

    private abstract class BulbModeBaseOnTouchListener implements View.OnTouchListener {
        int getAction(MotionEvent e) throws IllegalArgumentException {
            int action = e.getAction();
            if (MotionEvent.ACTION_UP == action) {
                return IBulbModePresenter.Events.ACTION_UP;
            } else if (MotionEvent.ACTION_DOWN == action) {
                return IBulbModePresenter.Events.ACTION_DOWN;
            } else if (MotionEvent.ACTION_MOVE == action) {
                return IBulbModePresenter.Events.ACTION_MOVE;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}
