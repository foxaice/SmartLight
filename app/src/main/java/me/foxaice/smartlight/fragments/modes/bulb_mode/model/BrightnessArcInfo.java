package me.foxaice.smartlight.fragments.modes.bulb_mode.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.foxaice.smartlight.fragments.modes.IModeBasePresenter;

public final class BrightnessArcInfo {
    @IntDef({Quarter.LEFT_TOP, Quarter.LEFT_BOTTOM, Quarter.RIGHT_TOP, Quarter.RIGHT_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    @interface Quarter {
        int LEFT_TOP = 1;
        int LEFT_BOTTOM = 2;
        int RIGHT_TOP = 3;
        int RIGHT_BOTTOM = 4;
    }

    private boolean isWithinArc;
    private boolean isProhibition;
    @Quarter
    private Integer prevQuarter;
    @Quarter
    private Integer curQuarter;

    public void setWithinArc() {
        isWithinArc = true;
    }

    public boolean isWithinArc() {
        return isWithinArc;
    }

    public boolean isProhibition() {
        return isProhibition;
    }

    public void updateBrightnessArcInfo(@IModeBasePresenter.Events int eventAction, float deltaX, float deltaY) {
        if (eventAction == IModeBasePresenter.Events.ACTION_DOWN) {
            this.prevQuarter = this.getQuarter(deltaX, deltaY);
            this.isProhibition = false;
        }
        if (eventAction == IModeBasePresenter.Events.ACTION_MOVE) {
            this.curQuarter = this.getQuarter(deltaX, deltaY);
            if (this.prevQuarter != null) {
                @Quarter int preQ = this.prevQuarter;
                @Quarter int curQ = this.curQuarter;
                if (this.isProhibition) {
                    if ((preQ == Quarter.LEFT_BOTTOM && curQ != Quarter.RIGHT_BOTTOM)
                            || (preQ == Quarter.RIGHT_BOTTOM && curQ != Quarter.LEFT_BOTTOM)) {
                        this.isProhibition = false;
                    }
                } else {
                    if ((preQ == Quarter.LEFT_BOTTOM && curQ == Quarter.RIGHT_BOTTOM)
                            || (preQ == Quarter.RIGHT_BOTTOM && curQ == Quarter.LEFT_BOTTOM)) {
                        this.isProhibition = true;
                    }
                }
                if (!this.isProhibition && curQ != preQ) {
                    this.prevQuarter = curQ;
                }
            }
        }
    }

    public void clearInfo() {
        isWithinArc = false;
        isProhibition = false;
        prevQuarter = null;
        curQuarter = null;
    }

    @Quarter
    private int getQuarter(float x, float y) {
        if (x >= 0 && y >= 0) return Quarter.RIGHT_BOTTOM;
        else if (x >= 0 && y < 0) return Quarter.RIGHT_TOP;
        else if (x < 0 && y >= 0) return Quarter.LEFT_BOTTOM;
        else return Quarter.LEFT_TOP;
    }
}
