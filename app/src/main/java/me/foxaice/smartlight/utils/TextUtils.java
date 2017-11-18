package me.foxaice.smartlight.utils;

import android.content.Context;

public final class TextUtils {
    private TextUtils() {}

    public static String addColonsToMacAddress(String macAddress) {
        if (Validator.isMacAddress(macAddress)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0, length = macAddress.length(); i < length; i++) {
                builder.append(macAddress.charAt(i));
                if (i % 2 != 0) builder.append(':');
            }
            builder.deleteCharAt(builder.length() - 1);
            return builder.toString();
        } else {
            return null;
        }
    }

    public static String[] getStringArrayFromResources(Context context, int... stringIds) {
        String[] strings = new String[stringIds.length];
        for (int i = 0; i < stringIds.length; i++) {
            strings[i] = context.getString(stringIds[i]);
        }
        return strings;
    }
}
