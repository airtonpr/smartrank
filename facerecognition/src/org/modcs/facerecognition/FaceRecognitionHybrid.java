/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.modcs.facerecognition;

import java.io.File;
import java.util.List;
import jjil.core.Rect;
import jjil.core.RgbImage;

/**
 *
 * @author Danilo
 */
public class FaceRecognitionHybrid implements FaceRecognition {

    private FaceRecognition remoteFaceRec;
    private FaceRecognition localFaceRec;
    private double[] face = EigenFaceCreator.readStream( FaceRecognitionHybrid.class.getResourceAsStream("/org/modcs/facerecognition/utils/7.ppm") );

    public FaceRecognitionHybrid(FaceRecognition fr) {
        this.remoteFaceRec = fr;
        this.localFaceRec = new FaceRecognitionImpl();
    }

    @Override
    public Matching recognizeFace(double[] face) {
        return remoteFaceRec.recognizeFace(face);
    }

    @Override
    public Matching recognizeFace2(double[] face2, File db) {
        return remoteFaceRec.recognizeFace2(face2, db);
    }

    @Override
    public Matching findFacesAndRecognize(final RgbImage img) {
                Thread t = new Thread() {
            @Override
            public void run() {
                System.out.println("Reconhecendo faces!");
                localFaceRec.findFaces(img);
                System.out.println("Reconheceu!");
                      
                remoteFaceRec.recognizeFace(face);
            }
        };
        
        t.start();

        return null;
    }

    @Override
    public Matching findFacesAndRecognize2(final RgbImage img, final File db) {

        Thread t = new Thread() {
            @Override
            public void run() {
                System.out.println("Reconhecendo faces!");
                localFaceRec.findFaces(img);
                System.out.println("Reconheceu!");
                      
                remoteFaceRec.recognizeFace2(face, db);
            }
        };
        
        t.start();

        return null;
    }

    @Override
    public List<Rect> findFaces(RgbImage img) {
        return remoteFaceRec.findFaces(img);
    }

    @Override
    public Matching sendFace(double[] face) {
        return remoteFaceRec.sendFace(face);
    }

    @Override
    public List<Rect> sendImage(RgbImage img) {
        return remoteFaceRec.sendImage(img);
    }

}
