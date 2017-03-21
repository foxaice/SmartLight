package me.foxaice.smartlight.activities.music_mode_settings.presenter;

import me.foxaice.smartlight.activities.music_mode_settings.view.IMusicModeSettingsView;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;

public interface IMusicModeSettingsPresenter {
    void saveMusicInfoToPreferences();
    void loadMusicInfoFromPreferences();
    void resetMusicInfo();
    void onChangeColorMode(@IMusicInfo.ColorMode int colorMode);
    void onChangeSoundViewType(@IMusicInfo.ViewType int viewType);
    void onChangeMaxFrequencyType(@IMusicInfo.MaxFrequencyType int maxFrequencyType);
    void onChangeMaxFrequency(int maxFrequency);
    void onChangeMaxVolume(int dBSPL);
    void onChangeMinVolume(int dBSPL);
    boolean isMusicModeChanged();
    @IMusicInfo.ColorMode
    int getColorMode();
    @IMusicInfo.ViewType
    int getSoundViewType();
    @IMusicInfo.MaxFrequencyType
    int getMaxFrequencyType();
    int getMaxFrequency();
    int getMaxVolume();
    int getMinVolume();
    void attach(IMusicModeSettingsView view);
    void detach();
}
