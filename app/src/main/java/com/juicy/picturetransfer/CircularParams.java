package com.juicy.picturetransfer;


import android.icu.math.MathContext;
import android.util.Log;

import androidx.core.math.MathUtils;

import java.util.Arrays;

public class CircularParams{

    public static boolean useOnlyLineOrder = true;
    public static int circleRadius;
    public static int circleRadiusChild;
    public static int circlePadding;
    public static int circlePaddingAdditional;
    public static int MIN_HEIGHT;
    public static int MAX_HEIGHT;
    public static int MIN_WIDTH;
    public static int MAX_WIDTH;

    public static int circleMaxPoints = 60;

    public static final int MAX_CIRCLE_DEGREES = 360;
    public static final double startAngle = - Math.PI*2;
    public static int circleCountPoints = 0;

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
    public static void setupMinMaxParams(int minHeight, int minWidth, int maxHeight, int maxWidth){
        CircularParams.MIN_HEIGHT = minHeight;
        CircularParams.MIN_WIDTH = minWidth;
        CircularParams.MAX_HEIGHT = maxHeight;
        CircularParams.MAX_WIDTH = maxWidth;
    }

    public static boolean circleIsOutOfBounds(double centerX, double centerY, double radius){
        return centerX-radius < MIN_WIDTH || centerX+radius > MAX_WIDTH || centerY-radius < MIN_HEIGHT || centerY+radius > MAX_HEIGHT;
    }

//    public static int[] getAllowedAngles(){
//        int[] allowedDegrees = new int[]{0, 360};
//        if(!circleIsOutOfBounds)
//            return allowedDegrees;
//
//    }

    public static double getAngleOutOfBoundsSegment(double radius, double sideOutOfBounds){
        return Math.toDegrees(2 * Math.acos(1-sideOutOfBounds/radius));
    }

    public static int[] calculateAllowedAngles(double centerX, double centerY, double radius){
        Log.d("CenterX", centerX+"");
        Log.d("CenterY", centerY+"");
        Log.d("Radius", radius+"");
        Log.d("+++++++++++++++", "+++++");
        int[] allowedDegrees = {0, 360};
        Positions positions = new Positions();
        int[] sideDegrees;
        double rightAngle = 0;
        double topAngle = 0;
        double bottomAngle = 0;
        double leftAngle = 0;
        if (centerX-radius < MIN_WIDTH) {
            leftAngle = getAngleOutOfBoundsSegment(radius, MIN_WIDTH-(centerX-radius));
            // Left side is out of bounds (from 3P/2 to P/2)
            // sideDegrees = calculateAngleOfSegment(270, );
            // allowedDegrees[0] = sideDegrees[0];
            // allowedDegrees[1] = sideDegrees[1];
            positions.add(Positions.posStart);

        }else if (centerX+radius > MAX_WIDTH) {
            rightAngle = getAngleOutOfBoundsSegment(radius, (centerX+radius)-MAX_WIDTH);
            // Right side is out of bounds (from P/2 to 3P/2)
            // sideDegrees = calculateAngleOfSegment(90, );
            //
            // allowedDegrees[0] = sideDegrees[0];
            // allowedDegrees[1] = sideDegrees[1];
            positions.add(Positions.posEnd);
        }


        if (centerY-radius < MIN_HEIGHT) {
            topAngle = getAngleOutOfBoundsSegment(radius, MIN_HEIGHT-(centerY-radius));
            // Top side is out of bounds (from 2P to P)
            // sideDegrees = calculateAngleOfSegment(0,);
            //
            // if (positions.hasFlag(Positions.posStart))
            //     //
            // else if (positions.hasFlag(Positions.posEnd))
            //     //
            positions.add(Positions.posTop);
        }else if (centerY+radius > MAX_HEIGHT) {
            bottomAngle = getAngleOutOfBoundsSegment(radius, (centerY+radius)-MAX_HEIGHT);
            // bottom side is out of bounds (from P to 2P)
            // sideDegrees = calculateAngleOfSegment(180,);
            // if (positions.hasFlag(Positions.posStart)) {
            //     allowedDegrees[0] = sideDegrees[0];
            // }else if (positions.hasFlag(Positions.posEnd))
            //     //
            //
            positions.add(Positions.posBottom);
        }
        if (positions.hasFlag(Positions.posStart)) {
            allowedDegrees[0] = (int) (270 + leftAngle/2);
            allowedDegrees[1] = (int) (270 - leftAngle/2);
            if (positions.hasFlag(Positions.posTop)){
                allowedDegrees[0] = (int) (0 + topAngle/2);
            }else if (positions.hasFlag(Positions.posBottom)){
                allowedDegrees[1] = (int) (180 - bottomAngle/2);
            }
        }else if (positions.hasFlag(Positions.posEnd)){
            allowedDegrees[0] = (int) (90 + rightAngle/2);
            allowedDegrees[1] = (int) (90 - rightAngle/2);
            if (positions.hasFlag(Positions.posTop)){
                allowedDegrees[1] = (int) (360 - topAngle/2);
            }else if (positions.hasFlag(Positions.posBottom)){
                allowedDegrees[0] = (int) (180 + bottomAngle/2);
            }
        }
        return allowedDegrees;
    }

    /*
    >>> def getAngleOutOfBoundsSegment(radius, sideOutOfBounds):
        return math.degrees(2 * math.acos(abs(1-sideOutOfBounds/radius)));

    >>> def calculateAngleOfSegment(centerOfSideInDegrees, angleSegment):
            degrees = [0,0];

            degrees[0] = centerOfSideInDegrees-angleSegment;
            degrees[1] = centerOfSideInDegrees+angleSegment;
            if (degrees[0] < 0):
                degrees[0] += 360;
            if (degrees[1] >= 360):
                degrees[1] %= 360;
            return degrees;

    >>> calculateAngleOfSegment(180, getAngleOutOfBoundsSegment(20, (90+20)-100))
    [59.999999999999986, 300.0]
    >>>
    */

    public static int[] calculateAngleOfSegment(int centerOfSideInDegrees, double angleSegment){
        int[] degrees = new int[2];

        degrees[0] = (int) (centerOfSideInDegrees-angleSegment);
        degrees[1] = (int) (centerOfSideInDegrees+angleSegment);
        if (degrees[0] < 0)
            degrees[0] += MAX_CIRCLE_DEGREES;
        if (degrees[1] >= MAX_CIRCLE_DEGREES)
            degrees[1] %= MAX_CIRCLE_DEGREES;
        return degrees;
    }

    public CircularParams(int zIndex, double centerX, double centerY, int angleFrom, int angleTo){
        this.zIndex = zIndex;
        this.centerX = centerX;
        this.centerY = centerY;

        this.angleFrom = angleFrom;
        this.angleTo = angleTo;
        //circleCountPoints++;
        Log.d("CircularParamsThis", this.toString());

    }

    public CircularParams(int zIndex, double[] centerXY, int[] angleFromTo){
        this(zIndex, centerXY[0], centerXY[1], angleFromTo[0], angleFromTo[1]);
    }

    public void updateCenterXY(double centerX, double centerY){
        this.centerX = centerX;
        this.centerY = centerY;
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
    double getZIndexOffset(){ return calculateOffset(getAnglesDifference(), circleCountPoints-1, zIndex); }
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

