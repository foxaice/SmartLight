package me.foxaice.smartlight.fragments.modes;

import android.content.Context;
import android.os.Parcelable;

public interface IModeBaseView {
    Parcelable loadParcelable(String objectKey);
    Context getContext();
    void onChangedControllerSettings();
}
