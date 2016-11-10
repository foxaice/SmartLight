package me.foxaice.smartlight.fragments.controllers_screen.controllers_list.presenter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.foxaice.controller_api.ControllerApi;
import me.foxaice.controller_api.IAdminControllerApi;
import me.foxaice.smartlight.activities.controllers_screen.model.IWifiState;
import me.foxaice.smartlight.activities.controllers_screen.model.WifiState;
import me.foxaice.smartlight.fragments.controllers_screen.controllers_list.view.IControllerListView;
import me.foxaice.smartlight.preferences.ISharedPreferencesController;
import me.foxaice.smartlight.preferences.SharedPreferencesController;
import me.foxaice.smartlight.utils.Validator;

public class ControllerListPresenter implements IControllerListPresenter {
    private static final int THREAD_QUANTITY = 2;
    private IControllerListView mView;
    private ISharedPreferencesController mSharedPreferencesController;
    private ExecutorService mExecutorService;
    private IWifiState mWifiState;
    private IAdminControllerApi mAdminControllerApi;
    private String mIPAddress;
    private boolean mIsSearching;

    @Override
    public void attach(IControllerListView view) {
        mView = view;
        if (mSharedPreferencesController == null) {
            mSharedPreferencesController = SharedPreferencesController.getInstance(mView.getContext());
        }
        if (mAdminControllerApi == null) {
            mIPAddress = mSharedPreferencesController.getIpAddress();
            int port = mSharedPreferencesController.getPort();
            mAdminControllerApi = new ControllerApi(mIPAddress, port);
        }
    }

    @Override
    public void detach() {
        mView = null;
    }

    @Override
    public void onUpdateWifiState(boolean isEnabled, boolean isConnected) {
        if (mWifiState == null) {
            mWifiState = new WifiState();
        } else if (mWifiState.isWifiEnabled() == isEnabled && mWifiState.isWifiConnected() == isConnected) {
            return;
        }
        mWifiState.updateWifiState(isEnabled, isConnected);
        mView.showContent(isEnabled, isConnected);
        if (isEnabled && isConnected) {
            mView.sendMessageStartSearch();
        } else {
            mView.sendMessageStopSearch();
        }
    }

    @Override
    public void startSearch() {
        if (!mIsSearching) {
            if (mExecutorService == null) {
                mExecutorService = Executors.newFixedThreadPool(THREAD_QUANTITY);
            }
            mExecutorService.submit(new ReceiverTaskRunnable());
            mExecutorService.submit(new SenderTaskRunnable());
            mIsSearching = true;
        }
    }

    @Override
    public void stopSearch() {
        if (mIsSearching) {
            if (mExecutorService != null) {
                mAdminControllerApi.closeSockets();
                mExecutorService.shutdown();
                mExecutorService = null;
            }
            mIsSearching = false;
        }
    }

    @Override
    public String getControllerNameByMACAddress(String macAddress) {
        return mSharedPreferencesController.getNameDevice(macAddress);
    }

    private class ReceiverTaskRunnable implements Runnable {
        @Override
        public void run() {
            boolean firstResponse = true;
            while (!mExecutorService.isShutdown()) {
                try {
                    String response = mAdminControllerApi.receiveAdminMessage(mIPAddress);
                    if (Validator.isCorrectResponseOnLinkWiFi(response)) {
                        mView.addToControllersList(response);
                        if (firstResponse) {
                            firstResponse = false;
                            mView.stopSwipeRefreshing();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SenderTaskRunnable implements Runnable {
        @Override
        public void run() {
            while (!mExecutorService.isShutdown()) {
                try {
                    mAdminControllerApi.sendCommandLinkWiFi();
                    Thread.sleep(200);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
