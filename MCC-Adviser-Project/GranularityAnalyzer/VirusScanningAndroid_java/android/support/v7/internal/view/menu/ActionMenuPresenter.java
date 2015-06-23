package android.support.v7.internal.view.menu;

import android.support.v4.view.*;
import android.support.v7.appcompat.*;
import java.util.*;
import android.view.*;
import android.support.v7.internal.view.*;
import android.content.res.*;
import android.content.*;
import android.widget.*;
import android.util.*;
import android.os.*;

public class ActionMenuPresenter extends BaseMenuPresenter implements SubUiVisibilityListener
{
    private static final String TAG = "ActionMenuPresenter";
    private final SparseBooleanArray mActionButtonGroups;
    private ActionButtonSubmenu mActionButtonPopup;
    private int mActionItemWidthLimit;
    private boolean mExpandedActionViewsExclusive;
    private int mMaxItems;
    private boolean mMaxItemsSet;
    private int mMinCellSize;
    int mOpenSubMenuId;
    private View mOverflowButton;
    private OverflowPopup mOverflowPopup;
    final PopupPresenterCallback mPopupPresenterCallback;
    private OpenOverflowRunnable mPostedOpenRunnable;
    private boolean mReserveOverflow;
    private boolean mReserveOverflowSet;
    private View mScrapActionButtonView;
    private boolean mStrictWidthLimit;
    private int mWidthLimit;
    private boolean mWidthLimitSet;
    
    public ActionMenuPresenter(final Context context) {
        super(context, R.layout.abc_action_menu_layout, R.layout.abc_action_menu_item_layout);
        this.mActionButtonGroups = new SparseBooleanArray();
        this.mPopupPresenterCallback = new PopupPresenterCallback();
    }
    
    private View findViewForItem(final MenuItem menuItem) {
        final ViewGroup viewGroup = (ViewGroup)this.mMenuView;
        if (viewGroup != null) {
            for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
                final Object child = viewGroup.getChildAt(i);
                if (child instanceof MenuView.ItemView && ((MenuView.ItemView)child).getItemData() == menuItem) {
                    return (View)child;
                }
            }
            return null;
        }
        final Object child = null;
        return (View)child;
    }
    
    @Override
    public void bindItemView(final MenuItemImpl menuItemImpl, final MenuView.ItemView itemView) {
        itemView.initialize(menuItemImpl, 0);
        ((ActionMenuItemView)itemView).setItemInvoker((MenuBuilder.ItemInvoker)this.mMenuView);
    }
    
    public boolean dismissPopupMenus() {
        return this.hideOverflowMenu() | this.hideSubMenus();
    }
    
    public boolean filterLeftoverView(final ViewGroup viewGroup, final int n) {
        return viewGroup.getChildAt(n) != this.mOverflowButton && super.filterLeftoverView(viewGroup, n);
    }
    
    @Override
    public boolean flagActionItems() {
        final ArrayList<MenuItemImpl> visibleItems = this.mMenu.getVisibleItems();
        final int size = visibleItems.size();
        int mMaxItems = this.mMaxItems;
        int mActionItemWidthLimit = this.mActionItemWidthLimit;
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(0, 0);
        final ViewGroup viewGroup = (ViewGroup)this.mMenuView;
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        boolean b = false;
        for (int i = 0; i < size; ++i) {
            final MenuItemImpl menuItemImpl = visibleItems.get(i);
            if (menuItemImpl.requiresActionButton()) {
                ++n;
            }
            else if (menuItemImpl.requestsActionButton()) {
                ++n2;
            }
            else {
                b = true;
            }
            if (this.mExpandedActionViewsExclusive && menuItemImpl.isActionViewExpanded()) {
                mMaxItems = 0;
            }
        }
        if (this.mReserveOverflow && (b || n + n2 > mMaxItems)) {
            --mMaxItems;
        }
        int n4 = mMaxItems - n;
        final SparseBooleanArray mActionButtonGroups = this.mActionButtonGroups;
        mActionButtonGroups.clear();
        final boolean mStrictWidthLimit = this.mStrictWidthLimit;
        int n5 = 0;
        int n6 = 0;
        if (mStrictWidthLimit) {
            n6 = mActionItemWidthLimit / this.mMinCellSize;
            n5 = this.mMinCellSize + mActionItemWidthLimit % this.mMinCellSize / n6;
        }
        for (int j = 0; j < size; ++j) {
            final MenuItemImpl menuItemImpl2 = visibleItems.get(j);
            if (menuItemImpl2.requiresActionButton()) {
                final View itemView = this.getItemView(menuItemImpl2, this.mScrapActionButtonView, viewGroup);
                if (this.mScrapActionButtonView == null) {
                    this.mScrapActionButtonView = itemView;
                }
                if (this.mStrictWidthLimit) {
                    n6 -= ActionMenuView.measureChildForCells(itemView, n5, n6, measureSpec, 0);
                }
                else {
                    itemView.measure(measureSpec, measureSpec);
                }
                final int measuredWidth = itemView.getMeasuredWidth();
                mActionItemWidthLimit -= measuredWidth;
                if (n3 == 0) {
                    n3 = measuredWidth;
                }
                final int groupId = menuItemImpl2.getGroupId();
                if (groupId != 0) {
                    mActionButtonGroups.put(groupId, true);
                }
                menuItemImpl2.setIsActionButton(true);
            }
            else if (menuItemImpl2.requestsActionButton()) {
                final int groupId2 = menuItemImpl2.getGroupId();
                final boolean value = mActionButtonGroups.get(groupId2);
                boolean isActionButton = (n4 > 0 || value) && mActionItemWidthLimit > 0 && (!this.mStrictWidthLimit || n6 > 0);
                if (isActionButton) {
                    final View itemView2 = this.getItemView(menuItemImpl2, this.mScrapActionButtonView, viewGroup);
                    if (this.mScrapActionButtonView == null) {
                        this.mScrapActionButtonView = itemView2;
                    }
                    if (this.mStrictWidthLimit) {
                        final int measureChildForCells = ActionMenuView.measureChildForCells(itemView2, n5, n6, measureSpec, 0);
                        n6 -= measureChildForCells;
                        if (measureChildForCells == 0) {
                            isActionButton = false;
                        }
                    }
                    else {
                        itemView2.measure(measureSpec, measureSpec);
                    }
                    final int measuredWidth2 = itemView2.getMeasuredWidth();
                    mActionItemWidthLimit -= measuredWidth2;
                    if (n3 == 0) {
                        n3 = measuredWidth2;
                    }
                    if (this.mStrictWidthLimit) {
                        isActionButton &= (mActionItemWidthLimit >= 0);
                    }
                    else {
                        isActionButton &= (mActionItemWidthLimit + n3 > 0);
                    }
                }
                if (isActionButton && groupId2 != 0) {
                    mActionButtonGroups.put(groupId2, true);
                }
                else if (value) {
                    mActionButtonGroups.put(groupId2, false);
                    for (int k = 0; k < j; ++k) {
                        final MenuItemImpl menuItemImpl3 = visibleItems.get(k);
                        if (menuItemImpl3.getGroupId() == groupId2) {
                            if (menuItemImpl3.isActionButton()) {
                                ++n4;
                            }
                            menuItemImpl3.setIsActionButton(false);
                        }
                    }
                }
                if (isActionButton) {
                    --n4;
                }
                menuItemImpl2.setIsActionButton(isActionButton);
            }
        }
        return true;
    }
    
    @Override
    public View getItemView(final MenuItemImpl menuItemImpl, View view, final ViewGroup viewGroup) {
        View view2 = menuItemImpl.getActionView();
        if (view2 == null || menuItemImpl.hasCollapsibleActionView()) {
            if (!(view instanceof ActionMenuItemView)) {
                view = null;
            }
            view2 = super.getItemView(menuItemImpl, view, viewGroup);
        }
        int visibility;
        if (menuItemImpl.isActionViewExpanded()) {
            visibility = 8;
        }
        else {
            visibility = 0;
        }
        view2.setVisibility(visibility);
        final ActionMenuView actionMenuView = (ActionMenuView)viewGroup;
        final ViewGroup$LayoutParams layoutParams = view2.getLayoutParams();
        if (!actionMenuView.checkLayoutParams(layoutParams)) {
            view2.setLayoutParams((ViewGroup$LayoutParams)actionMenuView.generateLayoutParams(layoutParams));
        }
        return view2;
    }
    
    @Override
    public MenuView getMenuView(final ViewGroup viewGroup) {
        final MenuView menuView = super.getMenuView(viewGroup);
        ((ActionMenuView)menuView).setPresenter(this);
        return menuView;
    }
    
    public boolean hideOverflowMenu() {
        if (this.mPostedOpenRunnable != null && this.mMenuView != null) {
            ((View)this.mMenuView).removeCallbacks((Runnable)this.mPostedOpenRunnable);
            this.mPostedOpenRunnable = null;
            return true;
        }
        final OverflowPopup mOverflowPopup = this.mOverflowPopup;
        if (mOverflowPopup != null) {
            mOverflowPopup.dismiss();
            return true;
        }
        return false;
    }
    
    public boolean hideSubMenus() {
        if (this.mActionButtonPopup != null) {
            this.mActionButtonPopup.dismiss();
            return true;
        }
        return false;
    }
    
    @Override
    public void initForMenu(final Context context, final MenuBuilder menuBuilder) {
        super.initForMenu(context, menuBuilder);
        final Resources resources = context.getResources();
        final ActionBarPolicy value = ActionBarPolicy.get(context);
        if (!this.mReserveOverflowSet) {
            this.mReserveOverflow = value.showsOverflowMenuButton();
        }
        if (!this.mWidthLimitSet) {
            this.mWidthLimit = value.getEmbeddedMenuWidthLimit();
        }
        if (!this.mMaxItemsSet) {
            this.mMaxItems = value.getMaxActionButtons();
        }
        int mWidthLimit = this.mWidthLimit;
        if (this.mReserveOverflow) {
            if (this.mOverflowButton == null) {
                this.mOverflowButton = (View)new OverflowMenuButton(this.mSystemContext);
                final int measureSpec = View$MeasureSpec.makeMeasureSpec(0, 0);
                this.mOverflowButton.measure(measureSpec, measureSpec);
            }
            mWidthLimit -= this.mOverflowButton.getMeasuredWidth();
        }
        else {
            this.mOverflowButton = null;
        }
        this.mActionItemWidthLimit = mWidthLimit;
        this.mMinCellSize = (int)(56.0f * resources.getDisplayMetrics().density);
        this.mScrapActionButtonView = null;
    }
    
    public boolean isOverflowMenuShowing() {
        return this.mOverflowPopup != null && this.mOverflowPopup.isShowing();
    }
    
    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }
    
    @Override
    public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        this.dismissPopupMenus();
        super.onCloseMenu(menuBuilder, b);
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        if (!this.mMaxItemsSet) {
            this.mMaxItems = this.mContext.getResources().getInteger(R.integer.abc_max_action_buttons);
        }
        if (this.mMenu != null) {
            this.mMenu.onItemsChanged(true);
        }
    }
    
    @Override
    public void onRestoreInstanceState(final Parcelable parcelable) {
        final SavedState savedState = (SavedState)parcelable;
        if (savedState.openSubMenuId > 0) {
            final MenuItem item = this.mMenu.findItem(savedState.openSubMenuId);
            if (item != null) {
                this.onSubMenuSelected((SubMenuBuilder)item.getSubMenu());
            }
        }
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState();
        savedState.openSubMenuId = this.mOpenSubMenuId;
        return (Parcelable)savedState;
    }
    
    @Override
    public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
        if (subMenuBuilder.hasVisibleItems()) {
            SubMenuBuilder subMenuBuilder2;
            for (subMenuBuilder2 = subMenuBuilder; subMenuBuilder2.getParentMenu() != this.mMenu; subMenuBuilder2 = (SubMenuBuilder)subMenuBuilder2.getParentMenu()) {}
            if (this.findViewForItem(subMenuBuilder2.getItem()) == null) {
                if (this.mOverflowButton == null) {
                    return false;
                }
                final View mOverflowButton = this.mOverflowButton;
            }
            this.mOpenSubMenuId = subMenuBuilder.getItem().getItemId();
            (this.mActionButtonPopup = new ActionButtonSubmenu(subMenuBuilder)).show(null);
            super.onSubMenuSelected(subMenuBuilder);
            return true;
        }
        return false;
    }
    
    @Override
    public void onSubUiVisibilityChanged(final boolean b) {
        if (b) {
            super.onSubMenuSelected(null);
            return;
        }
        this.mMenu.close(false);
    }
    
    public void setExpandedActionViewsExclusive(final boolean mExpandedActionViewsExclusive) {
        this.mExpandedActionViewsExclusive = mExpandedActionViewsExclusive;
    }
    
    public void setItemLimit(final int mMaxItems) {
        this.mMaxItems = mMaxItems;
        this.mMaxItemsSet = true;
    }
    
    public void setReserveOverflow(final boolean mReserveOverflow) {
        this.mReserveOverflow = mReserveOverflow;
        this.mReserveOverflowSet = true;
    }
    
    public void setWidthLimit(final int mWidthLimit, final boolean mStrictWidthLimit) {
        this.mWidthLimit = mWidthLimit;
        this.mStrictWidthLimit = mStrictWidthLimit;
        this.mWidthLimitSet = true;
    }
    
    @Override
    public boolean shouldIncludeItem(final int n, final MenuItemImpl menuItemImpl) {
        return menuItemImpl.isActionButton();
    }
    
    public boolean showOverflowMenu() {
        if (this.mReserveOverflow && !this.isOverflowMenuShowing() && this.mMenu != null && this.mMenuView != null && this.mPostedOpenRunnable == null) {
            this.mPostedOpenRunnable = new OpenOverflowRunnable(new OverflowPopup(this.mContext, this.mMenu, this.mOverflowButton, true));
            ((View)this.mMenuView).post((Runnable)this.mPostedOpenRunnable);
            super.onSubMenuSelected(null);
            return true;
        }
        return false;
    }
    
    @Override
    public void updateMenuView(final boolean b) {
        super.updateMenuView(b);
        if (this.mMenuView == null) {
            return;
        }
        if (this.mMenu != null) {
            final ArrayList<MenuItemImpl> actionItems = this.mMenu.getActionItems();
            for (int size = actionItems.size(), i = 0; i < size; ++i) {
                final ActionProvider supportActionProvider = actionItems.get(i).getSupportActionProvider();
                if (supportActionProvider != null) {
                    supportActionProvider.setSubUiVisibilityListener((ActionProvider.SubUiVisibilityListener)this);
                }
            }
        }
        ArrayList<MenuItemImpl> nonActionItems;
        if (this.mMenu != null) {
            nonActionItems = this.mMenu.getNonActionItems();
        }
        else {
            nonActionItems = null;
        }
        final boolean mReserveOverflow = this.mReserveOverflow;
        int n = 0;
        if (mReserveOverflow) {
            n = 0;
            if (nonActionItems != null) {
                final int size2 = nonActionItems.size();
                if (size2 == 1) {
                    if (!nonActionItems.get(0).isActionViewExpanded()) {
                        n = 1;
                    }
                    else {
                        n = 0;
                    }
                }
                else if (size2 > 0) {
                    n = 1;
                }
                else {
                    n = 0;
                }
            }
        }
        if (n != 0) {
            if (this.mOverflowButton == null) {
                this.mOverflowButton = (View)new OverflowMenuButton(this.mSystemContext);
            }
            final ViewGroup viewGroup = (ViewGroup)this.mOverflowButton.getParent();
            if (viewGroup != this.mMenuView) {
                if (viewGroup != null) {
                    viewGroup.removeView(this.mOverflowButton);
                }
                final ActionMenuView actionMenuView = (ActionMenuView)this.mMenuView;
                actionMenuView.addView(this.mOverflowButton, (ViewGroup$LayoutParams)actionMenuView.generateOverflowButtonLayoutParams());
            }
        }
        else if (this.mOverflowButton != null && this.mOverflowButton.getParent() == this.mMenuView) {
            ((ViewGroup)this.mMenuView).removeView(this.mOverflowButton);
        }
        ((ActionMenuView)this.mMenuView).setOverflowReserved(this.mReserveOverflow);
    }
    
    private class ActionButtonSubmenu extends MenuDialogHelper
    {
        public ActionButtonSubmenu(final SubMenuBuilder subMenuBuilder) {
            super(subMenuBuilder);
            ActionMenuPresenter.this.setCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
        }
        
        @Override
        public void onDismiss(final DialogInterface dialogInterface) {
            super.onDismiss(dialogInterface);
            ActionMenuPresenter.this.mActionButtonPopup = null;
            ActionMenuPresenter.this.mOpenSubMenuId = 0;
        }
    }
    
    private class OpenOverflowRunnable implements Runnable
    {
        private OverflowPopup mPopup;
        
        public OpenOverflowRunnable(final OverflowPopup mPopup) {
            super();
            this.mPopup = mPopup;
        }
        
        @Override
        public void run() {
            ActionMenuPresenter.this.mMenu.changeMenuMode();
            final View view = (View)ActionMenuPresenter.this.mMenuView;
            if (view != null && view.getWindowToken() != null && this.mPopup.tryShow()) {
                ActionMenuPresenter.this.mOverflowPopup = this.mPopup;
            }
            ActionMenuPresenter.this.mPostedOpenRunnable = null;
        }
    }
    
    private class OverflowMenuButton extends ImageButton implements ActionMenuChildView
    {
        public OverflowMenuButton(final Context context) {
            super(context, (AttributeSet)null, R.attr.actionOverflowButtonStyle);
            this.setClickable(true);
            this.setFocusable(true);
            this.setVisibility(0);
            this.setEnabled(true);
        }
        
        public boolean needsDividerAfter() {
            return false;
        }
        
        public boolean needsDividerBefore() {
            return false;
        }
        
        public boolean performClick() {
            if (super.performClick()) {
                return true;
            }
            this.playSoundEffect(0);
            ActionMenuPresenter.this.showOverflowMenu();
            return true;
        }
    }
    
    private class OverflowPopup extends MenuPopupHelper
    {
        public OverflowPopup(final Context context, final MenuBuilder menuBuilder, final View view, final boolean b) {
            super(context, menuBuilder, view, b);
            this.setCallback(ActionMenuPresenter.this.mPopupPresenterCallback);
        }
        
        @Override
        public void onDismiss() {
            super.onDismiss();
            ActionMenuPresenter.this.mMenu.close();
            ActionMenuPresenter.this.mOverflowPopup = null;
        }
    }
    
    private class PopupPresenterCallback implements Callback
    {
        @Override
        public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
            if (menuBuilder instanceof SubMenuBuilder) {
                ((SubMenuBuilder)menuBuilder).getRootMenu().close(false);
            }
        }
        
        @Override
        public boolean onOpenSubMenu(final MenuBuilder menuBuilder) {
            if (menuBuilder == null) {
                return false;
            }
            ActionMenuPresenter.this.mOpenSubMenuId = ((SubMenuBuilder)menuBuilder).getItem().getItemId();
            return false;
        }
    }
    
    private static class SavedState implements Parcelable
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        public int openSubMenuId;
        
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
        
        SavedState() {
            super();
        }
        
        SavedState(final Parcel parcel) {
            super();
            this.openSubMenuId = parcel.readInt();
        }
        
        public int describeContents() {
            return 0;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            parcel.writeInt(this.openSubMenuId);
        }
    }
}
