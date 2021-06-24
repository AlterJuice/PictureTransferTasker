package com.juicy.picturetransfer;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

public abstract class BubbleContainer {
    public ArrayList<Bubble> bubbles;
    int centerBubblePx;
    int childBubblePx;
    // BUBBLE INDEXED WITH 0 IS MAIN BUBBLE
    Context mContext;
    double centerOffsetRadius;

    BubbleContainer(Context context, int centerBubblePx, int childBubblePx){
        mContext = context;
        bubbles = new ArrayList<>();
        this.centerBubblePx = centerBubblePx;
        this.childBubblePx = childBubblePx;
        centerOffsetRadius = (Math.hypot(centerBubblePx, centerBubblePx)/8);

    }

    abstract Bubble generateBubbleView(Context context, int indexOfView, int backgroundResId, Positions positions);

    public Bubble createGetNewBubbleView(int backgroundResId){
        return generateBubbleView(mContext, bubbles.size(), backgroundResId, new Positions());
    }


    public void createAddNewBubble(int backgroundResId){
        Bubble bubble = createGetNewBubbleView(backgroundResId);
        bubble.addToWindow();
        bubbles.add(bubble);
    }


    public double[] getCenterXY() {
        double[] xy = {bubbles.get(0).layoutParams.x, bubbles.get(0).layoutParams.y};
        return xy;
        // bubbles.get(0).getLocationOnScreen(xy);
        // return new double[]{xy[0] + centerOffsetRadius, xy[1] + centerOffsetRadius};
    }
}
