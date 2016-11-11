package me.foxaice.smartlight.fragments.controllers_screen.controller_management.presenter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import me.foxaice.controller_api.IAdminControllerApi;
import me.foxaice.smartlight.utils.Validator;

class ResponseReceiver implements Runnable {
    private final Object mLock = new Object();
    private String mResponseLinkWiFi;
    private String mResponsePort;
    private String mResponseMode;
    private String mResponseOK;
    private String mResponseAPKey;
    private String mError4;
    private String mResponseScanNetworks;
    private String mResponseSSID;
    private String mResponseSTAKey;
    private boolean isRun;
    private IAdminControllerApi mAdminControllerApi;
    private ExecutorService mExecutorService;

    ResponseReceiver(ExecutorService executorService, IAdminControllerApi adminControllerApi) {
        mAdminControllerApi = adminControllerApi;
        mExecutorService = executorService;
        mExecutorService.submit(this);
    }

    @Override
    public void run() {
        isRun = true;
        while (!mExecutorService.isShutdown() && isRun) {
            try {
                String response = mAdminControllerApi.receiveAdminMessage(mAdminControllerApi.getIpAddress());
                if (Validator.isCorrectResponseOnLinkWiFi(response)) {
                    synchronized (mLock) {
                        mResponseLinkWiFi = response;
                    }
                } else if (Validator.isCorrectResponseOnGetNETP(response)) {
                    synchronized (mLock) {
                        mResponsePort = response.split(",")[2];
                    }
                } else if (Validator.isCorrectResponseOnGetMode(response)) {
                    synchronized (mLock) {
                        mResponseMode = response.split("=")[1];
                    }
                } else if (Validator.isCorrectResponseOK(response)) {
                    synchronized (mLock) {
                        mResponseOK = response;
                    }
                } else if (Validator.isCorrectResponseOnGetKey(response)) {
                    synchronized (mLock) {
                        String temp = response.split("=")[1];
                        mResponseSTAKey = temp;
                        if (Validator.isCorrectResponseOnGetKeyAP(response)) {
                            mResponseAPKey = temp;
                        }
                    }
                } else if (Validator.isCorrectResponseOnScanNetworks(response)) {
                    synchronized (mLock) {
                        mResponseScanNetworks = response;
                    }
                } else if (Validator.isError4(response)) {
                    synchronized (mLock) {
                        mError4 = response;
                    }
                } else if (Validator.isResponse(response)) {
                    synchronized (mLock) {
                        mResponseSSID = response.split("=")[1];
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    boolean isError4Received() {
        synchronized (mLock) {
            return mError4 != null;
        }
    }

    boolean isSSIDReceived() {
        synchronized (mLock) {
            return mResponseSSID != null;
        }
    }

    boolean isScanNetworksReceived() {
        synchronized (mLock) {
            return mResponseScanNetworks != null;
        }
    }

    boolean isLinkWiFiReceived() {
        synchronized (mLock) {
            return mResponseLinkWiFi != null;
        }
    }

    boolean isAPKeyReceived() {
        synchronized (mLock) {
            return mResponseAPKey != null;
        }
    }

    boolean isSTAKeyReceived() {
        synchronized (mLock) {
            return mResponseSTAKey != null;
        }
    }

    boolean isNETPReceived() {
        synchronized (mLock) {
            return mResponsePort != null;
        }
    }


    boolean isModeReceived() {
        synchronized (mLock) {
            return mResponseMode != null;
        }
    }

    boolean isOKReceived() {
        synchronized (mLock) {
            return mResponseOK != null;
        }
    }

    String getError4() {
        synchronized (mLock) {
            String temp = mError4;
            mError4 = null;
            return temp;
        }
    }

    String getResponseSSID() {
        synchronized (mLock) {
            String temp = mResponseSSID;
            mResponseSSID = null;
            return temp;
        }
    }

    String getResponseScanNetworks() {
        synchronized (mLock) {
            String temp = mResponseScanNetworks;
            mResponseScanNetworks = null;
            return temp;
        }
    }

    String getResponseLinkWiFi() {
        synchronized (mLock) {
            String temp = mResponseLinkWiFi;
            mResponseLinkWiFi = null;
            return temp;
        }
    }

    String getResponseAPKey() {
        synchronized (mLock) {
            String temp = mResponseAPKey;
            mResponseAPKey = null;
            return temp;
        }
    }

    String getResponseSTAKey() {
        synchronized (mLock) {
            String temp = mResponseSTAKey;
            mResponseSTAKey = null;
            return temp;
        }
    }

    String getResponsePort() {
        synchronized (mLock) {
            String temp = mResponsePort;
            mResponsePort = null;
            return temp;
        }
    }

    String getResponseMode() {
        synchronized (mLock) {
            String temp = mResponseMode;
            mResponseMode = null;
            return temp;
        }
    }

    String getResponseOK() {
        synchronized (mLock) {
            String temp = mResponseOK;
            mResponseOK = null;
            return temp;
        }
    }

    void clearAllResponses() {
        synchronized (mLock) {
            mResponseLinkWiFi = null;
            mResponsePort = null;
            mResponseMode = null;
            mResponseOK = null;
            mResponseAPKey = null;
            mResponseScanNetworks = null;
            mResponseSSID = null;
            mError4 = null;
        }
    }
}