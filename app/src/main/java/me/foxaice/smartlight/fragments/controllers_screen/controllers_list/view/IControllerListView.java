package me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view;

import android.content.Context;

public interface IControllerListView {
    void sendMessageStartSearch();
    void sendMessageStopSearch();
    void stopSwipeRefreshing();
    void addToControllersList(String controllerResponse);
    void showContent(boolean isEnabled, boolean isConnected);
    String getControllerNameByMACAddress(String macAddress);
    Context getContext();
}
