package me.foxaice.smartlight.activities.controllers_screen.presenter;

import me.foxaice.smartlight.activities.controllers_screen.view.IControllerScreenView;

public interface IControllerScreenPresenter {
    void attachView(IControllerScreenView view);
    void detachView();
    void onChangeWifiState(boolean isEnabled, boolean isConnected);
}
