package me.foxaice.smartlight.fragments.modes.bulb_mode.presenter;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.foxaice.smartlight.activities.main_screen.model.IBulbInfo;
import me.foxaice.smartlight.fragments.modes.ModeBasePresenter;
import me.foxaice.smartlight.fragments.modes.bulb_mode.view.IBulbModeView;

public class BulbModePresenter extends ModeBasePresenter<IBulbModeView> implements IBulbModePresenter {
    private boolean mIsInRGBCircle = false;
    private BrightnessArcInfo mBrightnessArcInfo = new BrightnessArcInfo();

    @Override
    public void onTouchColorCircle(float coordX, float coordY, float circleTargetRadius, float circleRadius, @Events int action) {
        float deltaX = coordX - circleRadius;
        float deltaY = coordY - circleRadius;
        float angle = getAngle(deltaX, deltaY);
        boolean isPointWithinRGBCircle = isPointWithinRGBCircle(coordX, coordY, circleTargetRadius, circleRadius);

        if (((action == Events.ACTION_DOWN) && isPointWithinRGBCircle) || (mIsInRGBCircle && (action == Events.ACTION_MOVE))) {
            mIsInRGBCircle = true;

            this.setColorByAngle(angle);

            float[] targetCoords = getPointsForRGBTarget(deltaX, deltaY, circleTargetRadius, circleRadius);
            modeView.setColorCircleTargetCoords(targetCoords[0], targetCoords[1]);
        }

        if (action == Events.ACTION_UP) {
            mIsInRGBCircle = false;
        }
    }

    @Override
    public boolean isPointWithinRGBCircle(float coordX, float coordY, float circleTargetRadius, float circleRadius) {
        float deltaX = coordX - circleRadius;
        float deltaY = coordY - circleRadius;
        boolean inner = (deltaX * deltaX + deltaY * deltaY) >= (circleRadius - circleTargetRadius * 2) * (circleRadius - circleTargetRadius * 2);
        boolean outer = (deltaX * deltaX + deltaY * deltaY) <= circleRadius * circleRadius;
        return inner && outer;
    }

    @Override
    public void onTouchPowerButton() {
        bulbInfo.setCurrentBulbGroupState(!bulbInfo.isCurrentBulbGroupOn());
        sendPowerCommand(bulbInfo.isCurrentBulbGroupOn());
        modeView.showBulbGroupStateMessage(bulbInfo.getCurrentBulbGroupName(), bulbInfo.isCurrentBulbGroupOn(), bulbInfo.getCurrentBulbGroup() == IBulbInfo.ALL_GROUP);
    }

    @Override
    public void onTouchWhiteColorButton() {
        sendWhiteColorCommand();
    }

    @Override
    public void onTouchBrightnessArc(float coordX, float coordY, float arcTargetRadius, float arcRadius, @Events int action) {
        float deltaX = coordX - arcRadius;
        float deltaY = coordY - arcRadius;
        float angle = getAngle(deltaX, deltaY);
        boolean isPointWithinArc = isPointWithinArc(deltaX, deltaY, arcTargetRadius, arcRadius);

        mBrightnessArcInfo.updateBrightnessArcInfo(action, deltaX, deltaY);

        if (((action == Events.ACTION_DOWN) && isPointWithinArc) || (action == Events.ACTION_MOVE && !mBrightnessArcInfo.isProhibition && mBrightnessArcInfo.isWithinArc)) {
            mBrightnessArcInfo.isWithinArc = true;
            Log.d("COORDS", String.format("deltaX: %f, deltaY: %f, angle: %f", deltaX, deltaY, angle));
            if (checkAngleOutOfRange(angle)) {
                if (angle < 142f && angle > 90) {
                    angle = 142f;
                } else {
                    angle = 38f;
                }
            }
            float[] targetCoords = getCoordsForBrightnessTarget(angle, arcTargetRadius, arcRadius);
            modeView.setBrightnessArcTargetCoords(targetCoords[0], targetCoords[1]);
            angle = getReformedAngle(angle);
            this.setBrightnessByAngle(angle);
            modeView.drawBrightnessArcBackground(angle);
        }

        if (action == Events.ACTION_UP) {
            mBrightnessArcInfo.clearInfo();
        }
    }

    private void setBrightnessByAngle(float angle) {
        float index = 256.4818f;
        int brightness = (int) (angle / index * 100);
        sendBrightnessCommand(brightness);
    }

    private void setColorByAngle(float angle) {
        float index = 1.40625f;
        int color = (int) (angle / index);
        sendColorCommand(color);
    }

    private float getReformedAngle(float angle) {
        float reformedAngle = 0;
        if (angle >= 142f && angle <= 360) {
            reformedAngle = angle - 142f;
        } else if (angle >= 0 && angle <= 38f) {
            reformedAngle = angle + 218f;
        }
        return reformedAngle;
    }

    private float[] getCoordsForBrightnessTarget(float angle, float arcTargetRadius, float arcRadius) {
        return null;
    }

    private float[] getPointsForRGBTarget(float deltaX, float deltaY, float circleTargetRadius, float circleRadius) {
        return null;
    }

    private boolean checkAngleOutOfRange(float angle) {
        return false;
    }

    private boolean isPointWithinArc(float deltaX, float deltaY, float arcTargetRadius, float arcRadius) {
        return false;
    }

    private float getAngle(float x, float y) {
        return 0;
    }

    @IntDef({Quarter.LEFT_TOP, Quarter.LEFT_BOTTOM, Quarter.RIGHT_TOP, Quarter.RIGHT_BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    @interface Quarter {
        int LEFT_TOP = 1;
        int LEFT_BOTTOM = 2;
        int RIGHT_TOP = 3;
        int RIGHT_BOTTOM = 4;
    }

    private static final class BrightnessArcInfo {
        private boolean isWithinArc;
        private boolean isProhibition;
        @Quarter
        private Integer prevQuarter;
        @Quarter
        private Integer curQuarter;

        @Quarter
        private int getQuarter(float x, float y) {
            if (x >= 0 && y >= 0) return Quarter.RIGHT_BOTTOM;
            else if (x >= 0 && y < 0) return Quarter.RIGHT_TOP;
            else if (x < 0 && y >= 0) return Quarter.LEFT_BOTTOM;
            else return Quarter.LEFT_TOP;
        }

        private void clearInfo() {
            isWithinArc = false;
            isProhibition = false;
            prevQuarter = null;
            curQuarter = null;
        }

        private void updateBrightnessArcInfo(@Events int eventAction, float deltaX, float deltaY) {
            if (eventAction == Events.ACTION_DOWN) {
                this.prevQuarter = this.getQuarter(deltaX, deltaY);
                this.isProhibition = false;
            }
            if (eventAction == Events.ACTION_MOVE) {
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
    }
}
