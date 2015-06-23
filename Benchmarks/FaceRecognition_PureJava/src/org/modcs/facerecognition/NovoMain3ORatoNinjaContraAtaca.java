/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.modcs.facerecognition;

import java.io.File;

/**
 *
 * @author danilo
 */
public class NovoMain3ORatoNinjaContraAtaca {

    public static void main(String[] args) throws Exception {
        EigenFaceCreator creator = new EigenFaceCreator();

        File dir = new File("faces");

        System.out.println("Lendo bundas");
        
        creator.readFaceBundles(dir.getAbsolutePath());

        System.out.println("Bundas lidas");
        
        String f = "/home/danilo/Desktop/test.ppm";
        
        double[] d = FaceRecognitionImpl.DEFAULT;
        
        Matching m = creator.checkAgainst(d);
        
        System.out.println(m);

    }
}
