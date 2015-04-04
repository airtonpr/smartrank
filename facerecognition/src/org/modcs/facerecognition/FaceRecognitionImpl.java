/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.modcs.facerecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jjil.algorithm.Gray8Rgb;
import jjil.algorithm.RgbAvgGray;
import jjil.core.Image;
import jjil.core.Rect;
import jjil.core.RgbImage;

/**
 *
 * @author Danilo
 */
public class FaceRecognitionImpl implements FaceRecognition, Serializable {

//    private static Thread thread = new Thread() {
//        @Override
//        public void run() {
//            try {
//                bundles = new ArrayList<EigenFaceCreator>();
//
//                for (int i = 1; i <= 10; i++) {
//                    String dir = BASE_DIR + "s" + i;
//                    System.out.println(dir);
//
//                    EigenFaceCreator creator = new EigenFaceCreator();
//                    creator.readFaceBundles(dir);
//                    creator.setName("s" + i);
//
//                    bundles.add(creator);
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    };
//
//    static {
//        thread.start();
//    }
    private static ArrayList<EigenFaceCreator> creators;

    private final static Thread thread = new Thread() {

        @Override
        public void run() {
            try {
                File database = new File("./db");

                List<String> list = Arrays.asList(database.list());
                creators = new ArrayList<EigenFaceCreator>();

                Collections.sort(list);

                System.out.println("Reading database");

                for (String str : list) {
                    EigenFaceCreator creator = readObject(new File(database, str));

                    creators.add(creator);
                }

                System.out.println("Finished!" + creators.size());

            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
    };

    static {
        //thread.start();
    }

    private static List<EigenFaceCreator> bundles;
    private static String BASE_DIR = "C:\\Users\\Danilo\\Dropbox\\admin\\faces\\";
    private static double THREESHOLD = 1.0;
    public static double[] DEFAULT = EigenFaceCreator.readStream(FaceRecognitionImpl.class.getResourceAsStream("/org/modcs/facerecognition/utils/7.ppm"));

    @Override
    public Matching recognizeFace(double[] face) {

        //        try {
//            thread.join();
//        } catch (InterruptedException ex) {
//        }
//        System.out.println("Comparando face com banco de dados...");
//
//        Matching small = new Matching("NONE", Double.MAX_VALUE);
//
//        try {
//
//            for (EigenFaceCreator c : creators) {
//
//                EigenFaceCreator creator = (EigenFaceCreator) c.clone();
//
//                if (creator != null) {
//                    Matching m = creator.checkAgainst(face);
//
//                    if (m.getDistance() < small.getDistance() && m.getDistance() < THREESHOLD) {
//                        small = m;
//                    }
//                }
//            }
//
//            return small;
//        } catch (Exception ex) {
//            return small;
//        }
        Matching small = new Matching("NONE", Double.MAX_VALUE);

        try {
            EigenFaceCreator creator = new EigenFaceCreator();

            File dir = new File("faces");

            creator.readFaceBundles(dir.getAbsolutePath());

            Matching m = creator.checkAgainst(face);

            return m;
        } catch (Exception ex) {
            Logger.getLogger(FaceRecognitionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return small;
    }

    @Override
    public Matching recognizeFace2(double[] face, File database) {
//        System.out.println("Comparando face com banco de dados...");
//
//        Matching small = new Matching("NONE", Double.MAX_VALUE);
//
//        try {
//            List<String> list = Arrays.asList(database.list());
//            Collections.sort(list);
//
//            for (String str : list) {
//
//                EigenFaceCreator creator = readObject(new File(database, str));
//
//                if (creator != null) {
//                    Matching m = creator.checkAgainst(face);
//
//                    if (m.getDistance() < small.getDistance() && m.getDistance() < THREESHOLD) {
//                        small = m;
//                    }
//
//                    creator = null;
//                }
//            }
//
//            return small;
//        } catch (Exception ex) {
//            return small;
//        }

        Matching small = new Matching("NONE", Double.MAX_VALUE);

        try {
            EigenFaceCreator creator = new EigenFaceCreator();

            creator.readFaceBundles(database.getAbsolutePath());

            Matching m = creator.checkAgainst(face);

            System.out.println("RONALDO " + m);
            
            return m;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return small;

    }

    @Override
    public Matching findFacesAndRecognize(RgbImage img) {
        try {
            return detectFace(img, 1, 40);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Matching findFacesAndRecognize2(RgbImage img, File db) {
        try {
            System.out.println("CAIU AQUI ESSA PORRA!!!");
            return detectFace2(img, 1, 40, db);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Rect> findFaces(RgbImage img) {
        try {
            return detectFace3(img, 1, 40);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Matching sendFace(double[] face) {
        return new Matching("NONE", Double.MAX_VALUE);
    }

    @Override
    public List<Rect> sendImage(RgbImage img) {
        List<Rect> rect = new ArrayList<Rect>();
        rect.add(new Rect(0, 0, 0, 0));
        return rect;
    }

    private Matching detectFace(RgbImage img, int minScale, int maxScale) throws Exception {
        try {
            InputStream is = FaceRecognitionImpl.class
                    .getResourceAsStream("/org/modcs/facerecognition/utils/HCSB.txt");
            Gray8DetectHaarMultiScale detectHaar = new Gray8DetectHaarMultiScale(
                    is, minScale, maxScale);

            RgbAvgGray toGray = new RgbAvgGray();
            toGray.push(img);

            List<Rect> results = detectHaar.pushAndReturn(toGray.getFront());
            System.out.println("Found " + results.size() + " faces");

            Image i = detectHaar.getFront();
            Gray8Rgb g2rgb = new Gray8Rgb();
            g2rgb.push(i);

//            RgbImageJ2se conv = new RgbImageJ2se();
//            conv.toFile((RgbImage) g2rgb.getFront(), output.getCanonicalPath());
            if (results.size() > 0) {
                return recognizeFace(DEFAULT);
            }

        } catch (jjil.core.Error error) {
        } catch (IOException iOException) {
        }

        return null;

    }

    private Matching detectFace2(RgbImage img, int minScale, int maxScale,
            File database) throws Exception {
        try {
            InputStream is = FaceRecognitionImpl.class
                    .getResourceAsStream("/org/modcs/facerecognition/utils/HCSB.txt");
            Gray8DetectHaarMultiScale detectHaar = new Gray8DetectHaarMultiScale(
                    is, minScale, maxScale);

            RgbAvgGray toGray = new RgbAvgGray();
            toGray.push(img);

            List<Rect> results = detectHaar.pushAndReturn(toGray.getFront());
            System.out.println("Found " + results.size() + " faces");

            Image i = detectHaar.getFront();
            Gray8Rgb g2rgb = new Gray8Rgb();
            g2rgb.push(i);

            if (results.size() > 0) {
                return recognizeFace2(DEFAULT, database);
            } else {
                return null;
            }

//            for (Rect rec : results) {
//                recognizeFace2(DEFAULT, database);
//            }            
//            RgbImageJ2se conv = new RgbImageJ2se();
//            conv.toFile((RgbImage) g2rgb.getFront(), "sharivan.jpg");
        } catch (Throwable error) {
            error.printStackTrace();
            return null;
        }

    }

    private List<Rect> detectFace3(RgbImage img, int minScale, int maxScale) throws Exception {
        try {

            long t0 = System.currentTimeMillis();

            InputStream is = FaceRecognitionImpl.class
                    .getResourceAsStream("/org/modcs/facerecognition/utils/HCSB.txt");
            Gray8DetectHaarMultiScale detectHaar = new Gray8DetectHaarMultiScale(
                    is, minScale, maxScale);

            RgbAvgGray toGray = new RgbAvgGray();
            toGray.push(img);

            List<Rect> results = detectHaar.pushAndReturn(toGray.getFront());
            System.out.println("Found " + results.size() + " faces");

            long t1 = System.currentTimeMillis();

            System.out.println("---> " + (t1 - t0));

            return results;

        } catch (Throwable error) {
            return null;
        }

    }

    private static EigenFaceCreator readObject(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            EigenFaceCreator efc = (EigenFaceCreator) ois.readObject();

            return efc;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {

        long t0 = System.currentTimeMillis();

        FaceRecognition fr = new FaceRecognitionImpl();

        double[] d = DEFAULT;

        Matching m = fr.recognizeFace(d);

        long t1 = System.currentTimeMillis();

        System.out.println(m + "< " + (t1 - t0));
    }

}
