package me.foxaice.smartlight.activities.main_screen.presenter;

import me.foxaice.smartlight.activities.main_screen.view.IMainScreenView;

public interface IMainScreenPresenter {
    void attach(IMainScreenView view);
    void detach();
    void onTouchUpSettingsButton();
    void onDrawerItemClick(int itemPosition, String itemText);
    void onDrawerSlide();
    void updateBulbInfoNames();
    void saveCurrentModeTagToSettings();
    void setCurrentBulbGroup(int group);
    int getCurrentBulbGroup();
    String getBulbInfoKey();
    String getFragmentTagFromSettings();
    String[] getZonesNamesFromSettings(String[] names);
    Object getBulbInfoObject();
}
