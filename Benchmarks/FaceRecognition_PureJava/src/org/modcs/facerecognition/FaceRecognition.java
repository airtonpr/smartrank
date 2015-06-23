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
public interface FaceRecognition {
    public Matching recognizeFace(double[] face);
    public Matching recognizeFace2(double[] face2, File db);
    public Matching findFacesAndRecognize(RgbImage img);
    public Matching findFacesAndRecognize2(RgbImage img, File db);    
    public List<Rect> findFaces(RgbImage img);
    
    public Matching sendFace(double[] face);
    public List<Rect> sendImage(RgbImage img);
}
