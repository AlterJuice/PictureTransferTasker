package com.juicy.picturetransfer;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

public class Bubble extends View{

    public CircularParams circularParams;
    public WindowManager.LayoutParams layoutParams;


    public Bubble(Context context) {
        super(context);
    }

    public void updateView(double[] centerXY){
        circularParams.updateCenterXY(centerXY[0], centerXY[1]);
        setX((float) circularParams.getEndpointX());
        setY((float) circularParams.getEndpointY());
    }
}
