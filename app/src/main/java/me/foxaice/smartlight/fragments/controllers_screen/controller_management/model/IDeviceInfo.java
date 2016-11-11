package me.foxaice.smartlight.fragments.controllers_screen.controller_management.model;

public interface IDeviceInfo {
    void setMAC(String mac);
    String getPort();
    void setPort(String port);
    String getMode();
    void setMode(String mode);
    String getName();
    void setName(String name);
    String getIP();
    void setIP(String ip);
    String getMac();
    String getNetwork();
    void setNetwork(String networkName);
}
