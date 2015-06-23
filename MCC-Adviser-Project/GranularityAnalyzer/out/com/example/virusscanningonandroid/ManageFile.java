package com.example.virusscanningonandroid;

import android.annotation.*;
import android.content.*;
import android.os.*;
import android.util.*;
import java.io.*;

@SuppressLint({ "NewApi" })
public class ManageFile
{
    private static final String TAG = "ManageFile";
    private Context context;
    private String logName;
    private boolean sdCardAvailable;
    private boolean sdCardReadableOnly;
    private boolean sdCardWritableReadable;
    
    public ManageFile(final Context context) {
        super();
        this.context = context;
    }
    
    public ManageFile(final String logName) {
        super();
        final File file = new File(Environment.getExternalStorageDirectory(), logName);
        while (true) {
            try {
                file.createNewFile();
                this.logName = logName;
            }
            catch (IOException ex) {
                ex.printStackTrace();
                continue;
            }
            break;
        }
    }
    
    public String ReadFile() throws FileNotFoundException, IOException {
        final File file = new File(this.context.getExternalFilesDir((String)null), "romar.txt");
        final FileInputStream fileInputStream = new FileInputStream(file);
        final byte[] array = new byte[(int)file.length()];
        fileInputStream.read(array);
        return new String(array);
    }
    
    @SuppressLint({ "NewApi" })
    public boolean WriteFile(final String s) throws IOException {
        final File file = new File(Environment.getExternalStorageDirectory(), this.logName);
        try {
            final FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write("\n".getBytes());
            fileOutputStream.write(s.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        }
        catch (Exception ex) {
            Log.e("ManageFile", ex.toString());
            return false;
        }
    }
    
    public boolean clearFile() {
        final File file = new File(Environment.getExternalStorageDirectory(), "Log.txt");
        try {
            new RandomAccessFile(file, "rws").setLength(0L);
            return true;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public void getStateSDcard() {
        final String externalStorageState = Environment.getExternalStorageState();
        if ("bad_removal".equals(externalStorageState)) {
            this.sdCardAvailable = false;
            this.sdCardWritableReadable = false;
            this.sdCardReadableOnly = false;
            Log.d("ManageFile", "Midia removida.");
        }
        else {
            if ("checking".equals(externalStorageState)) {
                this.sdCardAvailable = true;
                this.sdCardWritableReadable = false;
                this.sdCardReadableOnly = false;
                Log.d("ManageFile", "Midia sendo verificada.");
                return;
            }
            if ("mounted".equals(externalStorageState)) {
                this.sdCardAvailable = true;
                this.sdCardWritableReadable = true;
                this.sdCardReadableOnly = false;
                Log.d("ManageFile", "Midia com permiss\u00c3£o de escrita e leitura.");
                return;
            }
            if ("mounted_ro".equals(externalStorageState)) {
                this.sdCardAvailable = true;
                this.sdCardWritableReadable = false;
                this.sdCardReadableOnly = false;
                Log.d("ManageFile", "Midia com permiss\u00c3£o somente leitura.");
                return;
            }
            if ("nofs".equals(externalStorageState)) {
                this.sdCardAvailable = false;
                this.sdCardWritableReadable = false;
                this.sdCardReadableOnly = false;
                Log.d("ManageFile", "Midia com sistema de arquivos n\u00c3£o compat\u00c3\u00advel.");
                return;
            }
            if ("removed".equals(externalStorageState)) {
                this.sdCardAvailable = false;
                this.sdCardWritableReadable = false;
                this.sdCardReadableOnly = false;
                Log.d("ManageFile", "Midia n\u00c3£o presente.");
                return;
            }
            if ("shared".equals(externalStorageState)) {
                this.sdCardAvailable = false;
                this.sdCardWritableReadable = false;
                this.sdCardReadableOnly = false;
                Log.d("ManageFile", "Midia compartilhada via USB.");
                return;
            }
            if ("unmountable".equals(externalStorageState)) {
                this.sdCardAvailable = false;
                this.sdCardWritableReadable = false;
                this.sdCardReadableOnly = false;
                Log.d("ManageFile", "Midia n\u00c3£o pode ser montada");
                return;
            }
            if ("unmounted".equals(externalStorageState)) {
                this.sdCardAvailable = false;
                this.sdCardWritableReadable = false;
                this.sdCardReadableOnly = false;
                Log.d("ManageFile", "Midia n\u00c3£o montada.");
            }
        }
    }
    
    public boolean isSdCardAvailable() {
        return this.sdCardAvailable;
    }
    
    public boolean isSdCardReadableOnly() {
        return this.sdCardReadableOnly;
    }
    
    public boolean isSdCardWritableReadable() {
        return this.sdCardWritableReadable;
    }
    
    public void setSdCardAvailable(final boolean sdCardAvailable) {
        this.sdCardAvailable = sdCardAvailable;
    }
    
    public void setSdCardReadableOnly(final boolean sdCardReadableOnly) {
        this.sdCardReadableOnly = sdCardReadableOnly;
    }
    
    public void setSdCardWritableReadable(final boolean sdCardWritableReadable) {
        this.sdCardWritableReadable = sdCardWritableReadable;
    }
}
