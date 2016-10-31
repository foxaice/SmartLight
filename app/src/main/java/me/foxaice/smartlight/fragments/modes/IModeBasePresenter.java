package me.foxaice.smartlight.fragments.modes;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static me.foxaice.firstSaw.fragments.modes.IModeBasePresenter.Events.ACTION_DOWN;
import static me.foxaice.firstSaw.fragments.modes.IModeBasePresenter.Events.ACTION_MOVE;
import static me.foxaice.firstSaw.fragments.modes.IModeBasePresenter.Events.ACTION_UP;

public interface IModeBasePresenter<T extends IModeBaseView> {

    void attachView(T view);
    void detachView();
    void updateControllerSettings();
    void startExecutorService();
    void stopExecutorService();
    @IntDef({ACTION_DOWN, ACTION_UP, ACTION_MOVE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Events {
        int ACTION_DOWN = 0;
        int ACTION_UP = 1;
        int ACTION_MOVE = 2;
    }
}
