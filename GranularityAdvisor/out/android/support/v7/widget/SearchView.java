package android.support.v7.widget;

import android.support.v7.view.*;
import java.util.*;
import android.support.v4.widget.*;
import android.view.inputmethod.*;
import android.database.*;
import android.support.v4.view.*;
import android.support.v7.appcompat.*;
import android.graphics.*;
import android.content.res.*;
import android.net.*;
import android.app.*;
import android.text.*;
import android.text.style.*;
import android.graphics.drawable.*;
import android.util.*;
import android.content.pm.*;
import android.content.*;
import android.widget.*;
import java.lang.reflect.*;
import android.os.*;
import android.view.*;

public class SearchView extends LinearLayout implements CollapsibleActionView
{
    private static final boolean DBG = false;
    static final AutoCompleteTextViewReflector HIDDEN_METHOD_INVOKER;
    private static final String IME_OPTION_NO_MICROPHONE = "nm";
    private static final String LOG_TAG = "SearchView";
    private Bundle mAppSearchData;
    private boolean mClearingFocus;
    private ImageView mCloseButton;
    private int mCollapsedImeOptions;
    private View mDropDownAnchor;
    private boolean mExpandedInActionView;
    private boolean mIconified;
    private boolean mIconifiedByDefault;
    private int mMaxWidth;
    private CharSequence mOldQueryText;
    private final View$OnClickListener mOnClickListener;
    private OnCloseListener mOnCloseListener;
    private final TextView$OnEditorActionListener mOnEditorActionListener;
    private final AdapterView$OnItemClickListener mOnItemClickListener;
    private final AdapterView$OnItemSelectedListener mOnItemSelectedListener;
    private OnQueryTextListener mOnQueryChangeListener;
    private View$OnFocusChangeListener mOnQueryTextFocusChangeListener;
    private View$OnClickListener mOnSearchClickListener;
    private OnSuggestionListener mOnSuggestionListener;
    private final WeakHashMap<String, Drawable$ConstantState> mOutsideDrawablesCache;
    private CharSequence mQueryHint;
    private boolean mQueryRefinement;
    private SearchAutoComplete mQueryTextView;
    private Runnable mReleaseCursorRunnable;
    private View mSearchButton;
    private View mSearchEditFrame;
    private ImageView mSearchHintIcon;
    private View mSearchPlate;
    private SearchableInfo mSearchable;
    private Runnable mShowImeRunnable;
    private View mSubmitArea;
    private View mSubmitButton;
    private boolean mSubmitButtonEnabled;
    private CursorAdapter mSuggestionsAdapter;
    View$OnKeyListener mTextKeyListener;
    private TextWatcher mTextWatcher;
    private Runnable mUpdateDrawableStateRunnable;
    private CharSequence mUserQuery;
    private final Intent mVoiceAppSearchIntent;
    private View mVoiceButton;
    private boolean mVoiceButtonEnabled;
    private final Intent mVoiceWebSearchIntent;
    
    static {
        HIDDEN_METHOD_INVOKER = new AutoCompleteTextViewReflector();
    }
    
    public SearchView(final Context context) {
        this(context, null);
    }
    
    public SearchView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mShowImeRunnable = new Runnable() {
            @Override
            public void run() {
                final InputMethodManager inputMethodManager = (InputMethodManager)SearchView.this.getContext().getSystemService("input_method");
                if (inputMethodManager != null) {
                    SearchView.HIDDEN_METHOD_INVOKER.showSoftInputUnchecked(inputMethodManager, (View)SearchView.this, 0);
                }
            }
        };
        this.mUpdateDrawableStateRunnable = new Runnable() {
            @Override
            public void run() {
                SearchView.this.updateFocusedState();
            }
        };
        this.mReleaseCursorRunnable = new Runnable() {
            @Override
            public void run() {
                if (SearchView.this.mSuggestionsAdapter != null && SearchView.this.mSuggestionsAdapter instanceof SuggestionsAdapter) {
                    SearchView.this.mSuggestionsAdapter.changeCursor(null);
                }
            }
        };
        this.mOutsideDrawablesCache = new WeakHashMap<String, Drawable$ConstantState>();
        this.mOnClickListener = (View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                if (view == SearchView.this.mSearchButton) {
                    SearchView.this.onSearchClicked();
                }
                else {
                    if (view == SearchView.this.mCloseButton) {
                        SearchView.this.onCloseClicked();
                        return;
                    }
                    if (view == SearchView.this.mSubmitButton) {
                        SearchView.this.onSubmitQuery();
                        return;
                    }
                    if (view == SearchView.this.mVoiceButton) {
                        SearchView.this.onVoiceClicked();
                        return;
                    }
                    if (view == SearchView.this.mQueryTextView) {
                        SearchView.this.forceSuggestionQuery();
                    }
                }
            }
        };
        this.mTextKeyListener = (View$OnKeyListener)new View$OnKeyListener() {
            public boolean onKey(final View view, final int n, final KeyEvent keyEvent) {
                if (SearchView.this.mSearchable != null) {
                    if (SearchView.this.mQueryTextView.isPopupShowing() && SearchView.this.mQueryTextView.getListSelection() != -1) {
                        return SearchView.this.onSuggestionsKey(view, n, keyEvent);
                    }
                    if (!SearchView.this.mQueryTextView.isEmpty() && KeyEventCompat.hasNoModifiers(keyEvent) && keyEvent.getAction() == 1 && n == 66) {
                        view.cancelLongPress();
                        SearchView.this.launchQuerySearch(0, null, SearchView.this.mQueryTextView.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        };
        this.mOnEditorActionListener = (TextView$OnEditorActionListener)new TextView$OnEditorActionListener() {
            public boolean onEditorAction(final TextView textView, final int n, final KeyEvent keyEvent) {
                SearchView.this.onSubmitQuery();
                return true;
            }
        };
        this.mOnItemClickListener = (AdapterView$OnItemClickListener)new AdapterView$OnItemClickListener() {
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
                SearchView.this.onItemClicked(n, 0, null);
            }
        };
        this.mOnItemSelectedListener = (AdapterView$OnItemSelectedListener)new AdapterView$OnItemSelectedListener() {
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
                SearchView.this.onItemSelected(n);
            }
            
            public void onNothingSelected(final AdapterView<?> adapterView) {
            }
        };
        this.mTextWatcher = (TextWatcher)new TextWatcher() {
            public void afterTextChanged(final Editable editable) {
            }
            
            public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
            }
            
            public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
                SearchView.this.onTextChanged(charSequence);
            }
        };
        ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(R.layout.abc_search_view, (ViewGroup)this, true);
        this.mSearchButton = this.findViewById(R.id.search_button);
        (this.mQueryTextView = (SearchAutoComplete)this.findViewById(R.id.search_src_text)).setSearchView(this);
        this.mSearchEditFrame = this.findViewById(R.id.search_edit_frame);
        this.mSearchPlate = this.findViewById(R.id.search_plate);
        this.mSubmitArea = this.findViewById(R.id.submit_area);
        this.mSubmitButton = this.findViewById(R.id.search_go_btn);
        this.mCloseButton = (ImageView)this.findViewById(R.id.search_close_btn);
        this.mVoiceButton = this.findViewById(R.id.search_voice_btn);
        this.mSearchHintIcon = (ImageView)this.findViewById(R.id.search_mag_icon);
        this.mSearchButton.setOnClickListener(this.mOnClickListener);
        this.mCloseButton.setOnClickListener(this.mOnClickListener);
        this.mSubmitButton.setOnClickListener(this.mOnClickListener);
        this.mVoiceButton.setOnClickListener(this.mOnClickListener);
        this.mQueryTextView.setOnClickListener(this.mOnClickListener);
        this.mQueryTextView.addTextChangedListener(this.mTextWatcher);
        this.mQueryTextView.setOnEditorActionListener(this.mOnEditorActionListener);
        this.mQueryTextView.setOnItemClickListener(this.mOnItemClickListener);
        this.mQueryTextView.setOnItemSelectedListener(this.mOnItemSelectedListener);
        this.mQueryTextView.setOnKeyListener(this.mTextKeyListener);
        this.mQueryTextView.setOnFocusChangeListener((View$OnFocusChangeListener)new View$OnFocusChangeListener() {
            public void onFocusChange(final View view, final boolean b) {
                if (SearchView.this.mOnQueryTextFocusChangeListener != null) {
                    SearchView.this.mOnQueryTextFocusChangeListener.onFocusChange((View)SearchView.this, b);
                }
            }
        });
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.SearchView, 0, 0);
        this.setIconifiedByDefault(obtainStyledAttributes.getBoolean(3, true));
        final int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(0, -1);
        if (dimensionPixelSize != -1) {
            this.setMaxWidth(dimensionPixelSize);
        }
        final CharSequence text = obtainStyledAttributes.getText(4);
        if (!TextUtils.isEmpty(text)) {
            this.setQueryHint(text);
        }
        final int int1 = obtainStyledAttributes.getInt(2, -1);
        if (int1 != -1) {
            this.setImeOptions(int1);
        }
        final int int2 = obtainStyledAttributes.getInt(1, -1);
        if (int2 != -1) {
            this.setInputType(int2);
        }
        obtainStyledAttributes.recycle();
        final TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(set, R.styleable.View, 0, 0);
        final boolean boolean1 = obtainStyledAttributes2.getBoolean(0, true);
        obtainStyledAttributes2.recycle();
        this.setFocusable(boolean1);
        (this.mVoiceWebSearchIntent = new Intent("android.speech.action.WEB_SEARCH")).addFlags(268435456);
        this.mVoiceWebSearchIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "web_search");
        (this.mVoiceAppSearchIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH")).addFlags(268435456);
        this.mDropDownAnchor = this.findViewById(this.mQueryTextView.getDropDownAnchor());
        if (this.mDropDownAnchor != null) {
            if (Build$VERSION.SDK_INT >= 11) {
                this.addOnLayoutChangeListenerToDropDownAnchorSDK11();
            }
            else {
                this.addOnLayoutChangeListenerToDropDownAnchorBase();
            }
        }
        this.updateViewsVisibility(this.mIconifiedByDefault);
        this.updateQueryHint();
    }
    
    private void addOnLayoutChangeListenerToDropDownAnchorBase() {
        this.mDropDownAnchor.getViewTreeObserver().addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                SearchView.this.adjustDropDownSizeAndPosition();
            }
        });
    }
    
    private void addOnLayoutChangeListenerToDropDownAnchorSDK11() {
        this.mDropDownAnchor.addOnLayoutChangeListener((View$OnLayoutChangeListener)new View$OnLayoutChangeListener() {
            public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
                SearchView.this.adjustDropDownSizeAndPosition();
            }
        });
    }
    
    private void adjustDropDownSizeAndPosition() {
        if (this.mDropDownAnchor.getWidth() > 1) {
            final Resources resources = this.getContext().getResources();
            final int paddingLeft = this.mSearchPlate.getPaddingLeft();
            final Rect rect = new Rect();
            int n;
            if (this.mIconifiedByDefault) {
                n = resources.getDimensionPixelSize(R.dimen.abc_dropdownitem_icon_width) + resources.getDimensionPixelSize(R.dimen.abc_dropdownitem_text_padding_left);
            }
            else {
                n = 0;
            }
            this.mQueryTextView.getDropDownBackground().getPadding(rect);
            this.mQueryTextView.setDropDownHorizontalOffset(paddingLeft - (n + rect.left));
            this.mQueryTextView.setDropDownWidth(n + (this.mDropDownAnchor.getWidth() + rect.left + rect.right) - paddingLeft);
        }
    }
    
    private Intent createIntent(final String s, final Uri data, final String s2, final String s3, final int n, final String s4) {
        final Intent intent = new Intent(s);
        intent.addFlags(268435456);
        if (data != null) {
            intent.setData(data);
        }
        intent.putExtra("user_query", this.mUserQuery);
        if (s3 != null) {
            intent.putExtra("query", s3);
        }
        if (s2 != null) {
            intent.putExtra("intent_extra_data_key", s2);
        }
        if (this.mAppSearchData != null) {
            intent.putExtra("app_data", this.mAppSearchData);
        }
        if (n != 0) {
            intent.putExtra("action_key", n);
            intent.putExtra("action_msg", s4);
        }
        intent.setComponent(this.mSearchable.getSearchActivity());
        return intent;
    }
    
    private Intent createIntentFromSuggestion(final Cursor cursor, final int n, final String s) {
        String s2 = null;
        String s3 = null;
        String columnString;
        Uri parse;
        int position;
        Block_5_Outer:Label_0131_Outer:
        while (true) {
        Label_0102_Outer:
            while (true) {
                while (true) {
                    Label_0217: {
                        while (true) {
                            Label_0204: {
                                try {
                                    s2 = SuggestionsAdapter.getColumnString(cursor, "suggest_intent_action");
                                    if (s2 == null) {
                                        s2 = this.mSearchable.getSuggestIntentAction();
                                    }
                                    break Label_0204;
                                    // iftrue(Label_0217:, s3 == null)
                                    // iftrue(Label_0217:, columnString == null)
                                    while (true) {
                                        while (true) {
                                            s3 = s3 + "/" + Uri.encode(columnString);
                                            break Label_0217;
                                            s3 = this.mSearchable.getSuggestIntentData();
                                            Label_0049: {
                                                break Label_0049;
                                                parse = Uri.parse(s3);
                                                return this.createIntent(s2, parse, SuggestionsAdapter.getColumnString(cursor, "suggest_intent_extra_data"), SuggestionsAdapter.getColumnString(cursor, "suggest_intent_query"), n, s);
                                            }
                                            columnString = SuggestionsAdapter.getColumnString(cursor, "suggest_intent_data_id");
                                            continue Block_5_Outer;
                                        }
                                        s3 = SuggestionsAdapter.getColumnString(cursor, "suggest_intent_data");
                                        continue Label_0131_Outer;
                                    }
                                }
                                // iftrue(Label_0049:, s3 != null)
                                catch (RuntimeException ex) {
                                    try {
                                        position = cursor.getPosition();
                                        Log.w("SearchView", "Search suggestions cursor at row " + position + " returned exception.", (Throwable)ex);
                                        return null;
                                    }
                                    catch (RuntimeException ex2) {
                                        position = -1;
                                    }
                                }
                            }
                            if (s2 == null) {
                                s2 = "android.intent.action.SEARCH";
                                continue;
                            }
                            continue;
                        }
                    }
                    if (s3 == null) {
                        parse = null;
                        continue;
                    }
                    break;
                }
                continue Label_0102_Outer;
            }
        }
    }
    
    private Intent createVoiceAppSearchIntent(final Intent intent, final SearchableInfo searchableInfo) {
        final ComponentName searchActivity = searchableInfo.getSearchActivity();
        final Intent intent2 = new Intent("android.intent.action.SEARCH");
        intent2.setComponent(searchActivity);
        final PendingIntent activity = PendingIntent.getActivity(this.getContext(), 0, intent2, 1073741824);
        final Bundle bundle = new Bundle();
        if (this.mAppSearchData != null) {
            bundle.putParcelable("app_data", (Parcelable)this.mAppSearchData);
        }
        final Intent intent3 = new Intent(intent);
        String string = "free_form";
        int voiceMaxResults = 1;
        final Resources resources = this.getResources();
        if (searchableInfo.getVoiceLanguageModeId() != 0) {
            string = resources.getString(searchableInfo.getVoiceLanguageModeId());
        }
        final int voicePromptTextId = searchableInfo.getVoicePromptTextId();
        String string2 = null;
        if (voicePromptTextId != 0) {
            string2 = resources.getString(searchableInfo.getVoicePromptTextId());
        }
        final int voiceLanguageId = searchableInfo.getVoiceLanguageId();
        String string3 = null;
        if (voiceLanguageId != 0) {
            string3 = resources.getString(searchableInfo.getVoiceLanguageId());
        }
        if (searchableInfo.getVoiceMaxResults() != 0) {
            voiceMaxResults = searchableInfo.getVoiceMaxResults();
        }
        intent3.putExtra("android.speech.extra.LANGUAGE_MODEL", string);
        intent3.putExtra("android.speech.extra.PROMPT", string2);
        intent3.putExtra("android.speech.extra.LANGUAGE", string3);
        intent3.putExtra("android.speech.extra.MAX_RESULTS", voiceMaxResults);
        String flattenToShortString;
        if (searchActivity == null) {
            flattenToShortString = null;
        }
        else {
            flattenToShortString = searchActivity.flattenToShortString();
        }
        intent3.putExtra("calling_package", flattenToShortString);
        intent3.putExtra("android.speech.extra.RESULTS_PENDINGINTENT", (Parcelable)activity);
        intent3.putExtra("android.speech.extra.RESULTS_PENDINGINTENT_BUNDLE", bundle);
        return intent3;
    }
    
    private Intent createVoiceWebSearchIntent(final Intent intent, final SearchableInfo searchableInfo) {
        final Intent intent2 = new Intent(intent);
        final ComponentName searchActivity = searchableInfo.getSearchActivity();
        String flattenToShortString;
        if (searchActivity == null) {
            flattenToShortString = null;
        }
        else {
            flattenToShortString = searchActivity.flattenToShortString();
        }
        intent2.putExtra("calling_package", flattenToShortString);
        return intent2;
    }
    
    private void dismissSuggestions() {
        this.mQueryTextView.dismissDropDown();
    }
    
    private void forceSuggestionQuery() {
        SearchView.HIDDEN_METHOD_INVOKER.doBeforeTextChanged(this.mQueryTextView);
        SearchView.HIDDEN_METHOD_INVOKER.doAfterTextChanged(this.mQueryTextView);
    }
    
    private CharSequence getDecoratedHint(final CharSequence charSequence) {
        if (!this.mIconifiedByDefault) {
            return charSequence;
        }
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder((CharSequence)"   ");
        spannableStringBuilder.append(charSequence);
        final Drawable drawable = this.getContext().getResources().getDrawable(this.getSearchIconId());
        final int n = (int)(1.25 * this.mQueryTextView.getTextSize());
        drawable.setBounds(0, 0, n, n);
        spannableStringBuilder.setSpan((Object)new ImageSpan(drawable), 1, 2, 33);
        return (CharSequence)spannableStringBuilder;
    }
    
    private int getPreferredWidth() {
        return this.getContext().getResources().getDimensionPixelSize(R.dimen.abc_search_view_preferred_width);
    }
    
    private int getSearchIconId() {
        final TypedValue typedValue = new TypedValue();
        this.getContext().getTheme().resolveAttribute(R.attr.searchViewSearchIcon, typedValue, true);
        return typedValue.resourceId;
    }
    
    private boolean hasVoiceSearch() {
        final SearchableInfo mSearchable = this.mSearchable;
        boolean b = false;
        if (mSearchable != null) {
            final boolean voiceSearchEnabled = this.mSearchable.getVoiceSearchEnabled();
            b = false;
            if (voiceSearchEnabled) {
                Intent intent;
                if (this.mSearchable.getVoiceSearchLaunchWebSearch()) {
                    intent = this.mVoiceWebSearchIntent;
                }
                else {
                    final boolean voiceSearchLaunchRecognizer = this.mSearchable.getVoiceSearchLaunchRecognizer();
                    intent = null;
                    if (voiceSearchLaunchRecognizer) {
                        intent = this.mVoiceAppSearchIntent;
                    }
                }
                b = false;
                if (intent != null) {
                    final ResolveInfo resolveActivity = this.getContext().getPackageManager().resolveActivity(intent, 65536);
                    b = false;
                    if (resolveActivity != null) {
                        b = true;
                    }
                }
            }
        }
        return b;
    }
    
    static boolean isLandscapeMode(final Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }
    
    private boolean isSubmitAreaEnabled() {
        return (this.mSubmitButtonEnabled || this.mVoiceButtonEnabled) && !this.isIconified();
    }
    
    private void launchIntent(final Intent intent) {
        if (intent == null) {
            return;
        }
        try {
            this.getContext().startActivity(intent);
        }
        catch (RuntimeException ex) {
            Log.e("SearchView", "Failed launch activity: " + intent, (Throwable)ex);
        }
    }
    
    private void launchQuerySearch(final int n, final String s, final String s2) {
        this.getContext().startActivity(this.createIntent("android.intent.action.SEARCH", null, null, s2, n, s));
    }
    
    private boolean launchSuggestion(final int n, final int n2, final String s) {
        final Cursor cursor = this.mSuggestionsAdapter.getCursor();
        if (cursor != null && cursor.moveToPosition(n)) {
            this.launchIntent(this.createIntentFromSuggestion(cursor, n2, s));
            return true;
        }
        return false;
    }
    
    private void onCloseClicked() {
        if (TextUtils.isEmpty((CharSequence)this.mQueryTextView.getText())) {
            if (this.mIconifiedByDefault && (this.mOnCloseListener == null || !this.mOnCloseListener.onClose())) {
                this.clearFocus();
                this.updateViewsVisibility(true);
            }
            return;
        }
        this.mQueryTextView.setText((CharSequence)"");
        this.mQueryTextView.requestFocus();
        this.setImeVisibility(true);
    }
    
    private boolean onItemClicked(final int n, final int n2, final String s) {
        if (this.mOnSuggestionListener != null) {
            final boolean onSuggestionClick = this.mOnSuggestionListener.onSuggestionClick(n);
            final boolean b = false;
            if (onSuggestionClick) {
                return b;
            }
        }
        this.launchSuggestion(n, 0, null);
        this.setImeVisibility(false);
        this.dismissSuggestions();
        return true;
    }
    
    private boolean onItemSelected(final int n) {
        if (this.mOnSuggestionListener == null || !this.mOnSuggestionListener.onSuggestionSelect(n)) {
            this.rewriteQueryFromSuggestion(n);
            return true;
        }
        return false;
    }
    
    private void onSearchClicked() {
        this.updateViewsVisibility(false);
        this.mQueryTextView.requestFocus();
        this.setImeVisibility(true);
        if (this.mOnSearchClickListener != null) {
            this.mOnSearchClickListener.onClick((View)this);
        }
    }
    
    private void onSubmitQuery() {
        final Editable text = this.mQueryTextView.getText();
        if (text != null && TextUtils.getTrimmedLength((CharSequence)text) > 0 && (this.mOnQueryChangeListener == null || !this.mOnQueryChangeListener.onQueryTextSubmit(text.toString()))) {
            if (this.mSearchable != null) {
                this.launchQuerySearch(0, null, text.toString());
                this.setImeVisibility(false);
            }
            this.dismissSuggestions();
        }
    }
    
    private boolean onSuggestionsKey(final View view, final int n, final KeyEvent keyEvent) {
        if (this.mSearchable != null && this.mSuggestionsAdapter != null && keyEvent.getAction() == 0 && KeyEventCompat.hasNoModifiers(keyEvent)) {
            if (n == 66 || n == 84 || n == 61) {
                return this.onItemClicked(this.mQueryTextView.getListSelection(), 0, null);
            }
            if (n == 21 || n == 22) {
                int length;
                if (n == 21) {
                    length = 0;
                }
                else {
                    length = this.mQueryTextView.length();
                }
                this.mQueryTextView.setSelection(length);
                this.mQueryTextView.setListSelection(0);
                this.mQueryTextView.clearListSelection();
                SearchView.HIDDEN_METHOD_INVOKER.ensureImeVisible(this.mQueryTextView, true);
                return true;
            }
            if (n == 19 && this.mQueryTextView.getListSelection() == 0) {
                return false;
            }
        }
        return false;
    }
    
    private void onTextChanged(final CharSequence charSequence) {
        boolean b = true;
        final Editable text = this.mQueryTextView.getText();
        this.mUserQuery = (CharSequence)text;
        final boolean b2 = !TextUtils.isEmpty((CharSequence)text) && b;
        this.updateSubmitButton(b2);
        if (b2) {
            b = false;
        }
        this.updateVoiceButton(b);
        this.updateCloseButton();
        this.updateSubmitArea();
        if (this.mOnQueryChangeListener != null && !TextUtils.equals(charSequence, this.mOldQueryText)) {
            this.mOnQueryChangeListener.onQueryTextChange(charSequence.toString());
        }
        this.mOldQueryText = charSequence.toString();
    }
    
    private void onVoiceClicked() {
        if (this.mSearchable != null) {
            final SearchableInfo mSearchable = this.mSearchable;
            try {
                if (mSearchable.getVoiceSearchLaunchWebSearch()) {
                    this.getContext().startActivity(this.createVoiceWebSearchIntent(this.mVoiceWebSearchIntent, mSearchable));
                    return;
                }
            }
            catch (ActivityNotFoundException ex) {
                Log.w("SearchView", "Could not find voice search activity");
                return;
            }
            if (mSearchable.getVoiceSearchLaunchRecognizer()) {
                this.getContext().startActivity(this.createVoiceAppSearchIntent(this.mVoiceAppSearchIntent, mSearchable));
            }
        }
    }
    
    private void postUpdateFocusedState() {
        this.post(this.mUpdateDrawableStateRunnable);
    }
    
    private void rewriteQueryFromSuggestion(final int n) {
        final Editable text = this.mQueryTextView.getText();
        final Cursor cursor = this.mSuggestionsAdapter.getCursor();
        if (cursor == null) {
            return;
        }
        if (!cursor.moveToPosition(n)) {
            this.setQuery((CharSequence)text);
            return;
        }
        final CharSequence convertToString = this.mSuggestionsAdapter.convertToString(cursor);
        if (convertToString != null) {
            this.setQuery(convertToString);
            return;
        }
        this.setQuery((CharSequence)text);
    }
    
    private void setImeVisibility(final boolean b) {
        if (b) {
            this.post(this.mShowImeRunnable);
        }
        else {
            this.removeCallbacks(this.mShowImeRunnable);
            final InputMethodManager inputMethodManager = (InputMethodManager)this.getContext().getSystemService("input_method");
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(this.getWindowToken(), 0);
            }
        }
    }
    
    private void setQuery(final CharSequence text) {
        this.mQueryTextView.setText(text);
        final SearchAutoComplete mQueryTextView = this.mQueryTextView;
        int length;
        if (TextUtils.isEmpty(text)) {
            length = 0;
        }
        else {
            length = text.length();
        }
        mQueryTextView.setSelection(length);
    }
    
    private void updateCloseButton() {
        int n = 1;
        int n2;
        if (!TextUtils.isEmpty((CharSequence)this.mQueryTextView.getText())) {
            n2 = n;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0 && (!this.mIconifiedByDefault || this.mExpandedInActionView)) {
            n = 0;
        }
        final ImageView mCloseButton = this.mCloseButton;
        int visibility = 0;
        if (n == 0) {
            visibility = 8;
        }
        mCloseButton.setVisibility(visibility);
        final Drawable drawable = this.mCloseButton.getDrawable();
        int[] state;
        if (n2 != 0) {
            state = SearchView.ENABLED_STATE_SET;
        }
        else {
            state = SearchView.EMPTY_STATE_SET;
        }
        drawable.setState(state);
    }
    
    private void updateFocusedState() {
        final boolean hasFocus = this.mQueryTextView.hasFocus();
        final Drawable background = this.mSearchPlate.getBackground();
        int[] state;
        if (hasFocus) {
            state = SearchView.FOCUSED_STATE_SET;
        }
        else {
            state = SearchView.EMPTY_STATE_SET;
        }
        background.setState(state);
        final Drawable background2 = this.mSubmitArea.getBackground();
        int[] state2;
        if (hasFocus) {
            state2 = SearchView.FOCUSED_STATE_SET;
        }
        else {
            state2 = SearchView.EMPTY_STATE_SET;
        }
        background2.setState(state2);
        this.invalidate();
    }
    
    private void updateQueryHint() {
        if (this.mQueryHint != null) {
            this.mQueryTextView.setHint(this.getDecoratedHint(this.mQueryHint));
        }
        else {
            if (this.mSearchable == null) {
                this.mQueryTextView.setHint(this.getDecoratedHint(""));
                return;
            }
            final int hintId = this.mSearchable.getHintId();
            CharSequence string = null;
            if (hintId != 0) {
                string = this.getContext().getString(hintId);
            }
            if (string != null) {
                this.mQueryTextView.setHint(this.getDecoratedHint(string));
            }
        }
    }
    
    private void updateSearchAutoComplete() {
        int queryRefinement = 1;
        this.mQueryTextView.setThreshold(this.mSearchable.getSuggestThreshold());
        this.mQueryTextView.setImeOptions(this.mSearchable.getImeOptions());
        int inputType = this.mSearchable.getInputType();
        if ((inputType & 0xF) == queryRefinement) {
            inputType &= 0xFFFEFFFF;
            if (this.mSearchable.getSuggestAuthority() != null) {
                inputType = (0x80000 | (inputType | 0x10000));
            }
        }
        this.mQueryTextView.setInputType(inputType);
        if (this.mSuggestionsAdapter != null) {
            this.mSuggestionsAdapter.changeCursor(null);
        }
        if (this.mSearchable.getSuggestAuthority() != null) {
            this.mSuggestionsAdapter = new SuggestionsAdapter(this.getContext(), this, this.mSearchable, this.mOutsideDrawablesCache);
            this.mQueryTextView.setAdapter((ListAdapter)this.mSuggestionsAdapter);
            final SuggestionsAdapter suggestionsAdapter = (SuggestionsAdapter)this.mSuggestionsAdapter;
            if (this.mQueryRefinement) {
                queryRefinement = 2;
            }
            suggestionsAdapter.setQueryRefinement(queryRefinement);
        }
    }
    
    private void updateSubmitArea() {
        int visibility = 8;
        if (this.isSubmitAreaEnabled() && (this.mSubmitButton.getVisibility() == 0 || this.mVoiceButton.getVisibility() == 0)) {
            visibility = 0;
        }
        this.mSubmitArea.setVisibility(visibility);
    }
    
    private void updateSubmitButton(final boolean b) {
        int visibility = 8;
        if (this.mSubmitButtonEnabled && this.isSubmitAreaEnabled() && this.hasFocus() && (b || !this.mVoiceButtonEnabled)) {
            visibility = 0;
        }
        this.mSubmitButton.setVisibility(visibility);
    }
    
    private void updateViewsVisibility(final boolean mIconified) {
        boolean b = true;
        int visibility = 8;
        this.mIconified = mIconified;
        int visibility2;
        if (mIconified) {
            visibility2 = 0;
        }
        else {
            visibility2 = visibility;
        }
        final boolean b2 = !TextUtils.isEmpty((CharSequence)this.mQueryTextView.getText()) && b;
        this.mSearchButton.setVisibility(visibility2);
        this.updateSubmitButton(b2);
        final View mSearchEditFrame = this.mSearchEditFrame;
        int visibility3;
        if (mIconified) {
            visibility3 = visibility;
        }
        else {
            visibility3 = 0;
        }
        mSearchEditFrame.setVisibility(visibility3);
        final ImageView mSearchHintIcon = this.mSearchHintIcon;
        if (!this.mIconifiedByDefault) {
            visibility = 0;
        }
        mSearchHintIcon.setVisibility(visibility);
        this.updateCloseButton();
        if (b2) {
            b = false;
        }
        this.updateVoiceButton(b);
        this.updateSubmitArea();
    }
    
    private void updateVoiceButton(final boolean b) {
        int visibility = 8;
        if (this.mVoiceButtonEnabled && !this.isIconified() && b) {
            visibility = 0;
            this.mSubmitButton.setVisibility(8);
        }
        this.mVoiceButton.setVisibility(visibility);
    }
    
    public void clearFocus() {
        this.mClearingFocus = true;
        this.setImeVisibility(false);
        super.clearFocus();
        this.mQueryTextView.clearFocus();
        this.mClearingFocus = false;
    }
    
    public int getImeOptions() {
        return this.mQueryTextView.getImeOptions();
    }
    
    public int getInputType() {
        return this.mQueryTextView.getInputType();
    }
    
    public int getMaxWidth() {
        return this.mMaxWidth;
    }
    
    public CharSequence getQuery() {
        return (CharSequence)this.mQueryTextView.getText();
    }
    
    public CharSequence getQueryHint() {
        CharSequence mQueryHint;
        if (this.mQueryHint != null) {
            mQueryHint = this.mQueryHint;
        }
        else {
            if (this.mSearchable == null) {
                return null;
            }
            final int hintId = this.mSearchable.getHintId();
            mQueryHint = null;
            if (hintId != 0) {
                return this.getContext().getString(hintId);
            }
        }
        return mQueryHint;
    }
    
    public CursorAdapter getSuggestionsAdapter() {
        return this.mSuggestionsAdapter;
    }
    
    public boolean isIconfiedByDefault() {
        return this.mIconifiedByDefault;
    }
    
    public boolean isIconified() {
        return this.mIconified;
    }
    
    public boolean isQueryRefinementEnabled() {
        return this.mQueryRefinement;
    }
    
    public boolean isSubmitButtonEnabled() {
        return this.mSubmitButtonEnabled;
    }
    
    public void onActionViewCollapsed() {
        this.clearFocus();
        this.updateViewsVisibility(true);
        this.mQueryTextView.setImeOptions(this.mCollapsedImeOptions);
        this.mExpandedInActionView = false;
    }
    
    public void onActionViewExpanded() {
        if (this.mExpandedInActionView) {
            return;
        }
        this.mExpandedInActionView = true;
        this.mCollapsedImeOptions = this.mQueryTextView.getImeOptions();
        this.mQueryTextView.setImeOptions(0x2000000 | this.mCollapsedImeOptions);
        this.mQueryTextView.setText((CharSequence)"");
        this.setIconified(false);
    }
    
    protected void onDetachedFromWindow() {
        this.removeCallbacks(this.mUpdateDrawableStateRunnable);
        this.post(this.mReleaseCursorRunnable);
        super.onDetachedFromWindow();
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        return this.mSearchable != null && super.onKeyDown(n, keyEvent);
    }
    
    protected void onMeasure(final int n, final int n2) {
        if (this.isIconified()) {
            super.onMeasure(n, n2);
            return;
        }
        final int mode = View$MeasureSpec.getMode(n);
        int n3 = View$MeasureSpec.getSize(n);
        switch (mode) {
            case Integer.MIN_VALUE: {
                if (this.mMaxWidth > 0) {
                    n3 = Math.min(this.mMaxWidth, n3);
                    break;
                }
                n3 = Math.min(this.getPreferredWidth(), n3);
                break;
            }
            case 1073741824: {
                if (this.mMaxWidth > 0) {
                    n3 = Math.min(this.mMaxWidth, n3);
                    break;
                }
                break;
            }
            case 0: {
                if (this.mMaxWidth > 0) {
                    n3 = this.mMaxWidth;
                }
                else {
                    n3 = this.getPreferredWidth();
                }
                break;
            }
        }
        super.onMeasure(View$MeasureSpec.makeMeasureSpec(n3, 1073741824), n2);
    }
    
    void onQueryRefine(final CharSequence query) {
        this.setQuery(query);
    }
    
    void onTextFocusChanged() {
        this.updateViewsVisibility(this.isIconified());
        this.postUpdateFocusedState();
        if (this.mQueryTextView.hasFocus()) {
            this.forceSuggestionQuery();
        }
    }
    
    public void onWindowFocusChanged(final boolean b) {
        super.onWindowFocusChanged(b);
        this.postUpdateFocusedState();
    }
    
    public boolean requestFocus(final int n, final Rect rect) {
        boolean requestFocus;
        if (this.mClearingFocus) {
            requestFocus = false;
        }
        else {
            if (!this.isFocusable()) {
                return false;
            }
            if (this.isIconified()) {
                return super.requestFocus(n, rect);
            }
            requestFocus = this.mQueryTextView.requestFocus(n, rect);
            if (requestFocus) {
                this.updateViewsVisibility(false);
                return requestFocus;
            }
        }
        return requestFocus;
    }
    
    public void setAppSearchData(final Bundle mAppSearchData) {
        this.mAppSearchData = mAppSearchData;
    }
    
    public void setIconified(final boolean b) {
        if (b) {
            this.onCloseClicked();
            return;
        }
        this.onSearchClicked();
    }
    
    public void setIconifiedByDefault(final boolean mIconifiedByDefault) {
        if (this.mIconifiedByDefault == mIconifiedByDefault) {
            return;
        }
        this.updateViewsVisibility(this.mIconifiedByDefault = mIconifiedByDefault);
        this.updateQueryHint();
    }
    
    public void setImeOptions(final int imeOptions) {
        this.mQueryTextView.setImeOptions(imeOptions);
    }
    
    public void setInputType(final int inputType) {
        this.mQueryTextView.setInputType(inputType);
    }
    
    public void setMaxWidth(final int mMaxWidth) {
        this.mMaxWidth = mMaxWidth;
        this.requestLayout();
    }
    
    public void setOnCloseListener(final OnCloseListener mOnCloseListener) {
        this.mOnCloseListener = mOnCloseListener;
    }
    
    public void setOnQueryTextFocusChangeListener(final View$OnFocusChangeListener mOnQueryTextFocusChangeListener) {
        this.mOnQueryTextFocusChangeListener = mOnQueryTextFocusChangeListener;
    }
    
    public void setOnQueryTextListener(final OnQueryTextListener mOnQueryChangeListener) {
        this.mOnQueryChangeListener = mOnQueryChangeListener;
    }
    
    public void setOnSearchClickListener(final View$OnClickListener mOnSearchClickListener) {
        this.mOnSearchClickListener = mOnSearchClickListener;
    }
    
    public void setOnSuggestionListener(final OnSuggestionListener mOnSuggestionListener) {
        this.mOnSuggestionListener = mOnSuggestionListener;
    }
    
    public void setQuery(final CharSequence charSequence, final boolean b) {
        this.mQueryTextView.setText(charSequence);
        if (charSequence != null) {
            this.mQueryTextView.setSelection(this.mQueryTextView.length());
            this.mUserQuery = charSequence;
        }
        if (b && !TextUtils.isEmpty(charSequence)) {
            this.onSubmitQuery();
        }
    }
    
    public void setQueryHint(final CharSequence mQueryHint) {
        this.mQueryHint = mQueryHint;
        this.updateQueryHint();
    }
    
    public void setQueryRefinementEnabled(final boolean mQueryRefinement) {
        this.mQueryRefinement = mQueryRefinement;
        if (this.mSuggestionsAdapter instanceof SuggestionsAdapter) {
            final SuggestionsAdapter suggestionsAdapter = (SuggestionsAdapter)this.mSuggestionsAdapter;
            int queryRefinement;
            if (mQueryRefinement) {
                queryRefinement = 2;
            }
            else {
                queryRefinement = 1;
            }
            suggestionsAdapter.setQueryRefinement(queryRefinement);
        }
    }
    
    public void setSearchableInfo(final SearchableInfo mSearchable) {
        this.mSearchable = mSearchable;
        if (this.mSearchable != null) {
            this.updateSearchAutoComplete();
            this.updateQueryHint();
        }
        this.mVoiceButtonEnabled = this.hasVoiceSearch();
        if (this.mVoiceButtonEnabled) {
            this.mQueryTextView.setPrivateImeOptions("nm");
        }
        this.updateViewsVisibility(this.isIconified());
    }
    
    public void setSubmitButtonEnabled(final boolean mSubmitButtonEnabled) {
        this.mSubmitButtonEnabled = mSubmitButtonEnabled;
        this.updateViewsVisibility(this.isIconified());
    }
    
    public void setSuggestionsAdapter(final CursorAdapter mSuggestionsAdapter) {
        this.mSuggestionsAdapter = mSuggestionsAdapter;
        this.mQueryTextView.setAdapter((ListAdapter)this.mSuggestionsAdapter);
    }
    
    private static class AutoCompleteTextViewReflector
    {
        private Method doAfterTextChanged;
        private Method doBeforeTextChanged;
        private Method ensureImeVisible;
        private Method showSoftInputUnchecked;
        
        AutoCompleteTextViewReflector() {
            super();
            while (true) {
                try {
                    (this.doBeforeTextChanged = AutoCompleteTextView.class.getDeclaredMethod("doBeforeTextChanged", (Class<?>[])new Class[0])).setAccessible(true);
                    try {
                        (this.doAfterTextChanged = AutoCompleteTextView.class.getDeclaredMethod("doAfterTextChanged", (Class<?>[])new Class[0])).setAccessible(true);
                        try {
                            (this.ensureImeVisible = AutoCompleteTextView.class.getMethod("ensureImeVisible", Boolean.TYPE)).setAccessible(true);
                            try {
                                (this.showSoftInputUnchecked = InputMethodManager.class.getMethod("showSoftInputUnchecked", Integer.TYPE, ResultReceiver.class)).setAccessible(true);
                            }
                            catch (NoSuchMethodException ex) {}
                        }
                        catch (NoSuchMethodException ex2) {}
                    }
                    catch (NoSuchMethodException ex3) {}
                }
                catch (NoSuchMethodException ex4) {
                    continue;
                }
                break;
            }
        }
        
        void doAfterTextChanged(final AutoCompleteTextView autoCompleteTextView) {
            if (this.doAfterTextChanged == null) {
                return;
            }
            try {
                this.doAfterTextChanged.invoke(autoCompleteTextView, new Object[0]);
            }
            catch (Exception ex) {}
        }
        
        void doBeforeTextChanged(final AutoCompleteTextView autoCompleteTextView) {
            if (this.doBeforeTextChanged == null) {
                return;
            }
            try {
                this.doBeforeTextChanged.invoke(autoCompleteTextView, new Object[0]);
            }
            catch (Exception ex) {}
        }
        
        void ensureImeVisible(final AutoCompleteTextView autoCompleteTextView, final boolean b) {
            if (this.ensureImeVisible == null) {
                return;
            }
            try {
                this.ensureImeVisible.invoke(autoCompleteTextView, b);
            }
            catch (Exception ex) {}
        }
        
        void showSoftInputUnchecked(final InputMethodManager inputMethodManager, final View view, final int n) {
            if (this.showSoftInputUnchecked != null) {
                try {
                    this.showSoftInputUnchecked.invoke(inputMethodManager, n, null);
                    return;
                }
                catch (Exception ex) {}
            }
            inputMethodManager.showSoftInput(view, n);
        }
    }
    
    public interface OnCloseListener
    {
        boolean onClose();
    }
    
    public interface OnQueryTextListener
    {
        boolean onQueryTextChange(String p0);
        
        boolean onQueryTextSubmit(String p0);
    }
    
    public interface OnSuggestionListener
    {
        boolean onSuggestionClick(int p0);
        
        boolean onSuggestionSelect(int p0);
    }
    
    public static class SearchAutoComplete extends AutoCompleteTextView
    {
        private SearchView mSearchView;
        private int mThreshold;
        
        public SearchAutoComplete(final Context context) {
            super(context);
            this.mThreshold = this.getThreshold();
        }
        
        public SearchAutoComplete(final Context context, final AttributeSet set) {
            super(context, set);
            this.mThreshold = this.getThreshold();
        }
        
        public SearchAutoComplete(final Context context, final AttributeSet set, final int n) {
            super(context, set, n);
            this.mThreshold = this.getThreshold();
        }
        
        private boolean isEmpty() {
            return TextUtils.getTrimmedLength((CharSequence)this.getText()) == 0;
        }
        
        public boolean enoughToFilter() {
            return this.mThreshold <= 0 || super.enoughToFilter();
        }
        
        protected void onFocusChanged(final boolean b, final int n, final Rect rect) {
            super.onFocusChanged(b, n, rect);
            this.mSearchView.onTextFocusChanged();
        }
        
        public boolean onKeyPreIme(final int n, final KeyEvent keyEvent) {
            if (n == 4) {
                if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                    final KeyEvent$DispatcherState keyDispatcherState = this.getKeyDispatcherState();
                    if (keyDispatcherState != null) {
                        keyDispatcherState.startTracking(keyEvent, (Object)this);
                    }
                    return true;
                }
                if (keyEvent.getAction() == 1) {
                    final KeyEvent$DispatcherState keyDispatcherState2 = this.getKeyDispatcherState();
                    if (keyDispatcherState2 != null) {
                        keyDispatcherState2.handleUpEvent(keyEvent);
                    }
                    if (keyEvent.isTracking() && !keyEvent.isCanceled()) {
                        this.mSearchView.clearFocus();
                        this.mSearchView.setImeVisibility(false);
                        return true;
                    }
                }
            }
            return super.onKeyPreIme(n, keyEvent);
        }
        
        public void onWindowFocusChanged(final boolean b) {
            super.onWindowFocusChanged(b);
            if (b && this.mSearchView.hasFocus() && this.getVisibility() == 0) {
                ((InputMethodManager)this.getContext().getSystemService("input_method")).showSoftInput((View)this, 0);
                if (SearchView.isLandscapeMode(this.getContext())) {
                    SearchView.HIDDEN_METHOD_INVOKER.ensureImeVisible(this, true);
                }
            }
        }
        
        public void performCompletion() {
        }
        
        protected void replaceText(final CharSequence charSequence) {
        }
        
        void setSearchView(final SearchView mSearchView) {
            this.mSearchView = mSearchView;
        }
        
        public void setThreshold(final int n) {
            super.setThreshold(n);
            this.mThreshold = n;
        }
    }
}
