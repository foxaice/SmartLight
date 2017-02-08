package me.foxaice.smartlight.fragments.modes.music_mode.presenter;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;

import me.foxaice.smartlight.fragments.modes.ModeBasePresenter;
import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;
import me.foxaice.smartlight.fragments.modes.music_mode.view.IMusicModeView;
import me.foxaice.smartlight.utils.FrequencyCalculator;


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

    private class RecordThread implements Runnable {
        private static final int SAMPLE_RATE = 44100;
        private static final int MAX_AMPLITUDE = 32000;
        private static final int MIN_AMPLITUDE = 400;
        private static final int MAX_FREQUENCY = 6000;
        private static final int BUFFER_SIZE = 2048;
        private boolean mIsAlive;
        private AudioRecord mAudioRecord;
        private short[] mAudioBuffer;
        private double mMinVolumeThreshold;
        private double mMaxFrequency;
        private double mMaxVolumeThreshold;
        private double mMultiplierColor;
        private double mBrightnessMultiplier;
        private int mColorsQuantity;
        private boolean mIsReverse;
        private int mShift;

        private void startRecord() {
            mIsAlive = true;
            MusicModePresenter.this.executorService.submit(this);
        }

        private void stopRecord() {
            mIsAlive = false;
            if (mAudioRecord != null) mAudioRecord.release();
        }

        private void record() {
            prepareAudioRecord();

            if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                mAudioRecord.release();
                return;
            }
        }


        private void prepareAudioRecord() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);

            int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                bufferSize = SAMPLE_RATE * 2;
            }

            mAudioBuffer = new short[bufferSize / 2];

            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);
        }

        private int getUnsignedByte(int value) {
            if (value < 256) {
                return value;
            }
            return getUnsignedByte(value - 256);
        }

        private int calculateFrequency(int sampleRate, short[] audioBuffer) {
            int countCrossing = 0;
            for (int i = 0, j = audioBuffer.length - 1; i < j; i++) {
                if ((audioBuffer[i] > 0 && audioBuffer[i + 1] <= 0) || (audioBuffer[i] < 0 && audioBuffer[i + 1] >= 0)) {
                    countCrossing++;
                }
            }
            return (int) ((countCrossing / 2f) / ((float) audioBuffer.length / (float) sampleRate));
        }


        @Override
        public void run() {
            record();
        }
    }
}
