package me.foxaice.smartlight.fragments.modes.music_mode.model;

public class MusicInfo implements IMusicInfo {
    @ColorMode
    private int mColorMode;
    @ViewType
    private int mViewType;
    @MaxFrequencyType
    private int mMaxFrequencyType;
    private int mMaxFrequency;
    private int mMaxVolumeThreshold;
    private int mMinVolumeThreshold;
    private boolean mIsMusicModeChanged;

    public MusicInfo(@ColorMode int colorMode, @ViewType int viewType, @MaxFrequencyType int maxFrequencyType, int maxFrequency, int maxVolumeThreshold, int minVolumeThreshold) {
        mColorMode = colorMode;
        mViewType = viewType;
        mMaxFrequencyType = maxFrequencyType;
        mMaxFrequency = maxFrequency;
        mMaxVolumeThreshold = maxVolumeThreshold;
        mMinVolumeThreshold = minVolumeThreshold;
    }

    @Override
    @ColorMode
    public int getColorMode() {
        return mColorMode;
    }

    @Override
    public void setColorMode(@ColorMode int mode) {
        mIsMusicModeChanged = true;
        mColorMode = mode;
    }

    @Override
    public int getSoundViewType() {
        return mViewType;
    }

    @Override
    public void setSoundViewType(@ViewType int type) {
        mIsMusicModeChanged = true;
        mViewType = type;
    }

    @Override
    public int getMaxFrequencyType() {
        return mMaxFrequencyType;
    }

    @Override
    public void setMaxFrequencyType(@MaxFrequencyType int type) {
        mIsMusicModeChanged = true;
        mMaxFrequencyType = type;
    }

    @Override
    public int getMaxFrequency() {
        return mMaxFrequency;
    }

    @Override
    public void setMaxFrequency(int maxFrequency) {
        mIsMusicModeChanged = true;
        mMaxFrequency = maxFrequency;
    }

    @Override
    public int getMaxVolumeThreshold() {
        return mMaxVolumeThreshold;
    }

    @Override
    public void setMaxVolumeThreshold(int dBSPL) {
        mIsMusicModeChanged = true;
        mMaxVolumeThreshold = dBSPL;
    }

    @Override
    public int getMinVolumeThreshold() {
        return mMinVolumeThreshold;
    }

    @Override
    public void setMinVolumeThreshold(int dBSPL) {
        mIsMusicModeChanged = true;
        mMinVolumeThreshold = dBSPL;
    }

    @Override
    public boolean isMusicInfoChanged() {
        return mIsMusicModeChanged;
    }
}
