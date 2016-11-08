package me.foxaice.smartlight.activities.controllers_screen.presenter;

import me.foxaice.smartlight.activities.controllers_screen.model.IWifiState;
import me.foxaice.smartlight.activities.controllers_screen.model.WifiState;
import me.foxaice.smartlight.activities.controllers_screen.view.IControllerScreenView;

public class ControllerScreenPresenter implements IControllerScreenPresenter {
    private IControllerScreenView mView;
    private IWifiState mWifiState;

    @Override
    public void attachView(IControllerScreenView view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void onChangeWifiState(boolean isEnabled, boolean isConnected) {
        if (mWifiState == null) {
            mWifiState = new WifiState();
        } else if (mWifiState.isWifiEnabled() == isEnabled && mWifiState.isWifiConnected() == isConnected) {
            return;
        }
        mWifiState.updateWifiState(isEnabled, isConnected);
        mView.showControllersListFragment(isEnabled, isConnected);
    }
}
