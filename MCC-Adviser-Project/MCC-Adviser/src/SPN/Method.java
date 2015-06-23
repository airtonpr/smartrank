package SPN;

import java.util.ArrayList;

/*
 * Elemento b�sico que representa um m�todo.
 */

public class Method{
	private String name;
	private ArrayList<Method> dependsOn; //Lista de m�todos dos quais este depende para ser executado.
	private ArrayList<Method> dependents; //Lista de m�todos que dependem da sa�da deste.
	private double time; //Tempo da chamada

	public Method(){
		this.name = "";
		this.dependsOn = new ArrayList<Method>();
		this.dependents = new ArrayList<Method>();
		this.time = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Method> getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(Method dependsOn) {
		this.dependsOn.add(dependsOn);
	}

	public ArrayList<Method> getDependents() {
		return dependents;
	}

	public void setDependents(Method dependents) {
		this.dependents.add(dependents);
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

}
