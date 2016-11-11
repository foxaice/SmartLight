package me.foxaice.smartlight.fragments.controllers_screen.controller_management.presenter;

import me.foxaice.smartlight.fragments.controllers_screen.controller_management.view.IControllerManagementView;

public interface IControllerManagementPresenter {
    void attachView(IControllerManagementView view);
    void detachView();
    void stopExecutorService();
    void setDeviceMAC(String mac);
    String getDeviceName();
    void setDeviceName(String name);
    String getDeviceIP();
    void setDeviceIP(String ip);
    String getDeviceMode();
    String getDevicePort();
    String getDeviceMac();
    String getDeviceNetwork();
    void setParamsForChangePasswordAPTask(CharSequence params);
    void setParamsForConnectToNetworkTask(CharSequence params);
    void startSearchDeviceTask();
    void startDisconnectDeviceTask();
    void startConnectToSavedNetworkTask();
    void startChangePasswordTask();
    void startForgetNetworkTask();
    void startConnectToNetworkTask();
    void startScanNetworksTask();
    void startNextSubTask();
}
