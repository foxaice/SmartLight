package me.foxaice.smartlight.activities.renaming_zones_screen.presenter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.foxaice.controller_api.ControllerApi;
import me.foxaice.controller_api.IBulbControllerApi;
import me.foxaice.smartlight.activities.renaming_zones_screen.view.IRenamingZonesScreenView;
import me.foxaice.smartlight.preferences.ISharedPreferencesController;
import me.foxaice.smartlight.preferences.SharedPreferencesController;

public class RenamingZonesScreenPresenter implements IRenamingZonesScreenPresenter {
    private IRenamingZonesScreenView mView;
    private ISharedPreferencesController mSharedPreferences;
    private IBulbControllerApi mControllerApi;
    private ExecutorService mExecutorService;

    @Override
    public void attach(IRenamingZonesScreenView view) {
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
    public String[] getZonesNames() {
        return new String[]{
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_1),
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_2),
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_3),
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_4)
        };
    }

    @Override
    public void saveZoneName(int zoneID, String name) {
        String zone = null;
        switch (zoneID) {
            case 0:
                zone = ISharedPreferencesController.NameZone.ZONE_1;
                break;
            case 1:
                zone = ISharedPreferencesController.NameZone.ZONE_2;
                break;
            case 2:
                zone = ISharedPreferencesController.NameZone.ZONE_3;
                break;
            case 3:
                zone = ISharedPreferencesController.NameZone.ZONE_4;
                break;
        }
        if (zone != null) {
            mSharedPreferences.setGroupName(zone, name);
        }
    }

    @Override
    public String getZoneName(int zoneID) {
        String zone = null;
        switch (zoneID) {
            case 0:
                zone = ISharedPreferencesController.NameZone.ZONE_1;
                break;
            case 1:
                zone = ISharedPreferencesController.NameZone.ZONE_2;
                break;
            case 2:
                zone = ISharedPreferencesController.NameZone.ZONE_3;
                break;
            case 3:
                zone = ISharedPreferencesController.NameZone.ZONE_4;
                break;
        }
        if (zone != null) {
            return mSharedPreferences.getGroupName(zone);
        }
        return null;
    }
}
