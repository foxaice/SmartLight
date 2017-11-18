package me.foxaice.smartlight.activities.main_screen.view;

import android.content.Context;

public interface IMainScreenView {
    void updateNavigationDrawerData(String[] data);
    void hideSettingsFragment();
    void showSettingsFragment();
    void showBulbModeFragment();
    void showMusicModeFragment();
    void showDiscoModeFragment();
    void showMessageOfSelectedGroup(String message);
    void closeNavigationDrawer();
    boolean isNavigationDrawerOpen();
    boolean isSettingsOpen();
    String getCurrentFragmentTag();
    Context getContext();
}
