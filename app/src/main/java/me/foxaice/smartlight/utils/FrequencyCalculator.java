package me.foxaice.smartlight.utils;

public class FrequencyCalculator {
    private final int mSampleRate;
    private double[] mFrequenciesMagnitudes;
    private double[] mPCMArray;
    private double mDominantFrequencyMagnitude = -1;
    private double mDominantFrequency = -1;

    public FrequencyCalculator(int sampleRate) {
        mSampleRate = sampleRate;
    }

    private static double[] applyHannWindow(short[] pcm) {
        double[] res = new double[pcm.length];
        for (int i = 0; i < pcm.length; i++) {
            double window = 0.5 * (1 - Math.cos((2 * Math.PI * i) / (pcm.length - 1)));
            res[i] = pcm[i] * window;
        }
        return res;
    }

    private static double[] applyHammingWindow(short[] pcm) {
        double[] res = new double[pcm.length];
        for (int i = 0; i < pcm.length; i++) {
            double window = 0.54 - 0.46 * Math.cos((2 * Math.PI * i) / (pcm.length - 1));
            res[i] = pcm[i] * window;
        }
        return res;
    }

    public void calculateFrequencies(short[] pcm) {
        mPCMArray = FrequencyCalculator.applyHammingWindow(pcm);
        double[] fftData = FFT.getFFTArray(mPCMArray);
        double[] magnitudes = new double[fftData.length >> 1];

        double maxMagnitude = -1;
        double maxMagnitudeIndex = -1;
        for (int i = 0, length = magnitudes.length; i < length; i++) {
            if (i < length / 2) {
                magnitudes[i] = Math.sqrt(Math.pow(fftData[i << 1], 2) + Math.pow(fftData[(i << 1) + 1], 2));
                if (magnitudes[i] > maxMagnitude) {
                    maxMagnitude = magnitudes[i];
                    maxMagnitudeIndex = i;
                }
            } else {
                magnitudes[i] = magnitudes[length - 1 - i];
            }
        }

        mDominantFrequencyMagnitude = maxMagnitude;
        mFrequenciesMagnitudes = magnitudes;
        mDominantFrequency = maxMagnitudeIndex * mSampleRate / magnitudes.length;
    }

    public double getDominantFrequency() {
        return mDominantFrequency;
    }

    public double getDominantFrequencyMagnitude() {
        return mDominantFrequencyMagnitude;
    }

    public double[] getFrequenciesMagnitudes() {
        return mFrequenciesMagnitudes;
    }

    public double[] getArrayOfPCM() {
        return mPCMArray;
    }
}
