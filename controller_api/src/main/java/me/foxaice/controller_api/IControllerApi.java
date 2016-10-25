package me.foxaice.controller_api;

interface IControllerApi {
    String getIpAddress();
    void setIpAddress(String ipAddress);
    int getPort();
    void setPort(int port);
}
