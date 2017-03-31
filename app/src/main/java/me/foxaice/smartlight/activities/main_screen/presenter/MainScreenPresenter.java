package me.foxaice.smartlight.activities.main_screen.presenter;

import me.foxaice.smartlight.activities.main_screen.model.BulbInfo;
import me.foxaice.smartlight.activities.main_screen.model.IBulbInfo;
import me.foxaice.smartlight.activities.main_screen.view.IMainScreenView;
import me.foxaice.smartlight.preferences.ISharedPreferencesController;
import me.foxaice.smartlight.preferences.SharedPreferencesController;

public class MainScreenPresenter implements IMainScreenPresenter {
    private IBulbInfo mBulbInfo;
    private IMainScreenView mView;
    private ISharedPreferencesController mSharedPreferences;

    @Override
    public void attach(IMainScreenView view) {
        mView = view;
        if (mBulbInfo == null) {
            mBulbInfo = new BulbInfo();
        }
        if (mSharedPreferences == null) {
            mSharedPreferences = SharedPreferencesController.getInstance(mView.getContext());
        }
    }

    @Override
    public void detach() {
        mView = null;
    }

    @Override
    public String[] getZonesNamesFromSettings(String[] names) {
        String[] namesFromSettings = new String[]{
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_1),
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_2),
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_3),
                mSharedPreferences.getGroupName(ISharedPreferencesController.NameZone.ZONE_4)
        };

        for (int i = 1, j = names.length; i < j; i++) {
            if (i > namesFromSettings.length) break;
            if (namesFromSettings[i - 1] != null) {
                names[i] = namesFromSettings[i - 1];
            }
        }
        mBulbInfo.setBulbNames(names);
        return names;
    }

    @Override
    public String getFragmentTagFromSettings() {
        if (mSharedPreferences != null) {
            return mSharedPreferences.getFragmentTag();
        } else return null;
    }

    @Override
    public void onTouchUpSettingsButton() {
        if (mView.isSettingsOpen()) {
            mView.hideSettingsFragment();
        } else {
            mView.showSettingsFragment();
        }
        mView.closeNavigationDrawer();
    }

    @Override
    public void onDrawerSlide() {
        if (!mView.isNavigationDrawerOpen() && mView.isSettingsOpen()) {
            mView.hideSettingsFragment();
        }
    }

    @Override
    public void updateBulbInfoNames() {
        String[] updatedNames = getZonesNamesFromSettings(mBulbInfo.getBulbGroupNames());
        mView.updateNavigationDrawerData(updatedNames);
    }

    @Override
    public void onDrawerItemClick(int position, String itemText) {
        mBulbInfo.setCurrentBulbGroup(position);
        mView.showMessageOfSelectedGroup(position == 0 ? null : itemText);
        mView.closeNavigationDrawer();
    }

    @Override
    public void saveCurrentModeTagToSettings() {
        mSharedPreferences.setFragmentTag(mView.getCurrentFragmentTag());
    }

    @Override
    public int getCurrentBulbGroup() {
        return mBulbInfo.getCurrentBulbGroup();
    }

    @Override
    public void setCurrentBulbGroup(int group) {
        mBulbInfo.setCurrentBulbGroup(group);
    }

    @Override
    public String getBulbInfoKey() {
        return IBulbInfo.KEY_BULB_INFO;
    }

    @Override
    public Object getBulbInfoObject() {
        return mBulbInfo;
    }
}
