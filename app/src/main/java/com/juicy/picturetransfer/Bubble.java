package com.juicy.picturetransfer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class Bubble extends View{

    public final CircularParams circularParams;
    public final WindowManager.LayoutParams layoutParams;
    public final WindowManager windowManager;


    public Bubble(Context context, WindowManager windowManager, WindowManager.LayoutParams layoutParams, CircularParams circularParams) {
        super(context);
        this.circularParams = circularParams;
        this.layoutParams = layoutParams;
        this.windowManager = windowManager;
        // circularParams = new CircularParams(zIndex, centerXY, new int[]{0, 360});
    }

    public void addToWindow(){
        if (windowManager != null) {
            windowManager.addView(this, layoutParams);
            CircularParams.circleCountPoints++;
        }
    }
    public void removeFromWindow(){
        if (windowManager != null) {
            windowManager.removeView(this);
            CircularParams.circleCountPoints--;
        }
    }
    public void updateViewWithOffsetSize(double[] centerXY){
        updateView(centerXY[0] - (getWidth() >> 1), centerXY[1]-(getHeight() >> 1));
    }
    public void updateViewWithOffsetSize(double newX, double newY){
        updateView(newX - (CircularParams.circleRadius>>1), newY-(CircularParams.circleRadius>>1));
    }

    public void updateView(double[] centerXY){
        updateView(centerXY[0], centerXY[1]);
        // setX((float) circularParams.getEndpointX());
        // setY((float) circularParams.getEndpointY());
    }
    public void updateLayout(WindowManager.LayoutParams params){
        circularParams.updateCenterXY(params.x, params.y);
        windowManager.updateViewLayout(this, params);
    }
    public void updateView(double newX, double newY){
        layoutParams.x = (int) circularParams.getEndpointX();
        layoutParams.y = (int) circularParams.getEndpointY();
        windowManager.updateViewLayout(this, layoutParams);
    }
}
