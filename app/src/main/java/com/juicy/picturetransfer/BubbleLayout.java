package com.juicy.picturetransfer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    public void addView(View child) {
        super.addView(child);
        children.add(child);
        onChangeNotify();
    }
    public View createNewBubbleView(int backgroundResId){
        return generateChildBubbleView(children.size(), backgroundResId, getLayoutPosition());
    }

    public void createNewBubble(int backgroundResId){
        addView(createNewBubbleView(backgroundResId));
    }

    Positions getLayoutPosition(){
        return new Positions(Positions.posNone);
    }

/*
    public void newQ(){
        int centerX = getWidth()/2;
        int centerY= getHeight()/2;
        generateCenterBubbleView();
        // canvas.drawCircle(centerX,centerY,radius_main,mainPaint);
        for(int i = 0; i < children.size(); i++){
            double angle = 0;
            if(i==0){
                angle = startAngle;
            }else{
                angle = startAngle+(i * ((2 * Math.PI) / children.size()));
            }

            children.get(i).setX((float) (centerX + Math.cos(angle)*(radius_main+menuInnerPadding+radialCircleRadius)));
            children.get(i).setY((float) (centerY + Math.sin(angle)*(radius_main+menuInnerPadding+radialCircleRadius)));

            // canvas.drawCircle( elements.get(i).x,elements.get(i).y,radialCircleRadius,secondPaint);
            //float tW = textPaint.measureText(elements.get(i).text);
            //
            //canvas.drawText(elements.get(i).text,elements.get(i).x-tW/2,elements.get(i).y+radialCircleRadius+textPadding,textPaint);
        }

    }*/

    public void onChangeNotify(){
        CircularParams.setCircleMaxPoints(children.size());
        double[] centerXY = getCenterXY();
        int[] degreesFromTo = getLayoutPosition().getDegreesFromTo();
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
