package me.foxaice.smartlight.activities.music_mode_settings.view;

import android.content.Context;

import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;

public interface IMusicModeSettingsView {
    void setColorModeByColorMode(@IMusicInfo.ColorMode int colorMode);
    void setSoundViewType(@IMusicInfo.ViewType int viewType);
    void setMaxFrequencyType(@IMusicInfo.MaxFrequencyType int maxFrequencyType);
    void setMaxFrequency(int maxFrequency);
    void setMaxVolume(int dBSPL);
    void setMinVolume(int dBSPL);
    Context getContext();
}
