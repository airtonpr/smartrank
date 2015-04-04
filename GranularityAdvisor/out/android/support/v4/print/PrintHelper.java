package android.support.v4.print;

import android.content.*;
import android.os.*;
import android.graphics.*;
import android.net.*;
import java.io.*;

public final class PrintHelper
{
    public static final int COLOR_MODE_COLOR = 2;
    public static final int COLOR_MODE_MONOCHROME = 1;
    public static final int SCALE_MODE_FILL = 2;
    public static final int SCALE_MODE_FIT = 1;
    PrintHelperVersionImpl mImpl;
    
    public PrintHelper(final Context context) {
        super();
        if (systemSupportsPrint()) {
            this.mImpl = (PrintHelperVersionImpl)new PrintHelperKitkatImpl(context);
            return;
        }
        this.mImpl = (PrintHelperVersionImpl)new PrintHelperStubImpl();
    }
    
    public static boolean systemSupportsPrint() {
        return Build$VERSION.SDK_INT >= 19;
    }
    
    public int getColorMode() {
        return this.mImpl.getColorMode();
    }
    
    public int getScaleMode() {
        return this.mImpl.getScaleMode();
    }
    
    public void printBitmap(final String s, final Bitmap bitmap) {
        this.mImpl.printBitmap(s, bitmap);
    }
    
    public void printBitmap(final String s, final Uri uri) throws FileNotFoundException {
        this.mImpl.printBitmap(s, uri);
    }
    
    public void setColorMode(final int colorMode) {
        this.mImpl.setColorMode(colorMode);
    }
    
    public void setScaleMode(final int scaleMode) {
        this.mImpl.setScaleMode(scaleMode);
    }
    
    private static final class PrintHelperKitkatImpl implements PrintHelperVersionImpl
    {
        private final PrintHelperKitkat printHelper;
        
        PrintHelperKitkatImpl(final Context context) {
            super();
            this.printHelper = new PrintHelperKitkat(context);
        }
        
        @Override
        public int getColorMode() {
            return this.printHelper.getColorMode();
        }
        
        @Override
        public int getScaleMode() {
            return this.printHelper.getScaleMode();
        }
        
        @Override
        public void printBitmap(final String s, final Bitmap bitmap) {
            this.printHelper.printBitmap(s, bitmap);
        }
        
        @Override
        public void printBitmap(final String s, final Uri uri) throws FileNotFoundException {
            this.printHelper.printBitmap(s, uri);
        }
        
        @Override
        public void setColorMode(final int colorMode) {
            this.printHelper.setColorMode(colorMode);
        }
        
        @Override
        public void setScaleMode(final int scaleMode) {
            this.printHelper.setScaleMode(scaleMode);
        }
    }
    
    private static final class PrintHelperStubImpl implements PrintHelperVersionImpl
    {
        int mColorMode;
        int mScaleMode;
        
        private PrintHelperStubImpl() {
            super();
            this.mScaleMode = 2;
            this.mColorMode = 2;
        }
        
        @Override
        public int getColorMode() {
            return this.mColorMode;
        }
        
        @Override
        public int getScaleMode() {
            return this.mScaleMode;
        }
        
        @Override
        public void printBitmap(final String s, final Bitmap bitmap) {
        }
        
        @Override
        public void printBitmap(final String s, final Uri uri) {
        }
        
        @Override
        public void setColorMode(final int mColorMode) {
            this.mColorMode = mColorMode;
        }
        
        @Override
        public void setScaleMode(final int mScaleMode) {
            this.mScaleMode = mScaleMode;
        }
    }
    
    interface PrintHelperVersionImpl
    {
        int getColorMode();
        
        int getScaleMode();
        
        void printBitmap(String p0, Bitmap p1);
        
        void printBitmap(String p0, Uri p1) throws FileNotFoundException;
        
        void setColorMode(int p0);
        
        void setScaleMode(int p0);
    }
}
