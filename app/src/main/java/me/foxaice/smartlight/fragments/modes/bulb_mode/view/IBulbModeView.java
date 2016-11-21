package me.foxaice.smartlight.fragments.modes.bulb_mode.view;

import me.foxaice.smartlight.fragments.modes.IModeBaseView;

public interface IBulbModeView extends IModeBaseView {
    void drawBrightnessArcBackground(float angle);
    void showBulbGroupStateMessage(String group, boolean isPowerOn, boolean isAllGroup);
    void setBrightnessArcTargetCoords(float x, float y);
    void setColorCircleTargetCoords(float x, float y);
    int getScreenDpi();
}
