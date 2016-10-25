package me.foxaice.controller_api.udp;

import java.io.UnsupportedEncodingException;

public enum UdpAdminCommands {
    HELP("AT+H\r"),
    QUIT("AT+Q\r"),
    LINK_WIFI("Link_Wi-Fi"),
    NONE("AT+\r"),
    GET_NETP("AT+NETP\r"),
    OK("+ok"),
    SCAN_NETWORKS("AT+WSCAN\r"),
    RESTART("AT+Z\r"),
    SET_KEY_AP("AT+WAKEY="),
    GET_KEY_AP("AT+WAKEY\r"),
    GET_SSID_STA("AT+WSSSID\r"),
    SET_SSID_STA("AT+WSSSID="),
    GET_KEY_STA("AT+WSKEY\r"),
    SET_KEY_STA("AT+WSKEY="),
    GET_SETTINGS_STA("AT+WANN\r"),
    GET_MODE("AT+WMODE\r"),
    SET_MODE_STA("AT+WMODE=STA\r"),
    SET_MODE_AP("AT+WMODE=AP\r"),;

    private String text;

    UdpAdminCommands(String text) {
        this.text = text;
    }

    public byte[] getBytes() {
        try {
            return text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return text.getBytes();
        }
    }

    @Override
    public String toString() {
        return text;
    }
}
