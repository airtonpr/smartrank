package android.support.v4.content;

import java.io.*;
import android.content.*;
import android.os.*;

public class ContextCompat
{
    private static final String DIR_ANDROID = "Android";
    private static final String DIR_CACHE = "cache";
    private static final String DIR_DATA = "data";
    private static final String DIR_FILES = "files";
    private static final String DIR_OBB = "obb";
    
    private static File buildPath(final File file, final String... array) {
        final int length = array.length;
        int i = 0;
        File file2 = file;
        while (i < length) {
            final String s = array[i];
            File file3;
            if (file2 == null) {
                file3 = new File(s);
            }
            else if (s != null) {
                file3 = new File(file2, s);
            }
            else {
                file3 = file2;
            }
            ++i;
            file2 = file3;
        }
        return file2;
    }
    
    public static File[] getExternalCacheDirs(final Context context) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 19) {
            return ContextCompatKitKat.getExternalCacheDirs(context);
        }
        File file;
        if (sdk_INT >= 8) {
            file = ContextCompatFroyo.getExternalCacheDir(context);
        }
        else {
            file = buildPath(Environment.getExternalStorageDirectory(), "Android", "data", context.getPackageName(), "cache");
        }
        return new File[] { file };
    }
    
    public static File[] getExternalFilesDirs(final Context context, final String s) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 19) {
            return ContextCompatKitKat.getExternalFilesDirs(context, s);
        }
        File file;
        if (sdk_INT >= 8) {
            file = ContextCompatFroyo.getExternalFilesDir(context, s);
        }
        else {
            file = buildPath(Environment.getExternalStorageDirectory(), "Android", "data", context.getPackageName(), "files", s);
        }
        return new File[] { file };
    }
    
    public static File[] getObbDirs(final Context context) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 19) {
            return ContextCompatKitKat.getObbDirs(context);
        }
        File file;
        if (sdk_INT >= 11) {
            file = ContextCompatHoneycomb.getObbDir(context);
        }
        else {
            file = buildPath(Environment.getExternalStorageDirectory(), "Android", "obb", context.getPackageName());
        }
        return new File[] { file };
    }
    
    public static boolean startActivities(final Context context, final Intent[] array) {
        return startActivities(context, array, null);
    }
    
    public static boolean startActivities(final Context context, final Intent[] array, final Bundle bundle) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 16) {
            ContextCompatJellybean.startActivities(context, array, bundle);
            return true;
        }
        if (sdk_INT >= 11) {
            ContextCompatHoneycomb.startActivities(context, array);
            return true;
        }
        return false;
    }
}
