package com.juicy.picturetransfer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import static android.view.Gravity.CENTER_HORIZONTAL;

public class FloatingLayoutService extends Service {

    int centeredBubbleSizePx;
    int childBubbleSizePx;
    BubbleLayout bubbleLayout;
    BubbleContainer bbs;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        //инициализируем его

        centeredBubbleSizePx = getPxFromDp(120);
        childBubbleSizePx = getPxFromDp(80);
        int layoutSizePx = getPxFromDp(300);

        bbs = new BubbleContainer(this, centeredBubbleSizePx, childBubbleSizePx) {
            @Override
            Bubble generateBubbleView(Context context, int viewIndex0IsCenter, int backgroundResId, Positions positions) {
                int[] angleFromTo = positions.getDegreesFromTo();
                CircularParams circularParams;
                WindowManager.LayoutParams bubbleParams;

                if (viewIndex0IsCenter == 0)
                    circularParams = new CircularParams(viewIndex0IsCenter, new double[]{200, 200}, angleFromTo);
                else
                    circularParams = new CircularParams(viewIndex0IsCenter, getCenterXY(), angleFromTo);

                Bubble bubble = new Bubble(context, bubbleParams, circularParams);
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


        bubbleLayout = new BubbleLayout(this, 1000, centeredBubbleSizePx, childBubbleSizePx) {
            @Override
            View generateCenterBubbleView() {
                View v = new View(getContext());
                v.setId(View.generateViewId());
                RelativeLayout.LayoutParams vParams = new RelativeLayout.LayoutParams(centeredBubbleSizePx, centeredBubbleSizePx);
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
                //centeredBubbleSizePx/2, childBubbleSizePx/2,
                (int) Math.hypot(centeredBubbleSizePx / 2, centeredBubbleSizePx / 2),
                (int) Math.hypot(childBubbleSizePx / 2, childBubbleSizePx / 2),
                getPxFromDp(10),
                getPxFromDp(10));

        int offset = 100;

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        CircularParams.setupMinMaxParams(offset, offset, screenHeight - offset, screenWidth - offset);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int getPxFromDp(int dps) {
        return dps * getResources().getDimensionPixelSize(R.dimen.ONE_DP);
    }
}
