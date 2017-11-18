package me.foxaice.smartlight.activities.controllers_screen.view;

import android.content.Context;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.lang.ref.WeakReference;

import me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view.ControllerListFragment;

class ViewHandler extends Handler {
    private final WeakReference<ControllersScreenActivity> wrActivity;
    private WifiManager mWifiManager;
    private boolean mIsWifiEnabled;
    private boolean mIsWifiConnected;

    ViewHandler(ControllersScreenActivity activity) {
        this.wrActivity = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        ControllersScreenActivity activity = wrActivity.get();
        if (activity != null) {
            if (mWifiManager == null) {
                mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                mIsWifiEnabled = isWifiEnabled();
                mIsWifiConnected = isWifiConnected();
                activity.getPresenter().onChangeWifiState(mIsWifiEnabled, mIsWifiConnected);
            } else {
                boolean tempIsWifiEnabled = isWifiEnabled();
                boolean tempIsWifiConnected = isWifiConnected();
                if (mIsWifiEnabled != tempIsWifiEnabled || mIsWifiConnected != tempIsWifiConnected) {
                    Log.d(getClass().getSimpleName(), "en: " + tempIsWifiEnabled + " cn: " + tempIsWifiConnected);
                    mIsWifiEnabled = tempIsWifiEnabled;
                    mIsWifiConnected = tempIsWifiConnected;
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(
                            ControllerListFragment.getNetworkChangeIntent(mIsWifiEnabled, mIsWifiConnected)
                    );
                    activity.getPresenter().onChangeWifiState(mIsWifiEnabled, mIsWifiConnected);
                }
            }
            this.sendEmptyMessageDelayed(0, 2000);
            Log.d(getClass().getSimpleName(), "handleMessage: I'm looking for...");
        }
    }

    void removeAllCallbacksAndMessages() {
        removeCallbacksAndMessages(null);
    }

    void sendEmptyMessage(){
        sendEmptyMessage(0);
    }

    private boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    private boolean isWifiConnected() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        boolean isConnected = false;
        if (wifiInfo != null) {
            isConnected = wifiInfo.getNetworkId() != -1 && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED;
        }
        return isConnected;
    }
}
