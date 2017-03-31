package me.foxaice.smartlight.activities.music_mode_settings.view;

import me.foxaice.smartlight.fragments.modes.music_mode.model.IMusicInfo;

class ColorMode {
    private final int mContentDescriptionID;
    private final int mImageID;
    @IMusicInfo.ColorMode
    private final int mMode;

    ColorMode(@IMusicInfo.ColorMode int mode, int imageID, int contentDescriptionID) {
        mMode = mode;
        mImageID = imageID;
        mContentDescriptionID = contentDescriptionID;
    }

    int getImageID() {
        return mImageID;
    }

    @IMusicInfo.ColorMode
    int getMode() {
        return mMode;
    }

    public int getContentDescriptionID() {
        return mContentDescriptionID;
    }
}
