package component.advisor;

import java.util.HashSet;
import java.util.Set;

import org.xml.sax.HandlerBase;

public class BundleFullRecursive implements Comparable<BundleFullRecursive> {

	private Set<String> classes;

	public BundleFullRecursive(HashSet<String> classes) {
		super();
		this.classes = classes;
	}

	public BundleFullRecursive() {
		classes = new HashSet<String>();
	}

	@Override
	public String toString() {
		return classes.toString();
	}

	public Set<String> getEfferedClasses() {
		return classes;
	}

	public void setClasses(Set<String> classes) {
		this.classes = classes;
	}

	@Override
	public boolean equals(Object obj) {
		BundleFullRecursive b = (BundleFullRecursive) obj;

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
	public int compareTo(BundleFullRecursive b) {
		if (this.equals(b)) {
			return 0;
		}
		return -1;
	}
}
