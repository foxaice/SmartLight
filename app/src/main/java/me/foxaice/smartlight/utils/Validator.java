package me.foxaice.smartlight.utils;

public final class Validator {
    private Validator() {
    }

    public static boolean isIpAddress(String ipAddress) {
        return ipAddress != null && ipAddress.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
    }

    public static boolean isMacAddress(String macAddress) {
        return macAddress != null && macAddress.matches("[0-9A-Fa-f]{12}");
    }

    public static boolean isHexColorRRGGBB(String color) {
        return color != null && color.matches("[0-9A-Fa-f]{6}");
    }

    public static boolean isHexPasskey(String passkey) {
        return passkey.matches("[0-9A-Fa-f]{10}|[0-9A-Fa-f]{26}");
    }

    public static boolean isASCIIPasseky(String passkey) {
        return passkey.matches("[\\x00-\\x7F]{5}|[\\x00-\\x7F]{13}");
    }

    public static boolean isPort(int port) {
        return port >= 0 && port <= 0xFFFF;
    }

    public static boolean isCorrectResponseOnLinkWiFi(String response) {
        return response.matches("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]),[0-9A-Fa-f]{12}(,$|$)");
    }

    public static boolean isCorrectResponseOnGetNETP(String response) {
        return response.matches("^\\+ok=(TCP|UDP),(Server|Client),(6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[1-5][0-9]{4}|[0-9]{1,4}),((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])");
    }

    public static boolean isCorrectResponseOnGetMode(String response) {
        return response.matches("^\\+ok=(STA|AP)");
    }

    public static boolean isCorrectResponseOnAPMode(String response) {
        return response.equalsIgnoreCase("+ok=AP");
    }

    public static boolean isModeSTA(String mode) {
        return mode.matches("^STA$");
    }

    public static boolean isModeAP(String mode) {
        return mode.matches("^AP$");
    }

    public static boolean isCorrectResponseOnSTAMode(String response) {
        return response.equalsIgnoreCase("+ok=STA");
    }

    public static boolean isCorrectResponseOK(String response) {
        return response.equalsIgnoreCase("+ok");
    }

    public static boolean isCorrectResponseOnGetKeyAP(String response) {
        return response.matches("^\\+ok=(OPEN,NONE$|WPA2PSK,AES,[\\w|\\W]{8,63}$)");
    }

    public static boolean isCorrectResponseOnGetKey(String response) {
        return response.matches("^\\+ok=((OPEN,NONE)|((OPEN|SHARED),WEP-(A,([\\x00-\\x7F]{5}|[\\x00-\\x7F]{13})|H,([0-9A-Fa-f]{10}|[0-9A-Fa-f]{26})))|(WPA2?PSK),(TKIP|AES),(\\w|\\W){8,63})$");
    }

    public static boolean isCorrectResponseOnScanNetworks(String response) {
        return response.matches("^\\+ok=\\n\\r([^,]*,*){8}\\w*(\\n\\r\\d*,[^,]*,[0-9A-Fa-f]{2}(:[0-9A-Fa-f]{2}){5},[^,]*,\\d*,([^,]*,){4})*$");
    }

    public static boolean isResponse(String response) {
        return response.matches("\\+ok=[\\w\\W]*");
    }

    public static boolean isError1(String response) {
        return response.equalsIgnoreCase("+ERR=-1");
    }

    public static boolean isError4(String response) {
        return response.equalsIgnoreCase("+ERR=-4");
    }
}