package com.crowsofwar.avatar.common.helper;

public class MathHelper {

    public static float toRadians(final float degrees) {
        return degrees * (float) (Math.PI / 180.0D);
    }
    public static float transform(final float x, final float domainMin, final float domainMax, final float rangeMin, final float rangeMax) {
        if (x <= domainMin) {
            return rangeMin;
        }
        if (x >= domainMax) {
            return rangeMax;
        }
        return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
    }
}
