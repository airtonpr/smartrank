/*
 * $Id: \\dds\\src\\Research\\ckjm.RCS\\src\\gr\\spinellis\\ckjm\\ClassMetricsContainer.java,v 1.9 2005/08/10 16:53:36 dds Exp $
 *
 * (C) Copyright 2005 Diomidis Spinellis
 *
 * Permission to use, copy, and distribute this software and its
 * documentation for any purpose and without fee is hereby granted,
 * provided that the above copyright notice appear in all copies and that
 * both that copyright notice and this permission notice appear in
 * supporting documentation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package component.gr.spinellis.ckjm;

import org.apache.bcel.classfile.*;

import component.advisor.BundleFullRecursive;
import component.advisor.BundleOneLevel;

import java.util.*;
import java.io.*;


/**
 * A container of class metrics mapping class names to their metrics.
 * This class contains the the metrics for all class's during the filter's
 * operation.  Some metrics need to be updated as the program processes
 * other classes, so the class's metrics will be recovered from this
 * container to be updated.
 *
 * @version $Revision: 1.9 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
class ClassMetricsContainer {

    /** The map from class names to the corresponding metrics */
    private HashMap<String, ClassMetrics> m = new HashMap<String, ClassMetrics>();

    private Set<BundleFullRecursive> bundlesWithoutIntersection = new TreeSet<BundleFullRecursive>();
    
    //These bundles do not include the root class in the efferedClasses
    private List<BundleOneLevel> possibleBundles = new ArrayList<BundleOneLevel>();
    
    

	/** Return a class's metrics */
	public ClassMetrics getMetrics(String name) {
		ClassMetrics cm = m.get(name);
		if (cm == null) {
			cm = new ClassMetrics(name);
			m.put(name, cm);
		}
		return cm;
	}
	
	public BundleOneLevel getBundle(String name) {
		for (BundleOneLevel b : possibleBundles) {
			if (b.getMainClass().equals(name)) {
				return b;
			}
		}
		return new BundleOneLevel(name);
	}

    /** Print the metrics of all the visited classes. */
    public void printMetrics(CkjmOutputHandler handler) {
	Set<Map.Entry<String, ClassMetrics>> entries = m.entrySet();
	Iterator<Map.Entry<String, ClassMetrics>> i;

	int nom = 0, di = 0, cbo = 0, numberOfClasses = 0;
	//REPORT SUMMARY
	for (i = entries.iterator(); i.hasNext(); ) {
	    Map.Entry<String, ClassMetrics> e = i.next();
	    ClassMetrics cm = e.getValue();
	    if (cm.getWmc()>0) {
			
	    numberOfClasses++;
	    nom+=cm.getWmc();
	    di+=cm.getDit();
	    cbo+=cm.getCbo();
	    
	    HashSet<String> efferentCoupledClasses = cm.getEfferentCoupledClasses();
	    BundleOneLevel bundle = new BundleOneLevel(cm.getName(), efferentCoupledClasses);
		possibleBundles.add(bundle);
	    }
	    
	}

	handler.summary(""
			+ "\n-- List of Possible Components --\n" 
			+ classesBundleList() + 
			" \n\n-- Detailed Metrics by Classes -- \n"
			+ "\nLegend:\n"
			+ "* NOM - number of methods per class \n"
			+ "* DI (Depth of Inheritance) - The depth of inheritance (DI) metric provides for each class a measure of the inheritance levels from the object hierarchy top. In Java where all classes inherit Object the minimum value of DIT is 1.\n"
			+ "* CBO (Coupling between object classes) - The coupling between object classes (CBO) metric represents the number of classes coupled to a given class (efferent couplings, Ce). This coupling can occur through method calls, field accesses, inheritance, arguments, return types, and exceptions.\n\n"
			+ ""
			+ "\n-- Summary of Source Code Evaluation -- \n"
			+ "Total Number of Classes: "+numberOfClasses+"\n"
			+ "Total Number of Methods: "+nom+"\n"
			+ "Total Depth of Inheritance: "+di+"\n"
			+ "Number of Possible Components: "+ this.bundlesWithoutIntersection.size() +"\n"
			+ "Total Coupling: "+cbo+"\n"
											+ "");
	    
	//DETAILED PER CLASS
	for (i = entries.iterator(); i.hasNext(); ) {
	    Map.Entry<String, ClassMetrics> e = i.next();
	    ClassMetrics cm = e.getValue();
	    if (cm.isVisited() && (MetricsFilter.includeAll() || cm.isPublic()))
		handler.handleClass(e.getKey(), cm);
	}
    }
    
   public String classesBundleList(){
	   String s = "";
	   Set<Map.Entry<String, ClassMetrics>> entries = m.entrySet();
	   Iterator<Map.Entry<String, ClassMetrics>> i;
	   int j = 1;
	   for (BundleOneLevel bundle : possibleBundles) {
		    s+="\n\nBundle Number " + (j++) +" ";
		    String tab = "";
			s = buildBundleReport(tab , s, bundle);
			BundleFullRecursive bundleFullRecursive = new BundleFullRecursive();
			bundlesWithoutIntersection.add(buildBundleRecursive(bundle, bundleFullRecursive));
		}
	   return s;
    }

	private String buildBundleReport(String tab, String acumulatedResult, BundleOneLevel bundle) {
		tab += "  ";
		if (bundle.getEfferedClasses().isEmpty()) {
			return acumulatedResult += "\n"+tab+"Root Class:"+bundle.getMainClass() + " [LEAF]";
		} else {
				acumulatedResult += "\n"+tab+"Root Class: " + bundle.getMainClass()
						//+ " \n UnOffloadable? "//+ (cm.isNotOffloadableClass() ? "Yes" : "No")
						+ "\n"+tab+"Coupled Classes: ";
				for (String efferedClass : bundle.getEfferedClasses()) {
					//acumulatedResult += cm.getEfferentCoupledClasses();
					acumulatedResult = buildBundleReport(tab, acumulatedResult, getBundle(efferedClass));
				}
		}
		return acumulatedResult;
	}
	
	private BundleFullRecursive buildBundleRecursive(BundleOneLevel bundle,  BundleFullRecursive bundleFullRecursive) {
		if (bundle.getEfferedClasses().isEmpty()) {
			bundleFullRecursive.getEfferedClasses().add(bundle.getMainClass());
			return bundleFullRecursive;
		} else {
				bundleFullRecursive.getEfferedClasses().add(bundle.getMainClass());
				for (String efferedClass : bundle.getEfferedClasses()) {
					//acumulatedResult += cm.getEfferentCoupledClasses();
					bundleFullRecursive.getEfferedClasses().add(getBundle(efferedClass).getMainClass());
				}
		}
		return bundleFullRecursive;
	}
}
