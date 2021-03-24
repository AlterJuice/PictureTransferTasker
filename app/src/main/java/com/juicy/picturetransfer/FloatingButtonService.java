package com.juicy.picturetransfer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import static android.view.Gravity.CENTER_HORIZONTAL;


public class FloatingButtonService extends Service {

    int centerBubbleSizePx;
    int childBubbleSizePx;
    BubbleLayout bubbleLayout;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;

    public View getMainView(){
        return bubbleLayout;
    }


    public void onCenterClick(View view){
        ((View) view.getParent()).performClick();


    }

    public void onChildBubbleClick(View view){
        // ((View) view.getParent()).performClick();

    }

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
                v.setOnClickListener(FloatingButtonService.this::onChildBubbleClick);
                CircularParams cParams = new CircularParams(indexOfView, getCenterXY(), angleFromTo);
                setCircularParamsToView(v, cParams);
                return v;
            }
        };

        params = getWindowParams(layoutSizePx, layoutSizePx, 500, 500,
                Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        getMainView().setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private boolean shouldClick;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getRootView().onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        shouldClick = true;
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (shouldClick) {
                            View v1 = bubbleLayout.createNewBubbleView(R.drawable.ic_baseline_help_24);
                            WindowManager.LayoutParams l = new WindowManager.LayoutParams(
                                    childBubbleSizePx,
                                    childBubbleSizePx,
                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                                            WindowManager.LayoutParams.TYPE_PHONE,

                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                    PixelFormat.TRANSLUCENT);

                            l.gravity = Gravity.TOP | Gravity.LEFT;
                            l.x = 0;
                            l.y = 100;
                            windowManager.addView(v1, l);
                            //bubbleLayout.createNewBubble(R.drawable.ic_baseline_help_24);
                            windowManager.updateViewLayout(getMainView(), params);
                            Toast.makeText(getApplicationContext(), "Клик по тосту случился!", Toast.LENGTH_LONG).show();

                        }
                        shouldClick = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //shouldClick = false;
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        bubbleLayout.moveLayout((event.getRawX() - initialTouchX), (event.getRawY() - initialTouchY));
                        windowManager.updateViewLayout(getMainView(), params);

                        return true;
                }
                return false;
            }
        });

        windowManager.addView(getMainView(), params);
        CircularParams.setupOptions(
                true,
                //centerBubbleSizePx/2, childBubbleSizePx/2,
                (int) Math.hypot(centerBubbleSizePx/2, centerBubbleSizePx/2),
                (int) Math.hypot(childBubbleSizePx/2, childBubbleSizePx/2),
                getPxFromDp(10),
                getPxFromDp(10));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getMainView() != null)
            windowManager.removeView(getMainView());
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











