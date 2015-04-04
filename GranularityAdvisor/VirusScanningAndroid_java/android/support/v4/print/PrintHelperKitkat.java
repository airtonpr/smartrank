package android.support.v4.print;

import android.content.*;
import android.net.*;
import android.graphics.*;
import android.util.*;
import java.io.*;
import android.os.*;
import android.print.*;

public class PrintHelperKitkat
{
    public static final int COLOR_MODE_COLOR = 2;
    public static final int COLOR_MODE_MONOCHROME = 1;
    private static final String LOG_TAG = "PrintHelperKitkat";
    private static final int MAX_PRINT_SIZE = 3500;
    public static final int SCALE_MODE_FILL = 2;
    public static final int SCALE_MODE_FIT = 1;
    int mColorMode;
    final Context mContext;
    int mScaleMode;
    
    PrintHelperKitkat(final Context mContext) {
        super();
        this.mScaleMode = 2;
        this.mColorMode = 2;
        this.mContext = mContext;
    }
    
    private Bitmap loadBitmap(final Uri uri, final BitmapFactory$Options bitmapFactory$Options) throws FileNotFoundException {
        if (uri == null || this.mContext == null) {
            throw new IllegalArgumentException("bad argument to loadBitmap");
        }
        InputStream openInputStream = null;
        try {
            openInputStream = this.mContext.getContentResolver().openInputStream(uri);
            final Bitmap decodeStream = BitmapFactory.decodeStream(openInputStream, (Rect)null, bitmapFactory$Options);
            if (openInputStream == null) {
                return decodeStream;
            }
            try {
                openInputStream.close();
                return decodeStream;
            }
            catch (IOException ex) {
                Log.w("PrintHelperKitkat", "close fail ", (Throwable)ex);
                return decodeStream;
            }
        }
        finally {
            Label_0079: {
                if (openInputStream == null) {
                    break Label_0079;
                }
                try {
                    openInputStream.close();
                }
                catch (IOException ex2) {
                    Log.w("PrintHelperKitkat", "close fail ", (Throwable)ex2);
                }
            }
        }
    }
    
    private Bitmap loadConstrainedBitmap(final Uri uri, final int n) throws FileNotFoundException {
        if (n <= 0 || uri == null || this.mContext == null) {
            throw new IllegalArgumentException("bad argument to getScaledBitmap");
        }
        final BitmapFactory$Options bitmapFactory$Options = new BitmapFactory$Options();
        bitmapFactory$Options.inJustDecodeBounds = true;
        this.loadBitmap(uri, bitmapFactory$Options);
        final int outWidth = bitmapFactory$Options.outWidth;
        final int outHeight = bitmapFactory$Options.outHeight;
        if (outWidth > 0 && outHeight > 0) {
            int i;
            int inSampleSize;
            for (i = Math.max(outWidth, outHeight), inSampleSize = 1; i > n; i >>>= 1, inSampleSize <<= 1) {}
            if (inSampleSize > 0 && Math.min(outWidth, outHeight) / inSampleSize > 0) {
                final BitmapFactory$Options bitmapFactory$Options2 = new BitmapFactory$Options();
                bitmapFactory$Options2.inMutable = true;
                bitmapFactory$Options2.inSampleSize = inSampleSize;
                return this.loadBitmap(uri, bitmapFactory$Options2);
            }
        }
        return null;
    }
    
    public int getColorMode() {
        return this.mColorMode;
    }
    
    public int getScaleMode() {
        return this.mScaleMode;
    }
    
    public void printBitmap(final String s, final Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        final int mScaleMode = this.mScaleMode;
        final PrintManager printManager = (PrintManager)this.mContext.getSystemService("print");
        PrintAttributes$MediaSize mediaSize = PrintAttributes$MediaSize.UNKNOWN_PORTRAIT;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            mediaSize = PrintAttributes$MediaSize.UNKNOWN_LANDSCAPE;
        }
        printManager.print(s, (PrintDocumentAdapter)new PrintDocumentAdapter() {
            private PrintAttributes mAttributes;
            
            public void onLayout(final PrintAttributes printAttributes, final PrintAttributes mAttributes, final CancellationSignal cancellationSignal, final PrintDocumentAdapter$LayoutResultCallback printDocumentAdapter$LayoutResultCallback, final Bundle bundle) {
                boolean b = true;
                this.mAttributes = mAttributes;
                final PrintDocumentInfo build = new PrintDocumentInfo$Builder(s).setContentType((int)(b ? 1 : 0)).setPageCount((int)(b ? 1 : 0)).build();
                if (mAttributes.equals((Object)printAttributes)) {
                    b = false;
                }
                printDocumentAdapter$LayoutResultCallback.onLayoutFinished(build, b);
            }
            
            public void onWrite(final PageRange[] p0, final ParcelFileDescriptor p1, final CancellationSignal p2, final PrintDocumentAdapter$WriteResultCallback p3) {
                // 
                // This method could not be decompiled.
                // 
                // Original Bytecode:
                // 
                //     0: new             Landroid/print/pdf/PrintedPdfDocument;
                //     3: dup            
                //     4: aload_0        
                //     5: getfield        android/support/v4/print/PrintHelperKitkat$1.this$0:Landroid/support/v4/print/PrintHelperKitkat;
                //     8: getfield        android/support/v4/print/PrintHelperKitkat.mContext:Landroid/content/Context;
                //    11: aload_0        
                //    12: getfield        android/support/v4/print/PrintHelperKitkat$1.mAttributes:Landroid/print/PrintAttributes;
                //    15: invokespecial   android/print/pdf/PrintedPdfDocument.<init>:(Landroid/content/Context;Landroid/print/PrintAttributes;)V
                //    18: astore          5
                //    20: aload           5
                //    22: iconst_1       
                //    23: invokevirtual   android/print/pdf/PrintedPdfDocument.startPage:(I)Landroid/graphics/pdf/PdfDocument$Page;
                //    26: astore          8
                //    28: new             Landroid/graphics/RectF;
                //    31: dup            
                //    32: aload           8
                //    34: invokevirtual   android/graphics/pdf/PdfDocument$Page.getInfo:()Landroid/graphics/pdf/PdfDocument$PageInfo;
                //    37: invokevirtual   android/graphics/pdf/PdfDocument$PageInfo.getContentRect:()Landroid/graphics/Rect;
                //    40: invokespecial   android/graphics/RectF.<init>:(Landroid/graphics/Rect;)V
                //    43: astore          9
                //    45: new             Landroid/graphics/Matrix;
                //    48: dup            
                //    49: invokespecial   android/graphics/Matrix.<init>:()V
                //    52: astore          10
                //    54: aload           9
                //    56: invokevirtual   android/graphics/RectF.width:()F
                //    59: aload_0        
                //    60: getfield        android/support/v4/print/PrintHelperKitkat$1.val$bitmap:Landroid/graphics/Bitmap;
                //    63: invokevirtual   android/graphics/Bitmap.getWidth:()I
                //    66: i2f            
                //    67: fdiv           
                //    68: fstore          11
                //    70: aload_0        
                //    71: getfield        android/support/v4/print/PrintHelperKitkat$1.val$fittingMode:I
                //    74: iconst_2       
                //    75: if_icmpne       230
                //    78: fload           11
                //    80: aload           9
                //    82: invokevirtual   android/graphics/RectF.height:()F
                //    85: aload_0        
                //    86: getfield        android/support/v4/print/PrintHelperKitkat$1.val$bitmap:Landroid/graphics/Bitmap;
                //    89: invokevirtual   android/graphics/Bitmap.getHeight:()I
                //    92: i2f            
                //    93: fdiv           
                //    94: invokestatic    java/lang/Math.max:(FF)F
                //    97: fstore          12
                //    99: aload           10
                //   101: fload           12
                //   103: fload           12
                //   105: invokevirtual   android/graphics/Matrix.postScale:(FF)Z
                //   108: pop            
                //   109: aload           10
                //   111: aload           9
                //   113: invokevirtual   android/graphics/RectF.width:()F
                //   116: fload           12
                //   118: aload_0        
                //   119: getfield        android/support/v4/print/PrintHelperKitkat$1.val$bitmap:Landroid/graphics/Bitmap;
                //   122: invokevirtual   android/graphics/Bitmap.getWidth:()I
                //   125: i2f            
                //   126: fmul           
                //   127: fsub           
                //   128: fconst_2       
                //   129: fdiv           
                //   130: aload           9
                //   132: invokevirtual   android/graphics/RectF.height:()F
                //   135: fload           12
                //   137: aload_0        
                //   138: getfield        android/support/v4/print/PrintHelperKitkat$1.val$bitmap:Landroid/graphics/Bitmap;
                //   141: invokevirtual   android/graphics/Bitmap.getHeight:()I
                //   144: i2f            
                //   145: fmul           
                //   146: fsub           
                //   147: fconst_2       
                //   148: fdiv           
                //   149: invokevirtual   android/graphics/Matrix.postTranslate:(FF)Z
                //   152: pop            
                //   153: aload           8
                //   155: invokevirtual   android/graphics/pdf/PdfDocument$Page.getCanvas:()Landroid/graphics/Canvas;
                //   158: aload_0        
                //   159: getfield        android/support/v4/print/PrintHelperKitkat$1.val$bitmap:Landroid/graphics/Bitmap;
                //   162: aload           10
                //   164: aconst_null    
                //   165: invokevirtual   android/graphics/Canvas.drawBitmap:(Landroid/graphics/Bitmap;Landroid/graphics/Matrix;Landroid/graphics/Paint;)V
                //   168: aload           5
                //   170: aload           8
                //   172: invokevirtual   android/print/pdf/PrintedPdfDocument.finishPage:(Landroid/graphics/pdf/PdfDocument$Page;)V
                //   175: aload           5
                //   177: new             Ljava/io/FileOutputStream;
                //   180: dup            
                //   181: aload_2        
                //   182: invokevirtual   android/os/ParcelFileDescriptor.getFileDescriptor:()Ljava/io/FileDescriptor;
                //   185: invokespecial   java/io/FileOutputStream.<init>:(Ljava/io/FileDescriptor;)V
                //   188: invokevirtual   android/print/pdf/PrintedPdfDocument.writeTo:(Ljava/io/OutputStream;)V
                //   191: iconst_1       
                //   192: anewarray       Landroid/print/PageRange;
                //   195: astore          18
                //   197: aload           18
                //   199: iconst_0       
                //   200: getstatic       android/print/PageRange.ALL_PAGES:Landroid/print/PageRange;
                //   203: aastore        
                //   204: aload           4
                //   206: aload           18
                //   208: invokevirtual   android/print/PrintDocumentAdapter$WriteResultCallback.onWriteFinished:([Landroid/print/PageRange;)V
                //   211: aload           5
                //   213: ifnull          221
                //   216: aload           5
                //   218: invokevirtual   android/print/pdf/PrintedPdfDocument.close:()V
                //   221: aload_2        
                //   222: ifnull          229
                //   225: aload_2        
                //   226: invokevirtual   android/os/ParcelFileDescriptor.close:()V
                //   229: return         
                //   230: fload           11
                //   232: aload           9
                //   234: invokevirtual   android/graphics/RectF.height:()F
                //   237: aload_0        
                //   238: getfield        android/support/v4/print/PrintHelperKitkat$1.val$bitmap:Landroid/graphics/Bitmap;
                //   241: invokevirtual   android/graphics/Bitmap.getHeight:()I
                //   244: i2f            
                //   245: fdiv           
                //   246: invokestatic    java/lang/Math.min:(FF)F
                //   249: fstore          12
                //   251: goto            99
                //   254: astore          15
                //   256: ldc             "PrintHelperKitkat"
                //   258: ldc             "Error writing printed content"
                //   260: aload           15
                //   262: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
                //   265: pop            
                //   266: aload           4
                //   268: aconst_null    
                //   269: invokevirtual   android/print/PrintDocumentAdapter$WriteResultCallback.onWriteFailed:(Ljava/lang/CharSequence;)V
                //   272: goto            211
                //   275: astore          6
                //   277: aload           5
                //   279: ifnull          287
                //   282: aload           5
                //   284: invokevirtual   android/print/pdf/PrintedPdfDocument.close:()V
                //   287: aload_2        
                //   288: ifnull          295
                //   291: aload_2        
                //   292: invokevirtual   android/os/ParcelFileDescriptor.close:()V
                //   295: aload           6
                //   297: athrow         
                //   298: astore          17
                //   300: return         
                //   301: astore          7
                //   303: goto            295
                //    Exceptions:
                //  Try           Handler
                //  Start  End    Start  End    Type                 
                //  -----  -----  -----  -----  ---------------------
                //  20     99     275    298    Any
                //  99     175    275    298    Any
                //  175    211    254    275    Ljava/io/IOException;
                //  175    211    275    298    Any
                //  225    229    298    301    Ljava/io/IOException;
                //  230    251    275    298    Any
                //  256    272    275    298    Any
                //  291    295    301    306    Ljava/io/IOException;
                // 
                // The error that occurred was:
                // 
                // java.lang.IndexOutOfBoundsException: Index: 148, Size: 148
                //     at java.util.ArrayList.rangeCheck(Unknown Source)
                //     at java.util.ArrayList.get(Unknown Source)
                //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3305)
                //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:114)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformCall(AstMethodBodyBuilder.java:1163)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:1010)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformByteCode(AstMethodBodyBuilder.java:554)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformExpression(AstMethodBodyBuilder.java:540)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformNode(AstMethodBodyBuilder.java:392)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.transformBlock(AstMethodBodyBuilder.java:333)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:294)
                //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
                //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
                //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
                //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:304)
                //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:225)
                //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:110)
                //     at advisor.main.Main.transformClassesToJava(Main.java:90)
                //     at advisor.main.Main.analyzeAPK(Main.java:51)
                //     at advisor.main.Main.main(Main.java:24)
                // 
                throw new IllegalStateException("An error occurred while decompiling this method.");
            }
        }, new PrintAttributes$Builder().setMediaSize(mediaSize).setColorMode(this.mColorMode).build());
    }
    
    public void printBitmap(final String s, final Uri uri) throws FileNotFoundException {
        this.printBitmap(s, this.loadConstrainedBitmap(uri, 3500));
    }
    
    public void setColorMode(final int mColorMode) {
        this.mColorMode = mColorMode;
    }
    
    public void setScaleMode(final int mScaleMode) {
        this.mScaleMode = mScaleMode;
    }
}
