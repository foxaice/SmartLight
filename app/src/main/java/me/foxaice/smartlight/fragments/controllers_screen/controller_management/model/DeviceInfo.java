package me.foxaice.smartlight.fragments.controllers_screen.controller_management.model;

public class DeviceInfo implements IDeviceInfo {
    private String mName;
    private String mIP;
    private String mMAC;
    private String mNetwork;
    private String mMode;
    private String mPort;

    @Override
    public void setMAC(String mac) {
        mMAC = mac;
    }

    @Override
    public String getPort() {
        return mPort;
    }

    @Override
    public void setPort(String port) {
        mPort = port;
    }

    @Override
    public String getMode() {
        return mMode;
    }

    @Override
    public void setMode(String mode) {
        mMode = mode;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getIP() {
        return mIP;
    }

    @Override
    public void setIP(String ip) {
        mIP = ip;
    }

    @Override
    public String getMac() {
        return mMAC;
    }

    @Override
    public String getNetwork() {
        return mNetwork;
    }

    @Override
    public void setNetwork(String networkName) {
        mNetwork = networkName;
    }
}
