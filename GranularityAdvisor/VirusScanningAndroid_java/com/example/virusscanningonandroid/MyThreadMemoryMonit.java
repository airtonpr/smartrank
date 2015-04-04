package com.example.virusscanningonandroid;

import java.io.*;

public class MyThreadMemoryMonit implements Runnable
{
    private ManageFile manageFile;
    
    private float getUsedCPU() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: new             Ljava/io/RandomAccessFile;
        //     3: dup            
        //     4: ldc             "/proc/stat"
        //     6: ldc             "r"
        //     8: invokespecial   java/io/RandomAccessFile.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //    11: astore_1       
        //    12: aload_1        
        //    13: invokevirtual   java/io/RandomAccessFile.readLine:()Ljava/lang/String;
        //    16: ldc             " "
        //    18: invokevirtual   java/lang/String.split:(Ljava/lang/String;)[Ljava/lang/String;
        //    21: astore_3       
        //    22: aload_3        
        //    23: iconst_5       
        //    24: aaload         
        //    25: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //    28: lstore          4
        //    30: aload_3        
        //    31: iconst_2       
        //    32: aaload         
        //    33: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //    36: aload_3        
        //    37: iconst_3       
        //    38: aaload         
        //    39: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //    42: ladd           
        //    43: aload_3        
        //    44: iconst_4       
        //    45: aaload         
        //    46: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //    49: ladd           
        //    50: aload_3        
        //    51: bipush          6
        //    53: aaload         
        //    54: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //    57: ladd           
        //    58: aload_3        
        //    59: bipush          7
        //    61: aaload         
        //    62: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //    65: ladd           
        //    66: lstore          6
        //    68: aload_3        
        //    69: bipush          8
        //    71: aaload         
        //    72: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //    75: lstore          8
        //    77: lload           6
        //    79: lload           8
        //    81: ladd           
        //    82: lstore          10
        //    84: ldc2_w          2000
        //    87: invokestatic    java/lang/Thread.sleep:(J)V
        //    90: aload_1        
        //    91: lconst_0       
        //    92: invokevirtual   java/io/RandomAccessFile.seek:(J)V
        //    95: aload_1        
        //    96: invokevirtual   java/io/RandomAccessFile.readLine:()Ljava/lang/String;
        //    99: astore          13
        //   101: aload_1        
        //   102: invokevirtual   java/io/RandomAccessFile.close:()V
        //   105: aload           13
        //   107: ldc             " "
        //   109: invokevirtual   java/lang/String.split:(Ljava/lang/String;)[Ljava/lang/String;
        //   112: astore          14
        //   114: aload           14
        //   116: iconst_5       
        //   117: aaload         
        //   118: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //   121: lstore          15
        //   123: aload           14
        //   125: iconst_2       
        //   126: aaload         
        //   127: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //   130: aload           14
        //   132: iconst_3       
        //   133: aaload         
        //   134: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //   137: ladd           
        //   138: aload           14
        //   140: iconst_4       
        //   141: aaload         
        //   142: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //   145: ladd           
        //   146: aload           14
        //   148: bipush          6
        //   150: aaload         
        //   151: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //   154: ladd           
        //   155: aload           14
        //   157: bipush          7
        //   159: aaload         
        //   160: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //   163: ladd           
        //   164: lstore          17
        //   166: aload           14
        //   168: bipush          8
        //   170: aaload         
        //   171: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //   174: lstore          19
        //   176: lload           17
        //   178: lload           19
        //   180: ladd           
        //   181: lstore          21
        //   183: lload           21
        //   185: lload           10
        //   187: lsub           
        //   188: l2f            
        //   189: lload           21
        //   191: lload           15
        //   193: ladd           
        //   194: lload           10
        //   196: lload           4
        //   198: ladd           
        //   199: lsub           
        //   200: l2f            
        //   201: fdiv           
        //   202: freturn        
        //   203: astore_2       
        //   204: aload_2        
        //   205: invokevirtual   java/io/IOException.printStackTrace:()V
        //   208: fconst_0       
        //   209: freturn        
        //   210: astore          12
        //   212: goto            90
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                 
        //  -----  -----  -----  -----  ---------------------
        //  0      77     203    210    Ljava/io/IOException;
        //  84     90     210    215    Ljava/lang/Exception;
        //  84     90     203    210    Ljava/io/IOException;
        //  90     176    203    210    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0090:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2592)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
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
    
    public float getUsedMemorySize() throws IOException {
        float n = -1.0f;
        try {
            final Runtime runtime = Runtime.getRuntime();
            final float n2 = runtime.freeMemory();
            final float n3 = runtime.totalMemory();
            n = (n3 - n2) / n3;
            Thread.sleep(1000L);
            return n;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return n;
        }
    }
    
    @Override
    public void run() {
        this.manageFile = new ManageFile("Log-virus-local-memory-" + System.currentTimeMillis() + ".txt");
        try {
            while (true) {
                this.manageFile.WriteFile(String.valueOf(100.0f * this.getUsedMemorySize()));
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
        }
    }
}
