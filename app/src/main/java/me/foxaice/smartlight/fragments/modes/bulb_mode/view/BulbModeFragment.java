package me.foxaice.smartlight.fragments.modes.bulb_mode.view;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import me.foxaice.smartlight.R;
import me.foxaice.smartlight.fragments.modes.ModeBaseView;
import me.foxaice.smartlight.fragments.modes.bulb_mode.presenter.BulbModePresenter;
import me.foxaice.smartlight.fragments.modes.bulb_mode.presenter.IBulbModePresenter;

public class BulbModeFragment extends ModeBaseView implements IBulbModeView {
    public static final String TAG = "BULB_MODE_FRAGMENT";
    private static final String EXTRA_ANGLE = "EXTRA_ANGLE";
    private static final String EXTRA_COLOR_TARGET_COORDS = "COLOR_COORDS";
    private static final String EXTRA_BRIGHTNESS_TARGET_COORDS = "BRIGHTNESS_COORDS";
    private float mAngle = 50;
    private float[] mColorTargetCoords;
    private float[] mBrightnessTargetCoords;
    private View mRGBCircleView;
    private View mRGBTargetView;
    private View mPowerButtonView;
    private View mWhiteColorButtonView;
    private BrightnessArcView mBrightnessArcImage;
    private ImageView mBrightnessTargetImage;
    private IBulbModePresenter mBulbModePresenter = new BulbModePresenter();
    private Integer mDensityDpi;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bulb_mode, container, false);
        initViews(view);
        restoreValues(view, savedInstanceState);
        setListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBulbModePresenter.attachView(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mBulbModePresenter.stopExecutorService();
        mBulbModePresenter.detachView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(EXTRA_ANGLE, mAngle);
        outState.putFloatArray(EXTRA_COLOR_TARGET_COORDS, mColorTargetCoords);
        outState.putFloatArray(EXTRA_BRIGHTNESS_TARGET_COORDS, mBrightnessTargetCoords);
    }

    @Override
    public void drawBrightnessArcBackground(float angle) {
        mBrightnessArcImage.setAngle(angle);
        mAngle = angle;
    }

    @Override
    public void setBrightnessArcTargetCoords(float x, float y) {
        if (mBrightnessTargetCoords == null) mBrightnessTargetCoords = new float[2];
        mBrightnessTargetCoords[0] = x;
        mBrightnessTargetCoords[1] = y;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (x < 225) x += 1;
            else x -= 1;
            if (y < 225) y += 1.5;
            mBrightnessTargetImage.setX(x);
            mBrightnessTargetImage.setY(y);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mBrightnessTargetImage.getMeasuredHeight(), mBrightnessTargetImage.getMeasuredWidth());
            if (x > 255) {
                params.leftMargin = (int) x - 1;
            } else {
                params.leftMargin = (int) x + 1;
            }
            params.topMargin = (int) y + 2;
            mBrightnessTargetImage.setLayoutParams(params);
        }

    }

    @Override
    public void setColorCircleTargetCoords(float x, float y) {
        if (mColorTargetCoords == null) mColorTargetCoords = new float[2];
        mColorTargetCoords[0] = x;
        mColorTargetCoords[1] = y;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            y += 0.5;
            x += 0.5;
            mRGBTargetView.setX(x);
            mRGBTargetView.setY(y);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mRGBTargetView.getMeasuredHeight(), mRGBTargetView.getMeasuredWidth());
            params.leftMargin = (int) x + 1;
            params.topMargin = (int) y + 1;
            mRGBTargetView.setLayoutParams(params);
        }
    }

    @Override
    public int getScreenDpi() {
        if (mDensityDpi == null) {
            mDensityDpi = getResources().getDisplayMetrics().densityDpi;
        }
        return mDensityDpi;
    }

    @Override
    public void showBulbGroupStateMessage(String group, boolean isPowerOn, boolean isAllGroup) {
        String message;
        String power = getString(isPowerOn ? R.string.bulb_power_on : R.string.bulb_power_off);
        if (isAllGroup) {
            message = getString(R.string.bulb_all_groups_state, power);
        } else {
            message = getString(R.string.bulb_group_state, group, power);
        }
        if (this.getView() != null) {
            Snackbar.make(this.getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChangedControllerSettings() {
        mBulbModePresenter.updateControllerSettings();
    }

    private void initViews(View root) {
        mPowerButtonView = root.findViewById(R.id.fragment_bulb_mode_image_power_button);
        mWhiteColorButtonView = root.findViewById(R.id.fragment_bulb_mode_image_white_color_button);
        mRGBCircleView = root.findViewById(R.id.fragment_bulb_mode_image_rgb_circle);
        mRGBTargetView = root.findViewById(R.id.fragment_bulb_mode_image_rgb_target_circle);
        mBrightnessArcImage = (BrightnessArcView) root.findViewById(R.id.fragment_bulb_mode_image_brightness_arc);
        mBrightnessTargetImage = (ImageView) root.findViewById(R.id.fragment_bulb_mode_image_brightness_target_arc);
    }

    private void restoreValues(View root, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mAngle = savedInstanceState.getFloat(EXTRA_ANGLE);
            mColorTargetCoords = savedInstanceState.getFloatArray(EXTRA_COLOR_TARGET_COORDS);
            mBrightnessTargetCoords = savedInstanceState.getFloatArray(EXTRA_BRIGHTNESS_TARGET_COORDS);
        }
        root.post(new Runnable() {
            @Override
            public void run() {
                if (mColorTargetCoords != null) {
                    setColorCircleTargetCoords(mColorTargetCoords[0], mColorTargetCoords[1]);
                }
                if (mBrightnessTargetCoords != null) {
                    setBrightnessArcTargetCoords(mBrightnessTargetCoords[0], mBrightnessTargetCoords[1]);
                }
                drawBrightnessArcBackground(mAngle);
                mBrightnessArcImage.invalidate();
            }
        });
    }

    private void setListeners() {
        mWhiteColorButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBulbModePresenter.onTouchWhiteColorButton();
            }
        });

        mPowerButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBulbModePresenter.onTouchPowerButton();
            }
        });

        mRGBCircleView.setOnTouchListener(new BulbModeBaseOnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                @IBulbModePresenter.Events int action;
                try {
                    action = this.getAction(event);
                } catch (IllegalArgumentException e) {
                    return false;
                }
                float circleRadius = mRGBCircleView.getMeasuredHeight() / 2f;
                float circleTargetRadius = mRGBTargetView.getMeasuredHeight() / 2f;
                if (!mBulbModePresenter.isPointWithinRGBCircle(event.getX(), event.getY(), circleTargetRadius, circleRadius)
                        && action == IBulbModePresenter.Events.ACTION_DOWN) {
                    return mBrightnessArcImage.onTouchEvent(event);
                }
                mBulbModePresenter.onTouchColorCircle(event.getX(), event.getY(), circleTargetRadius, circleRadius, action);
                return true;
            }
        });

        mBrightnessArcImage.setOnTouchListener(new BulbModeBaseOnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                @IBulbModePresenter.Events int action;
                try {
                    action = this.getAction(event);
                } catch (IllegalArgumentException e) {
                    return false;
                }
                float arcRadius = mBrightnessArcImage.getMeasuredWidth() / 2f;
                float arcTargetRadius = mBrightnessTargetImage.getMeasuredWidth() / 2f;
                mBulbModePresenter.onTouchBrightnessArc(event.getX(), event.getY(), arcTargetRadius, arcRadius, action);
                return true;
            }
        });
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
