/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.modcs.facerecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author danilo
 */
public class NovoMain2 {

    public static void main(String[] args) throws Exception {
        String basedir = "/home/danilo/Desktop/database/CroppedYale/yaleB";
        String f = "/home/danilo/Desktop/teste.ppm";

        File database = new File("db");

        long t0 = System.currentTimeMillis();

        List<String> list = Arrays.asList(database.list());
        Collections.sort(list);
        for (String str : list) {
            EigenFaceCreator efc = readObject(new File(database, str));
            if (efc != null) {
                String id = efc.checkAgainst(f);
                System.out.println("ROnaldo: " + id);
            }
        }

        long t1 = System.currentTimeMillis();

        System.out.println("**** " + (t1 - t0));
    }

    private static EigenFaceCreator readObject(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            EigenFaceCreator efc = (EigenFaceCreator) ois.readObject();

            return efc;
        } catch (Exception ex) {
            return null;
        }
    }

}
