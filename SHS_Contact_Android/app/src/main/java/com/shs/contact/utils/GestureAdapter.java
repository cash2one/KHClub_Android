package com.shs.contact.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by wenhai on 2016/2/19.
 * 手势操作适配器根据不同的需求重写写相关方法即可
 */
public class GestureAdapter implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener{
    /**
     * 单击事件
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }
 /**
   * 双击事件
   */
    @Override
    public boolean onDoubleTap(MotionEvent e) { return false; }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
    // 用户轻触触摸屏
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }
   // 轻击一下屏幕，立刻抬起来，才会有这个触发
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
    // 用户按下触摸屏，并拖动
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }
    // 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
