package android.support.v4.view;

import android.content.*;
import android.os.*;
import android.view.*;

public class GestureDetectorCompat
{
    private final GestureDetectorCompatImpl mImpl;
    
    public GestureDetectorCompat(final Context context, final GestureDetector$OnGestureListener gestureDetector$OnGestureListener) {
        this(context, gestureDetector$OnGestureListener, null);
    }
    
    public GestureDetectorCompat(final Context context, final GestureDetector$OnGestureListener gestureDetector$OnGestureListener, final Handler handler) {
        super();
        if (Build$VERSION.SDK_INT > 17) {
            this.mImpl = (GestureDetectorCompatImpl)new GestureDetectorCompatImplJellybeanMr2(context, gestureDetector$OnGestureListener, handler);
            return;
        }
        this.mImpl = (GestureDetectorCompatImpl)new GestureDetectorCompatImplBase(context, gestureDetector$OnGestureListener, handler);
    }
    
    public boolean isLongpressEnabled() {
        return this.mImpl.isLongpressEnabled();
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return this.mImpl.onTouchEvent(motionEvent);
    }
    
    public void setIsLongpressEnabled(final boolean isLongpressEnabled) {
        this.mImpl.setIsLongpressEnabled(isLongpressEnabled);
    }
    
    public void setOnDoubleTapListener(final GestureDetector$OnDoubleTapListener onDoubleTapListener) {
        this.mImpl.setOnDoubleTapListener(onDoubleTapListener);
    }
    
    interface GestureDetectorCompatImpl
    {
        boolean isLongpressEnabled();
        
        boolean onTouchEvent(MotionEvent p0);
        
        void setIsLongpressEnabled(boolean p0);
        
        void setOnDoubleTapListener(GestureDetector$OnDoubleTapListener p0);
    }
    
    static class GestureDetectorCompatImplBase implements GestureDetectorCompatImpl
    {
        private static final int DOUBLE_TAP_TIMEOUT = 0;
        private static final int LONGPRESS_TIMEOUT = 0;
        private static final int LONG_PRESS = 2;
        private static final int SHOW_PRESS = 1;
        private static final int TAP = 3;
        private static final int TAP_TIMEOUT;
        private boolean mAlwaysInBiggerTapRegion;
        private boolean mAlwaysInTapRegion;
        private MotionEvent mCurrentDownEvent;
        private boolean mDeferConfirmSingleTap;
        private GestureDetector$OnDoubleTapListener mDoubleTapListener;
        private int mDoubleTapSlopSquare;
        private float mDownFocusX;
        private float mDownFocusY;
        private final Handler mHandler;
        private boolean mInLongPress;
        private boolean mIsDoubleTapping;
        private boolean mIsLongpressEnabled;
        private float mLastFocusX;
        private float mLastFocusY;
        private final GestureDetector$OnGestureListener mListener;
        private int mMaximumFlingVelocity;
        private int mMinimumFlingVelocity;
        private MotionEvent mPreviousUpEvent;
        private boolean mStillDown;
        private int mTouchSlopSquare;
        private VelocityTracker mVelocityTracker;
        
        static {
            LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
            TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
            DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
        }
        
        public GestureDetectorCompatImplBase(final Context context, final GestureDetector$OnGestureListener mListener, final Handler handler) {
            super();
            if (handler != null) {
                this.mHandler = new GestureHandler(handler);
            }
            else {
                this.mHandler = new GestureHandler();
            }
            this.mListener = mListener;
            if (mListener instanceof GestureDetector$OnDoubleTapListener) {
                this.setOnDoubleTapListener((GestureDetector$OnDoubleTapListener)mListener);
            }
            this.init(context);
        }
        
        private void cancel() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
            this.mIsDoubleTapping = false;
            this.mStillDown = false;
            this.mAlwaysInTapRegion = false;
            this.mAlwaysInBiggerTapRegion = false;
            this.mDeferConfirmSingleTap = false;
            if (this.mInLongPress) {
                this.mInLongPress = false;
            }
        }
        
        private void cancelTaps() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
            this.mIsDoubleTapping = false;
            this.mAlwaysInTapRegion = false;
            this.mAlwaysInBiggerTapRegion = false;
            this.mDeferConfirmSingleTap = false;
            if (this.mInLongPress) {
                this.mInLongPress = false;
            }
        }
        
        private void dispatchLongPress() {
            this.mHandler.removeMessages(3);
            this.mDeferConfirmSingleTap = false;
            this.mInLongPress = true;
            this.mListener.onLongPress(this.mCurrentDownEvent);
        }
        
        private void init(final Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null");
            }
            if (this.mListener == null) {
                throw new IllegalArgumentException("OnGestureListener must not be null");
            }
            this.mIsLongpressEnabled = true;
            final ViewConfiguration value = ViewConfiguration.get(context);
            final int scaledTouchSlop = value.getScaledTouchSlop();
            final int scaledDoubleTapSlop = value.getScaledDoubleTapSlop();
            this.mMinimumFlingVelocity = value.getScaledMinimumFlingVelocity();
            this.mMaximumFlingVelocity = value.getScaledMaximumFlingVelocity();
            this.mTouchSlopSquare = scaledTouchSlop * scaledTouchSlop;
            this.mDoubleTapSlopSquare = scaledDoubleTapSlop * scaledDoubleTapSlop;
        }
        
        private boolean isConsideredDoubleTap(final MotionEvent motionEvent, final MotionEvent motionEvent2, final MotionEvent motionEvent3) {
            if (this.mAlwaysInBiggerTapRegion && motionEvent3.getEventTime() - motionEvent2.getEventTime() <= GestureDetectorCompatImplBase.DOUBLE_TAP_TIMEOUT) {
                final int n = (int)motionEvent.getX() - (int)motionEvent3.getX();
                final int n2 = (int)motionEvent.getY() - (int)motionEvent3.getY();
                if (n * n + n2 * n2 < this.mDoubleTapSlopSquare) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean isLongpressEnabled() {
            return this.mIsLongpressEnabled;
        }
        
        @Override
        public boolean onTouchEvent(final MotionEvent motionEvent) {
            final int action = motionEvent.getAction();
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            boolean b;
            if ((action & 0xFF) == 0x6) {
                b = true;
            }
            else {
                b = false;
            }
            int actionIndex;
            if (b) {
                actionIndex = MotionEventCompat.getActionIndex(motionEvent);
            }
            else {
                actionIndex = -1;
            }
            float n = 0.0f;
            float n2 = 0.0f;
            final int pointerCount = MotionEventCompat.getPointerCount(motionEvent);
            for (int i = 0; i < pointerCount; ++i) {
                if (actionIndex != i) {
                    n += MotionEventCompat.getX(motionEvent, i);
                    n2 += MotionEventCompat.getY(motionEvent, i);
                }
            }
            int n3;
            if (b) {
                n3 = pointerCount - 1;
            }
            else {
                n3 = pointerCount;
            }
            final float n4 = n / n3;
            final float n5 = n2 / n3;
            final int n6 = action & 0xFF;
            boolean onScroll = false;
            Label_0200: {
                switch (n6) {
                    case 5: {
                        this.mLastFocusX = n4;
                        this.mDownFocusX = n4;
                        this.mLastFocusY = n5;
                        this.mDownFocusY = n5;
                        this.cancelTaps();
                        return false;
                    }
                    case 6: {
                        this.mLastFocusX = n4;
                        this.mDownFocusX = n4;
                        this.mLastFocusY = n5;
                        this.mDownFocusY = n5;
                        this.mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumFlingVelocity);
                        final int actionIndex2 = MotionEventCompat.getActionIndex(motionEvent);
                        final int pointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex2);
                        final float xVelocity = VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, pointerId);
                        final float yVelocity = VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, pointerId);
                        int n7 = 0;
                        while (true) {
                            final int n8 = n7;
                            onScroll = false;
                            if (n8 >= pointerCount) {
                                break Label_0200;
                            }
                            if (n7 != actionIndex2) {
                                final int pointerId2 = MotionEventCompat.getPointerId(motionEvent, n7);
                                if (xVelocity * VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, pointerId2) + yVelocity * VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, pointerId2) < 0.0f) {
                                    break;
                                }
                            }
                            ++n7;
                        }
                        this.mVelocityTracker.clear();
                        return false;
                    }
                    case 0: {
                        final GestureDetector$OnDoubleTapListener mDoubleTapListener = this.mDoubleTapListener;
                        boolean b2 = false;
                        if (mDoubleTapListener != null) {
                            final boolean hasMessages = this.mHandler.hasMessages(3);
                            if (hasMessages) {
                                this.mHandler.removeMessages(3);
                            }
                            if (this.mCurrentDownEvent != null && this.mPreviousUpEvent != null && hasMessages && this.isConsideredDoubleTap(this.mCurrentDownEvent, this.mPreviousUpEvent, motionEvent)) {
                                this.mIsDoubleTapping = true;
                                b2 = (false | this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | this.mDoubleTapListener.onDoubleTapEvent(motionEvent));
                            }
                            else {
                                this.mHandler.sendEmptyMessageDelayed(3, (long)GestureDetectorCompatImplBase.DOUBLE_TAP_TIMEOUT);
                                b2 = false;
                            }
                        }
                        this.mLastFocusX = n4;
                        this.mDownFocusX = n4;
                        this.mLastFocusY = n5;
                        this.mDownFocusY = n5;
                        if (this.mCurrentDownEvent != null) {
                            this.mCurrentDownEvent.recycle();
                        }
                        this.mCurrentDownEvent = MotionEvent.obtain(motionEvent);
                        this.mAlwaysInTapRegion = true;
                        this.mAlwaysInBiggerTapRegion = true;
                        this.mStillDown = true;
                        this.mInLongPress = false;
                        this.mDeferConfirmSingleTap = false;
                        if (this.mIsLongpressEnabled) {
                            this.mHandler.removeMessages(2);
                            this.mHandler.sendEmptyMessageAtTime(2, this.mCurrentDownEvent.getDownTime() + GestureDetectorCompatImplBase.TAP_TIMEOUT + GestureDetectorCompatImplBase.LONGPRESS_TIMEOUT);
                        }
                        this.mHandler.sendEmptyMessageAtTime(1, this.mCurrentDownEvent.getDownTime() + GestureDetectorCompatImplBase.TAP_TIMEOUT);
                        return b2 | this.mListener.onDown(motionEvent);
                    }
                    case 2: {
                        final boolean mInLongPress = this.mInLongPress;
                        onScroll = false;
                        if (mInLongPress) {
                            break;
                        }
                        final float n9 = this.mLastFocusX - n4;
                        final float n10 = this.mLastFocusY - n5;
                        if (this.mIsDoubleTapping) {
                            return false | this.mDoubleTapListener.onDoubleTapEvent(motionEvent);
                        }
                        if (!this.mAlwaysInTapRegion) {
                            if (Math.abs(n9) < 1.0f) {
                                final float n11 = fcmpl(Math.abs(n10), 1.0f);
                                onScroll = false;
                                if (n11 < 0) {
                                    break;
                                }
                            }
                            final boolean onScroll2 = this.mListener.onScroll(this.mCurrentDownEvent, motionEvent, n9, n10);
                            this.mLastFocusX = n4;
                            this.mLastFocusY = n5;
                            return onScroll2;
                        }
                        final int n12 = (int)(n4 - this.mDownFocusX);
                        final int n13 = (int)(n5 - this.mDownFocusY);
                        final int n14 = n12 * n12 + n13 * n13;
                        final int mTouchSlopSquare = this.mTouchSlopSquare;
                        onScroll = false;
                        if (n14 > mTouchSlopSquare) {
                            onScroll = this.mListener.onScroll(this.mCurrentDownEvent, motionEvent, n9, n10);
                            this.mLastFocusX = n4;
                            this.mLastFocusY = n5;
                            this.mAlwaysInTapRegion = false;
                            this.mHandler.removeMessages(3);
                            this.mHandler.removeMessages(1);
                            this.mHandler.removeMessages(2);
                        }
                        if (n14 > this.mTouchSlopSquare) {
                            this.mAlwaysInBiggerTapRegion = false;
                            return onScroll;
                        }
                        break;
                    }
                    case 1: {
                        this.mStillDown = false;
                        final MotionEvent obtain = MotionEvent.obtain(motionEvent);
                        boolean b3 = false;
                        Label_0943: {
                            if (this.mIsDoubleTapping) {
                                b3 = (false | this.mDoubleTapListener.onDoubleTapEvent(motionEvent));
                            }
                            else if (this.mInLongPress) {
                                this.mHandler.removeMessages(3);
                                this.mInLongPress = false;
                                b3 = false;
                            }
                            else if (this.mAlwaysInTapRegion) {
                                b3 = this.mListener.onSingleTapUp(motionEvent);
                                if (this.mDeferConfirmSingleTap && this.mDoubleTapListener != null) {
                                    this.mDoubleTapListener.onSingleTapConfirmed(motionEvent);
                                }
                            }
                            else {
                                final VelocityTracker mVelocityTracker = this.mVelocityTracker;
                                final int pointerId3 = MotionEventCompat.getPointerId(motionEvent, 0);
                                mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumFlingVelocity);
                                final float yVelocity2 = VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId3);
                                final float xVelocity2 = VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId3);
                                if (Math.abs(yVelocity2) <= this.mMinimumFlingVelocity) {
                                    final float n15 = fcmpl(Math.abs(xVelocity2), (float)this.mMinimumFlingVelocity);
                                    b3 = false;
                                    if (n15 <= 0) {
                                        break Label_0943;
                                    }
                                }
                                b3 = this.mListener.onFling(this.mCurrentDownEvent, motionEvent, xVelocity2, yVelocity2);
                            }
                        }
                        if (this.mPreviousUpEvent != null) {
                            this.mPreviousUpEvent.recycle();
                        }
                        this.mPreviousUpEvent = obtain;
                        if (this.mVelocityTracker != null) {
                            this.mVelocityTracker.recycle();
                            this.mVelocityTracker = null;
                        }
                        this.mIsDoubleTapping = false;
                        this.mDeferConfirmSingleTap = false;
                        this.mHandler.removeMessages(1);
                        this.mHandler.removeMessages(2);
                        return b3;
                    }
                    case 3: {
                        this.cancel();
                        return false;
                    }
                }
            }
            return onScroll;
        }
        
        @Override
        public void setIsLongpressEnabled(final boolean mIsLongpressEnabled) {
            this.mIsLongpressEnabled = mIsLongpressEnabled;
        }
        
        @Override
        public void setOnDoubleTapListener(final GestureDetector$OnDoubleTapListener mDoubleTapListener) {
            this.mDoubleTapListener = mDoubleTapListener;
        }
        
        private class GestureHandler extends Handler
        {
            GestureHandler() {
                super();
            }
            
            GestureHandler(final Handler handler) {
                super(handler.getLooper());
            }
            
            public void handleMessage(final Message message) {
                switch (message.what) {
                    default: {
                        throw new RuntimeException("Unknown message " + message);
                    }
                    case 1: {
                        GestureDetectorCompatImplBase.this.mListener.onShowPress(GestureDetectorCompatImplBase.this.mCurrentDownEvent);
                        break;
                    }
                    case 2: {
                        GestureDetectorCompatImplBase.this.dispatchLongPress();
                    }
                    case 3: {
                        if (GestureDetectorCompatImplBase.this.mDoubleTapListener == null) {
                            break;
                        }
                        if (!GestureDetectorCompatImplBase.this.mStillDown) {
                            GestureDetectorCompatImplBase.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetectorCompatImplBase.this.mCurrentDownEvent);
                            return;
                        }
                        GestureDetectorCompatImplBase.this.mDeferConfirmSingleTap = true;
                    }
                }
            }
        }
    }
    
    static class GestureDetectorCompatImplJellybeanMr2 implements GestureDetectorCompatImpl
    {
        private final GestureDetector mDetector;
        
        public GestureDetectorCompatImplJellybeanMr2(final Context context, final GestureDetector$OnGestureListener gestureDetector$OnGestureListener, final Handler handler) {
            super();
            this.mDetector = new GestureDetector(context, gestureDetector$OnGestureListener, handler);
        }
        
        @Override
        public boolean isLongpressEnabled() {
            return this.mDetector.isLongpressEnabled();
        }
        
        @Override
        public boolean onTouchEvent(final MotionEvent motionEvent) {
            return this.mDetector.onTouchEvent(motionEvent);
        }
        
        @Override
        public void setIsLongpressEnabled(final boolean isLongpressEnabled) {
            this.mDetector.setIsLongpressEnabled(isLongpressEnabled);
        }
        
        @Override
        public void setOnDoubleTapListener(final GestureDetector$OnDoubleTapListener onDoubleTapListener) {
            this.mDetector.setOnDoubleTapListener(onDoubleTapListener);
        }
    }
}
