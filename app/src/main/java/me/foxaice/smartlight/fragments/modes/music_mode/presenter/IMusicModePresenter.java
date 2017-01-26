package me.foxaice.smartlight.fragments.modes.music_mode.presenter;

import me.foxaice.smartlight.fragments.modes.IModeBasePresenter;
import me.foxaice.smartlight.fragments.modes.music_mode.view.IMusicModeView;

public interface IMusicModePresenter extends IModeBasePresenter<IMusicModeView> {
    void onTouchPlayButton();
    void onTouchStopButton();
    void loadMusicInfoFromPreferences();
}
