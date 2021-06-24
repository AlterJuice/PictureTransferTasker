package com.juicy.picturetransfer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Arrays;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.LEFT;


public class FloatingButtonService extends Service {

    int centerBubbleSizePx;
    int childBubbleSizePx;
    BubbleLayout bubbleLayout;
    private WindowManager windowManager;
    private WindowManager.LayoutParams centerParams;
    BubbleContainer bbs;


    public void onCenterClick(View view){
        try {
            ((View) view.getParent()).performClick();
        }catch (Exception e){
            e.printStackTrace();
        }

        double[] xy = bbs.getCenterXY();
        Log.d("Calculating angles", Arrays.toString(CircularParams.calculateAllowedAngles(xy[0], xy[1], centerBubbleSizePx / 2)));



    }

    public void onBubbleClick(View view){
        bbs.createAddNewBubble(R.drawable.ic_baseline_help_24);
        updateBubbles();
        // ((View) view.getParent()).performClick();

    }

    View.OnTouchListener bubbleListener = new View.OnTouchListener() {
            private float initialTouchX;
            private float initialTouchY;

            private int initialX;
            private int initialY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getRootView().onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shouldClick = true;
                        initialX = centerParams.x;
                        initialY = centerParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        Log.d("XY", initialTouchX + " " + initialTouchY);
                        return true;
                    case MotionEvent.ACTION_UP:
                        onCenterClick(v);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        centerParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        centerParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        bbs.bubbles.get(0).updateLayout(centerParams);
                        updateBubbles();
                        return true;
                }
                return false;
            }
        };

    WindowManager.LayoutParams getWindowParams(int w, int h, int x, int y, int gravity){
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        layoutParams.width = w;
        layoutParams.height = h;
        layoutParams.x = x;
        layoutParams.y = y;
        layoutParams.gravity = gravity;
        return layoutParams;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        //инициализируем его
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        centerBubbleSizePx = getPxFromDp(120);
        childBubbleSizePx = getPxFromDp(80);
        int layoutSizePx = getPxFromDp(300);

        bbs = new BubbleContainer(this, centerBubbleSizePx, childBubbleSizePx) {
            @Override
            Bubble generateBubbleView(Context context, int viewIndex0IsCenter, int backgroundResId, Positions positions) {
                int[] angleFromTo = positions.getDegreesFromTo();
                CircularParams circularParams;
                WindowManager.LayoutParams bubbleParams;

                if (viewIndex0IsCenter == 0)
                    circularParams = new CircularParams(viewIndex0IsCenter, new double[] {200, 200}, angleFromTo);
                else
                    circularParams = new CircularParams(viewIndex0IsCenter, getCenterXY(), angleFromTo);

                if (viewIndex0IsCenter == 0) {
                    centerParams = getWindowParams(centerBubbleSizePx, centerBubbleSizePx, 200, 200, Gravity.TOP | CENTER_HORIZONTAL);
                    bubbleParams = centerParams;
                }else
                    bubbleParams = getWindowParams(childBubbleSizePx, childBubbleSizePx, (int) circularParams.getEndpointX(), (int) circularParams.getEndpointY(),0);

                Bubble bubble = new Bubble(context, windowManager, bubbleParams, circularParams);
                bubble.setId(View.generateViewId());
                bubble.setBackgroundResource(backgroundResId);
                bubble.setOnClickListener(FloatingButtonService.this::onBubbleClick);
                if (viewIndex0IsCenter != 0)
                    bubble.setBackgroundTintList(getColorStateList(R.color.black_trans));
                bubble.setOnTouchListener(bubbleListener);
                //windowManager.addView(bubble, bubbleParams);
                bubbles.add(bubble);

                // vParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
                return bubble;
            }
        };
        bbs.createAddNewBubble(R.drawable.ic_baseline_help_24);
        updateBubbles();



        bubbleLayout = new BubbleLayout(this, 1000, centerBubbleSizePx, childBubbleSizePx) {
            @Override
            View generateCenterBubbleView() {
                View v = new View(getContext());
                v.setId(View.generateViewId());
                RelativeLayout.LayoutParams vParams = new RelativeLayout.LayoutParams(centerBubbleSizePx, centerBubbleSizePx);
                vParams.addRule(RelativeLayout.CENTER_IN_PARENT, 1);
                v.setLayoutParams(vParams);
                v.setBackgroundResource(R.drawable.ic_baseline_help_24);
                v.setOnClickListener(FloatingButtonService.this::onCenterClick);
                return v;
            }

            @Override
            View generateChildBubbleView(int indexOfView, int backgroundResId, Positions position) {
                View v = new View(getContext());
                v.setId(View.generateViewId());
                v.setLayoutParams(new RelativeLayout.LayoutParams(childBubbleSizePx, childBubbleSizePx));
                v.setBackgroundResource(backgroundResId);
                v.setBackgroundTintList(getColorStateList(R.color.black_trans));
                int[] angleFromTo = position.getDegreesFromTo();
                v.setOnClickListener(FloatingButtonService.this::onBubbleClick);
                CircularParams cParams = new CircularParams(indexOfView, getCenterXY(), angleFromTo);
                setCircularParamsToView(v, cParams);
                return v;
            }
        };

        CircularParams.setupOptions(
                true,
                //centerBubbleSizePx/2, childBubbleSizePx/2,
                (int) Math.hypot(centerBubbleSizePx/2, centerBubbleSizePx/2),
                (int) Math.hypot(childBubbleSizePx/2, childBubbleSizePx/2),
                getPxFromDp(10),
                getPxFromDp(10));

        int offset = 100;

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        CircularParams.setupMinMaxParams(offset, offset, screenHeight-offset, screenWidth-offset);
    }

    public void updateBubbles(){
        // double[] centerXY = {centerParams.x, centerParams.y};
        for (int i = 0; i < bbs.bubbles.size(); i++) {

            bbs.bubbles.get(i).updateView(centerParams.x, centerParams.y);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < bbs.bubbles.size(); i++) {
            bbs.bubbles.get(i).removeFromWindow();
            windowManager.removeView(bbs.bubbles.get(i));
        }
        bbs.bubbles.clear();
        CircularParams.circleCountPoints = 0;
    }


/*
    RelativeLayout generateInflatedHandLineCircle(int zIndex, byte position) {
        if (created == 0) {
            CircularParams.setCircularRadiusPX(image.getWidth());
            Log.d("Circle radius", CircularParams.circleRadius + "");
        }

        int[] centerXY1 = getFloatingCenter();
        int[] angleFromTo = Positions.getAllowedDegreesFromTo(position);
        CircularParams circularParams = getCircularParams(zIndex, centerXY1, angleFromTo);

        RelativeLayout x = (RelativeLayout) View.inflate(this, R.layout.line_circle, null);
        x.findViewWithTag("hand_line").getLayoutParams().width = handLineWidthDPSEven;
        x.findViewWithTag("hand_circle").setBackgroundResource(R.drawable.ic_baseline_help_24);
        x.findViewWithTag("hand_circle").setRotation((float) -circularParams.getEndpointAngle());


        double A = Math.atan2(centerXY1[1] - circularParams.getEndpointY(), centerXY1[0] - circularParams.getEndpointX()) / Math.PI * 180;
        A = (A < 0) ? A + 360 : A;   //Без этого диапазон от 0...180 и -1...-180


        x.setRotation((float) ((float) circularParams.getEndpointAngle() + Math.toRadians(A)));
        x.setX((float) circularParams.getEndpointX());
        x.setY((float) circularParams.getEndpointY() - (float) getPxFromDp(46) / 2 - 5);
        // x.setRotation(zIndex);
        // x.setPivotX(centerXY1[0]);
        // x.setPivotY(x.getMeasuredHeight()+200);


        created++;
        // findViewWithTag("hand_line").animate()
        // x.findViewWithTag("hand_line").getLayoutParams().width += 100;

        return x;
    }*/

/*
    RelativeLayout generateHandLineCircle(int zIndex, byte position) {
        if (created == 0) {
            CircularParams.setCircularRadiusPX(image.getWidth());
            Log.d("Circle radius", CircularParams.circleRadius + "");
        }

        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int px5dps = getPxFromDp(5);
        layout_params.setMargins(px5dps, px5dps, px5dps, px5dps);
        layout.setLayoutParams(layout_params);
        layout.setMinimumHeight(getPxFromDp(60));
        layout.setPadding(px5dps * 2, px5dps * 2, px5dps * 2, px5dps * 2);
        layout.setBackgroundResource(R.drawable.rounded_shape);
        layout.setBackgroundColor(getColor(R.color.black));


        View hand_line = new View(this);
        RelativeLayout.LayoutParams line_params = new RelativeLayout.LayoutParams(getHandLineWidthPX(zIndex), getHandLineHeightPX());
        line_params.addRule(RelativeLayout.ALIGN_PARENT_START, 1);
        line_params.addRule(RelativeLayout.CENTER_VERTICAL, 1);
        line_params.rightMargin = px5dps * 2;
        hand_line.setLayoutParams(line_params);
        hand_line.setBackgroundResource(R.drawable.rounded_shape);
        hand_line.setBackgroundColor(getColor(R.color.teal_700));
        hand_line.setId(View.generateViewId());


        View hand_circle = new View(this);
        RelativeLayout.LayoutParams circle_params = new RelativeLayout.LayoutParams(getPxFromDp(25), getPxFromDp(25));
        circle_params.addRule(RelativeLayout.CENTER_VERTICAL, 1);
        circle_params.addRule(RelativeLayout.END_OF, hand_line.getId());
        hand_circle.setLayoutParams(circle_params);

        layout.addView(hand_line);
        layout.addView(hand_circle);


        int[] centerXY = getFloatingCenter();
        int[] angleFromTo = Positions.getAllowedDegreesFromTo(position);
        CircularParams circularParams = getCircularParams(zIndex, centerXY, angleFromTo);

        layout.setRotation((float) circularParams.getEndpointAngle());
        layout.setX((float) circularParams.getEndpointX());
        layout.setY((float) circularParams.getEndpointY() - (float) getPxFromDp(46) / 2 - 5);
        return layout;
    }*/

    private int getPxFromDp(int dps) {
        return dps * getResources().getDimensionPixelSize(R.dimen.ONE_DP);
    }


}











