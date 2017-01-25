package me.foxaice.smartlight.fragments.modes.music_mode.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface IMusicInfo {
    boolean isMusicInfoChanged();
    @ColorMode
    int getColorMode();
    void setColorMode(@ColorMode int mode);
    @ViewType
    int getSoundViewType();
    void setSoundViewType(@ViewType int type);
    @MaxFrequencyType
    int getMaxFrequencyType();
    void setMaxFrequencyType(@MaxFrequencyType int type);
    int getMaxFrequency();
    void setMaxFrequency(int maxFrequency);
    int getMaxVolumeThreshold();
    void setMaxVolumeThreshold(int dBSPL);
    int getMinVolumeThreshold();
    void setMinVolumeThreshold(int dBSPL);
    @IntDef({ColorMode.BGRM, ColorMode.RBGY, ColorMode.GRBC, ColorMode.RGBM, ColorMode.BRGC, ColorMode.GBRY})
    @Retention(RetentionPolicy.SOURCE)
    @interface ColorMode {
        int BGRM = 0x0001;
        int RBGY = 0x0002;
        int GRBC = 0x0003;
        int RGBM = 0x0004;
        int BRGC = 0x0005;
        int GBRY = 0x0006;
    }

    @IntDef({ViewType.FREQUENCIES, ViewType.WAVEFORM, ViewType.NONE})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
        int FREQUENCIES = 0x0001;
        int WAVEFORM = 0x0002;
        int NONE = 0x0003;
    }

    @IntDef({MaxFrequencyType.DYNAMIC, MaxFrequencyType.STATIC})
    @Retention(RetentionPolicy.SOURCE)
    @interface MaxFrequencyType {
        int DYNAMIC = 0x0001;
        int STATIC = 0x0002;
    }

    interface DefaultValues {
        @ColorMode
        int COLOR_MODE = ColorMode.RGBM;
        @ViewType
        int VIEW_TYPE = ViewType.FREQUENCIES;
        @MaxFrequencyType
        int MAX_FREQUENCY_TYPE = MaxFrequencyType.DYNAMIC;
        int MAX_FREQUENCY = 18000;
        int MAX_VOLUME = 75;
        int MIN_VOLUME = 0;
    }
}
