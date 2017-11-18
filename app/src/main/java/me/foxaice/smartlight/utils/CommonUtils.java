package me.foxaice.smartlight.utils;

final class CommonUtils {
    private CommonUtils() {}

    static int nextPowOf2(int value) {
        if (isPowOf2(value)) {
            return value;
        } else {
            return Integer.highestOneBit(value) << 1;
        }
    }

    static boolean isPowOf2(int value) {
        return (value & value - 1) == 0;
    }
}
