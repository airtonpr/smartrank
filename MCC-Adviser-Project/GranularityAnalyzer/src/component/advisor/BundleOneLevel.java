package component.advisor;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.HandlerBase;

public class BundleOneLevel implements Comparable<BundleOneLevel>{

	private String mainClass;
	private Set<String> efferedClasses;

	public BundleOneLevel(String mainClass) {
		super();
		this.mainClass = mainClass;
		this.efferedClasses = new HashSet<String>();
	}
	
	public BundleOneLevel(String mainClass, Set<String> classes) {
		super();
		this.mainClass = mainClass;
		this.efferedClasses = classes;
	}
	
	@Override
	public String toString() {
		return this.getMainClass();
	}

	public String getMainClass() {
		return mainClass;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public Set<String> getEfferedClasses() {
		return efferedClasses;
	}

	public void setClasses(Set<String> classes) {
		this.efferedClasses = classes;
	}

	@Override
	public boolean equals(Object obj) {
		BundleOneLevel b = (BundleOneLevel) obj;
		
		if (this.getMainClass().equals(b.getMainClass())) {
			return true;
		}
		for (String klass : b.getEfferedClasses()) {
			for (String klass2 : this.getEfferedClasses()) {
				if (klass.equals(klass2)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int compareTo(BundleOneLevel b) {
		if (this.equals(b)) {
			return 0;
		}
		return -1;
	}
}
