package me.foxaice.smartlight.fragments.modes.bulb_mode.presenter;

import android.util.DisplayMetrics;

import me.foxaice.smartlight.activities.main_screen.model.IBulbInfo;
import me.foxaice.smartlight.fragments.modes.ModeBasePresenter;
import me.foxaice.smartlight.fragments.modes.bulb_mode.model.BrightnessArcInfo;
import me.foxaice.smartlight.fragments.modes.bulb_mode.view.IBulbModeView;

public class BulbModePresenter extends ModeBasePresenter<IBulbModeView> implements IBulbModePresenter {
    private boolean mIsInRGBCircle = false;
    private BrightnessArcInfo mBrightnessArcInfo = new BrightnessArcInfo();

    @Override
    public void onTouchColorCircle(float coordX, float coordY, float circleTargetRadius, float circleRadius, @Events int action) {
        float deltaX = coordX - circleRadius;
        float deltaY = coordY - circleRadius;
        float angle = BulbModePresenter.getAngle(deltaX, deltaY);
        boolean isPointWithinRGBCircle = isPointWithinRGBCircle(coordX, coordY, circleTargetRadius, circleRadius);

        if (((action == Events.ACTION_DOWN) && isPointWithinRGBCircle) || (mIsInRGBCircle && (action == Events.ACTION_MOVE))) {
            mIsInRGBCircle = true;

            this.setColorByAngle(angle);

            float[] targetCoords = BulbModePresenter.getPointsForRGBTarget(deltaX, deltaY, circleTargetRadius, circleRadius);
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
        bulbInfo.setCurrentBulbGroupPowerOn(!bulbInfo.isCurrentBulbGroupOn());
        sendPowerCommand(bulbInfo.isCurrentBulbGroupOn());
        modeView.showBulbGroupStateMessage(bulbInfo.getCurrentBulbGroupName(), bulbInfo.isCurrentBulbGroupOn(), bulbInfo.getCurrentBulbGroup() == IBulbInfo.GroupID.ALL_GROUP);
    }

    @Override
    public void onTouchWhiteColorButton() {
        sendWhiteColorCommand();
    }

    @Override
    public void onTouchBrightnessArc(float coordX, float coordY, float arcTargetRadius, float arcRadius, @Events int action) {
        float deltaX = coordX - arcRadius;
        float deltaY = coordY - arcRadius;
        float angle = BulbModePresenter.getAngle(deltaX, deltaY);
        boolean isPointWithinArc = BulbModePresenter.isPointWithinArc(deltaX, deltaY, arcTargetRadius, arcRadius);

        mBrightnessArcInfo.updateBrightnessArcInfo(action, deltaX, deltaY);

        if (((action == Events.ACTION_DOWN) && isPointWithinArc) || (action == Events.ACTION_MOVE && !mBrightnessArcInfo.isProhibition() && mBrightnessArcInfo.isWithinArc())) {
            mBrightnessArcInfo.setWithinArc();
            if (BulbModePresenter.checkAngleOutOfRange(angle)) {
                if (angle < 142f && angle > 90) {
                    angle = 142f;
                } else {
                    angle = 38f;
                }
            }
            float[] targetCoords = getCoordsForBrightnessTarget(angle, arcTargetRadius, arcRadius);
            modeView.setBrightnessArcTargetCoords(targetCoords[0], targetCoords[1]);
            angle = BulbModePresenter.getReformedAngle(angle);
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

    private float[] getCoordsForBrightnessTarget(float angle, float arcTargetRadius, float arcRadius) {
        double radians = Math.toRadians(angle);
        double cosine = Math.cos(radians);
        double sine = Math.sin(radians);
        float x, y;
        int screenDpi = modeView.getScreenDpi();
        if (screenDpi == DisplayMetrics.DENSITY_LOW) {
            if (angle >= 0 && angle <= 180) {
                x = (float) ((arcRadius - arcTargetRadius * 1.2f) * cosine) + arcRadius - arcTargetRadius * 1.07f;
                y = (float) ((arcRadius - arcTargetRadius * 1.3f) * sine) + arcRadius + arcTargetRadius * 0.3f;
            } else {
                x = (float) ((arcRadius - arcTargetRadius * 1.21f) * cosine) + arcRadius - arcTargetRadius * 1.08f;
                y = (float) ((arcRadius - arcTargetRadius * 1.2f) * sine) + arcRadius + arcTargetRadius * 0.3f;
            }
        } else if (screenDpi == DisplayMetrics.DENSITY_MEDIUM) {
            if (angle >= 0 && angle <= 180) {
                x = (float) ((arcRadius - arcTargetRadius * 1.23f) * cosine) + arcRadius - arcTargetRadius * 1.05f;
                y = (float) ((arcRadius - arcTargetRadius * 1.23f) * sine) + arcRadius + arcTargetRadius * 0.3f;
            } else {
                x = (float) ((arcRadius - arcTargetRadius * 1.23f) * cosine) + arcRadius - arcTargetRadius * 1.06f;
                y = (float) ((arcRadius - arcTargetRadius * 1.1f) * sine) + arcRadius + arcTargetRadius * 0.45f;
            }
        } else if (screenDpi == DisplayMetrics.DENSITY_HIGH) {
            if (angle >= 0 && angle <= 180) {
                x = (float) ((arcRadius - arcTargetRadius * 1.18f) * cosine) + arcRadius - arcTargetRadius * .99f;
                y = (float) ((arcRadius - arcTargetRadius * 1.15f) * sine) + arcRadius + arcTargetRadius * 0.4f;
            } else {
                x = (float) ((arcRadius - arcTargetRadius * 1.16f) * cosine) + arcRadius - arcTargetRadius * .99f;
                y = (float) ((arcRadius - arcTargetRadius * 1.15f) * sine) + arcRadius + arcTargetRadius * 0.4f;
            }
        } else if (screenDpi == DisplayMetrics.DENSITY_XHIGH) {
            if (angle >= 0 && angle <= 180) {
                x = (float) ((arcRadius - arcTargetRadius * 1.18f) * cosine) + arcRadius - arcTargetRadius * .99f;
                y = (float) ((arcRadius - arcTargetRadius * 1.3f) * sine) + arcRadius + arcTargetRadius * 0.4f;
            } else {
                x = (float) ((arcRadius - arcTargetRadius * 1.18f) * cosine) + arcRadius - arcTargetRadius * .99f;
                y = (float) ((arcRadius - arcTargetRadius * 1.15f) * sine) + arcRadius + arcTargetRadius * 0.45f;
            }

        } else {
            if (angle >= 0 && angle <= 180) {
                x = (float) ((arcRadius - arcTargetRadius * 1.2f) * cosine) + arcRadius - arcTargetRadius * 0.98f;
                y = (float) ((arcRadius - arcTargetRadius * 1.25f) * sine) + arcRadius + arcTargetRadius * 0.45f;
            } else {
                x = (float) ((arcRadius - arcTargetRadius * 1.2f) * cosine) + arcRadius - arcTargetRadius;
                y = (float) ((arcRadius - arcTargetRadius * 1.2f) * sine) + arcRadius + arcTargetRadius * 0.4f;
            }
        }
        return new float[]{x, y};
    }

    private static float[] getPointsForRGBTarget(float deltaX, float deltaY, float circleTargetRadius, float circleRadius) {
        double hypotenuse = Math.hypot(deltaX, deltaY);
        double cosine = deltaX / hypotenuse;
        double sine = deltaY / hypotenuse;
        float x = (float) (circleRadius + (circleRadius - circleTargetRadius) * cosine) - circleTargetRadius;
        float y = (float) (circleRadius + (circleRadius - circleTargetRadius) * sine) - circleTargetRadius;
        return new float[]{x, y};
    }

    private static boolean checkAngleOutOfRange(float angle) {
        return angle >= 38f && angle <= 142f;
    }

    private static boolean isPointWithinArc(float deltaX, float deltaY, float arcTargetRadius, float arcRadius) {
        boolean inner = deltaX * deltaX + deltaY * deltaY >= (arcRadius - arcTargetRadius * 2) * (arcRadius - arcTargetRadius * 2);
        boolean outer = deltaX * deltaX + deltaY * deltaY <= arcRadius * arcRadius;
        boolean outsideArc = deltaX > -120 && deltaY > 119 && deltaX < 120;
        return inner && outer && !outsideArc;
    }

    private static float getAngle(float x, float y) {
        float deg = 0;
        if (x != 0) deg = y / x;
        deg = (float) Math.toDegrees(Math.atan(deg));
        if (x < 0) deg += 180;
        else if (x > 0 && y < 0) deg += 360;
        return deg;
    }

    private static float getReformedAngle(float angle) {
        float reformedAngle = 0;
        if (angle >= 142f && angle <= 360) {
            reformedAngle = angle - 142f;
        } else if (angle >= 0 && angle <= 38f) {
            reformedAngle = angle + 218f;
        }
        return reformedAngle;
    }
}
