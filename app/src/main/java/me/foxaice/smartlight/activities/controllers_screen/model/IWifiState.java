package me.foxaice.smartlight.activities.controllers_screen.model;

public interface IWifiState {
    void updateWifiState(boolean isEnabled, boolean isConnected);
    void setWifiEnabled(boolean isEnabled);
    void setWifiConnected(boolean isConnected);
    boolean isWifiEnabled();
    boolean isWifiConnected();
}
