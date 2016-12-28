package me.foxaice.smartlight.fragments.modes.disco_mode.presenter;

import me.foxaice.smartlight.fragments.modes.IModeBasePresenter;
import me.foxaice.smartlight.fragments.modes.disco_mode.view.IDiscoModeView;

public interface IDiscoModePresenter extends IModeBasePresenter<IDiscoModeView> {
    void onTouchSpeedUpButton(@Events int eventAction);
    void onTouchSpeedDownButton(@Events int eventAction);
    void onTouchNextModeButton(@Events int eventAction);
}
