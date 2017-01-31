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


}
