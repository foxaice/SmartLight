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

    public double[] getArrayOfPCM() {
        return mPCMArray;
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

    private double[] applyHannWindow(short[] pcm) {
        double[] res = new double[pcm.length];
        for (int i = 0; i < pcm.length; i++) {
            double window = 0.5 * (1 - Math.cos((2 * Math.PI * i) / (pcm.length - 1)));
            res[i] = pcm[i] * window;
        }
        return res;
    }

    private double[] applyHammingWindow(short[] pcm) {
        double[] res = new double[pcm.length];
        for (int i = 0; i < pcm.length; i++) {
            double window = 0.54 - 0.46 * Math.cos((2 * Math.PI * i) / (pcm.length - 1));
            res[i] = pcm[i] * window;
        }
        return res;
    }
}
