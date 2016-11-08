package me.foxaice.smartlight.activities.controllers_screen.model;

public interface IWifiState {
    boolean isWifiEnabled();
    void setWifiEnabled(boolean isEnabled);
    boolean isWifiConnected();
    void setWifiConnected(boolean isConnected);
    void updateWifiState(boolean isEnabled, boolean isConnected);
}
