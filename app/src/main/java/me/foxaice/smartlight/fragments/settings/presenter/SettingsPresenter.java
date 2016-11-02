package me.foxaice.smartlight.fragments.settings.presenter;

import me.foxaice.smartlight.fragments.settings.view.ISettingsView;

public class SettingsPresenter implements ISettingsPresenter {
    private ISettingsView mSettingsView;

    @Override
    public void attachView(ISettingsView view) {
        mSettingsView = view;
    }

    @Override
    public void detachView() {
        mSettingsView = null;
    }

    @Override
    public void onListViewItemClick(int position) {
        if (position == 0) {
            mSettingsView.startControllerSettingsScreenActivity();
        } else if (position == 1) {
            mSettingsView.startRedefinitionZonesScreenActivity();
        } else if (position == 2) {
            mSettingsView.startRenamingZonesScreenActivity();
        } else if (position == 3) {
            mSettingsView.showSettingIPDialog();
        } else if (position == 4) {
            mSettingsView.showSettingPortDialog();
        }
    }
}
