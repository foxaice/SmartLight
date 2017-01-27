package me.foxaice.smartlight.fragments.modes.music_mode.presenter;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;

import me.foxaice.smartlight.fragments.modes.ModeBasePresenter;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;
import me.foxaice.smartlight.fragments.modes.music_mode.view.IMusicModeView;


public class MusicModePresenter extends ModeBasePresenter<IMusicModeView> implements IMusicModePresenter {
    private IMusicInfo mMusicInfo;
    private String[] mBytesColors;


    @Override
    public void onTouchPlayButton() {

    }

    @Override
    public void onTouchStopButton() {

    }

    @Override
    public void loadMusicInfoFromPreferences() {

    }
}
