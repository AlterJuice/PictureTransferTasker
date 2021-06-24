package com.juicy.picturetransfer;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;


public abstract class BubbleLayout extends RelativeLayout {
    View centerView;
    ArrayList<View> children = new ArrayList<>();
    int centerBubblePx;
    int childBubblePx;

    public BubbleLayout(Context context, int layoutHeightWidth, int centerBubblePx, int childBubblePx) {
        super(context);
        this.centerBubblePx = centerBubblePx;
        this.childBubblePx = childBubblePx;

        setLayoutParams(new LayoutParams(layoutHeightWidth, layoutHeightWidth));
        setBackgroundColor(getContext().getColor(R.color.black_trans));
        centerView = generateCenterBubbleView();
        addView(centerView);
        // children.add(null);
    }

    abstract View generateCenterBubbleView();
    abstract View generateChildBubbleView(int indexOfView, int backgroundResId, Positions position);

    public void moveLayout(float deltaX, float deltaY){
        centerView.setX(deltaX);
        centerView.setY(deltaY);
        for (int i = 0; i < children.size(); i++) {
            View v = children.get(i);
            v.setX(v.getX()+deltaX);
            v.setY(v.getY()+deltaY);
        }
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        children.add(child);
        onChangeNotify();
    }
    
    public View createNewBubbleView(int backgroundResId){
        return generateChildBubbleView(children.size(), backgroundResId, new Positions());
    }

    public void createNewBubble(int backgroundResId){
        addView(createNewBubbleView(backgroundResId));
    }


    public void onChangeNotify(){
        CircularParams.setCircleMaxPoints(children.size());
        double[] centerXY = getCenterXY();
        int[] degreesFromTo = new Positions().getDegreesFromTo();
        for (int i = 0; i < children.size(); i++) {
            CircularParams cParams = new CircularParams(i, centerXY, degreesFromTo);
            setCircularParamsToView(children.get(i), cParams);

        }
    }

    void setCircularParamsToView(View view, CircularParams cParams){
        double viewRadius = Math.hypot(childBubblePx/2, childBubblePx/2);
        view.setX((float) (cParams.getEndpointX()));
        view.setY((float) (cParams.getEndpointY()));
        // view.setX((float) (cParams.centerX));
        // view.setY((float) (cParams.centerY));

        // view.setX((float) cParams.getEndpointX());
        // view.setY((float) cParams.getEndpointY());
        // view.setRotation((float) cParams.getEndpointAngle());
    }

    double[] getCenterXY(){
        return new double[]{
                // (double) getWidth()/2, (double) getHeight()/2
                centerView.getX() + Math.hypot(centerBubblePx, centerBubblePx)/8 ,
                centerView.getY() + Math.hypot(centerBubblePx, centerBubblePx)/8
        };
    }


}
