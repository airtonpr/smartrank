package mainpack;

import java.io.IOException;

import method.advisor.ClassNode;
import method.advisor.Dependence;
import method.advisor.MethodNode;
import method.japa.parser.ParseException;
import advisor.main.MethodAdvisor;

public class Example {
	public static void main(String[] args) {
		try {
			ClassNode cn = MethodAdvisor.runMethodAdvisor("Test.java");

			for (MethodNode mn : cn.getMethodNodes()) {
				for (Dependence d : mn.getDependences()) {
					System.out.println("Chamada apontadora: " + d.getAssign() + " Método apontado: " + d.getCall());
				}
			}
			
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}

}
