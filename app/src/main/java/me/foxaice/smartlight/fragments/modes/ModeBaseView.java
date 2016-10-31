package me.foxaice.smartlight.fragments.modes;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

public abstract class ModeBaseView extends Fragment implements IModeBaseView {
    protected Bundle args;

    @Override
    public Parcelable loadParcelable(String objectKey) {
        return args.getParcelable(objectKey);
    }

    @Override
    public void setArguments(Bundle args) {
        if (getArguments() == null) {
            super.setArguments(args);
        }
        this.args = args;
    }
}
