package android.support.v7.internal.widget;

import android.database.*;
import android.graphics.*;
import android.content.*;
import android.widget.*;
import android.util.*;
import android.view.*;
import android.os.*;

abstract class AbsSpinnerICS extends AdapterViewICS<SpinnerAdapter>
{
    SpinnerAdapter mAdapter;
    boolean mBlockLayoutRequests;
    private DataSetObserver mDataSetObserver;
    int mHeightMeasureSpec;
    final RecycleBin mRecycler;
    int mSelectionBottomPadding;
    int mSelectionLeftPadding;
    int mSelectionRightPadding;
    int mSelectionTopPadding;
    final Rect mSpinnerPadding;
    private Rect mTouchFrame;
    int mWidthMeasureSpec;
    
    AbsSpinnerICS(final Context context) {
        super(context);
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mSpinnerPadding = new Rect();
        this.mRecycler = new RecycleBin();
        this.initAbsSpinner();
    }
    
    AbsSpinnerICS(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    AbsSpinnerICS(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mSelectionLeftPadding = 0;
        this.mSelectionTopPadding = 0;
        this.mSelectionRightPadding = 0;
        this.mSelectionBottomPadding = 0;
        this.mSpinnerPadding = new Rect();
        this.mRecycler = new RecycleBin();
        this.initAbsSpinner();
    }
    
    static /* synthetic */ void access$100(final AbsSpinnerICS absSpinnerICS, final View view, final boolean b) {
        absSpinnerICS.removeDetachedView(view, b);
    }
    
    private void initAbsSpinner() {
        this.setFocusable(true);
        this.setWillNotDraw(false);
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup$LayoutParams(-1, -2);
    }
    
    @Override
    public SpinnerAdapter getAdapter() {
        return this.mAdapter;
    }
    
    int getChildHeight(final View view) {
        return view.getMeasuredHeight();
    }
    
    int getChildWidth(final View view) {
        return view.getMeasuredWidth();
    }
    
    @Override
    public int getCount() {
        return this.mItemCount;
    }
    
    @Override
    public View getSelectedView() {
        if (this.mItemCount > 0 && this.mSelectedPosition >= 0) {
            return this.getChildAt(this.mSelectedPosition - this.mFirstPosition);
        }
        return null;
    }
    
    abstract void layout(final int p0, final boolean p1);
    
    protected void onMeasure(final int mWidthMeasureSpec, final int mHeightMeasureSpec) {
        final int mode = View$MeasureSpec.getMode(mWidthMeasureSpec);
        int left = this.getPaddingLeft();
        int top = this.getPaddingTop();
        int right = this.getPaddingRight();
        int bottom = this.getPaddingBottom();
        final Rect mSpinnerPadding = this.mSpinnerPadding;
        if (left <= this.mSelectionLeftPadding) {
            left = this.mSelectionLeftPadding;
        }
        mSpinnerPadding.left = left;
        final Rect mSpinnerPadding2 = this.mSpinnerPadding;
        if (top <= this.mSelectionTopPadding) {
            top = this.mSelectionTopPadding;
        }
        mSpinnerPadding2.top = top;
        final Rect mSpinnerPadding3 = this.mSpinnerPadding;
        if (right <= this.mSelectionRightPadding) {
            right = this.mSelectionRightPadding;
        }
        mSpinnerPadding3.right = right;
        final Rect mSpinnerPadding4 = this.mSpinnerPadding;
        if (bottom <= this.mSelectionBottomPadding) {
            bottom = this.mSelectionBottomPadding;
        }
        mSpinnerPadding4.bottom = bottom;
        if (this.mDataChanged) {
            this.handleDataChanged();
        }
        boolean b = true;
        final int selectedItemPosition = this.getSelectedItemPosition();
        int n = 0;
        int n2 = 0;
        if (selectedItemPosition >= 0) {
            final SpinnerAdapter mAdapter = this.mAdapter;
            n = 0;
            n2 = 0;
            if (mAdapter != null) {
                final int count = this.mAdapter.getCount();
                n = 0;
                n2 = 0;
                if (selectedItemPosition < count) {
                    View view = this.mRecycler.get(selectedItemPosition);
                    if (view == null) {
                        view = this.mAdapter.getView(selectedItemPosition, (View)null, (ViewGroup)this);
                    }
                    if (view != null) {
                        this.mRecycler.put(selectedItemPosition, view);
                    }
                    n = 0;
                    n2 = 0;
                    if (view != null) {
                        if (view.getLayoutParams() == null) {
                            this.mBlockLayoutRequests = true;
                            view.setLayoutParams(this.generateDefaultLayoutParams());
                            this.mBlockLayoutRequests = false;
                        }
                        this.measureChild(view, mWidthMeasureSpec, mHeightMeasureSpec);
                        n = this.getChildHeight(view) + this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
                        n2 = this.getChildWidth(view) + this.mSpinnerPadding.left + this.mSpinnerPadding.right;
                        b = false;
                    }
                }
            }
        }
        if (b) {
            n = this.mSpinnerPadding.top + this.mSpinnerPadding.bottom;
            if (mode == 0) {
                n2 = this.mSpinnerPadding.left + this.mSpinnerPadding.right;
            }
        }
        this.setMeasuredDimension(resolveSize(Math.max(n2, this.getSuggestedMinimumWidth()), mWidthMeasureSpec), resolveSize(Math.max(n, this.getSuggestedMinimumHeight()), mHeightMeasureSpec));
        this.mHeightMeasureSpec = mHeightMeasureSpec;
        this.mWidthMeasureSpec = mWidthMeasureSpec;
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.selectedId >= 0L) {
            this.mDataChanged = true;
            this.mNeedSync = true;
            this.mSyncRowId = savedState.selectedId;
            this.mSyncPosition = savedState.position;
            this.mSyncMode = 0;
            this.requestLayout();
        }
    }
    
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.selectedId = this.getSelectedItemId();
        if (savedState.selectedId >= 0L) {
            savedState.position = this.getSelectedItemPosition();
            return (Parcelable)savedState;
        }
        savedState.position = -1;
        return (Parcelable)savedState;
    }
    
    public int pointToPosition(final int n, final int n2) {
        Rect rect = this.mTouchFrame;
        if (rect == null) {
            this.mTouchFrame = new Rect();
            rect = this.mTouchFrame;
        }
        for (int i = -1 + this.getChildCount(); i >= 0; --i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 0) {
                child.getHitRect(rect);
                if (rect.contains(n, n2)) {
                    return i + this.mFirstPosition;
                }
            }
        }
        return -1;
    }
    
    void recycleAllViews() {
        final int childCount = this.getChildCount();
        final RecycleBin mRecycler = this.mRecycler;
        final int mFirstPosition = this.mFirstPosition;
        for (int i = 0; i < childCount; ++i) {
            mRecycler.put(mFirstPosition + i, this.getChildAt(i));
        }
    }
    
    public void requestLayout() {
        if (!this.mBlockLayoutRequests) {
            super.requestLayout();
        }
    }
    
    void resetList() {
        this.mDataChanged = false;
        this.mNeedSync = false;
        this.removeAllViewsInLayout();
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        this.setSelectedPositionInt(-1);
        this.setNextSelectedPositionInt(-1);
        this.invalidate();
    }
    
    @Override
    public void setAdapter(final SpinnerAdapter mAdapter) {
        int nextSelectedPositionInt = -1;
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
            this.resetList();
        }
        this.mAdapter = mAdapter;
        this.mOldSelectedPosition = nextSelectedPositionInt;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        if (this.mAdapter != null) {
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
            this.checkFocus();
            this.mDataSetObserver = new AdapterDataSetObserver(this);
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            if (this.mItemCount > 0) {
                nextSelectedPositionInt = 0;
            }
            this.setSelectedPositionInt(nextSelectedPositionInt);
            this.setNextSelectedPositionInt(nextSelectedPositionInt);
            if (this.mItemCount == 0) {
                this.checkSelectionChanged();
            }
        }
        else {
            this.checkFocus();
            this.resetList();
            this.checkSelectionChanged();
        }
        this.requestLayout();
    }
    
    @Override
    public void setSelection(final int nextSelectedPositionInt) {
        this.setNextSelectedPositionInt(nextSelectedPositionInt);
        this.requestLayout();
        this.invalidate();
    }
    
    public void setSelection(final int n, final boolean b) {
        this.setSelectionInt(n, b && this.mFirstPosition <= n && n <= -1 + (this.mFirstPosition + this.getChildCount()));
    }
    
    void setSelectionInt(final int nextSelectedPositionInt, final boolean b) {
        if (nextSelectedPositionInt != this.mOldSelectedPosition) {
            this.mBlockLayoutRequests = true;
            final int n = nextSelectedPositionInt - this.mSelectedPosition;
            this.setNextSelectedPositionInt(nextSelectedPositionInt);
            this.layout(n, b);
            this.mBlockLayoutRequests = false;
        }
    }
    
    class RecycleBin
    {
        private final SparseArray<View> mScrapHeap;
        
        RecycleBin() {
            super();
            this.mScrapHeap = (SparseArray<View>)new SparseArray();
        }
        
        void clear() {
            final SparseArray<View> mScrapHeap = this.mScrapHeap;
            for (int size = mScrapHeap.size(), i = 0; i < size; ++i) {
                final View view = (View)mScrapHeap.valueAt(i);
                if (view != null) {
                    AbsSpinnerICS.access$100(AbsSpinnerICS.this, view, true);
                }
            }
            mScrapHeap.clear();
        }
        
        View get(final int n) {
            final View view = (View)this.mScrapHeap.get(n);
            if (view != null) {
                this.mScrapHeap.delete(n);
            }
            return view;
        }
        
        public void put(final int n, final View view) {
            this.mScrapHeap.put(n, (Object)view);
        }
    }
    
    static class SavedState extends View$BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int position;
        long selectedId;
        
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
            this.selectedId = parcel.readLong();
            this.position = parcel.readInt();
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public String toString() {
            return "AbsSpinner.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " selectedId=" + this.selectedId + " position=" + this.position + "}";
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeLong(this.selectedId);
            parcel.writeInt(this.position);
        }
    }
}
