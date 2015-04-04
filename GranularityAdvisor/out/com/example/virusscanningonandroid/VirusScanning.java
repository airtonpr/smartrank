package com.example.virusscanningonandroid;

import android.os.*;
import java.security.*;
import android.util.*;
import java.io.*;

public class VirusScanning
{
    public static final String MNT_SDCARD;
    private static final int SIGNATURE_SIZE = 10677;
    public static final String SMARTRANK_FOLDER;
    private static final String TAG = "VirusScanning";
    public static final String VIRUS_DB_PATH;
    public static final String VIRUS_FOLDER_TO_SCAN;
    public static final String VISUS_SCANNING_FOLDER;
    private String[] signatureDB;
    
    static {
        MNT_SDCARD = String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath()) + "/";
        SMARTRANK_FOLDER = String.valueOf(VirusScanning.MNT_SDCARD) + "smartrank/";
        VISUS_SCANNING_FOLDER = String.valueOf(VirusScanning.SMARTRANK_FOLDER) + "virusscanning/";
        VIRUS_DB_PATH = String.valueOf(VirusScanning.VISUS_SCANNING_FOLDER) + "virusDB/";
        VIRUS_FOLDER_TO_SCAN = String.valueOf(VirusScanning.VISUS_SCANNING_FOLDER) + "virusFolderToScan/";
    }
    
    public static String byteArrayToHexString(final byte[] array) {
        String string = "";
        for (int i = 0; i < array.length; ++i) {
            string = String.valueOf(string) + Integer.toString(256 + (0xFF & array[i]), 16).substring(1);
        }
        return string;
    }
    
    private boolean checkIfFileVirus(final File file) {
        try {
            final MessageDigest instance = MessageDigest.getInstance("SHA-1");
            char[] array;
            FileReader fileReader;
            int i;
            for (array = new char[10677], fileReader = new FileReader(file), i = 0; i != 10677; i += fileReader.read(array, i, array.length - i)) {}
            fileReader.close();
            if (i > 0) {
                return this.isInVirusDB(byteArrayToHexString(instance.digest(new String(array).getBytes())));
            }
            fileReader.close();
            return false;
        }
        catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return false;
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
            return false;
        }
        catch (Exception ex3) {
            ex3.printStackTrace();
            return false;
        }
    }
    
    public static void executeShellCommand(final String s, final String s2, final boolean b) {
        Label_0082: {
            if (!b) {
                break Label_0082;
            }
            try {
                Process process = Runtime.getRuntime().exec("su " + s2);
                while (true) {
                    final DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
                    dataOutputStream.writeBytes("exit\n");
                    dataOutputStream.close();
                    process.waitFor();
                    Log.i(s, "Executed cmd: " + s2);
                    return;
                    process = Runtime.getRuntime().exec(s2);
                    continue;
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            catch (InterruptedException ex2) {
                ex2.printStackTrace();
            }
        }
    }
    
    private boolean heavyCheckIfFileVirus(final File file) {
        try {
            final MessageDigest instance = MessageDigest.getInstance("SHA-1");
            final int n = (int)file.length();
            final char[] array = new char[n];
            final FileReader fileReader = new FileReader(file);
            final int read = fileReader.read(array, 0, n - 0);
            fileReader.close();
            if (read > 0 && this.isInVirusDB(byteArrayToHexString(instance.digest(new String(array).getBytes())))) {
                return true;
            }
            fileReader.close();
            return false;
        }
        catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return false;
        }
        catch (IOException ex2) {
            ex2.printStackTrace();
            return false;
        }
        catch (Exception ex3) {
            ex3.printStackTrace();
            return false;
        }
    }
    
    private void initSignatureDB(final String s) {
        Block_0: {
            break Block_0;
        Label_0207:
            while (true) {
                int i;
                int length;
                do {
                    Label_0051: {
                        break Label_0051;
                        try {
                            final MessageDigest instance = MessageDigest.getInstance("SHA-1");
                            final File[] listFiles = new File(s).listFiles();
                            this.signatureDB = new String[listFiles.length];
                            final char[] array = new char[10677];
                            length = listFiles.length;
                            int n = 0;
                            i = 0;
                            continue Label_0207;
                            final FileReader fileReader = new FileReader(listFiles[i]);
                            fileReader.read(array, 0, array.length - 0);
                            final String[] signatureDB = this.signatureDB;
                            final int n2 = n + 1;
                            signatureDB[n] = byteArrayToHexString(instance.digest(new String(array).getBytes()));
                            fileReader.close();
                            ++i;
                            n = n2;
                            continue Label_0207;
                        }
                        catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                            return;
                        }
                        catch (IOException ex2) {
                            ex2.printStackTrace();
                            return;
                        }
                        catch (NoSuchAlgorithmException ex3) {
                            Log.e("VirusScanning", "NoSuchAlgorithmException " + ex3.getMessage());
                            return;
                        }
                        catch (Exception ex4) {
                            ex4.printStackTrace();
                            Log.e("VirusScanning", "Exception " + ex4.getMessage());
                            return;
                        }
                    }
                    continue Label_0207;
                } while (i < length);
                break;
            }
        }
    }
    
    private boolean isInVirusDB(final String s) {
        for (int i = 0; i < this.signatureDB.length; ++i) {
            if (s.equals(this.signatureDB[i])) {
                return true;
            }
        }
        return false;
    }
    
    public int localScanFolder() {
        Log.i("VirusScanning", "Scan folder");
        Log.i("VirusScanning", "Started signature initialization on folder: " + VirusScanning.VIRUS_DB_PATH);
        this.initSignatureDB(VirusScanning.VIRUS_DB_PATH);
        Log.i("VirusScanning", "Finished signature initialization");
        int n = 0;
        Log.i("VirusScanning", "Scanning folder: " + VirusScanning.VIRUS_FOLDER_TO_SCAN);
        final File[] listFiles = new File(VirusScanning.VIRUS_FOLDER_TO_SCAN).listFiles();
        final int length = listFiles.length;
        Log.i("VirusScanning", "Nr signatures: " + this.signatureDB.length + ". Nr files to scan: " + length);
        Log.i("VirusScanning", "Checking files: " + length);
        for (int i = 0; i < length; ++i) {
            if (this.heavyCheckIfFileVirus(listFiles[i])) {
                ++n;
            }
        }
        return n;
    }
    
    public int localScanFolderReduce(final int[] array) {
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            n += array[i];
        }
        return n;
    }
}
