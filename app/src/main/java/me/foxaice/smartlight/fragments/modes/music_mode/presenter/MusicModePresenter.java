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
    private RecordThread mAudioRecord;


    @Override
    public void onTouchPlayButton() {
        mAudioRecord = new RecordThread();
        turnOnBulbGroup();
        mAudioRecord.startRecord();
    }

    @Override
    public void onTouchStopButton() {

    }

    @Override
    public void loadMusicInfoFromPreferences() {
        mMusicInfo = sharedPreferences.getMusicInfo();
        modeView.setMaxVolumeText(mMusicInfo.getMaxVolumeThreshold());
        modeView.setMinVolumeText(mMusicInfo.getMinVolumeThreshold());
        modeView.setColorModeText(mMusicInfo.getColorMode());
        modeView.setWaveFormVisible(mMusicInfo.getSoundViewType() != IMusicInfo.ViewType.NONE);
    }

    private void turnOnBulbGroup() {
        sendPowerCommand(true);
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

            mAudioRecord.startRecording();
            boolean dynamicType = mMusicInfo.getMaxFrequencyType() == IMusicInfo.MaxFrequencyType.DYNAMIC;
            mIsReverse = isReverseMode();
            mMaxVolumeThreshold = mMusicInfo.getMaxVolumeThreshold();
            mMinVolumeThreshold = mMusicInfo.getMinVolumeThreshold();
            mBrightnessMultiplier = calculateBrightnessMultiplier(mMaxVolumeThreshold, mMinVolumeThreshold);
            mColorsQuantity = getColorsQuantity();
            mShift = getColorShift();
            mMaxFrequency = !dynamicType ? mMusicInfo.getMaxFrequency() : 0;
            mMultiplierColor = mMaxFrequency / mColorsQuantity == 0 ? 1 : mMaxFrequency / mColorsQuantity;
            FrequencyCalculator frequencyCalculator = new FrequencyCalculator(SAMPLE_RATE);
            while (mIsAlive) {
                mAudioRecord.read(mAudioBuffer, 0, mAudioBuffer.length);
                double dbSPL = 0;
                for (short anAudioBuffer : mAudioBuffer) {
                    dbSPL += anAudioBuffer * anAudioBuffer;
                }
                double avgAmplitude = Math.sqrt(dbSPL / mAudioBuffer.length);
                dbSPL = (int) (20 * Math.log10(avgAmplitude));

                if (dbSPL > mMinVolumeThreshold) {
                    if (dbSPL > mMaxVolumeThreshold) dbSPL = mMaxVolumeThreshold;
                    frequencyCalculator.calculateFrequencies(mAudioBuffer);
                    int frequency = (int) frequencyCalculator.getDominantFrequency();
                    if (dynamicType) {
                        if (frequency > mMaxFrequency) {
                            mMaxFrequency = frequency;
                            mMultiplierColor = mMaxFrequency / mColorsQuantity;
                            if (mMultiplierColor < 1) mMultiplierColor = 1;
                        }
                    } else {
                        if (frequency > mMaxFrequency) {
                            frequency = (int) mMaxFrequency;
                        }
                    }
                    int color = calculateColorByFrequency(frequency);
                    int brightness = (int) ((dbSPL - mMinVolumeThreshold) / mBrightnessMultiplier);
                    MusicModePresenter.this.sendColorCommand(color);
                    MusicModePresenter.this.sendBrightnessCommand(brightness);
                    if (mMusicInfo.getSoundViewType() == IMusicInfo.ViewType.WAVEFORM) {
                        MusicModePresenter.this.modeView.drawWaveFormView(frequencyCalculator.getArrayOfPCM(), mBytesColors[color], avgAmplitude, mMusicInfo.getSoundViewType());
                    } else if (mMusicInfo.getSoundViewType() == IMusicInfo.ViewType.FREQUENCIES) {
                        MusicModePresenter.this.modeView.drawWaveFormView(frequencyCalculator.getFrequenciesMagnitudes(), mBytesColors[color], mMaxVolumeThreshold, mMusicInfo.getSoundViewType());
                    }
                    MusicModePresenter.this.modeView.setFrequencyText(frequency);
                }
                MusicModePresenter.this.modeView.setCurrentVolumeText(dbSPL);
            }

            mAudioRecord.stop();
            mAudioRecord.release();
        }

        private int calculateColorByFrequency(int frequency) {
            int color = (int) (frequency / mMultiplierColor);
            if (mIsReverse) {
                color = mColorsQuantity - color;
            }
            color += mShift;
            return getUnsignedByte(color);
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

        private int getColorShift() {
            switch (mMusicInfo.getColorMode()) {
                case IMusicInfo.ColorMode.BGRM:
                    return 0;
                case IMusicInfo.ColorMode.RBGY:
                    return 173;
                case IMusicInfo.ColorMode.GRBC:
                    return 99;
                case IMusicInfo.ColorMode.RGBM:
                    return 214;
                case IMusicInfo.ColorMode.GBRY:
                    return 128;
                case IMusicInfo.ColorMode.BRGC:
                    return 59;
                default:
                    throw new IllegalArgumentException("Wrong mode frequency parameter!");
            }
        }

        private int getColorsQuantity() {
            switch (mMusicInfo.getColorMode()) {
                case IMusicInfo.ColorMode.BGRM:
                    return 214;
                case IMusicInfo.ColorMode.RBGY:
                    return 211;
                case IMusicInfo.ColorMode.GRBC:
                    return 216;
                case IMusicInfo.ColorMode.RGBM:
                    return 215;
                case IMusicInfo.ColorMode.GBRY:
                    return 227;
                case IMusicInfo.ColorMode.BRGC:
                    return 197;
                default:
                    throw new IllegalArgumentException("Wrong mode frequency parameter!");
            }
        }

        private double calculateBrightnessMultiplier(double maxAmplitude, double threshold) {
            double multiplierBrightness = (maxAmplitude - threshold) / 100f;
            if (multiplierBrightness < 1) {
                return 1;
            } else {
                return multiplierBrightness;
            }
        }

        private boolean isReverseMode() {
            return mMusicInfo.getColorMode() == IMusicInfo.ColorMode.RGBM
                    || mMusicInfo.getColorMode() == IMusicInfo.ColorMode.BRGC
                    || mMusicInfo.getColorMode() == IMusicInfo.ColorMode.GBRY;
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

        @Override
        public void run() {
            record();
        }
    }
}
