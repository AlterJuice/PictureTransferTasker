package com.juicy.picturetransfer;

import android.view.View;

import java.util.ArrayList;

public abstract class BubbleContainer {
    public ArrayList<Bubble> bubbles;
    int centerBubblePx;
    int childBubblePx;
    View centerView;
    // BUBBLE INDEXED WITH 0 IS MAIN BUBBLE

    abstract Bubble generateCenterBubbleView();
    abstract Bubble generateChildBubbleView(int indexOfView, int backgroundResId, Positions position);

    public Bubble createNewBubbleView(int backgroundResId){
        return generateChildBubbleView(bubbles.size(), backgroundResId, new Positions());
    }

    public void createNewBubble(int backgroundResId){
        bubbles.add(createNewBubbleView(backgroundResId));
    }

    public void updateBubbles(){
        double[] centerXY = getCenterXY();
        for (int i = 0; i < bubbles.size(); i++) {
            bubbles.get(i).updateView(centerXY);

        }
    }

    public double[] getCenterXY() {
        return new double[]{
                // (double) getWidth()/2, (double) getHeight()/2
                centerView.getX() + Math.hypot(centerBubblePx, centerBubblePx)/8 ,
                centerView.getY() + Math.hypot(centerBubblePx, centerBubblePx)/8
        };
    }
}
