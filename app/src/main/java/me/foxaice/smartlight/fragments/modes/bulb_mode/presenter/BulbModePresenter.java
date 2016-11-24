package me.foxaice.smartlight.fragments.modes.bulb_mode.presenter;

import me.foxaice.smartlight.activities.main_screen.model.IBulbInfo;
import me.foxaice.smartlight.fragments.modes.ModeBasePresenter;
import me.foxaice.smartlight.fragments.modes.bulb_mode.view.IBulbModeView;

public class BulbModePresenter extends ModeBasePresenter<IBulbModeView> implements IBulbModePresenter {
    private boolean mIsInRGBCircle = false;

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
    }

    @Override
    public void onTouchBrightnessArc(float coordX, float coordY, float arcTargetRadius, float arcRadius, @Events int action) {
    }

    private void setBrightnessByAngle(float angle) {
    }

    private void setColorByAngle(float angle) {
    }

    private float getReformedAngle(float angle) {
        return 0;
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

}
