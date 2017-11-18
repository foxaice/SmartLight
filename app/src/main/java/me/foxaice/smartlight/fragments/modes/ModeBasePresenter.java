package me.foxaice.smartlight.fragments.modes;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.foxaice.controller_api.ControllerApi;
import me.foxaice.controller_api.IBulbControllerApi;
import me.foxaice.smartlight.activities.main_screen.model.BulbInfo;
import me.foxaice.smartlight.activities.main_screen.model.IBulbInfo;
import me.foxaice.smartlight.preferences.ISharedPreferencesController;
import me.foxaice.smartlight.preferences.SharedPreferencesController;

public abstract class ModeBasePresenter<T extends IModeBaseView> implements IModeBasePresenter<T> {
    protected T modeView;
    protected IBulbInfo bulbInfo;
    protected IBulbControllerApi bulbControllerApi;
    protected ISharedPreferencesController sharedPreferences;
    protected ExecutorService executorService;
    private String mIpAddress;
    private int mPort;
    private int mCurrentBulbGroupID;

    @Override
    public void attachView(T view) {
        modeView = view;
        bulbInfo = (IBulbInfo) this.modeView.loadParcelable(BulbInfo.KEY_BULB_INFO);
        mCurrentBulbGroupID = bulbInfo.getCurrentBulbGroup();
        if (sharedPreferences == null) {
            sharedPreferences = SharedPreferencesController.getInstance(this.modeView.getContext());
        }
        if (bulbControllerApi == null) {
            this.onLoadConnectionInfoFromSettings();
            bulbControllerApi = new ControllerApi(mIpAddress, mPort);
        }
        startExecutorService();
    }

    @Override
    public void detachView() {
        modeView = null;
    }

    @Override
    public void startExecutorService() {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
    }

    @Override
    public void stopExecutorService() {
        if (executorService != null) {
            bulbControllerApi.closeSockets();
            executorService.shutdown();
        }
        executorService = null;
    }

    @Override
    public void updateControllerSettings() {
        this.onLoadConnectionInfoFromSettings();
        bulbControllerApi.setIpAddress(mIpAddress);
        bulbControllerApi.setPort(mPort);
    }

    protected void turnBulbOn() throws IOException, InterruptedException {
        if (bulbInfo.getCurrentBulbGroup() != mCurrentBulbGroupID) {
            bulbInfo.setCurrentBulbGroupPowerOn(true);
            mCurrentBulbGroupID = bulbInfo.getCurrentBulbGroup();
            bulbControllerApi.powerOnGroup(bulbInfo.getCurrentBulbGroup());
            Thread.sleep(100L);
        }
    }

    protected void sendBrightnessCommand(int brightness) {
        executorService.submit(getRunnableChangeBrightnessTask(brightness));
    }

    protected void sendColorCommand(int color) {
        executorService.submit(getRunnableChangeColorTask(color));
    }

    protected void sendPowerCommand(boolean isPowerOn) {
        executorService.submit(getRunnablePowerTask(isPowerOn));
    }

    protected void sendWhiteColorCommand() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    turnBulbOn();
                    bulbControllerApi.setWhiteColorOfGroup(bulbInfo.getCurrentBulbGroup());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onLoadConnectionInfoFromSettings() {
        mIpAddress = sharedPreferences.getIpAddress();
        mPort = sharedPreferences.getPort();
    }

    private Runnable getRunnableChangeColorTask(final int color) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    turnBulbOn();
                    bulbControllerApi.setColorOfCurrentGroup(color);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Runnable getRunnableChangeBrightnessTask(final int brightness) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    turnBulbOn();
                    bulbControllerApi.setBrightnessOfCurrentGroup(brightness);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Runnable getRunnablePowerTask(final boolean powerOn) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    if (powerOn) {
                        bulbControllerApi.powerOnGroup(bulbInfo.getCurrentBulbGroup());
                    } else {
                        bulbControllerApi.powerOffGroup(bulbInfo.getCurrentBulbGroup());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
