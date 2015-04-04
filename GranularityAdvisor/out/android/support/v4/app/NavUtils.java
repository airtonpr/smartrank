package android.support.v4.app;

import android.os.*;
import android.app.*;
import android.content.*;
import android.support.v4.content.*;
import android.content.pm.*;
import android.util.*;

public class NavUtils
{
    private static final NavUtilsImpl IMPL;
    public static final String PARENT_ACTIVITY = "android.support.PARENT_ACTIVITY";
    private static final String TAG = "NavUtils";
    
    static {
        if (Build$VERSION.SDK_INT >= 16) {
            IMPL = (NavUtilsImpl)new NavUtilsImplJB();
            return;
        }
        IMPL = (NavUtilsImpl)new NavUtilsImplBase();
    }
    
    public static Intent getParentActivityIntent(final Activity activity) {
        return NavUtils.IMPL.getParentActivityIntent(activity);
    }
    
    public static Intent getParentActivityIntent(final Context context, final ComponentName componentName) throws PackageManager$NameNotFoundException {
        final String parentActivityName = getParentActivityName(context, componentName);
        if (parentActivityName == null) {
            return null;
        }
        final ComponentName component = new ComponentName(componentName.getPackageName(), parentActivityName);
        if (getParentActivityName(context, component) == null) {
            return IntentCompat.makeMainActivity(component);
        }
        return new Intent().setComponent(component);
    }
    
    public static Intent getParentActivityIntent(final Context context, final Class<?> clazz) throws PackageManager$NameNotFoundException {
        final String parentActivityName = getParentActivityName(context, new ComponentName(context, (Class)clazz));
        if (parentActivityName == null) {
            return null;
        }
        final ComponentName component = new ComponentName(context, parentActivityName);
        if (getParentActivityName(context, component) == null) {
            return IntentCompat.makeMainActivity(component);
        }
        return new Intent().setComponent(component);
    }
    
    public static String getParentActivityName(final Activity activity) {
        try {
            return getParentActivityName((Context)activity, activity.getComponentName());
        }
        catch (PackageManager$NameNotFoundException ex) {
            throw new IllegalArgumentException((Throwable)ex);
        }
    }
    
    public static String getParentActivityName(final Context context, final ComponentName componentName) throws PackageManager$NameNotFoundException {
        return NavUtils.IMPL.getParentActivityName(context, context.getPackageManager().getActivityInfo(componentName, 128));
    }
    
    public static void navigateUpFromSameTask(final Activity activity) {
        final Intent parentActivityIntent = getParentActivityIntent(activity);
        if (parentActivityIntent == null) {
            throw new IllegalArgumentException("Activity " + activity.getClass().getSimpleName() + " does not have a parent activity name specified." + " (Did you forget to add the android.support.PARENT_ACTIVITY <meta-data> " + " element in your manifest?)");
        }
        navigateUpTo(activity, parentActivityIntent);
    }
    
    public static void navigateUpTo(final Activity activity, final Intent intent) {
        NavUtils.IMPL.navigateUpTo(activity, intent);
    }
    
    public static boolean shouldUpRecreateTask(final Activity activity, final Intent intent) {
        return NavUtils.IMPL.shouldUpRecreateTask(activity, intent);
    }
    
    interface NavUtilsImpl
    {
        Intent getParentActivityIntent(Activity p0);
        
        String getParentActivityName(Context p0, ActivityInfo p1);
        
        void navigateUpTo(Activity p0, Intent p1);
        
        boolean shouldUpRecreateTask(Activity p0, Intent p1);
    }
    
    static class NavUtilsImplBase implements NavUtilsImpl
    {
        @Override
        public Intent getParentActivityIntent(final Activity activity) {
            final String parentActivityName = NavUtils.getParentActivityName(activity);
            if (parentActivityName == null) {
                return null;
            }
            final ComponentName component = new ComponentName((Context)activity, parentActivityName);
            Intent intent;
            try {
                if (NavUtils.getParentActivityName((Context)activity, component) == null) {
                    intent = IntentCompat.makeMainActivity(component);
                }
                else {
                    intent = new Intent().setComponent(component);
                }
            }
            catch (PackageManager$NameNotFoundException ex) {
                Log.e("NavUtils", "getParentActivityIntent: bad parentActivityName '" + parentActivityName + "' in manifest");
                return null;
            }
            return intent;
        }
        
        @Override
        public String getParentActivityName(final Context context, final ActivityInfo activityInfo) {
            String string;
            if (activityInfo.metaData == null) {
                string = null;
            }
            else {
                string = activityInfo.metaData.getString("android.support.PARENT_ACTIVITY");
                if (string == null) {
                    return null;
                }
                if (string.charAt(0) == '.') {
                    return context.getPackageName() + string;
                }
            }
            return string;
        }
        
        @Override
        public void navigateUpTo(final Activity activity, final Intent intent) {
            intent.addFlags(67108864);
            activity.startActivity(intent);
            activity.finish();
        }
        
        @Override
        public boolean shouldUpRecreateTask(final Activity activity, final Intent intent) {
            final String action = activity.getIntent().getAction();
            return action != null && !action.equals("android.intent.action.MAIN");
        }
    }
    
    static class NavUtilsImplJB extends NavUtilsImplBase
    {
        @Override
        public Intent getParentActivityIntent(final Activity activity) {
            Intent intent = NavUtilsJB.getParentActivityIntent(activity);
            if (intent == null) {
                intent = this.superGetParentActivityIntent(activity);
            }
            return intent;
        }
        
        @Override
        public String getParentActivityName(final Context context, final ActivityInfo activityInfo) {
            String s = NavUtilsJB.getParentActivityName(activityInfo);
            if (s == null) {
                s = super.getParentActivityName(context, activityInfo);
            }
            return s;
        }
        
        @Override
        public void navigateUpTo(final Activity activity, final Intent intent) {
            NavUtilsJB.navigateUpTo(activity, intent);
        }
        
        @Override
        public boolean shouldUpRecreateTask(final Activity activity, final Intent intent) {
            return NavUtilsJB.shouldUpRecreateTask(activity, intent);
        }
        
        Intent superGetParentActivityIntent(final Activity activity) {
            return super.getParentActivityIntent(activity);
        }
    }
}
