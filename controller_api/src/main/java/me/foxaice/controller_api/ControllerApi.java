package me.foxaice.controller_api;

import java.io.IOException;

import me.foxaice.controller_api.bulb.BulbCommands;
import me.foxaice.controller_api.bulb.BulbGroup;
import me.foxaice.controller_api.udp.UdpAdminCommands;
import me.foxaice.controller_api.udp.UdpController;

public class ControllerApi implements IAdminControllerApi, IBulbControllerApi {
    private UdpController mUdpController;
    private String mIpAddress;
    private int mPort;
    private static final String BROADCAST_IP = "255.255.255.255";
    private static final int ADMIN_PORT = 48899;

    public ControllerApi(String ipAddress, int port) {
        mUdpController = new UdpController();
        mIpAddress = ipAddress;
        mPort = port;
    }

    @Override
    public String getIpAddress() {
        return mIpAddress;
    }

    @Override
    public void setIpAddress(String ipAddress) {
        mIpAddress = ipAddress;
    }

    @Override
    public int getPort() {
        return mPort;
    }

    @Override
    public void setPort(int port) {
        mPort = port;
    }

    @Override
    public void setBrightnessOfCurrentGroup(int brightness) throws IOException {
        sendMessage(BulbCommands.getBrightnessArray(brightness));
    }

    @Override
    public void setBrightnessOfGroup(int group, int brightness) throws IOException, InterruptedException {
        setGroup(group);
        Thread.sleep(100L);
        setBrightnessOfCurrentGroup(brightness);
    }

    @Override
    public void setColorOfCurrentGroup(int color) throws IOException {
        sendMessage(BulbCommands.getColorArray(color));
    }

    @Override
    public void setColorOfGroup(int group, int color) throws IOException, InterruptedException {
        setGroup(group);
        Thread.sleep(100L);
        setColorOfCurrentGroup(color);
    }

    @Override
    public void toggleDiscoModeOfCurrentGroup() throws IOException {
        sendMessage(BulbCommands.getToggleDiscoModeArray());
    }

    @Override
    public void toggleDiscoModeOfGroup(int group) throws IOException, InterruptedException {
        setGroup(group);
        Thread.sleep(100L);
        toggleDiscoModeOfCurrentGroup();
    }

    @Override
    public void speedUpDiscoModeOfCurrentGroup() throws IOException {
        sendMessage(BulbCommands.getSpeedUpDiscoModeArray());
    }

    @Override
    public void speedUpDiscoModeOfGroup(int group) throws IOException, InterruptedException {
        setGroup(group);
        Thread.sleep(100L);
        speedUpDiscoModeOfCurrentGroup();
    }

    @Override
    public void speedDownDiscoModeOfCurrentGroup() throws IOException {
        sendMessage(BulbCommands.getSlowDownDiscoModeArray());
    }

    @Override
    public void speedDownDiscoModeOfGroup(int group) throws IOException, InterruptedException {
        setGroup(group);
        Thread.sleep(100L);
        speedDownDiscoModeOfCurrentGroup();
    }

    @Override
    public void powerOnGroup(int group) throws IOException {
        sendMessage(BulbCommands.getPowerOnArray(getBulbGroup(group)));
    }

    @Override
    public void powerOffGroup(int group) throws IOException {
        sendMessage(BulbCommands.getPowerOffArray(getBulbGroup(group)));
    }

    @Override
    public void setWhiteColorOfGroup(int group) throws IOException, InterruptedException {
        setGroup(group);
        Thread.sleep(100L);
        sendMessage(BulbCommands.getWhiteColorCurrentGroupArray(getBulbGroup(group)));
    }

    @Override
    public void setCurrentGroup(int group) throws IOException {
        setGroup(group);
    }

    @Override
    public void sendCommandQuit() throws IOException {
        sendAdminMessage(UdpAdminCommands.QUIT, mIpAddress);
    }

    @Override
    public void sendCommandLinkWiFi() throws IOException {
        sendAdminMessage(UdpAdminCommands.LINK_WIFI, BROADCAST_IP);
    }

    @Override
    public void sendCommandGetNETP() throws IOException {
        sendAdminMessage(UdpAdminCommands.GET_NETP, mIpAddress);
    }

    @Override
    public void sendCommandOk() throws IOException {
        sendAdminMessage(UdpAdminCommands.OK, mIpAddress);
    }

    @Override
    public void sendCommandRestart() throws IOException {
        sendAdminMessage(UdpAdminCommands.RESTART, mIpAddress);
    }

    @Override
    public void sendCommandScanNetworks() throws IOException {
        sendAdminMessage(UdpAdminCommands.SCAN_NETWORKS, mIpAddress);
    }

    @Override
    public void sendCommandGetSettingsSTA() throws IOException {
        sendAdminMessage(UdpAdminCommands.GET_SETTINGS_STA, mIpAddress);
    }

    @Override
    public void sendCommandGetMode() throws IOException {
        sendAdminMessage(UdpAdminCommands.GET_MODE, mIpAddress);
    }

    @Override
    public void sendCommandSetModeSTA() throws IOException {
        sendAdminMessage(UdpAdminCommands.SET_MODE_STA, mIpAddress);
    }

    @Override
    public void sendCommandSetModeAP() throws IOException {
        sendAdminMessage(UdpAdminCommands.SET_MODE_AP, mIpAddress);
    }

    @Override
    public void sendCommandSetPasswordAP(CharSequence pass) throws IOException {
        String params;
        if (pass.length() == 0) {
            params = "OPEN,NONE";
        } else {
            params = "WPA2PSK,AES,";
        }
        sendAdminMessage(UdpAdminCommands.SET_KEY_AP, mIpAddress, params + pass.toString());
    }

    @Override
    public void sendCommandSetPasswordSTA(CharSequence params) throws IOException {
        sendAdminMessage(UdpAdminCommands.SET_KEY_STA, mIpAddress, params.toString());
    }

    @Override
    public void sendCommandGetPasswordSTA() throws IOException {
        sendAdminMessage(UdpAdminCommands.GET_KEY_STA, mIpAddress);
    }

    @Override
    public void sendCommandSetSSIDSTA(CharSequence params) throws IOException {
        sendAdminMessage(UdpAdminCommands.SET_SSID_STA, mIpAddress, params.toString());
    }

    @Override
    public void sendCommandGetSSIDSTA() throws IOException {
        sendAdminMessage(UdpAdminCommands.GET_SSID_STA, mIpAddress);
    }

    @Override
    public void sendCommandGetPasswordAp() throws IOException {
        sendAdminMessage(UdpAdminCommands.GET_KEY_AP, mIpAddress);
    }

    @Override
    public String receiveAdminMessage(String ipAddress) throws IOException {
        return receiveMessage(ipAddress);
    }

    @Override
    public void closeSockets() {
        mUdpController.closeSocket();
    }

    @Override
    public void initSockets() {
        if (mUdpController != null) {
            mUdpController.closeSocket();
        }
        mUdpController = new UdpController();
    }

    @Override
    public void initSocket(int port) {
        if (mUdpController != null) {
            mUdpController.closeSocket();
        }
        mUdpController = new UdpController(port);
    }

    private void setGroup(int group) throws IOException {
        sendMessage(BulbCommands.getGroupArray(getBulbGroup(group)));
    }

    private BulbGroup getBulbGroup(int group) {
        if (group == 1) {
            return BulbGroup.GROUP_1;
        } else if (group == 2) {
            return BulbGroup.GROUP_2;
        } else if (group == 3) {
            return BulbGroup.GROUP_3;
        } else if (group == 4) {
            return BulbGroup.GROUP_4;
        } else {
            return BulbGroup.All;
        }
    }

    private void sendMessage(byte[] arr) throws IOException {
        mUdpController.sendMessage(arr, mIpAddress, mPort);
    }

    private void sendAdminMessage(UdpAdminCommands command, String ipAddress, String params) throws IOException {
        mUdpController.sendAdminMessage(command, ipAddress, ADMIN_PORT, params);
    }


    private void sendAdminMessage(UdpAdminCommands command, String ipAddress) throws IOException {
        mUdpController.sendAdminMessage(command, ipAddress, ADMIN_PORT);
    }

    private String receiveMessage(String ipAddress) throws IOException {
        return mUdpController.receiveAdminMessage(ipAddress);
    }
}
