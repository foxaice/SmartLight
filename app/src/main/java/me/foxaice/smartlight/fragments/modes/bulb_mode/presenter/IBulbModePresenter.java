package me.foxaice.smartlight.fragments.modes.bulb_mode.presenter;

import me.foxaice.smartlight.fragments.modes.IModeBasePresenter;
import me.foxaice.smartlight.fragments.modes.bulb_mode.view.IBulbModeView;

public interface IBulbModePresenter extends IModeBasePresenter<IBulbModeView> {
    boolean isPointWithinRGBCircle(float coordX, float coordY, float circleTargetRadius, float circleRadius);
    void onTouchColorCircle(float coordX, float coordY, float circleTargetRadius, float circleRadius, @Events int action);
    void onTouchBrightnessArc(float coordX, float coordY, float arcTargetRadius, float arcRadius, @Events int action);
    void onTouchPowerButton();
    void onTouchWhiteColorButton();
}
