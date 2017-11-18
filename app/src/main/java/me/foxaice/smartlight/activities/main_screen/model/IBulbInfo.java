package me.foxaice.smartlight.activities.main_screen.model;

import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static me.foxaice.smartlight.activities.main_screen.model.IBulbInfo.GroupID.*;

public interface IBulbInfo extends Parcelable {
    String KEY_BULB_INFO = "KEY_BULB_INFO";

    @IntDef({ALL_GROUP, GROUP_1, GROUP_2, GROUP_3, GROUP_4})
    @Retention(RetentionPolicy.SOURCE)
    @interface GroupID {
        int ALL_GROUP = 0;
        int GROUP_1 = 1;
        int GROUP_2 = 2;
        int GROUP_3 = 3;
        int GROUP_4 = 4;
    }

    void setCurrentBulbGroup(@GroupID int group);
    void setBulbNames(String[] names);
    void setCurrentBulbGroupPowerOn(boolean isOn);
    void setSpecificBulbGroupState(@GroupID int group, boolean isOn);
    int getCurrentBulbGroup();
    String[] getBulbGroupNames();
    String getCurrentBulbGroupName();
    String getSpecificBulbGroupName(@GroupID int group);
    boolean isCurrentBulbGroupOn();
    boolean isSpecificBulbGroupOn(@GroupID int group);
}
