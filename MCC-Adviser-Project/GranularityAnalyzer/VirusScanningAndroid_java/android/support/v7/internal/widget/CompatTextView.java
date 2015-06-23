package android.support.v7.internal.widget;

import android.widget.*;
import android.content.*;
import android.util.*;
import android.support.v7.appcompat.*;
import android.text.method.*;
import android.content.res.*;
import java.util.*;
import android.view.*;
import android.graphics.*;

public class CompatTextView extends TextView
{
    public CompatTextView(final Context context) {
        this(context, null);
    }
    
    public CompatTextView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public CompatTextView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.CompatTextView, n, 0);
        final boolean boolean1 = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
        if (boolean1) {
            this.setTransformationMethod((TransformationMethod)new AllCapsTransformationMethod(context));
        }
    }
    
    private static class AllCapsTransformationMethod implements TransformationMethod
    {
        private final Locale mLocale;
        
        public AllCapsTransformationMethod(final Context context) {
            super();
            this.mLocale = context.getResources().getConfiguration().locale;
        }
        
        public CharSequence getTransformation(final CharSequence charSequence, final View view) {
            if (charSequence != null) {
                return charSequence.toString().toUpperCase(this.mLocale);
            }
            return null;
        }
        
        public void onFocusChanged(final View view, final CharSequence charSequence, final boolean b, final int n, final Rect rect) {
        }
    }
}
