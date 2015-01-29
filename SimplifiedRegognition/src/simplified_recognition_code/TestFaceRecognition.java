package simplified_recognition_code;

/*
 *
 * Copyright (c) 2002 by Konrad Rzeszutek
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
import java.lang.*;
import java.io.*;



public class TestFaceRecognition  {

    public static void main(String args[]) {

        args = new String[]{"C:\\WORKSPACES\\smartrank\\SimplifiedRegognition\\trainingimages", 
        		"C:\\WORKSPACES\\smartrank\\SimplifiedRegognition\\100a.jpg"};
        
        if (args.length != 2) {
            prUsage();
            System.exit(0);
        }

        String dir = args[0];
        String file = args[1];

      try {
        EigenFaceCreator creator = new EigenFaceCreator();

        //creator.USE_CACHE = -1;
        System.out.println("Constructing face-spaces from "+dir+" ...");
        creator.readFaceBundles(dir);

        System.out.println("Comparing "+file+" ...");
        
        
        long t0 = System.currentTimeMillis();
        String result = creator.checkAgainst(file);
        long t1 = System.currentTimeMillis();
        
        System.out.println("Most closly reseambling: "+result+" with "+creator.DISTANCE+" distance. And time: " + (t1 - t0));

      } catch (Exception e) { e.printStackTrace(); }
    }

    static void prUsage() {
        System.err.println("Usage: java TestFaceRecognition <directory of training images> <image to test against>");
    }


}
