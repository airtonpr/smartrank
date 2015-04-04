package method.advisor;

import java.util.List;

public class NodeDependence {

	private boolean isAssign;
	/* significa que este nó esta atrelado a outro método */
	private boolean isClustered;
	private List<String> variables;
	private String method;
	private int line;

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	@Override
	public String toString() {
		return this.getMethod() + " L:" + line;
	}

	@Override
	public boolean equals(Object obj) {
		NodeDependence nod = (NodeDependence) obj;

		if (this.getLine() == nod.getLine()
				&& this.getMethod() == nod.getMethod()) {
			return true;
		}
		return false;
	}

	public NodeDependence(boolean isAssign, List<String> variables,
			String method, int line) {
		super();
		this.isAssign = isAssign;
		this.variables = variables;
		this.method = method;
		this.line = line;
	}

	public boolean isAssign() {
		return isAssign;
	}

	public void setAssign(boolean isAssign) {
		this.isAssign = isAssign;
	}

	public List<String> getVariables() {
		return variables;
	}

	public void setVariables(List<String> variables) {
		this.variables = variables;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public boolean isClustered() {
		return isClustered;
	}

	public void setClustered(boolean isClustered) {
		this.isClustered = isClustered;
	}

	public boolean hasDependenceTo(NodeDependence postNode) {
		boolean correctOrder = this.getLine() < postNode.getLine();
		if (correctOrder && this.isAssign) {
			for (String var : this.getVariables()) {
				for (String var2 : postNode.getVariables()) {
					if (var2.trim().contains(var.trim())) {
						return true;
					}
				}
			}
		} else {
			return false;
		}
		return false;
	}
}
