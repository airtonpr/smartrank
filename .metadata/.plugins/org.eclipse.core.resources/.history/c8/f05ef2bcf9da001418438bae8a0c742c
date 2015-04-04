package method.advisor;

import java.util.ArrayList;
import java.util.List;

import method.japa.parser.ast.body.MethodDeclaration;

public class MethodNode {

	private List<NodeDependence> assignNodes = new ArrayList<NodeDependence>();
	private List<NodeDependence> callNodes = new ArrayList<NodeDependence>();
	private List<Dependence> dependences = new ArrayList<Dependence>();
	private List<ClusterDependence> clusterDependences = new ArrayList<ClusterDependence>();
	private MethodDeclaration methodDeclaration;
	private String javaFileName;
	private String javaPackage;
	private int granularity;
	
	
	public MethodNode(MethodDeclaration methodDeclaration, String javaFileName, String javaPackage) {
		this.methodDeclaration = methodDeclaration;
		this.javaFileName = javaFileName;
		this.javaPackage = javaPackage;
	}
	
	
	
	public int getGranularity() {
		return granularity;
	}



	public void setGranularity(int granularity) {
		this.granularity = granularity;
	}



	public String getJavaPackage() {
		return javaPackage;
	}

	public void setJavaPackage(String javaPackage) {
		this.javaPackage = javaPackage;
	}



	public String getJavaFileName() {
		return javaFileName;
	}


	public void setJavaFileName(String javaFileName) {
		this.javaFileName = javaFileName;
	}


	public MethodDeclaration getMethodDeclaration() {
		return methodDeclaration;
	}

	public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
		this.methodDeclaration = methodDeclaration;
	}

	public List<Dependence> getDependences() {
		return dependences;
	}

	public void setDependences(List<Dependence> dependences) {
		this.dependences = dependences;
	}

	public List<ClusterDependence> getClusterDependences() {
		return clusterDependences;
	}

	public void setClusterDependences(List<ClusterDependence> clusterDependences) {
		this.clusterDependences = clusterDependences;
	}

/*
	@Override
	public String toString() {
		String s = "";
		int i = 1;
		for (ClusterDependence dep : clusterDependences) {
			s += " "+ i++ +"º Cluster \n";
			s += dep;
		}
		return s;
	}
*/	

	@Override
	public String toString() {
		String s = "";
		int i = 1;
		for (ClusterDependence dep : clusterDependences) {
			s += dep;
		}
		return s;
	}
	
	public void runClustering() {
		
		//Cria as dependencias com dois nós
		
		for (NodeDependence assignNode : assignNodes) {
			for (NodeDependence callNode : callNodes) {
				if (assignNode.hasDependenceTo(callNode)) {
					this.getDependences().add(new Dependence(assignNode, callNode));
					callNode.setClustered(true);
				}
			}
		}
		
		//Cria as falsas dependencias, ou seja tem apenas um nó chamado
		for (NodeDependence callNode : callNodes) {
			if (!callNode.isClustered()) {
				this.getDependences().add(new Dependence(null, callNode));
			}
		}

		//Criar os clusters
		for (Dependence dep : this.getDependences()) {
			ClusterDependence clusterDependence = getClusterWithDependence(dep);
			if (clusterDependence!= null) {
				if(dep.getAssign() != null){
					clusterDependence.getDependences().add(dep);
				}
			}else{
				ClusterDependence clusterDependence2 = new ClusterDependence();
				clusterDependence2.getDependences().add(dep);
				clusterDependences.add(clusterDependence2);
			}
		}
	}
	
	public ClusterDependence getClusterWithDependence(Dependence dep){
		for (ClusterDependence clusterDependence : clusterDependences) {
			if (clusterDependence.hasDependence(dep)) {
				return clusterDependence;
			}
		}
		return null;
	}
	
	public String getRootName() {
		return this.getMethodDeclaration().getName();
	}

	public int getStartLine() {
		return this.getMethodDeclaration().getBeginLine();
	}
	public List<NodeDependence> getAssignNodes() {
		return assignNodes;
	}

	public void setAssignNodes(List<NodeDependence> assignNodes) {
		this.assignNodes = assignNodes;
	}

	public List<NodeDependence> getCallNodes() {
		return callNodes;
	}

	public void setCallNodes(List<NodeDependence> callNodes) {
		this.callNodes = callNodes;
	}

}
