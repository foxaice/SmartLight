package me.foxaice.smartlight.fragments.modes.music_mode.presenter;

import me.foxaice.smartlight.fragments.modes.ModeBasePresenter;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;
import me.foxaice.smartlight.fragments.modes.music_mode.view.IMusicModeView;
import me.foxaice.smartlight.utils.FrequencyCalculator;

public class MusicModePresenter extends ModeBasePresenter<IMusicModeView> implements IMusicModePresenter {
    private RecordThread mAudioRecord;
    IMusicInfo musicInfo;
    private String[] mBytesColors;

    @Override
    public void attachView(IMusicModeView view) {
        super.attachView(view);
        if (mBytesColors == null) {
            mBytesColors = modeView.getBytesColorsFromResources();
        }
    }

    @Override
    public void onTouchPlayButton() {
        mAudioRecord = new RecordThread(this);
        turnOnBulbGroup();
        mAudioRecord.startRecord();
    }

    @Override
    public void onTouchStopButton() {
        if (mAudioRecord != null) mAudioRecord.stopRecord();
    }

    @Override
    public void loadMusicInfoFromPreferences() {
        musicInfo = sharedPreferences.getMusicInfo();
        modeView.setMaxVolumeText(musicInfo.getMaxVolumeThreshold());
        modeView.setMinVolumeText(musicInfo.getMinVolumeThreshold());
        modeView.setColorModeText(musicInfo.getColorMode());
        modeView.setWaveFormVisible(musicInfo.getSoundViewType() != IMusicInfo.ViewType.NONE);
    }

    void startRunnableTask(Runnable runnable) {
        executorService.submit(runnable);
    }

    void setColorAndBrightness(int color, int brightness) {
        sendColorCommand(color);
        sendBrightnessCommand(brightness);
    }

    void updateView(int color, int frequency, double dbSPL, double max, double[] data) {
        if (dbSPL > musicInfo.getMinVolumeThreshold()) {
            modeView.drawWaveFormView(data, mBytesColors[color], max, musicInfo.getSoundViewType());
            modeView.setFrequencyText(frequency);
        }
        modeView.setCurrentVolumeText(dbSPL);
    }

    private void turnOnBulbGroup() {
        sendPowerCommand(true);
    }
}
