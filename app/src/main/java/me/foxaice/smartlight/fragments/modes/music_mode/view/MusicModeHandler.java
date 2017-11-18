package me.foxaice.smartlight.fragments.modes.music_mode.view;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import me.foxaice.smartlight.R;

class MusicModeHandler extends Handler {
    private static final int SET_FREQUENCY = 0x0001;
    private static final int SET_VOLUME = 0x0002;
    private final WeakReference<MusicModeFragment> wrFragment;
    private String mFrequencyText;
    private String mVolumeText;

    MusicModeHandler(MusicModeFragment fragment) {
        this.wrFragment = new WeakReference<>(fragment);
    }

    @Override
    public void handleMessage(Message msg) {
        MusicModeFragment fragment = wrFragment.get();
        if (fragment != null) {
            if (mFrequencyText == null) {
                mFrequencyText = fragment.getString(R.string.frequency_with_param);
            }
            if (mVolumeText == null) {
                mVolumeText = fragment.getString(R.string.volume_with_param);
            }

            if (msg.what == SET_FREQUENCY) {
                fragment.setCurrentFrequencyText(String.format(mFrequencyText, msg.obj));
            } else if (msg.what == SET_VOLUME) {
                fragment.setCurrentVolumeText(String.format(mVolumeText, msg.obj));
            }
        }
    }

    void sendFrequencyValueMessage(int value) {
        sendMessage(Message.obtain(this, SET_FREQUENCY, value));
    }

    void sendVolumeValueMessage(double value) {
        sendMessage(Message.obtain(this, SET_VOLUME, value));
    }
}