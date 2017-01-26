package me.foxaice.smartlight.fragments.modes.music_mode.view;

import me.foxaice.smartlight.fragments.modes.IModeBaseView;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;

public interface IMusicModeView extends IModeBaseView {
    void drawWaveFormView(double[] data, String color, double max, @IMusicInfo.ViewType int viewType);
    void setCurrentVolumeText(double value);
    void setFrequencyText(int value);
    void setMaxVolumeText(int value);
    void setMinVolumeText(int value);
    void setColorModeText(@IMusicInfo.ColorMode int colorMode);
    void setWaveFormVisible(boolean visible);
    String[] getBytesColorsFromResources();
}
