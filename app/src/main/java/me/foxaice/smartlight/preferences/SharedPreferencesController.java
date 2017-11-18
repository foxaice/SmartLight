package me.foxaice.smartlight.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;
import me.foxaice.smartlight.fragments.modes.music_mode.model.MusicInfo;
import me.foxaice.smartlight.utils.Validator;

public class SharedPreferencesController implements ISharedPreferencesController {
    private static final String SHARED_PREFERENCE_FILE_NAME = "SmartLightSettings";
    private static final String KEY_IP = "IP";
    private static final String KEY_PORT = "PORT";
    private static final String KEY_FRAGMENT_TAG = "TAG";
    private static final String DEFAULT_NAME_DEVICE = "Unnamed Device";
    private static final String DEFAULT_NAME_ZONE = "Zone %s";
    private static final String DEFAULT_IP_ADDRESS = "255.255.255.255";
    private static final int DEFAULT_PORT = 8899;
    private volatile static SharedPreferencesController mInstance;
    private SharedPreferences mSharedPreferences;

    private interface MusicInfoKeys {
        String COLOR_MODE = "MUSIC_INFO_COLOR_MODE";
        String VIEW_TYPE = "MUSIC_INFO_VIEW_TYPE";
        String MAX_FREQUENCY_TYPE = "MUSIC_INFO_MAX_FREQ_TYPE";
        String MAX_FREQUENCY = "MUSIC_INFO_MAX_FREQ";
        String MAX_VOLUME = "MUSIC_INFO_MAX_VOL";
        String MIN_VOLUME = "MUSIC_INFO_MIN_VOL";
    }

    private SharedPreferencesController(Context context) {
        mSharedPreferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static ISharedPreferencesController getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SharedPreferencesController.class) {
                mInstance = new SharedPreferencesController(context);
            }
        }
        return mInstance;
    }

    @Override
    public int getPort() {
        if (mSharedPreferences != null) {
            if (!mSharedPreferences.contains(KEY_PORT)) {
                putValue(KEY_PORT, DEFAULT_PORT);
            }
            return mSharedPreferences.getInt(KEY_PORT, DEFAULT_PORT);
        }
        return DEFAULT_PORT;
    }

    @Override
    public String getIpAddress() {
        if (mSharedPreferences != null) {
            if (!mSharedPreferences.contains(KEY_IP)) {
                putValue(KEY_IP, DEFAULT_IP_ADDRESS);
            }
            return mSharedPreferences.getString(KEY_IP, DEFAULT_IP_ADDRESS);
        }
        return DEFAULT_IP_ADDRESS;
    }

    @Override
    public String getNameDevice(String macAddress) {
        if (mSharedPreferences != null) {
            if (!mSharedPreferences.contains(macAddress)) {
                putValue(macAddress, DEFAULT_NAME_DEVICE);
            }
            return mSharedPreferences.getString(macAddress, DEFAULT_NAME_DEVICE);
        }
        return DEFAULT_NAME_DEVICE;
    }

    @Override
    public String getGroupColor(@ColorZone String colorZone) {
        if (mSharedPreferences != null) {
            if (colorZone.equals(ColorZone.ZONE_1)
                    || colorZone.equals(ColorZone.ZONE_2)
                    || colorZone.equals(ColorZone.ZONE_3)
                    || colorZone.equals(ColorZone.ZONE_4)) {
                return mSharedPreferences.getString(colorZone, null);
            }
        }
        return null;
    }

    @Override
    public String getGroupName(@NameZone String nameZone) {
        String defaultName = null;
        switch (nameZone) {
            case NameZone.ZONE_1:
                defaultName = String.format(DEFAULT_NAME_ZONE, 1);
                break;
            case NameZone.ZONE_2:
                defaultName = String.format(DEFAULT_NAME_ZONE, 2);
                break;
            case NameZone.ZONE_3:
                defaultName = String.format(DEFAULT_NAME_ZONE, 3);
                break;
            case NameZone.ZONE_4:
                defaultName = String.format(DEFAULT_NAME_ZONE, 4);
        }
        if (mSharedPreferences != null) {
            if (defaultName != null) {
                return mSharedPreferences.getString(nameZone, defaultName);
            }
        }
        return null;
    }

    @Override
    public String getFragmentTag() {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getString(KEY_FRAGMENT_TAG, null);
        } else return null;
    }

    @Override
    public IMusicInfo getMusicInfo() {
        if (mSharedPreferences != null) {
            @IMusicInfo.ColorMode int colorMode = mSharedPreferences.getInt(MusicInfoKeys.COLOR_MODE, IMusicInfo.DefaultValues.COLOR_MODE);
            @IMusicInfo.ViewType int viewType = mSharedPreferences.getInt(MusicInfoKeys.VIEW_TYPE, IMusicInfo.DefaultValues.VIEW_TYPE);
            @IMusicInfo.MaxFrequencyType int maxFrequencyType = mSharedPreferences.getInt(MusicInfoKeys.MAX_FREQUENCY_TYPE, IMusicInfo.DefaultValues.MAX_FREQUENCY_TYPE);
            int maxFrequency = mSharedPreferences.getInt(MusicInfoKeys.MAX_FREQUENCY, IMusicInfo.DefaultValues.MAX_FREQUENCY);
            int maxVolume = mSharedPreferences.getInt(MusicInfoKeys.MAX_VOLUME, IMusicInfo.DefaultValues.MAX_VOLUME);
            int minVolume = mSharedPreferences.getInt(MusicInfoKeys.MIN_VOLUME, IMusicInfo.DefaultValues.MIN_VOLUME);
            return new MusicInfo(colorMode, viewType, maxFrequencyType, maxFrequency, maxVolume, minVolume);
        } else {
            return null;
        }
    }

    @Override
    public void setPort(int port) {
        if (Validator.isPort(port)) {
            putValue(KEY_PORT, port);
        }
    }

    @Override
    public void setIpAddress(String ipAddress) {
        if (Validator.isIpAddress(ipAddress)) {
            putValue(KEY_IP, ipAddress);
        }
    }

    @Override
    public void setFragmentTag(String tag) {
        if (tag != null) {
            putValue(KEY_FRAGMENT_TAG, tag);
        }
    }

    @Override
    public void setMusicInfo(IMusicInfo musicInfo) {
        Map<String, Integer> values = new HashMap<>();
        values.put(MusicInfoKeys.COLOR_MODE, musicInfo.getColorMode());
        values.put(MusicInfoKeys.VIEW_TYPE, musicInfo.getSoundViewType());
        values.put(MusicInfoKeys.MAX_FREQUENCY_TYPE, musicInfo.getMaxFrequencyType());
        values.put(MusicInfoKeys.MAX_FREQUENCY, musicInfo.getMaxFrequency());
        values.put(MusicInfoKeys.MAX_VOLUME, musicInfo.getMaxVolumeThreshold());
        values.put(MusicInfoKeys.MIN_VOLUME, musicInfo.getMinVolumeThreshold());
        putValues(values);
    }

    @Override
    public void setNameDevice(String deviceMacAddress, String deviceName) {
        if (Validator.isMacAddress(deviceMacAddress)) {
            putValue(deviceMacAddress, deviceName);
        }
    }

    @Override
    public void setGroupColor(@ColorZone String colorZone, String color) {
        if (colorZone.equals(ColorZone.ZONE_1) || colorZone.equals(ColorZone.ZONE_2)
                || colorZone.equals(ColorZone.ZONE_3) || colorZone.equals(ColorZone.ZONE_4)) {
            if (Validator.isHexColorRRGGBB(color)) {
                putValue(colorZone, color);
            }
        }
    }

    @Override
    public void setGroupName(@NameZone String nameZone, String name) {
        if (name != null && !name.equals("") && name.split(" ").length != 0) {
            if (nameZone.equals(NameZone.ZONE_1) || nameZone.equals(NameZone.ZONE_2)
                    || nameZone.equals(NameZone.ZONE_3) || nameZone.equals(NameZone.ZONE_4)) {
                putValue(nameZone, name);
            }
        }
    }

    private void putValue(String key, int value) {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().putInt(key, value).apply();
        }
    }

    private void putValue(String key, String value) {
        if (mSharedPreferences != null) {
            mSharedPreferences.edit().putString(key, value).apply();
        }
    }

    private void putValues(Map<String, Integer> values) {
        if (mSharedPreferences != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            for (Map.Entry<String, Integer> item : values.entrySet()) {
                editor.putInt(item.getKey(), item.getValue());
            }
            editor.apply();
        }
    }
}
