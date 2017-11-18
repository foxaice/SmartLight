package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.wifi_list;

class WifiPoint {
    private String mChannel;
    private String mSSID;
    private String mBSSID;
    private String mSecurity;
    private int mSignal;

    WifiPoint(String channel, String SSID, String BSSID, String security, String signal) {
        mChannel = channel;
        mSSID = SSID;
        mBSSID = BSSID;
        mSecurity = security;
        mSignal = Integer.valueOf(signal);
    }

    String getChannel() {
        return mChannel;
    }

    String getSSID() {
        return mSSID;
    }

    String getBSSID() {
        return mBSSID;
    }

    String getSecurity() {
        return mSecurity;
    }

    int getSignal() {
        return mSignal;
    }
}