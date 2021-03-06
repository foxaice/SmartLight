package me.foxaice.smartlight.activities.music_mode_settings.presenter;

import me.foxaice.smartlight.activities.music_mode_settings.view.IMusicModeSettingsView;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;
import me.foxaice.smartlight.preferences.ISharedPreferencesController;
import me.foxaice.smartlight.preferences.SharedPreferencesController;

public class MusicModeSettingsPresenter implements IMusicModeSettingsPresenter {
    private IMusicInfo mMusicInfo;
    private IMusicModeSettingsView mView;
    private ISharedPreferencesController mSharedPreferences;

    @Override
    public void attach(IMusicModeSettingsView view) {
        mView = view;
        if (mSharedPreferences == null) {
            mSharedPreferences = SharedPreferencesController.getInstance(mView.getContext());
        }
    }

    @Override
    public void loadMusicInfoFromPreferences() {
        mMusicInfo = mSharedPreferences.getMusicInfo();
    }

    @Override
    public void detach() {
        mView = null;
    }

    @Override
    public void saveMusicInfoToPreferences() {
        if (mSharedPreferences != null && mMusicInfo != null) {
            mSharedPreferences.setMusicInfo(mMusicInfo);
        }
    }

    @Override
    public void resetMusicInfo() {
        onChangeColorMode(IMusicInfo.DefaultValues.COLOR_MODE);
        onChangeSoundViewType(IMusicInfo.DefaultValues.VIEW_TYPE);
        onChangeMaxFrequencyType(IMusicInfo.DefaultValues.MAX_FREQUENCY_TYPE);
        onChangeMaxFrequency(IMusicInfo.DefaultValues.MAX_FREQUENCY);
        onChangeMaxVolume(IMusicInfo.DefaultValues.MAX_VOLUME);
        onChangeMinVolume(IMusicInfo.DefaultValues.MIN_VOLUME);
    }

    @Override
    public void onChangeColorMode(@IMusicInfo.ColorMode int colorMode) {
        mMusicInfo.setColorMode(colorMode);
        mView.setColorModeByColorMode(colorMode);
    }

    @Override
    public void onChangeSoundViewType(@IMusicInfo.ViewType int viewType) {
        mMusicInfo.setSoundViewType(viewType);
        mView.setSoundViewType(viewType);
    }

    @Override
    public void onChangeMaxFrequencyType(@IMusicInfo.MaxFrequencyType int maxFrequencyType) {
        mMusicInfo.setMaxFrequencyType(maxFrequencyType);
        mView.setMaxFrequencyType(maxFrequencyType);
    }

    @Override
    public void onChangeMaxFrequency(int maxFrequency) {
        mMusicInfo.setMaxFrequency(maxFrequency);
        mView.setMaxFrequency(maxFrequency);
    }

    @Override
    public void onChangeMaxVolume(int dBSPL) {
        mMusicInfo.setMaxVolumeThreshold(dBSPL);
        mView.setMaxVolume(dBSPL);
    }

    @Override
    public void onChangeMinVolume(int dBSPL) {
        mMusicInfo.setMinVolumeThreshold(dBSPL);
        mView.setMinVolume(dBSPL);
    }

    @Override
    public boolean isMusicModeChanged() {
        return mMusicInfo.isMusicInfoChanged();
    }

    @Override
    public int getColorMode() {
        return mMusicInfo.getColorMode();
    }

    @Override
    public int getSoundViewType() {
        return mMusicInfo.getSoundViewType();
    }

    @Override
    public int getMaxFrequencyType() {
        return mMusicInfo.getMaxFrequencyType();
    }

    @Override
    public int getMaxFrequency() {
        return mMusicInfo.getMaxFrequency();
    }

    @Override
    public int getMaxVolume() {
        return mMusicInfo.getMaxVolumeThreshold();
    }

    @Override
    public int getMinVolume() {
        return mMusicInfo.getMinVolumeThreshold();
    }

}
