package me.foxaice.smartlight.activities.main_screen.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BulbInfo implements IBulbInfo, Parcelable {

    public static final Creator<BulbInfo> CREATOR;
    private static final int BULB_QUANTITY = 5;
    private Bulb mCurrentBulb;
    private List<Bulb> mBulbsList;

    static {
        CREATOR = new Creator<BulbInfo>() {
            @Override
            public BulbInfo createFromParcel(Parcel in) {
                return new BulbInfo(in);
            }

            @Override
            public BulbInfo[] newArray(int size) {
                return new BulbInfo[size];
            }
        };
    }

    public BulbInfo() {
        mBulbsList = new ArrayList<>(Arrays.asList(
                new Bulb(ALL_GROUP),
                new Bulb(GROUP_1),
                new Bulb(GROUP_2),
                new Bulb(GROUP_3),
                new Bulb(GROUP_4))
        );
        mCurrentBulb = mBulbsList.get(ALL_GROUP);
    }

    public BulbInfo(@GroupID int currentGroupBulb, boolean isCurrentOn) {
        this();
        if (currentGroupBulb >= 0 && currentGroupBulb < BULB_QUANTITY) {
            mCurrentBulb = mBulbsList.get(currentGroupBulb);
            mCurrentBulb.setOn(isCurrentOn);
        } else {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Must be between 0 and %d inclusive", BULB_QUANTITY - 1));
        }
    }

    private BulbInfo(Parcel in) {
        mCurrentBulb = (Bulb) in.readValue(Bulb.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mBulbsList = new ArrayList<>(BULB_QUANTITY);
            in.readList(mBulbsList, Bulb.class.getClassLoader());
        } else {
            mBulbsList = null;
        }
    }

    @Override
    public void setBulbNames(String[] names) {
        for (int i = 0, j = names.length; i < j; i++) {
            mBulbsList.get(i).setName(names[i]);
        }
    }

    @Override
    public String getCurrentBulbGroupName() {
        return mCurrentBulb.getName();
    }

    @Override
    public String[] getBulbGroupNames() {
        List<String> names = new ArrayList<>(mBulbsList.size());
        for (Bulb b : mBulbsList) {
            names.add(b.getName());
        }
        return names.toArray(new String[0]);
    }

    @Override
    public String getSpecificBulbGroupName(@GroupID int group) {
        return mBulbsList.get(group).getName();
    }

    @Override
    public void setCurrentBulbGroupPowerOn(boolean isOn) {
        if (mCurrentBulb.getID() == ALL_GROUP) {
            for (Bulb bulb : mBulbsList) {
                bulb.setOn(isOn);
            }
        } else {
            mCurrentBulb.setOn(isOn);
        }
    }

    @Override
    public void setSpecificBulbGroupState(@GroupID int group, boolean isOn) {
        checkGroupNumberIntoRange(group);
        mBulbsList.get(group).setOn(isOn);
    }

    @Override
    public int getCurrentBulbGroup() {
        return mCurrentBulb.getID();
    }

    @Override
    public void setCurrentBulbGroup(@GroupID int group) {
        checkGroupNumberIntoRange(group);
        mCurrentBulb = mBulbsList.get(group);
    }

    @Override
    public boolean isCurrentBulbGroupOn() {
        return mCurrentBulb.isOn();
    }

    @Override
    public boolean isSpecificBulbGroupOn(@GroupID int group) {
        checkGroupNumberIntoRange(group);
        return mBulbsList.get(group).isOn();
    }

    private void checkGroupNumberIntoRange(@GroupID int group) {
        if (group < 0 || group >= BULB_QUANTITY) {
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, "Must be between 0 and %d inclusive", BULB_QUANTITY - 1));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mCurrentBulb);
        if (mBulbsList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mBulbsList);
        }
    }
}


