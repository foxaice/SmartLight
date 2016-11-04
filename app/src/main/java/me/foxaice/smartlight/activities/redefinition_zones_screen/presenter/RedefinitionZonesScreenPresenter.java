package me.foxaice.smartlight.activities.redefinition_zones_screen.presenter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.foxaice.controller_api.ControllerApi;
import me.foxaice.controller_api.IBulbControllerApi;
import me.foxaice.smartlight.activities.redefinition_zones_screen.view.IRedefinitionZonesScreenView;
import me.foxaice.smartlight.preferences.ISharedPreferencesController;
import me.foxaice.smartlight.preferences.SharedPreferencesController;

public class RedefinitionZonesScreenPresenter implements IRedefinitionZonesScreenPresenter {
    private IRedefinitionZonesScreenView mView;
    private ISharedPreferencesController mSharedPreferences;
    private IBulbControllerApi mControllerApi;
    private ExecutorService mExecutorService;

    @Override
    public void attach(IRedefinitionZonesScreenView view) {
        mView = view;
        if (mSharedPreferences == null) {
            mSharedPreferences = SharedPreferencesController.getInstance(mView.getContext());
        }
        if (mControllerApi == null) {
            mControllerApi = new ControllerApi(mSharedPreferences.getIpAddress(), mSharedPreferences.getPort());
        }
        if (mExecutorService == null) {
            mExecutorService = Executors.newCachedThreadPool();
        }
    }

    @Override
    public void detach() {
        mView = null;
    }

    @Override
    public void stopExecutorService() {
        if (mExecutorService != null) {
            mExecutorService.shutdown();
            mExecutorService = null;
        }
    }

    @Override
    public String[] getZonesNames() {
        return new String[]{
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_1),
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_2),
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_3),
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_4)
        };
    }

    @Override
    public void onListViewItemClick(final int position) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mControllerApi.setWhiteColorOfGroup(position);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
