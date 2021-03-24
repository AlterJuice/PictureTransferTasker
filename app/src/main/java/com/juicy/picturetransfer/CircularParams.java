package com.juicy.picturetransfer;


import android.util.Log;

import java.util.Arrays;

public class CircularParams{

    public static boolean useOnlyLineOrder = true;
    public static int circleRadius;
    public static int circleRadiusChild;
    public static int circlePadding;
    public static int circlePaddingAdditional;

    public static int circleMaxPoints = 60;

    public static final int MAX_CIRCLE_DEGREES = 360;
    public static final double startAngle = - Math.PI/2f;

    double centerX;
    double centerY;
    int angleFrom;
    int angleTo;
    int zIndex;

    public static void setupOptions(boolean useOnlyLineOrder, int circleRadius, int circleRadiusChild, int circlePadding, int circlePaddingAdditional){
        CircularParams.useOnlyLineOrder = useOnlyLineOrder;
        CircularParams.circleRadius = circleRadius;
        CircularParams.circleRadiusChild = circleRadiusChild;
        CircularParams.circlePadding = circlePadding;
        CircularParams.circlePaddingAdditional = circlePaddingAdditional;
    }

    public CircularParams(int zIndex, double centerX, double centerY, int angleFrom, int angleTo){
        this.zIndex = zIndex;
        this.centerX = centerX;
        this.centerY = centerY;

        this.angleFrom = angleFrom;
        this.angleTo = angleTo;
        Log.d("CircularParamsThis", this.toString());

    }

    public CircularParams(int zIndex, double[] centerXY, int[] angleFromTo){
        this(zIndex, centerXY[0], centerXY[1], angleFromTo[0], angleFromTo[1]);
    }


    // public void update(int zIndex, int angleFrom, int angleTo){ this.zIndex = zIndex;this.angleFrom = angleFrom;this.angleTo = angleTo; }

    @Override
    public String toString() {
        return "CircularParams{" +
                " zIndex=" + zIndex +
                " centerXY=" + Arrays.toString(getCenterXY()) +
                " endpointXY=" + Arrays.toString(getEndpointXY()) +
                // " degreesFromTo={" + angleFrom +", " + angleTo +"} "+
                " deltaXY="+ Arrays.toString(getDeltaXY()) +
                " endAngle="+getEndpointAngle() +
                " totalOffset=" + getTotalOffset(zIndex) +
                "}";
    }

    double getAnglesDifference(){ return calculateAnglesDifference(angleFrom, angleTo); }
    double getZIndexOffset(){ return calculateOffset(getAnglesDifference(), circleMaxPoints, zIndex); }
    double getEndpointAngle(){return calculateEndAngle(angleFrom, getZIndexOffset());
    }
    double getEndpointX(){ return calculateEndX(centerX, getEndpointAngle(), getTotalOffset(zIndex)); }
    double getEndpointY(){ return calculateEndY(centerY, getEndpointAngle(), getTotalOffset(zIndex)); }
    double getDeltaX(){ return getEndpointX() - centerX; }
    double getDeltaY(){ return getEndpointY() - centerY; }

    double[] getCenterXY(){ return new double[]{centerX, centerY};}
    double[] getEndpointXY() {return new double[]{getEndpointX(), getEndpointY()};}
    double[] getDeltaXY(){ return new double[]{getDeltaX(), getDeltaY()};}


    // =============== STATIC METHODS OF CALCULATING ===============
    public static int getTotalOffset(int zIndex){
        if (!useOnlyLineOrder)
            if (zIndex % 2 == 0)
                return circleRadius + circlePadding + circlePaddingAdditional + circleRadiusChild;
        return circleRadius + circlePadding + circleRadiusChild;
    }

    public static void setCircleMaxPoints(int newCircleMaxPoints){ circleMaxPoints = newCircleMaxPoints; }

    public static double calculateAnglesDifference(double angleFrom, double angleTo){
        if (angleFrom % MAX_CIRCLE_DEGREES == angleTo % MAX_CIRCLE_DEGREES)
            return MAX_CIRCLE_DEGREES;
        return ((MAX_CIRCLE_DEGREES - angleFrom + angleTo) % MAX_CIRCLE_DEGREES);
    }

    static double calculateOffset(double absoluteAngleDifference, int maxCountPoints, int zIndexPoint){
        if (zIndexPoint == 0)
            return Math.toDegrees(startAngle);
        return Math.toDegrees(startAngle+(zIndexPoint * ((2 * Math.PI) / maxCountPoints)));
        // return (absoluteAngleDifference / maxCountPoints) * zIndexPoint;
    }

    public static double calculateEndAngle(double angleFrom, double calculatedAngleOffset){
        return ((angleFrom + calculatedAngleOffset) % MAX_CIRCLE_DEGREES);
    }

    static double calculateEndX(double centerX, double endpointAngle, int totalRadius){
        return centerX + Math.cos(Math.toRadians(endpointAngle))*totalRadius;
    }
    static double calculateEndY(double centerY, double endpointAngle, int totalRadius){
        return centerY + Math.sin(Math.toRadians(endpointAngle))*totalRadius;
    }

}

