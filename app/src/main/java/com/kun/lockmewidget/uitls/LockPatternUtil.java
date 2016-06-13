package com.kun.lockmewidget.uitls;

import com.kun.lockmewidget.view.LockPatternView;

/**
 * Created by Kun on 16-6-12.
 */
public class LockPatternUtil {
    public static boolean isInCircle(LockPatternView.Point point, int mRadius, float x, float y) {
        return Math.pow(point.getX() - x, 2) + Math.pow(point.getY() - y, 2) < Math.pow(mRadius, 2);
    }

    public static float getDistanceBetweenTwoPoints(float fpX, float fpY, float spX, float spY) {
        return (float) Math.sqrt((spX - fpX) * (spX - fpX) + (spY - fpY) * (spY - fpY));
    }

    public static float getAngleLineIntersectX(float fpX, float fpY, float spX, float spY, float distance) {
        return (float) Math.toDegrees(Math.acos((spX - fpX) / distance));
    }

    public static float getAngleLineIntersectY(float fpX, float fpY, float spX, float spY, float distance) {
        return (float) Math.toDegrees(Math.acos((spY - fpY) / distance));
    }
}
