package me.foxaice.smartlight.activities.renaming_zones_screen.presenter;

import me.foxaice.smartlight.activities.renaming_zones_screen.view.IRenamingZonesScreenView;

public interface IRenamingZonesScreenPresenter {
    void attach(IRenamingZonesScreenView view);
    void detach();
    String[] getZonesNames();
    void saveZoneName(int zoneID, String name);
    String getZoneName(int zoneID);
}
