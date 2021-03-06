package me.foxaice.smartlight.preferences;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;

public interface ISharedPreferencesController {
    @StringDef({ColorZone.ZONE_1, ColorZone.ZONE_2, ColorZone.ZONE_3, ColorZone.ZONE_4})
    @Retention(RetentionPolicy.SOURCE)
    @interface ColorZone {
        String ZONE_1 = "COLOR_ZONE_1";
        String ZONE_2 = "COLOR_ZONE_2";
        String ZONE_3 = "COLOR_ZONE_3";
        String ZONE_4 = "COLOR_ZONE_4";
    }
    @StringDef({NameZone.ZONE_1, NameZone.ZONE_2, NameZone.ZONE_3, NameZone.ZONE_4})
    @Retention(RetentionPolicy.SOURCE)
    @interface NameZone {
        String ZONE_1 = "NAME_ZONE_1";
        String ZONE_2 = "NAME_ZONE_2";
        String ZONE_3 = "NAME_ZONE_3";
        String ZONE_4 = "NAME_ZONE_4";
    }

    int getPort();
    String getIpAddress();
    String getNameDevice(String macAddress);
    String getGroupColor(@ColorZone String colorZone);
    String getGroupName(@NameZone String nameZone);
    String getFragmentTag();
    IMusicInfo getMusicInfo();
    void setPort(int port);
    void setIpAddress(String ipAddress);
    void setFragmentTag(String tag);
    void setMusicInfo(IMusicInfo musicInfo);
    void setNameDevice(String deviceMacAddress, String deviceName);
    void setGroupColor(@ColorZone String colorZone, String color);
    void setGroupName(@NameZone String nameZone, String name);
}
