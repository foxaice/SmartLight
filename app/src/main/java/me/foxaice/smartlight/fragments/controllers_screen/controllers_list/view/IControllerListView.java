package me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view;

import android.content.Context;

public interface IControllerListView {
    void sendMessageStartSearch();
    void sendMessageStopSearch();
    void stopSwipeRefreshing();
    void showContent(boolean isEnabled, boolean isConnected);
    void addToControllersList(String controllerResponse);
    Context getContext();
    String getControllerNameByMACAddress(String macAddress);
}
