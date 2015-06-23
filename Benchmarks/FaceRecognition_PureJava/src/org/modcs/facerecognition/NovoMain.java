/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.modcs.facerecognition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Danilo
 */
public class NovoMain {

    public static void main(String[] args) throws Exception {
        String basedir = "/home/danilo/Desktop/database/CroppedYale/yaleB";

        String f = "/home/danilo/Desktop/test.ppm";
        
        File database = new File("db");
        
        if(!database.exists()){
            database.mkdir();
        }
        
        List<EigenFaceCreator> bundles = new ArrayList<EigenFaceCreator>();

        for (int i = 11; i <= 39; i++) {
            
            String index = i <= 9 ? "0" + i : "" + i;
            
            String dir = basedir + index;
            System.out.println(dir);
            
            File directory = new File(dir);
            if(!directory.exists()){
                System.out.println("Diretório não existe.");
                continue;
            }
            
            EigenFaceCreator creator = new EigenFaceCreator();
            
            creator.readFaceBundles(dir);
            creator.setName("yaleB" + i);
            
            bundles.add(creator);
            
            File file = new File(database, creator.getName() + ".obj");
            
            serializeBundle(creator, file);
        }

        long t0 = System.currentTimeMillis();
        
        for (EigenFaceCreator creator : bundles) {
            String id = creator.checkAgainst(f);
            System.out.println("ROnaldo: " + id);
        }
        
        long t1 = System.currentTimeMillis();
        
        System.out.println("**** " + (t1 - t0));
    }

    private static void serializeBundle(EigenFaceCreator creator, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            
            oos.writeObject(creator);
            
            oos.close();
            fos.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
