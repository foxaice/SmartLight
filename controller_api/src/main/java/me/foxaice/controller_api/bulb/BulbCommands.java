package me.foxaice.controller_api.bulb;

/**
 * This class contains methods that form bytes array of a command to be sent to LimitlessLedWiFiBridge
 */
public final class BulbCommands {

    private BulbCommands() {
    }

    /**
     * This method forms array of bytes to be sent the command for set brightness level to LimitlessLedWiFiBridge
     *
     * @param brightness the argument is a percentage of the brightness, which can vary between 0 and 100 inclusive
     * @return the formed array of bytes for the sending by {@code brightness}
     */
    public static byte[] getBrightnessArray(int brightness) {
        return getFormedArray(0x4E, (brightness / 4 + 2));
    }


    /**
     * This method forms array of bytes to be sent the command for set color to LimitlessLedWiFiBridge
     *
     * @param color the argument is a byte of the color, which can vary between -128 and 127 inclusive or 0 and 255 (byte overflow) also inclusive
     * @return the formed array of bytes for the sending by {@code color}
     */
    public static byte[] getColorArray(int color) {
        return getFormedArray(0x40, color);
    }

    /**
     * This method forms array of bytes to be sent the command for switch to the next disco mode to LimitlessLedWiFiBridge
     *
     * @return the formed array of byte for the sending
     * @see #getSpeedUpDiscoModeArray()
     * @see #getSlowDownDiscoModeArray()
     */
    public static byte[] getToggleDiscoModeArray() {
        return getFormedArray(0x4D, 0x00);
    }

    /**
     * This method forms array of bytes to be sent the command for speed up a disco mode to LimitlessLedWiFiBridge
     *
     * @return the formed array of byte for the sending
     * @see #getSlowDownDiscoModeArray()
     * @see #getToggleDiscoModeArray()
     */
    public static byte[] getSpeedUpDiscoModeArray() {
        return getFormedArray(0x44, 0x00);
    }

    /**
     * This method forms array of bytes to be sent the command for slow down a disco mode to LimitlessLedWiFiBridge
     *
     * @return the formed array of byte for the sending
     * @see #getSpeedUpDiscoModeArray()
     * @see #getToggleDiscoModeArray()
     */
    public static byte[] getSlowDownDiscoModeArray() {
        return getFormedArray(0x43, 0x00);
    }

    /**
     * This method forms array of byte to be sent the command for switch a color of bulbs' group to white to LimitlessLedWiFiBridge
     *
     * @param bulbGroup the argument is a group of bulbs which is used for the command:
     *                  <ul>
     *                  <li>{@code ALL_GROUP}
     *                  <li>{@code GROUP_1}
     *                  <li>{@code GROUP_2}
     *                  <li>{@code GROUP_3}
     *                  <li>{@code GROUP_4}
     *                  </ul>
     * @return the formed array of byte for the sending by {@code bulbGroup} if {@code bulbGroup} is one of the enumerated:
     * <ul>
     * <li>{@code ALL_GROUP}
     * <li>{@code GROUP_1}
     * <li>{@code GROUP_2}
     * <li>{@code GROUP_3}
     * <li>{@code GROUP_4}
     * </ul>
     * else <b>return</b> the null value
     * @see BulbGroup
     */
    public static byte[] getWhiteColorCurrentGroupArray(BulbGroup bulbGroup) {
        if (bulbGroup == BulbGroup.All) {
            return getFormedArray(0xC2, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_1) {
            return getFormedArray(0xC5, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_2) {
            return getFormedArray(0xC7, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_3) {
            return getFormedArray(0xC9, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_4) {
            return getFormedArray(0xCB, 0x00);
        }
        return null;
    }


    /**
     * This method forms array of byte to be sent the command for turn a group of bulbs on to LimitlessLedWiFiBridge
     *
     * @param bulbGroup the argument is a group of bulbs which is used for the command:
     *                  <ul>
     *                  <li>{@code ALL_GROUP}
     *                  <li>{@code GROUP_1}
     *                  <li>{@code GROUP_2}
     *                  <li>{@code GROUP_3}
     *                  <li>{@code GROUP_4}
     *                  </ul>
     * @return the formed array of byte for the sending by {@code bulbGroup} if {@code bulbGroup} is one of the enumerated:
     * <ul>
     * <li>{@code ALL_GROUP}
     * <li>{@code GROUP_1}
     * <li>{@code GROUP_2}
     * <li>{@code GROUP_3}
     * <li>{@code GROUP_4}
     * </ul>
     * else <b>return</b> the null value
     * @see BulbGroup
     */
    public static byte[] getPowerOnArray(BulbGroup bulbGroup) {
        if (bulbGroup == BulbGroup.All) {
            return getFormedArray(0x42, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_1) {
            return getFormedArray(0x45, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_2) {
            return getFormedArray(0x47, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_3) {
            return getFormedArray(0x49, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_4) {
            return getFormedArray(0x4B, 0x00);
        }
        return null;
    }

    /**
     * This method forms array of byte to be sent the command for turn a group of bulbs off to LimitlessLedWiFiBridge
     *
     * @param bulbGroup the argument is a group of bulbs which is used for the command:
     *                  <ul>
     *                  <li>{@code ALL_GROUP}
     *                  <li>{@code GROUP_1}
     *                  <li>{@code GROUP_2}
     *                  <li>{@code GROUP_3}
     *                  <li>{@code GROUP_4}
     *                  </ul>
     * @return the formed array of byte for the sending by {@code bulbGroup} if {@code bulbGroup} is one of the enumerated:
     * <ul>
     * <li>{@code ALL_GROUP}
     * <li>{@code GROUP_1}
     * <li>{@code GROUP_2}
     * <li>{@code GROUP_3}
     * <li>{@code GROUP_4}
     * </ul>
     * else <b>return</b> the null value
     * @see BulbGroup
     */
    public static byte[] getPowerOffArray(BulbGroup bulbGroup) {
        if (bulbGroup == BulbGroup.All) {
            return getFormedArray(0x41, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_1) {
            return getFormedArray(0x46, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_2) {
            return getFormedArray(0x48, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_3) {
            return getFormedArray(0x4A, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_4) {
            return getFormedArray(0x4C, 0x00);
        }
        return null;
    }


    /**
     * This method forms array of byte to be sent the command to selected a group of bulbs which will execute commands to LimitlessLedWiFiBridge:
     * <ul>
     * <li>{@code getColorArray}
     * <li>{@code getBrightnessArray}
     * <li>{@code getToggleDiscoModeArray}
     * <li>{@code getSpeedUpDiscoModeArray}
     * <li>{@code getSlowDownDiscoModeArray}
     * <li>{@code getWhiteColorCurrentGroupArray}
     * </ul>
     *
     * @param bulbGroup the argument is a group of bulbs which is used for the command:
     *                  <ul>
     *                  <li>{@code ALL_GROUP}
     *                  <li>{@code GROUP_1}
     *                  <li>{@code GROUP_2}
     *                  <li>{@code GROUP_3}
     *                  <li>{@code GROUP_4}
     *                  </ul>
     * @return the formed array of byte for the sending by {@code bulbGroup} if {@code bulbGroup} is one of the enumerated:
     * <ul>
     * <li>{@code ALL_GROUP}
     * <li>{@code GROUP_1}
     * <li>{@code GROUP_2}
     * <li>{@code GROUP_3}
     * <li>{@code GROUP_4}
     * </ul>
     * else <b>return</b> the null value
     * @see BulbGroup
     */
    public static byte[] getGroupArray(BulbGroup bulbGroup) {
        if (bulbGroup == BulbGroup.All) {
            return getFormedArray(0x42, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_1) {
            return getFormedArray(0x45, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_2) {
            return getFormedArray(0x47, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_3) {
            return getFormedArray(0x49, 0x00);
        } else if (bulbGroup == BulbGroup.GROUP_4) {
            return getFormedArray(0x4B, 0x00);
        }
        return null;
    }

    private static byte[] getFormedArray(int firstByte, int secondByte) {
        return new byte[]{(byte) firstByte, (byte) secondByte, 0x55};
    }
}