package me.foxaice.smartlight.activities.main_screen.model;

import android.os.Parcel;
import android.os.Parcelable;

class Bulb implements Parcelable {
    public static final Creator<Bulb> CREATOR;
    private boolean mIsOn;
    private int mID;
    private String mName;

    static {
        CREATOR = new Creator<Bulb>() {
            @Override
            public Bulb createFromParcel(Parcel in) {
                return new Bulb(in);
            }

            @Override
            public Bulb[] newArray(int size) {
                return new Bulb[size];
            }
        };
    }

    Bulb(@IBulbInfo.GroupID int BulbGroupID) {
        mID = BulbGroupID;
        mIsOn = false;
        mName = null;
    }

    private Bulb(Parcel in) {
        mIsOn = in.readByte() != 0x00;
        mID = in.readInt();
        mName = in.readString();
    }

    String getName() {
        return mName;
    }

    void setName(String name) {
        mName = name;
    }

    boolean isOn() {
        return mIsOn;
    }

    void setOn(boolean isOn) {
        this.mIsOn = isOn;
    }

    int getID() {
        return mID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mIsOn ? 0x01 : 0x00));
        dest.writeInt(mID);
        dest.writeString(mName);
    }
}