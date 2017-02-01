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
}
