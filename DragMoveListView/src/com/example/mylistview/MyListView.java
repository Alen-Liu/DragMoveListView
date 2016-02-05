package com.example.mylistview;

import android.R.integer;
import android.R.xml;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 网上看了一些关于 拖动排序 和 左右滑动 弹出菜单的实例, 感觉 github上的源码都很复杂, 可能是功能实现更丰富
 * 性能更好些.但是对于个人平时的使用没啥用处 
 * 长按响应拖动排序: 
 * 1. 响应 onTouchevent 函数(OndispatchEvent也可以).
 * 2. 在Action_down 事件中, 新建 longPressedRunnable.
 * 3. 在Action_Move 事件中, 根据当前的位置信息 改变list 数据并且更新listview.
 * 4. 在Action_up 事件中, 重置相关的参数.
 * */


/**
 * 左右滑动 展开 listItem:
 * 1. 集成 OnGestureListener 
 * 2. 
 * */
public class MyListView extends ListView {
    private Context mContext;
    private final int LONG_PRESSED_TIME = 1000;
    private ImageView mMoveView;
    WindowManager mWindowManager;
    WindowManager.LayoutParams mWindowParams;
    MyListAdapter myListAdapter;
    private int mFirstY = 0; //刚开始拖动的时候的y值
    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mWindowManager = (WindowManager) getContext().getSystemService("window");
        
        myListAdapter = (MyListAdapter) getAdapter();
    }

    private void initFloatView(Bitmap bm) {
        // TODO Auto-generated method stub
        mMoveView = new ImageView(mContext);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.gravity = Gravity.TOP;
        mWindowParams.x = 0;
        mWindowParams.y = mFirstY;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 不需获取焦点
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 不需接受触摸事件
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON// 保持设备常开，并保持亮度不变。
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;// 窗口占满整个屏幕，忽略周围的装饰边框（例如状态栏）。此窗口需考虑到装饰边框的内容。

        // windowParams.format = PixelFormat.TRANSLUCENT;// 默认为不透明，这里设成透明效果.
        mWindowParams.windowAnimations = 0;// 窗口所使用的动画设置

        mMoveView = new ImageView(getContext());
        mMoveView.setImageBitmap(bm);
        mWindowManager.addView(mMoveView, mWindowParams);
    }

    float mDownY;
    boolean mPressed;
    boolean mLongPressed;
    int mDownPos;
    int mLastTargetPos = -1;
    int mMovedObject ;//action_down的记录一下被移动的元素 待会action_up重新添加回去
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mDownY = ev.getY();
            mPressed = true;
            mLongPressed = false;
            // set long pressed event
            postDelayed(mLongPressedRunnable, LONG_PRESSED_TIME);
            break;

        case MotionEvent.ACTION_MOVE:
            if(Math.abs(mDownY - ev.getY()) < 10){ // 忽略小范围的move
                return false;
            }
            this.removeCallbacks(mLongPressedRunnable);
            if (mLongPressed) { // 处理长按之后的move事件 
                //1. 移动floatview
                mWindowParams.y = (int) (mFirstY + (ev.getY() - mDownY));
                mWindowManager.updateViewLayout(mMoveView, mWindowParams);
                
                //2. 判断当前移动到了哪个位置. 替换和更新相应的view.
                int movePos = pointToPosition(0, (int)ev.getY());
                if(movePos != mLastTargetPos && movePos != -1){ //移动位置
                    myListAdapter.mData.remove(mLastTargetPos);
                    myListAdapter.mData.add(movePos, mMovedObject);
                    mLastTargetPos = movePos;
                    myListAdapter.notifyDataSetChanged();//更新UI
                }
                //3. 一起移动scrollView 否则没办法对超出屏幕的item进行替换
                doScroller((int)ev.getY());
                return true;
            }
            break;

        case MotionEvent.ACTION_UP: //删掉当前的moveView
            this.removeCallbacks(mLongPressedRunnable);
            if (mLongPressed) {
                mLongPressed = false;
                mWindowManager.removeView(mMoveView);
                mMoveView = null;
                mLastTargetPos = -1;
            }
            break;

        default:

            break;
        }
        return super.onTouchEvent(ev);
    }

    //在移动的时候 同步的scroll listview
    private void doScroller(int y) { // 这样滚动有些太快
        // TODO 判断是否需要上移和下移,如果需要就每次都移动一定的距离
        if(getFirstVisiblePosition() > 0 && y <= getTop() + mMoveView.getHeight()) {//如果移动的view 快要超过最上面的item了 就向上移动
            setSelection(getFirstVisiblePosition() - 1);
        } else if(getLastVisiblePosition() < getAdapter().getCount() && y > getBottom() - mMoveView.getHeight()) {//如果移动的view 快要低于最下面的item了 就向下移动
            setSelection(getLastVisiblePosition() + 1);
        }        
    }

    // 在这个runnable中设置longpressed的状态并且 初始化要跟随手指移动的moveView
    private Runnable mLongPressedRunnable = new Runnable() {
        public void run() {
            mLongPressed = true;

            mDownPos = pointToPosition(0, (int) mDownY);
            if (mDownPos == INVALID_POSITION)
                return;
           
            View dragView = getChildAt(mDownPos - getFirstVisiblePosition());
            dragView.setDrawingCacheEnabled(true);
            Bitmap bt = Bitmap.createBitmap(dragView.getDrawingCache());
            Rect r = new Rect();
            dragView.getGlobalVisibleRect(r);
            mFirstY = r.top;
            initFloatView(bt);

            myListAdapter = (MyListAdapter) getAdapter();
            mMovedObject = myListAdapter.mData.get(mDownPos);
            mLastTargetPos = mDownPos;
        }
    };

    
    // 跟左右 滑动 展开listItem相关的

}
