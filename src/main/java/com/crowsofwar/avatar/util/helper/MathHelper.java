package com.crowsofwar.avatar.util.helper;

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

    public static float lerp(final float a, final float b, final float t) {
        return t <= 0.0F ? a : t >= 1.0F ? b : a + (b - a) * t;
    }

    public static float lerpDegrees(final float a, final float b, final float t) {
        return a + t * getDifference(a, b, 360.0F);
    }

    public static double lerpDegrees(final double a, final double b, final double t) {
        return a + t * getDifference(a, b, 360.0D);
    }

    private static float getDifference(final float a, final float b, final float rot) {
        return mod(b - a + rot / 2.0F, rot) - rot / 2.0F;
    }

    private static double getDifference(final double a, final double b, final double rot) {
        return mod(b - a + rot / 2.0D, rot) - rot / 2.0D;
    }


    public static int mod(final int a, final int b) {
        return (a % b + b) % b;
    }

    public static float mod(final float a, final float b) {
        return (a % b + b) % b;
    }

    public static double mod(final double a, final double b) {
        return (a % b + b) % b;
    }

}
