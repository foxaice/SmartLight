package me.foxaice.smartlight.fragments.modes.disco_mode.presenter;

import java.io.IOException;

import me.foxaice.smartlight.fragments.modes.IModeBasePresenter;
import me.foxaice.smartlight.fragments.modes.ModeBasePresenter;
import me.foxaice.smartlight.fragments.modes.disco_mode.view.IDiscoModeView;

public class DiscoModePresenter extends ModeBasePresenter<IDiscoModeView> implements IDiscoModePresenter {

    @Override
    public void onTouchSpeedUpButton(@Events int eventAction) {

    }

    @Override
    public void onTouchSpeedDownButton(@Events int eventAction) {

    }

    @Override
    public void onTouchNextModeButton(@Events int eventAction) {

    }

    private Runnable getRunnableSpeedUpTask() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    turnBulbOn();
                    bulbControllerApi.speedUpDiscoModeOfCurrentGroup();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Runnable getRunnableSpeedDownTask() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    turnBulbOn();
                    bulbControllerApi.speedDownDiscoModeOfCurrentGroup();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Runnable getRunnableNextModeTask() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    turnBulbOn();
                    bulbControllerApi.toggleDiscoModeOfCurrentGroup();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
