package me.foxaice.smartlight.fragments.controllers_screen.controllers_list.presenter;

import me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view.IControllerListView;

public interface IControllerListPresenter {
    void attach(IControllerListView view);
    void detach();
    void onUpdateWifiState(boolean isEnabled, boolean isConnected);
    void startSearch();
    void stopSearch();
    String getControllerNameByMACAddress(String macAddress);
}
