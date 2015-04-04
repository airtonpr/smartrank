package android.support.v4.text;

import android.util.*;
import java.lang.reflect.*;

class ICUCompatIcs
{
    private static final String TAG = "ICUCompatIcs";
    private static Method sAddLikelySubtagsMethod;
    private static Method sGetScriptMethod;
    
    static {
        try {
            final Class<?> forName = Class.forName("libcore.icu.ICU");
            if (forName != null) {
                ICUCompatIcs.sGetScriptMethod = forName.getMethod("getScript", String.class);
                ICUCompatIcs.sAddLikelySubtagsMethod = forName.getMethod("addLikelySubtags", String.class);
            }
        }
        catch (Exception ex) {
            Log.w("ICUCompatIcs", (Throwable)ex);
        }
    }
    
    public static String addLikelySubtags(final String s) {
        try {
            if (ICUCompatIcs.sAddLikelySubtagsMethod != null) {
                return (String)ICUCompatIcs.sAddLikelySubtagsMethod.invoke(null, s);
            }
            goto Label_0041;
        }
        catch (IllegalAccessException ex) {
            Log.w("ICUCompatIcs", (Throwable)ex);
        }
        catch (InvocationTargetException ex2) {
            Log.w("ICUCompatIcs", (Throwable)ex2);
            goto Label_0041;
        }
    }
    
    public static String getScript(final String s) {
        try {
            if (ICUCompatIcs.sGetScriptMethod != null) {
                return (String)ICUCompatIcs.sGetScriptMethod.invoke(null, s);
            }
            goto Label_0041;
        }
        catch (IllegalAccessException ex) {
            Log.w("ICUCompatIcs", (Throwable)ex);
        }
        catch (InvocationTargetException ex2) {
            Log.w("ICUCompatIcs", (Throwable)ex2);
            goto Label_0041;
        }
    }
}
