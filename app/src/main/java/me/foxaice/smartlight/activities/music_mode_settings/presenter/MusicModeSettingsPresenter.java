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
    public void saveMusicInfoToPreferences() {
        if (mSharedPreferences != null && mMusicInfo != null) {
            mSharedPreferences.setMusicInfo(mMusicInfo);
        }
    }

    @Override
    public void loadMusicInfoFromPreferences() {
        mMusicInfo = mSharedPreferences.getMusicInfo();
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

    }

    @Override
    public void onChangeSoundViewType(@IMusicInfo.ViewType int viewType) {

    }

    @Override
    public void onChangeMaxFrequencyType(@IMusicInfo.MaxFrequencyType int maxFrequencyType) {

    }

    @Override
    public void onChangeMaxFrequency(int maxFrequency) {

    }

    @Override
    public void onChangeMaxVolume(int dBSPL) {

    }

    @Override
    public void onChangeMinVolume(int dBSPL) {

    }

    @Override
    public boolean isMusicModeChanged() {
        return false;
    }

    @Override
    public int getColorMode() {
        return 0;
    }

    @Override
    public int getSoundViewType() {
        return 0;
    }

    @Override
    public int getMaxFrequencyType() {
        return 0;
    }

    @Override
    public int getMaxFrequency() {
        return 0;
    }

    @Override
    public int getMaxVolume() {
        return 0;
    }

    @Override
    public int getMinVolume() {
        return 0;
    }

    @Override
    public void attach(IMusicModeSettingsView view) {
        mView = view;
        if (mSharedPreferences == null) {
            mSharedPreferences = SharedPreferencesController.getInstance(mView.getContext());
        }
    }

    @Override
    public void detach() {
        mView = null;
    }
}
