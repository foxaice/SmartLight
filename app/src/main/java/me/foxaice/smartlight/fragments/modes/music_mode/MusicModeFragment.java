package me.foxaice.smartlight.fragments.modes.music_mode;

import me.foxaice.smartlight.fragments.modes.ModeBaseView;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;
import me.foxaice.smartlight.fragments.modes.music_mode.view.IMusicModeView;

public class MusicModeFragment extends ModeBaseView implements IMusicModeView {
    public static final String TAG = "MUSIC_MODE_FRAGMENT";

    @Override
    public void onChangedControllerSettings() {

    }

    @Override
    public void drawWaveFormView(double[] data, String color, double max, @IMusicInfo.ViewType int viewType) {

    }

    @Override
    public void setCurrentVolumeText(double value) {

    }

    @Override
    public void setFrequencyText(int value) {

    }

    @Override
    public void setMaxVolumeText(int value) {

    }

    @Override
    public void setMinVolumeText(int value) {

    }

    @Override
    public void setColorModeText(@IMusicInfo.ColorMode int colorMode) {

    }

    @Override
    public void setWaveFormVisible(boolean visible) {

    }

    @Override
    public String[] getBytesColorsFromResources() {
        return new String[0];
    }
}
