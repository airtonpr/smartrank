package method.advisor;

import java.util.ArrayList;
import java.util.List;

public class ClassNode {

	private List<MethodNode> methodNodes = new ArrayList<MethodNode>();
	private String fileName;
	private int granularity;
	private String javaPackage;
	
	
	
	public String getJavaPackage() {
		return javaPackage;
	}
	public void setJavaPackage(String javaPackage) {
		this.javaPackage = javaPackage;
	}
	public int getGranularity() {
		return granularity;
	}
	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}
	public List<MethodNode> getMethodNodes() {
		return methodNodes;
	}
	public void setMethodNodes(List<MethodNode> methodNodes) {
		this.methodNodes = methodNodes;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public ClassNode(
			String fileName, String javaPackage) {
		super();
		this.fileName = fileName;
		this.javaPackage = javaPackage;
	}
	
	
}
