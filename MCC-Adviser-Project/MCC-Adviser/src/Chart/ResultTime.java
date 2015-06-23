package Chart;

public class ResultTime {
	private double time;
	private double probability;

	public ResultTime(double time, double probability){
		this.time = time;
		this.probability = probability;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public double getProb() {
		return probability;
	}

	public void setProb(double probability) {
		this.probability = probability;
	}
	

}

