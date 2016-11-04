package me.foxaice.smartlight.activities.redefinition_zones_screen.presenter;

import me.foxaice.smartlight.activities.redefinition_zones_screen.view.IRedefinitionZonesScreenView;

public interface IRedefinitionZonesScreenPresenter {
    void attach(IRedefinitionZonesScreenView view);
    void detach();
    String[] getZonesNames();
    void onListViewItemClick(int position);
    void stopExecutorService();
}
