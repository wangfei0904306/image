package com.waskj.image.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/10/7 0007.
 */
public class DragScaleView extends RelativeLayout implements View.OnTouchListener {
    protected int screenWidth;
    protected int screenHeight;
    protected int lastX;
    protected int lastY;
    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;
    private int dragDirection;
    private static final int TOP = 0x15;
    private static final int LEFT = 0x16;
    private static final int BOTTOM = 0x17;
    private static final int RIGHT = 0x18;
    private static final int LEFT_TOP = 0x11;
    private static final int RIGHT_TOP = 0x12;
    private static final int LEFT_BOTTOM = 0x13;
    private static final int RIGHT_BOTTOM = 0x14;
    private static final int CENTER = 0x19;
    private int offsetHorizontal = 2;  //边框与边缘距离
    private int offsetVertital = 100;  //边框与边缘距离
    private int dragAreaWidthHorzontal = 0;
    private int dragAreaWidthVertital = 100;
    private int strokeWidth = 4;
    protected Paint paint = new Paint();


    protected void initScreenW_H() {
        screenHeight = getResources().getDisplayMetrics().heightPixels - dragAreaWidthVertital;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    public DragScaleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragScaleView(Context context) {
        super(context);
        setOnTouchListener(this);
        initScreenW_H();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(offsetHorizontal, offsetVertital, getWidth() - offsetHorizontal, getHeight()
                - offsetVertital, paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            oriLeft = v.getLeft();
            oriRight = v.getRight();
            oriTop = v.getTop();
            oriBottom = v.getBottom();
            lastY = (int) event.getRawY();
            lastX = (int) event.getRawX();
            dragDirection = getDirection(v, (int) event.getX(),
                    (int) event.getY());
        }
        // 处理拖动事件
        delDrag(v, event, action);
        invalidate();
        return false;
    }


    protected void delDrag(View v, MotionEvent event, int action) {
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                switch (dragDirection) {
                    case LEFT: // 左边缘
                        left(v, dx);
                        break;
                    case RIGHT: // 右边缘
                        right(v, dx);
                        break;
                    case BOTTOM: // 下边缘
                        bottom(v, dy);
                        break;
                    case TOP: // 上边缘
                        top(v, dy);
                        break;
                    case CENTER: // 点击中心-->>移动
                        center(v, dx, dy);
                        break;
                    case LEFT_BOTTOM: // 左下
                        left(v, dx);
                        bottom(v, dy);
                        break;
                    case LEFT_TOP: // 左上
                        left(v, dx);
                        top(v, dy);
                        break;
                    case RIGHT_BOTTOM: // 右下
                        right(v, dx);
                        bottom(v, dy);
                        break;
                    case RIGHT_TOP: // 右上
                        right(v, dx);
                        top(v, dy);
                        break;
                }
                if (dragDirection != CENTER) {
                    v.layout(oriLeft, oriTop, oriRight, oriBottom);
                }
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                dragDirection = 0;
                break;
        }
    }


    private void center(View v, int dx, int dy) {
        int left = v.getLeft() + dx;
        int top = v.getTop() + dy;
        int right = v.getRight() + dx;
        int bottom = v.getBottom() + dy;
        if (left < -offsetHorizontal) {
            left = -offsetHorizontal;
            right = left + v.getWidth();
        }
        if (right > screenWidth + offsetHorizontal) {
            right = screenWidth + offsetHorizontal;
            left = right - v.getWidth();
        }
        if (top < -offsetVertital) {
            top = -offsetVertital;
            bottom = top + v.getHeight();
        }
        if (bottom > screenHeight + offsetVertital) {
            bottom = screenHeight + offsetVertital;
            top = bottom - v.getHeight();
        }
        v.layout(left, top, right, bottom);
    }


    private void top(View v, int dy) {
        oriTop += dy;
        if (oriTop < -offsetVertital) {
            oriTop = -offsetVertital;
        }
        if (oriBottom - oriTop - 2 * offsetVertital < 200) {
            oriTop = oriBottom - 2 * offsetVertital - 200;
        }
    }


    private void bottom(View v, int dy) {
        oriBottom += dy;
        if (oriBottom > screenHeight + offsetVertital) {
            oriBottom = screenHeight + offsetVertital;
        }
        if (oriBottom - oriTop - 2 * offsetVertital < 200) {
            oriBottom = 200 + oriTop + 2 * offsetVertital;
        }
    }


    private void right(View v, int dx) {
        oriRight += dx;
        if (oriRight > screenWidth + offsetHorizontal) {
            oriRight = screenWidth + offsetHorizontal;
        }
        if (oriRight - oriLeft - 2 * offsetHorizontal < 200) {
            oriRight = oriLeft + 2 * offsetHorizontal + 200;
        }
    }


    private void left(View v, int dx) {
        oriLeft += dx;
        if (oriLeft < -offsetHorizontal) {
            oriLeft = -offsetHorizontal;
        }
        if (oriRight - oriLeft - 2 * offsetHorizontal < 200) {
            oriLeft = oriRight - 2 * offsetHorizontal - 200;
        }
    }


    protected int getDirection(View v, int x, int y) {
        int left = v.getLeft();
        int right = v.getRight();
        int bottom = v.getBottom();
        int top = v.getTop();
        if (x < dragAreaWidthHorzontal && y < dragAreaWidthVertital) {
            return LEFT_TOP;
        }
        if (y < dragAreaWidthVertital && right - left - x < dragAreaWidthHorzontal) {
            return RIGHT_TOP;
        }
        if (x < dragAreaWidthHorzontal && bottom - top - y < dragAreaWidthVertital) {
            return LEFT_BOTTOM;
        }
        if (right - left - x < dragAreaWidthHorzontal && bottom - top - y < dragAreaWidthVertital) {
            return RIGHT_BOTTOM;
        }
        if (x < dragAreaWidthHorzontal) {
            return LEFT;
        }
        if (y < dragAreaWidthVertital) {
            return TOP;
        }
        if (right - left - x < dragAreaWidthHorzontal) {
            return RIGHT;
        }
        if (bottom - top - y < dragAreaWidthVertital) {
            return BOTTOM;
        }
        return CENTER;
    }


    public int getCutWidth() {
        return getWidth() - 2 * offsetHorizontal -2*strokeWidth;
    }

    public int getCutHeight() {
        return getHeight() - 2 * offsetVertital -2*strokeWidth;
    }

    public int getCutX() {
        return (int)getX() + offsetHorizontal + strokeWidth;
    }
    public int getCutY() {
        return (int)getY() + offsetVertital + strokeWidth;
    }
}