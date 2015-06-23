/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.modcs.facerecognition;

import java.io.Serializable;

/**
 *
 * @author Danilo
 */
public class Matching implements Comparable<Matching>, Serializable{
    public String name;
    public double distance;

    public Matching(String name, double distance) {
        this.name = name;
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public String getSetName() {
        return name;
    }

    @Override
    public String toString() {
        return "(" + name + ", " + distance + ")";
    }

    @Override
    public int compareTo(Matching m) {
        Double d = distance;
        Double d2 = m.distance;
        
        return d.compareTo(d2);
    }       
    
    
}
