package me.foxaice.smartlight.activities.controllers_screen.model;

public class WifiState implements IWifiState {
    private boolean mIsWifiEnabled;
    private boolean mIsWifiConnected;

    @Override
    public void updateWifiState(boolean isEnabled, boolean isConnected) {
        mIsWifiEnabled = isEnabled;
        mIsWifiConnected = isConnected;
    }

    @Override
    public void setWifiEnabled(boolean isEnabled) {
        mIsWifiEnabled = isEnabled;
        if (!isEnabled) {
            mIsWifiConnected = false;
        }
    }

    @Override
    public void setWifiConnected(boolean isConnected) {
        mIsWifiConnected = isConnected;
    }

    @Override
    public boolean isWifiEnabled() {
        return mIsWifiEnabled;
    }

    @Override
    public boolean isWifiConnected() {
        return mIsWifiConnected;
    }
}
