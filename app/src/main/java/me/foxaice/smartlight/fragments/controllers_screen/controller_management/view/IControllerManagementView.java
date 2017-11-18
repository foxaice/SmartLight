package me.foxaice.smartlight.fragments.controllers_screen.controller_management.view;

import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface IControllerManagementView {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({Tasks.DISCONNECT_TASK, Tasks.CHANGE_PASSWORD_TASK,
            Tasks.CONNECT_TO_NETWORK_TASK, Tasks.CONNECT_TO_SAVED_NETWORK_TASK,
            Tasks.FORGET_NETWORK})
    @interface Tasks {
        int DISCONNECT_TASK = 0x0001;
        int CHANGE_PASSWORD_TASK = 0x0002;
        int CONNECT_TO_NETWORK_TASK = 0x0003;
        int CONNECT_TO_SAVED_NETWORK_TASK = 0x0004;
        int FORGET_NETWORK = 0x0005;
    }
    void onSuccessfulConnect();
    void onUpdateDeviceName();
    void subTaskCompleted(int position);
    void onExecutionTaskCompleted(boolean successful);
    void updateWifiListDialog(String response, String savedNetworkSSID);
    Context getContext();
}
