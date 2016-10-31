package me.foxaice.smartlight.activities.main_screen.presenter;

import me.foxaice.smartlight.activities.main_screen.view.IMainScreenView;

public interface IMainScreenPresenter {
    void attach(IMainScreenView view);
    void detach();
    void onDrawerItemClick(int itemPosition, String itemText);
    void onTouchUpSettingsButton();
    void onDrawerSlide();
    void updateBulbInfoNames();
    void saveCurrentModeTagToSettings();
    int getCurrentBulbGroup();
    void setCurrentBulbGroup(int group);
    String getBulbInfoKey();
    Object getBulbInfoObject();
    String[] getZonesNamesFromSettings(String[] names);
    String getFragmentTagFromSettings();
}
