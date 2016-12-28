package me.foxaice.smartlight.fragments.modes.disco_mode.view;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.foxaice.smartlight.fragments.modes.IModeBaseView;

import static me.foxaice.smartlight.fragments.modes.disco_mode.view.IDiscoModeView.Action.NEXT_MODE;
import static me.foxaice.smartlight.fragments.modes.disco_mode.view.IDiscoModeView.Action.SPEED_DOWN;
import static me.foxaice.smartlight.fragments.modes.disco_mode.view.IDiscoModeView.Action.SPEED_UP;

public interface IDiscoModeView extends IModeBaseView {

    void spinVinylImage(@Action int action);
    void setPressedSpeedUpButton(boolean isPressed);
    void setPressedSpeedDownButton(boolean isPressed);
    void setPressedNextModeButton(boolean isPressed);
    @IntDef({SPEED_UP, SPEED_DOWN, NEXT_MODE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Action {
        int SPEED_UP = 0;
        int SPEED_DOWN = 1;
        int NEXT_MODE = 2;
    }
}
