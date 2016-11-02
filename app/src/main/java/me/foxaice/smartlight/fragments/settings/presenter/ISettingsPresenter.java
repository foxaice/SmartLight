package me.foxaice.smartlight.fragments.settings.presenter;

import me.foxaice.smartlight.fragments.settings.view.ISettingsView;

public interface ISettingsPresenter {
    void attachView(ISettingsView view);
    void detachView();
    void onListViewItemClick(int position);
}
