package me.foxaice.smartlight.activities.main_screen.model;

import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface IBulbInfo extends Parcelable {
    String KEY_BULB_INFO = "KEY_BULB_INFO";
    int ALL_GROUP = 0;
    int GROUP_1 = 1;
    int GROUP_2 = 2;
    int GROUP_3 = 3;
    int GROUP_4 = 4;
    boolean isCurrentBulbGroupOn();
    boolean isSpecificBulbGroupOn(@GroupID int group);
    int getCurrentBulbGroup();
    void setCurrentBulbGroup(@GroupID int group);
    String getCurrentBulbGroupName();
    String getSpecificBulbGroupName(@GroupID int group);
    String[] getBulbGroupNames();
    void setBulbNames(String[] names);
    void setCurrentBulbGroupState(boolean isOn);
    void setSpecificBulbGroupState(@GroupID int group, boolean isOn);
    @IntDef({ALL_GROUP, GROUP_1, GROUP_2, GROUP_3, GROUP_4})
    @Retention(RetentionPolicy.SOURCE)
    @interface GroupID {}
}
