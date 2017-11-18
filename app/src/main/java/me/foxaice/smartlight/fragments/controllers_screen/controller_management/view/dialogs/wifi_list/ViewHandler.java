package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.dialogs.wifi_list;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

class ViewHandler extends Handler {
    private static final int UPDATE_WIFI_LIST = 0x0000;
    private final WeakReference<WifiListDialog> wrDialog;

    ViewHandler(WifiListDialog dialog) {
        this.wrDialog = new WeakReference<>(dialog);
    }

    @Override
    public void handleMessage(Message msg) {
        WifiListDialog dialog = wrDialog.get();
        if (dialog != null) {
            if (msg.what == UPDATE_WIFI_LIST) {
                dialog.updateWifiList(String.valueOf(msg.obj));
            }
        }
    }

    void sendUpdateWifiListMessage(String listItem) {
        sendMessage(obtainMessage(UPDATE_WIFI_LIST, listItem));
    }

    void removeAllCallbacksAndMessages(){
        removeCallbacksAndMessages(null);
    }
}

