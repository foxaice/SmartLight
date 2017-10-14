package me.foxaice.smartlight.fragments.settings.view.dialogs;

import android.text.TextWatcher;

class CustomTextWatcher {
    static TextWatcher getIPTextWatcher(final ConnectionSettingsDialog dialog) {
        return TextWatcherAdapter.initializeIpAddressTextWatcher(dialog);
    }

    static TextWatcher getPortTextWatcher(final ConnectionSettingsDialog dialog) {
        return TextWatcherAdapter.initializePortTextWatcher(dialog);
    }
}
