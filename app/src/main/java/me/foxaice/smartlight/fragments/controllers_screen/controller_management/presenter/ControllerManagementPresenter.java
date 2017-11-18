package me.foxaice.smartlight.fragments.controllers_screen.controller_management.presenter;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.foxaice.controller_api.ControllerApi;
import me.foxaice.controller_api.IAdminControllerApi;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.model.DeviceInfo;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.model.IDeviceInfo;
import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.IControllerManagementView;
import me.foxaice.smartlight.preferences.ISharedPreferencesController;
import me.foxaice.smartlight.preferences.SharedPreferencesController;

public class ControllerManagementPresenter implements IControllerManagementPresenter {
    private static final String TASK_EXECUTE_IS_NOT_PREPARED = "Task Execute IS NOT prepared!";
    private static final String IP_ADDRESS_IS_NULL = "IP address must be defined before ExecutorService starts!";
    private static final int THREAD_QUANTITY = 3;
    private static final int ADMIN_PORT = 48899;
    private IControllerManagementView mView;
    private IDeviceInfo mDeviceInfo;
    private ExecutorService mExecutorService;
    private IAdminControllerApi mAdminControllerApi;
    private ISharedPreferencesController mSharedPreferencesController;
    private CommandSender mCurrentCommandSender;
    private CommandSender mChangePasswordTaskSender;
    private CommandSender mConnectToNetworkSender;

    @Override
    public void attachView(IControllerManagementView view) {
        mView = view;
        if (mDeviceInfo == null) {
            mDeviceInfo = new DeviceInfo();
            WifiManager wifiManager = (WifiManager) mView.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    mDeviceInfo.setNetwork(wifiInfo.getSSID());
                }
            }
        }
        if (mSharedPreferencesController == null) {
            mSharedPreferencesController = SharedPreferencesController.getInstance(mView.getContext());
        }
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void stopExecutorService() {
        if (mExecutorService != null) {
            mAdminControllerApi.closeSockets();
            mExecutorService.shutdown();
            mExecutorService = null;
        }
    }

    @Override
    public void setDeviceMAC(String mac) {
        mDeviceInfo.setMAC(mac);
    }

    @Override
    public String getDeviceName() {
        return mDeviceInfo.getName();
    }

    @Override
    public void setDeviceName(String name) {
        mDeviceInfo.setName(name);
        mSharedPreferencesController.setNameDevice(mDeviceInfo.getMac(), mDeviceInfo.getName());
        mView.onUpdateDeviceName();
    }

    @Override
    public String getDeviceIP() {
        return mDeviceInfo.getIP();
    }

    @Override
    public void setDeviceIP(String ip) {
        mDeviceInfo.setIP(ip);
    }

    @Override
    public String getDeviceMac() {
        return mDeviceInfo.getMac();
    }

    @Override
    public String getDeviceNetwork() {
        return mDeviceInfo.getNetwork();
    }

    @Override
    public String getDeviceMode() {
        return mDeviceInfo.getMode();
    }

    @Override
    public String getDevicePort() {
        return mDeviceInfo.getPort();
    }

    @Override
    public void setParamsForChangePasswordAPTask(CharSequence params) {
        mChangePasswordTaskSender = new CommandSender(this, CommandSender.TASK_CHANGE_PASSWORD, params);
    }

    @Override
    public void setParamsForConnectToNetworkTask(CharSequence params) {
        mConnectToNetworkSender = new CommandSender(this, CommandSender.TASK_CONNECT_TO_NETWORK, params);
    }

    @Override
    public void startSearchDeviceTask() {
        startExecutorService();
        CommandSender task = new CommandSender(this, CommandSender.TASK_SEARCH_DEVICE);
        mExecutorService.submit(task);
    }

    @Override
    public void startDisconnectDeviceTask() {
        stopExecutorService();
        startExecutorService();
        mCurrentCommandSender = new CommandSender(this, CommandSender.TASK_DISCONNECT);
        mExecutorService.submit(mCurrentCommandSender);
    }

    @Override
    public void startConnectToSavedNetworkTask() {
        stopExecutorService();
        startExecutorService();
        mCurrentCommandSender = new CommandSender(this, CommandSender.TASK_CONNECT_TO_SAVED_NETWORK);
        mExecutorService.submit(mCurrentCommandSender);
    }

    @Override
    public void startChangePasswordTask() {
        stopExecutorService();
        startExecutorService();
        if (mChangePasswordTaskSender != null) {
            mCurrentCommandSender = mChangePasswordTaskSender;
            mExecutorService.submit(mCurrentCommandSender);
            mChangePasswordTaskSender = null;
        } else throw new IllegalArgumentException(TASK_EXECUTE_IS_NOT_PREPARED);
    }

    @Override
    public void startConnectToNetworkTask() {
        stopExecutorService();
        startExecutorService();
        if (mConnectToNetworkSender != null) {
            mCurrentCommandSender = mConnectToNetworkSender;
            mExecutorService.submit(mCurrentCommandSender);
            mConnectToNetworkSender = null;
        } else throw new IllegalArgumentException(TASK_EXECUTE_IS_NOT_PREPARED);
    }

    @Override
    public void startScanNetworksTask() {
        stopExecutorService();
        startExecutorService();
        CommandSender task = new CommandSender(this, CommandSender.TASK_SCAN_NETWORKS);
        mExecutorService.submit(task);
    }

    @Override
    public void startForgetNetworkTask() {
        stopExecutorService();
        startExecutorService();
        mCurrentCommandSender = new CommandSender(this, CommandSender.TASK_FORGET_NETWORK);
        mExecutorService.submit(mCurrentCommandSender);
    }

    @Override
    public void startNextSubTask() {
        if (mCurrentCommandSender != null) {
            mCurrentCommandSender.startNextSubTask();
        }
    }

    void searchDeviceTaskIsDone() {
        mView.onSuccessfulConnect();
    }

    void scanNetworkTaskIsDone(String response, String savedNetworkSSID) {
        mView.updateWifiListDialog(response, savedNetworkSSID);
    }

    void subTaskCompleted(int position) {
        mView.subTaskCompleted(position);
    }

    ExecutorService getExecutorService() {
        return mExecutorService;
    }

    void subTaskFailed() {
        mView.onExecutionTaskCompleted(false);
    }

    IDeviceInfo getDeviceInfo() {
        return mDeviceInfo;
    }

    IAdminControllerApi getAdminControllerApi() {
        return mAdminControllerApi;
    }

    private void startExecutorService() {
        if (mAdminControllerApi == null) {
            String ip = getDeviceIP();
            if (ip != null) {
                mAdminControllerApi = new ControllerApi(ip, ADMIN_PORT);
            } else {
                throw new NullPointerException(IP_ADDRESS_IS_NULL);
            }
        }
        if (mExecutorService == null) {
            mExecutorService = Executors.newFixedThreadPool(THREAD_QUANTITY);
        }
    }
}
