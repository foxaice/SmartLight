package me.foxaice.controller_api;

import java.io.IOException;

public interface IAdminControllerApi extends IControllerApi {
    void sendCommandQuit() throws IOException;
    void sendCommandLinkWiFi() throws IOException;
    void sendCommandGetNETP() throws IOException;
    void sendCommandOk() throws IOException;
    void sendCommandRestart() throws IOException;
    void sendCommandGetSettingsSTA() throws IOException;
    void sendCommandGetMode() throws IOException;
    void sendCommandSetModeSTA() throws IOException;
    void sendCommandSetModeAP() throws IOException;
    void sendCommandSetPasswordAP(CharSequence pass) throws IOException;
    void sendCommandGetPasswordAp() throws IOException;
    void sendCommandScanNetworks() throws IOException;
    void sendCommandSetPasswordSTA(CharSequence params) throws IOException;
    void sendCommandGetPasswordSTA() throws IOException;
    void sendCommandSetSSIDSTA(CharSequence params) throws IOException;
    void sendCommandGetSSIDSTA() throws IOException;
    void closeSockets();
    void initSockets();
    void initSocket(int port);
    String receiveAdminMessage(String ipAddress) throws IOException;
}
