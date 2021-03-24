package com.juicy.picturetransfer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class CircleMenu extends View {

    public static interface IMenuListener{

        public void onMenuClick(MenuCircle item);
    }

    public static class MenuCircle{
        private int x, y, radius;
        public int id;
        public String text;

    }

    private Paint mainPaint;
    private Paint secondPaint;
    private Paint textPaint;
    private int radius_main =130;

    private int menuInnerPadding = 40;
    private int radialCircleRadius = 60;
    private int textPadding = 25;
    private double startAngle = - Math.PI/2f;
    private ArrayList<MenuCircle> elements;
    private IMenuListener listener;

    public void setListener(IMenuListener listener){
        this.listener = listener;
    }
    public void clear(){
        elements.clear();
        listener=null;
    }
    public CircleMenu(Context context) {
        super(context);
        init();
        /*
        // https://stackoverflow.com/questions/33259384/how-to-align-8-little-circles-around-of-a-centered-big-circle-like-attached-ima
        menu = new CircleMenu(this);
        menu.addMenuItem("one",1);
        menu.addMenuItem("two",2);
        menu.addMenuItem("three",3);
        menu.addMenuItem("ten",10);
        menu.addMenuItem("oh oh",156);
        menu.addMenuItem("exit",134);

        menu.setListener(new CircleMenu.IMenuListener() {
            @Override
            public void onMenuClick(CircleMenu.MenuCircle item) {
                Toast.makeText(FloatingButtonService.this,item.text+" "+item.id,Toast.LENGTH_LONG).show();
            }
        });

        */
    }

    public CircleMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        elements = new ArrayList<>();
        mainPaint = new Paint();
        mainPaint.setColor(Color.BLUE);
        secondPaint = new Paint();
        secondPaint.setColor(Color.DKGRAY);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
    }
    public void addMenuItem(String text,int id){
        MenuCircle item = new MenuCircle();
        item.id = id;
        item.text=text;
        elements.add(item);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mainPaint = new Paint();
        mainPaint.setColor(Color.BLUE);
        secondPaint = new Paint();
        secondPaint.setColor(Color.DKGRAY);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = canvas.getWidth()/2 ;
        int centerY= canvas.getHeight()/2;
        canvas.drawCircle(centerX,centerY,radius_main,mainPaint);
        for(int i=0;i<elements.size();i++){
            double angle =0;
            if(i==0){
                angle = startAngle;
            }else{
                angle = startAngle+(i * ((2 * Math.PI) / elements.size()));
            }
            elements.get(i).x = (int) (centerX + Math.cos(angle)*(radius_main+menuInnerPadding+radialCircleRadius));
            elements.get(i).y = (int) (centerY + Math.sin(angle)*(radius_main+menuInnerPadding+radialCircleRadius));

            canvas.drawCircle( elements.get(i).x,elements.get(i).y,radialCircleRadius,secondPaint);
            float tW = textPaint.measureText(elements.get(i).text);
            canvas.drawText(elements.get(i).text,elements.get(i).x-tW/2,elements.get(i).y+radialCircleRadius+textPadding,textPaint);
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            for(MenuCircle mc : elements){
                double distance =  Math.hypot(event.getX()-mc.x,event.getY()-mc.y);
                if(distance<= radialCircleRadius){
                    //touched
                    if(listener!=null)
                        listener.onMenuClick(mc);
                    return true;
                }
            }

        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }
}