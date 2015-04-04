package android.support.v7.internal.widget;

import android.content.*;
import android.util.*;
import android.content.res.*;
import android.graphics.drawable.shapes.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.animation.*;
import android.view.*;
import android.os.*;

public class ProgressBarICS extends View
{
    private static final int ANIMATION_RESOLUTION = 200;
    private static final int MAX_LEVEL = 10000;
    private static final int[] android_R_styleable_ProgressBar;
    private AlphaAnimation mAnimation;
    private int mBehavior;
    private Drawable mCurrentDrawable;
    private int mDuration;
    private boolean mInDrawing;
    private boolean mIndeterminate;
    private Drawable mIndeterminateDrawable;
    private Interpolator mInterpolator;
    private long mLastDrawTime;
    private int mMax;
    int mMaxHeight;
    int mMaxWidth;
    int mMinHeight;
    int mMinWidth;
    private boolean mNoInvalidate;
    private boolean mOnlyIndeterminate;
    private int mProgress;
    private Drawable mProgressDrawable;
    private RefreshProgressRunnable mRefreshProgressRunnable;
    Bitmap mSampleTile;
    private int mSecondaryProgress;
    private boolean mShouldStartAnimationDrawable;
    private Transformation mTransformation;
    private long mUiThreadId;
    
    static {
        android_R_styleable_ProgressBar = new int[] { 16843062, 16843063, 16843064, 16843065, 16843066, 16843067, 16843068, 16843069, 16843070, 16843071, 16843039, 16843072, 16843040, 16843073 };
    }
    
    public ProgressBarICS(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n);
        this.mUiThreadId = Thread.currentThread().getId();
        this.initProgressBar();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, ProgressBarICS.android_R_styleable_ProgressBar, n, n2);
        this.mNoInvalidate = true;
        this.setMax(obtainStyledAttributes.getInt(0, this.mMax));
        this.setProgress(obtainStyledAttributes.getInt(1, this.mProgress));
        this.setSecondaryProgress(obtainStyledAttributes.getInt(2, this.mSecondaryProgress));
        final boolean boolean1 = obtainStyledAttributes.getBoolean(3, this.mIndeterminate);
        this.mOnlyIndeterminate = obtainStyledAttributes.getBoolean(4, this.mOnlyIndeterminate);
        final Drawable drawable = obtainStyledAttributes.getDrawable(5);
        if (drawable != null) {
            this.setIndeterminateDrawable(this.tileifyIndeterminate(drawable));
        }
        final Drawable drawable2 = obtainStyledAttributes.getDrawable(6);
        if (drawable2 != null) {
            this.setProgressDrawable(this.tileify(drawable2, false));
        }
        this.mDuration = obtainStyledAttributes.getInt(7, this.mDuration);
        this.mBehavior = obtainStyledAttributes.getInt(8, this.mBehavior);
        this.mMinWidth = obtainStyledAttributes.getDimensionPixelSize(9, this.mMinWidth);
        this.mMaxWidth = obtainStyledAttributes.getDimensionPixelSize(10, this.mMaxWidth);
        this.mMinHeight = obtainStyledAttributes.getDimensionPixelSize(11, this.mMinHeight);
        this.mMaxHeight = obtainStyledAttributes.getDimensionPixelSize(12, this.mMaxHeight);
        final int resourceId = obtainStyledAttributes.getResourceId(13, 17432587);
        if (resourceId > 0) {
            this.setInterpolator(context, resourceId);
        }
        obtainStyledAttributes.recycle();
        this.mNoInvalidate = false;
        boolean indeterminate = false;
        Label_0296: {
            if (!this.mOnlyIndeterminate) {
                indeterminate = false;
                if (!boolean1) {
                    break Label_0296;
                }
            }
            indeterminate = true;
        }
        this.setIndeterminate(indeterminate);
    }
    
    private void doRefreshProgress(final int n, final int n2, final boolean b, final boolean b2) {
        while (true) {
        Label_0059_Outer:
            while (true) {
                while (true) {
                    float n3 = 0.0f;
                    Drawable drawableByLayerId = null;
                    Label_0097: {
                        synchronized (this) {
                            if (this.mMax > 0) {
                                n3 = n2 / this.mMax;
                            }
                            else {
                                n3 = 0.0f;
                            }
                            final Drawable mCurrentDrawable = this.mCurrentDrawable;
                            if (mCurrentDrawable == null) {
                                this.invalidate();
                                return;
                            }
                            final boolean b3 = mCurrentDrawable instanceof LayerDrawable;
                            drawableByLayerId = null;
                            if (b3) {
                                drawableByLayerId = ((LayerDrawable)mCurrentDrawable).findDrawableByLayerId(n);
                            }
                            break Label_0097;
                            drawableByLayerId = mCurrentDrawable;
                            final int level;
                            drawableByLayerId.setLevel(level);
                            return;
                        }
                    }
                    final int level = (int)(10000.0f * n3);
                    if (drawableByLayerId != null) {
                        continue;
                    }
                    break;
                }
                continue Label_0059_Outer;
            }
        }
    }
    
    private void initProgressBar() {
        this.mMax = 100;
        this.mProgress = 0;
        this.mSecondaryProgress = 0;
        this.mIndeterminate = false;
        this.mOnlyIndeterminate = false;
        this.mDuration = 4000;
        this.mBehavior = 1;
        this.mMinWidth = 24;
        this.mMaxWidth = 48;
        this.mMinHeight = 24;
        this.mMaxHeight = 48;
    }
    
    private void refreshProgress(final int n, final int n2, final boolean b) {
        while (true) {
            while (true) {
                Label_0070: {
                    synchronized (this) {
                        if (this.mUiThreadId == Thread.currentThread().getId()) {
                            this.doRefreshProgress(n, n2, b, true);
                        }
                        else {
                            if (this.mRefreshProgressRunnable == null) {
                                break Label_0070;
                            }
                            final RefreshProgressRunnable mRefreshProgressRunnable = this.mRefreshProgressRunnable;
                            this.mRefreshProgressRunnable = null;
                            mRefreshProgressRunnable.setup(n, n2, b);
                            this.post((Runnable)mRefreshProgressRunnable);
                        }
                        return;
                    }
                }
                final RefreshProgressRunnable mRefreshProgressRunnable = new RefreshProgressRunnable(n, n2, b);
                continue;
            }
        }
    }
    
    private Drawable tileify(final Drawable drawable, final boolean b) {
        LayerDrawable layerDrawable2;
        if (drawable instanceof LayerDrawable) {
            final LayerDrawable layerDrawable = (LayerDrawable)drawable;
            final int numberOfLayers = layerDrawable.getNumberOfLayers();
            final Drawable[] array = new Drawable[numberOfLayers];
            for (int i = 0; i < numberOfLayers; ++i) {
                final int id = layerDrawable.getId(i);
                array[i] = this.tileify(layerDrawable.getDrawable(i), id == 16908301 || id == 16908303);
            }
            layerDrawable2 = new LayerDrawable(array);
            for (int j = 0; j < numberOfLayers; ++j) {
                layerDrawable2.setId(j, layerDrawable.getId(j));
            }
        }
        else {
            if (!(drawable instanceof BitmapDrawable)) {
                return drawable;
            }
            final Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            if (this.mSampleTile == null) {
                this.mSampleTile = bitmap;
            }
            Object o = new ShapeDrawable(this.getDrawableShape());
            ((ShapeDrawable)o).getPaint().setShader((Shader)new BitmapShader(bitmap, Shader$TileMode.REPEAT, Shader$TileMode.CLAMP));
            if (b) {
                o = new ClipDrawable((Drawable)o, 3, 1);
            }
            layerDrawable2 = (LayerDrawable)o;
        }
        return (Drawable)layerDrawable2;
    }
    
    private Drawable tileifyIndeterminate(Drawable drawable) {
        if (drawable instanceof AnimationDrawable) {
            final AnimationDrawable animationDrawable = (AnimationDrawable)drawable;
            final int numberOfFrames = animationDrawable.getNumberOfFrames();
            final AnimationDrawable animationDrawable2 = new AnimationDrawable();
            animationDrawable2.setOneShot(animationDrawable.isOneShot());
            for (int i = 0; i < numberOfFrames; ++i) {
                final Drawable tileify = this.tileify(animationDrawable.getFrame(i), true);
                tileify.setLevel(10000);
                animationDrawable2.addFrame(tileify, animationDrawable.getDuration(i));
            }
            animationDrawable2.setLevel(10000);
            drawable = (Drawable)animationDrawable2;
        }
        return drawable;
    }
    
    private void updateDrawableBounds(final int n, final int n2) {
        int n3 = n - this.getPaddingRight() - this.getPaddingLeft();
        int n4 = n2 - this.getPaddingBottom() - this.getPaddingTop();
        if (this.mIndeterminateDrawable != null) {
            final boolean mOnlyIndeterminate = this.mOnlyIndeterminate;
            int n5 = 0;
            int n6 = 0;
            if (mOnlyIndeterminate) {
                final boolean b = this.mIndeterminateDrawable instanceof AnimationDrawable;
                n5 = 0;
                n6 = 0;
                if (!b) {
                    final float n7 = this.mIndeterminateDrawable.getIntrinsicWidth() / this.mIndeterminateDrawable.getIntrinsicHeight();
                    final float n8 = n / n2;
                    final float n9 = fcmpl(n7, n8);
                    n5 = 0;
                    n6 = 0;
                    if (n9 != 0) {
                        if (n8 > n7) {
                            final int n10 = (int)(n7 * n2);
                            n5 = (n - n10) / 2;
                            n3 = n5 + n10;
                        }
                        else {
                            final int n11 = (int)(n * (1.0f / n7));
                            n6 = (n2 - n11) / 2;
                            n4 = n6 + n11;
                            n5 = 0;
                        }
                    }
                }
            }
            this.mIndeterminateDrawable.setBounds(n5, n6, n3, n4);
        }
        if (this.mProgressDrawable != null) {
            this.mProgressDrawable.setBounds(0, 0, n3, n4);
        }
    }
    
    private void updateDrawableState() {
        final int[] drawableState = this.getDrawableState();
        if (this.mProgressDrawable != null && this.mProgressDrawable.isStateful()) {
            this.mProgressDrawable.setState(drawableState);
        }
        if (this.mIndeterminateDrawable != null && this.mIndeterminateDrawable.isStateful()) {
            this.mIndeterminateDrawable.setState(drawableState);
        }
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.updateDrawableState();
    }
    
    Shape getDrawableShape() {
        return (Shape)new RoundRectShape(new float[] { 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f, 5.0f }, (RectF)null, (float[])null);
    }
    
    public Drawable getIndeterminateDrawable() {
        return this.mIndeterminateDrawable;
    }
    
    public Interpolator getInterpolator() {
        return this.mInterpolator;
    }
    
    public int getMax() {
        synchronized (this) {
            return this.mMax;
        }
    }
    
    public int getProgress() {
        synchronized (this) {
            int mProgress;
            if (this.mIndeterminate) {
                mProgress = 0;
            }
            else {
                mProgress = this.mProgress;
            }
            return mProgress;
        }
    }
    
    public Drawable getProgressDrawable() {
        return this.mProgressDrawable;
    }
    
    public int getSecondaryProgress() {
        synchronized (this) {
            int mSecondaryProgress;
            if (this.mIndeterminate) {
                mSecondaryProgress = 0;
            }
            else {
                mSecondaryProgress = this.mSecondaryProgress;
            }
            return mSecondaryProgress;
        }
    }
    
    public final void incrementProgressBy(final int n) {
        synchronized (this) {
            this.setProgress(n + this.mProgress);
        }
    }
    
    public final void incrementSecondaryProgressBy(final int n) {
        synchronized (this) {
            this.setSecondaryProgress(n + this.mSecondaryProgress);
        }
    }
    
    public void invalidateDrawable(final Drawable drawable) {
        if (!this.mInDrawing) {
            if (!this.verifyDrawable(drawable)) {
                super.invalidateDrawable(drawable);
                return;
            }
            final Rect bounds = drawable.getBounds();
            final int n = this.getScrollX() + this.getPaddingLeft();
            final int n2 = this.getScrollY() + this.getPaddingTop();
            this.invalidate(n + bounds.left, n2 + bounds.top, n + bounds.right, n2 + bounds.bottom);
        }
    }
    
    public boolean isIndeterminate() {
        synchronized (this) {
            return this.mIndeterminate;
        }
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIndeterminate) {
            this.startAnimation();
        }
    }
    
    protected void onDetachedFromWindow() {
        if (this.mIndeterminate) {
            this.stopAnimation();
        }
        if (this.mRefreshProgressRunnable != null) {
            this.removeCallbacks((Runnable)this.mRefreshProgressRunnable);
        }
        super.onDetachedFromWindow();
    }
    
    protected void onDraw(final Canvas canvas) {
        synchronized (this) {
            super.onDraw(canvas);
            final Drawable mCurrentDrawable = this.mCurrentDrawable;
            if (mCurrentDrawable == null) {
                return;
            }
            canvas.save();
            canvas.translate((float)this.getPaddingLeft(), (float)this.getPaddingTop());
            final long drawingTime = this.getDrawingTime();
            Label_0121: {
                if (this.mAnimation == null) {
                    break Label_0121;
                }
                this.mAnimation.getTransformation(drawingTime, this.mTransformation);
                final float alpha = this.mTransformation.getAlpha();
                try {
                    this.mInDrawing = true;
                    mCurrentDrawable.setLevel((int)(10000.0f * alpha));
                    this.mInDrawing = false;
                    if (SystemClock.uptimeMillis() - this.mLastDrawTime >= 200L) {
                        this.mLastDrawTime = SystemClock.uptimeMillis();
                        this.postInvalidateDelayed(200L);
                    }
                    mCurrentDrawable.draw(canvas);
                    canvas.restore();
                    if (this.mShouldStartAnimationDrawable && mCurrentDrawable instanceof Animatable) {
                        ((Animatable)mCurrentDrawable).start();
                        this.mShouldStartAnimationDrawable = false;
                    }
                }
                finally {
                    this.mInDrawing = false;
                }
            }
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        synchronized (this) {
            final Drawable mCurrentDrawable = this.mCurrentDrawable;
            int max = 0;
            int max2 = 0;
            if (mCurrentDrawable != null) {
                max2 = Math.max(this.mMinWidth, Math.min(this.mMaxWidth, mCurrentDrawable.getIntrinsicWidth()));
                max = Math.max(this.mMinHeight, Math.min(this.mMaxHeight, mCurrentDrawable.getIntrinsicHeight()));
            }
            this.updateDrawableState();
            this.setMeasuredDimension(resolveSize(max2 + (this.getPaddingLeft() + this.getPaddingRight()), n), resolveSize(max + (this.getPaddingTop() + this.getPaddingBottom()), n2));
        }
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.setProgress(savedState.progress);
        this.setSecondaryProgress(savedState.secondaryProgress);
    }
    
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.progress = this.mProgress;
        savedState.secondaryProgress = this.mSecondaryProgress;
        return (Parcelable)savedState;
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        this.updateDrawableBounds(n, n2);
    }
    
    protected void onVisibilityChanged(final View view, final int n) {
        super.onVisibilityChanged(view, n);
        if (this.mIndeterminate) {
            if (n != 8 && n != 4) {
                this.startAnimation();
                return;
            }
            this.stopAnimation();
        }
    }
    
    public void postInvalidate() {
        if (!this.mNoInvalidate) {
            super.postInvalidate();
        }
    }
    
    public void setIndeterminate(final boolean mIndeterminate) {
        synchronized (this) {
            if ((!this.mOnlyIndeterminate || !this.mIndeterminate) && mIndeterminate != this.mIndeterminate) {
                this.mIndeterminate = mIndeterminate;
                if (mIndeterminate) {
                    this.mCurrentDrawable = this.mIndeterminateDrawable;
                    this.startAnimation();
                }
                else {
                    this.mCurrentDrawable = this.mProgressDrawable;
                    this.stopAnimation();
                }
            }
        }
    }
    
    public void setIndeterminateDrawable(final Drawable drawable) {
        if (drawable != null) {
            drawable.setCallback((Drawable$Callback)this);
        }
        this.mIndeterminateDrawable = drawable;
        if (this.mIndeterminate) {
            this.mCurrentDrawable = drawable;
            this.postInvalidate();
        }
    }
    
    public void setInterpolator(final Context context, final int n) {
        this.setInterpolator(AnimationUtils.loadInterpolator(context, n));
    }
    
    public void setInterpolator(final Interpolator mInterpolator) {
        this.mInterpolator = mInterpolator;
    }
    
    public void setMax(int n) {
        // monitorenter(this)
        if (n < 0) {
            n = 0;
        }
        try {
            if (n != this.mMax) {
                this.mMax = n;
                this.postInvalidate();
                if (this.mProgress > n) {
                    this.mProgress = n;
                }
                this.refreshProgress(16908301, this.mProgress, false);
            }
        }
        finally {
        }
        // monitorexit(this)
    }
    
    public void setProgress(final int n) {
        synchronized (this) {
            this.setProgress(n, false);
        }
    }
    
    void setProgress(int mMax, final boolean b) {
        synchronized (this) {
            if (!this.mIndeterminate) {
                if (mMax < 0) {
                    mMax = 0;
                }
                if (mMax > this.mMax) {
                    mMax = this.mMax;
                }
                if (mMax != this.mProgress) {
                    this.refreshProgress(16908301, this.mProgress = mMax, b);
                }
            }
        }
    }
    
    public void setProgressDrawable(final Drawable drawable) {
        int n;
        if (this.mProgressDrawable != null && drawable != this.mProgressDrawable) {
            this.mProgressDrawable.setCallback((Drawable$Callback)null);
            n = 1;
        }
        else {
            n = 0;
        }
        if (drawable != null) {
            drawable.setCallback((Drawable$Callback)this);
            final int minimumHeight = drawable.getMinimumHeight();
            if (this.mMaxHeight < minimumHeight) {
                this.mMaxHeight = minimumHeight;
                this.requestLayout();
            }
        }
        this.mProgressDrawable = drawable;
        if (!this.mIndeterminate) {
            this.mCurrentDrawable = drawable;
            this.postInvalidate();
        }
        if (n != 0) {
            this.updateDrawableBounds(this.getWidth(), this.getHeight());
            this.updateDrawableState();
            this.doRefreshProgress(16908301, this.mProgress, false, false);
            this.doRefreshProgress(16908303, this.mSecondaryProgress, false, false);
        }
    }
    
    public void setSecondaryProgress(int mMax) {
        synchronized (this) {
            if (!this.mIndeterminate) {
                if (mMax < 0) {
                    mMax = 0;
                }
                if (mMax > this.mMax) {
                    mMax = this.mMax;
                }
                if (mMax != this.mSecondaryProgress) {
                    this.refreshProgress(16908303, this.mSecondaryProgress = mMax, false);
                }
            }
        }
    }
    
    public void setVisibility(final int visibility) {
        if (this.getVisibility() != visibility) {
            super.setVisibility(visibility);
            if (this.mIndeterminate) {
                if (visibility != 8 && visibility != 4) {
                    this.startAnimation();
                    return;
                }
                this.stopAnimation();
            }
        }
    }
    
    void startAnimation() {
        if (this.getVisibility() != 0) {
            return;
        }
        if (this.mIndeterminateDrawable instanceof Animatable) {
            this.mShouldStartAnimationDrawable = true;
            this.mAnimation = null;
        }
        else {
            if (this.mInterpolator == null) {
                this.mInterpolator = (Interpolator)new LinearInterpolator();
            }
            this.mTransformation = new Transformation();
            (this.mAnimation = new AlphaAnimation(0.0f, 1.0f)).setRepeatMode(this.mBehavior);
            this.mAnimation.setRepeatCount(-1);
            this.mAnimation.setDuration((long)this.mDuration);
            this.mAnimation.setInterpolator(this.mInterpolator);
            this.mAnimation.setStartTime(-1L);
        }
        this.postInvalidate();
    }
    
    void stopAnimation() {
        this.mAnimation = null;
        this.mTransformation = null;
        if (this.mIndeterminateDrawable instanceof Animatable) {
            ((Animatable)this.mIndeterminateDrawable).stop();
            this.mShouldStartAnimationDrawable = false;
        }
        this.postInvalidate();
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        return drawable == this.mProgressDrawable || drawable == this.mIndeterminateDrawable || super.verifyDrawable(drawable);
    }
    
    private class RefreshProgressRunnable implements Runnable
    {
        private boolean mFromUser;
        private int mId;
        private int mProgress;
        
        RefreshProgressRunnable(final int mId, final int mProgress, final boolean mFromUser) {
            super();
            this.mId = mId;
            this.mProgress = mProgress;
            this.mFromUser = mFromUser;
        }
        
        @Override
        public void run() {
            ProgressBarICS.this.doRefreshProgress(this.mId, this.mProgress, this.mFromUser, true);
            ProgressBarICS.this.mRefreshProgressRunnable = this;
        }
        
        public void setup(final int mId, final int mProgress, final boolean mFromUser) {
            this.mId = mId;
            this.mProgress = mProgress;
            this.mFromUser = mFromUser;
        }
    }
    
    static class SavedState extends View$BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int progress;
        int secondaryProgress;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        private SavedState(final Parcel parcel) {
            super(parcel);
            this.progress = parcel.readInt();
            this.secondaryProgress = parcel.readInt();
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.progress);
            parcel.writeInt(this.secondaryProgress);
        }
    }
}
