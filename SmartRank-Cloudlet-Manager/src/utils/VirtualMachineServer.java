package utils;



public class VirtualMachineServer implements Comparable{

	private double loadPercentage;
	private int latency;
	private double capacity;

	private double cost;
	private String name;
	private int totalItensToProcess;
	
	public VirtualMachineServer(String name){
		this.name = name;
	}
			
	public VirtualMachineServer(String name, double loadPercentage, int latency, int capacity) {
		super();
		this.name = name;
		this.loadPercentage = loadPercentage;
		this.latency = latency;
		this.capacity = capacity;
	}
	
	public double getCapacity() {
		return capacity;
	}
	
	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}
	public double getLoadPercentage() {
		return loadPercentage;
	}

	public void setLoadPercentage(double loadPercentage) {
		this.loadPercentage = loadPercentage;
	}

	public double getRtt() {
		return this.latency + this.capacity;
	}
	
	public int getLatency() {
		return latency;
	}

	public void setLatency(int latency) {
		this.latency = latency;
	}

	public String getName() {
		return name;
	}

	public void setName(String ip) {
		this.name = ip;
	}

	@Override
	public int compareTo(Object otherPingResult) {
		if (!(otherPingResult instanceof VirtualMachineServer)) {
			 throw new ClassCastException("A PingResult object expected.");
		}
		double anotherFitness = ((VirtualMachineServer) otherPingResult).getCost();
		return Double.compare(this.getCost(), anotherFitness);
	}
	
	@Override
	public String toString() {
		return "name " + name + " capacity " + capacity + " load " + loadPercentage + " latency " + latency + "  cost " + cost;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public int getTotalItensToProcess() {
		return totalItensToProcess;
	}

	public void setTotalItensToProcess(int totalItensToProcess) {
		this.totalItensToProcess = totalItensToProcess;
	}
	
	
	
}
	
