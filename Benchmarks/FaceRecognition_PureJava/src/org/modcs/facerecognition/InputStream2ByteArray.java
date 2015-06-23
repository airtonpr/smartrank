/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.modcs.facerecognition;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;

public class InputStream2ByteArray {

    /**
     * @param args
     */
    public static void main(String[] args) {

        InputStream ins = InputStream2ByteArray.class
                    .getResourceAsStream("/org/modcs/facerecognition/utils/HCSB.txt");
        
        
        System.out.println(ins);
        
        byte[] streamAsBytes = stream2Bytes(ins);

        System.out.println(Arrays.toString(streamAsBytes));
        System.out.println(new String(streamAsBytes));
    }

    public static byte[] stream2Bytes(InputStream ins) {

        byte[] availableBytes = new byte[0];

        try {
            byte[] buffer = new byte[4096];
            ByteArrayOutputStream outs = new ByteArrayOutputStream();

            int read = 0;
            while ((read = ins.read(buffer)) != -1) {
                outs.write(buffer, 0, read);
            }

            ins.close();
            outs.close();
            availableBytes = outs.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableBytes;
    }
}
