package SPN;

import java.util.ArrayList;

import org.modcs.tools.parser.model.ExecutionRuntime;
import org.modcs.tools.parser.model.SPNModel;
import org.modcs.tools.parser.model.metrics.Metric;
import org.modcs.tools.parser.model.metrics.SPNStationaryAnalysisMetric;
import org.modcs.tools.parser.model.metrics.SPNTransientAnalysisMetric;

import Chart.ResultTime;

/*
 * Elemento b�sico que representa uma SPN.
 */

public class SpnStructure {

	private ArrayList<Method> methods; //Lista de Method (m�todos da entrada)
	private SPNModel.Place INACTIVE_SYSTEM_P; //Place inicial do Inactive System
	private SPNModel.TimedTransition INACTIVE_SYSTEM_T; //Transition inicial do Inactive System
	private ExecutionRuntime runtime;
	private SPNModel model;
	private int quantityOfResource = 0; //Quantidade de recursos do Resource Pool
	private int finalParalelism = 0; //Quantidade de m�todo paralelos no final do modelo (que se ligam a transition 'FINAL')

	public SpnStructure(ArrayList<Method> methods, int qtResources) {

		this.methods = methods;
		this.INACTIVE_SYSTEM_P = null;
		this.INACTIVE_SYSTEM_T = null;

		this.runtime = new ExecutionRuntime();
		this.model = new SPNModel(this.runtime, "model1");
		this.runtime.putModel("model1", this.model);
		this.quantityOfResource = qtResources;
	}

	//A partir da lista de Methods cria os places e transistions 
	public void methodStructure () throws Exception{

		//Setup the inactive state
		this.INACTIVE_SYSTEM_P = new SPNModel.Place("INACTIVE_PLACE", 1);
		this.model.addPlace(this.INACTIVE_SYSTEM_P);

		this.INACTIVE_SYSTEM_T = new SPNModel.TimedTransition("INACTIVE_TRANSITION", this.model);
		this.INACTIVE_SYSTEM_T.setDelay(methods.get(0).getTime()*0.1);
		this.model.addTransition(this.INACTIVE_SYSTEM_T);


		for(int i = 0; i<this.methods.size(); i++){ //Para cada Method...

			Method method = this.methods.get(i);

			if(method.getDependents().size() == 0){ //Se ningu�m depende deste m�todo ele aumenta o grau de paralelismo final da SPN.
				this.finalParalelism++;
			}

			/*
			 * Se o m�todo n�o depender de nenhum outro, ele precisar� de um place inicial na sua representa��o (Ex: p1_add). Caso contr�rio
			 * o seu place inicial ser� o place final do m�todo do qual depende. 
			 */
			if(method.getDependsOn().size() == 0){ 
				SPNModel.Place place_1 = new SPNModel.Place("p1_" + method.getName());
				this.model.addPlace(place_1);
			}

			//Cria a transi��o imediata.
			SPNModel.ImmediateTransition transition_1 = new SPNModel.ImmediateTransition("RI_" + method.getName(), this.model);
			this.model.addTransition(transition_1);

			//Cria o place intermedi�rio.
			SPNModel.Place place_2 = new SPNModel.Place("p2_" + method.getName(), 0);
			this.model.addPlace(place_2);

			//Cria a transi��o exponencial e coloca o delay (tempo da chamada)
			SPNModel.TimedTransition transition_2 = new SPNModel.TimedTransition("RE_" + method.getName(), this.model);
			transition_2.setDelay(method.getTime());
			this.model.addTransition(transition_2);

			//Cria o place final
			SPNModel.Place pFinal = new SPNModel.Place("pFinal_" + method.getName(), 0);
			this.model.addPlace(pFinal);


		}
	}

	//Cria o place ResourcePool com a quantidade de recursos desejada.
	public void createResourcePool(){

		SPNModel.Place pool = new SPNModel.Place("ResourcePool", this.quantityOfResource);
		this.model.addPlace(pool);
	}

	//Busca por algum Method pelo nome
	public Method getMethod (String nome){
		for(int l = 0; l < this.methods.size(); l++){
			if(this.methods.get(l).getName().equals(nome)){
				return this.methods.get(l);
			}
		}
		return null;
	}

	//Liga os arcos para formar a estrutura para um calculo de an�lise transiente.
	public void createArcsTransientAnalysis (){

		SPNModel.Place resourcePlace = this.model.getPlace("ResourcePool");

		Object transitionsName [] = this.model.getTransitions().keySet().toArray();

		for (int i = 0; i < transitionsName.length; i++) {//For each Transitions...

			if(this.model.getTransition(transitionsName[i].toString()).getName().substring(0,2).equals("RI")){ //If is a Immediate Transition


				String transitionCompleteName = transitionsName[i].toString();
				String methodName = transitionCompleteName.substring(3,transitionCompleteName.length());

				//Se a transi��o for imediata criamos arcos de entrada partindo do place p1_... e do Resource Pool
				SPNModel.Place p1 = this.model.getPlace("p1_" + methodName);

				if(p1 == null){ //Se p1 for nulo quer dizer que o m�todo depende de outro, ent�o sua estrutura no modelo s� ter� p2_ e pFinal_.

					Method mAtual = getMethod(methodName); //M�todo atual
					
					//Ent�o, criamos com arcos de entrada os places pFinal_ dos m�todos que dependem dele a transi��o imediata dele, e depois o Resource Pool.
					for(int j = 0; j<mAtual.getDependsOn().size(); j++){
						SPNModel.Place pFinalA = this.model.getPlace("pFinal_" + mAtual.getDependsOn().get(j).getName());

						SPNModel.Arc a1 = new SPNModel.Arc(pFinalA.getName());
						this.model.getTransition(transitionsName[i].toString()).addInputArc(a1);
					}
					
					SPNModel.Arc a2 = new SPNModel.Arc(resourcePlace.getName());
					this.model.getTransition(transitionsName[i].toString()).addInputArc(a2);

				} else{
					
					//Mas se na representa��o do m�todo possuir p1, ent�o criamos arcos de entrada partindo do place p1_... e do Resource Pool
					SPNModel.Arc a1 = new SPNModel.Arc(p1.getName());
					this.model.getTransition(transitionsName[i].toString()).addInputArc(a1);

					SPNModel.Arc a2 = new SPNModel.Arc(resourcePlace.getName());
					this.model.getTransition(transitionsName[i].toString()).addInputArc(a2);

				}

				//E um arco de sa�da para o place p2_...
				SPNModel.Place p2 = this.model.getPlace("p2_" + methodName);

				SPNModel.Arc a3 = new SPNModel.Arc(p2.getName());
				this.model.getTransition(transitionsName[i].toString()).addOutputArc(a3);

			}else if(this.model.getTransition(transitionsName[i].toString()).getName().substring(0,2).equals("RE")){ //If is a Exponential Transition

				String transitionCompleteName = transitionsName[i].toString();
				String methodName = transitionCompleteName.substring(3,transitionCompleteName.length());

				Method m = getMethod(methodName);

				//Se a transi��o for exponencial criamos arcos de entrada partindo do place p2_...
				SPNModel.Place p2 = this.model.getPlace("p2_" + methodName);
				SPNModel.Arc a3 = new SPNModel.Arc(p2.getName());
				this.model.getTransition(transitionsName[i].toString()).addInputArc(a3);


				//E um arco de sa�da para o place pFinal_ e para o Resource Pool...
				SPNModel.Place pFinal = this.model.getPlace("pFinal_" + m.getName());
				SPNModel.Arc a5 = new SPNModel.Arc(pFinal.getName());
				this.model.getTransition(transitionsName[i].toString()).addOutputArc(a5);

				SPNModel.Arc a6 = new SPNModel.Arc(resourcePlace.getName());
				this.model.getTransition(transitionsName[i].toString()).addOutputArc(a6);
			}
		}

		//Para a transi��o INACTIVE_SYSTEM_T
		SPNModel.TimedTransition initialTransition = this.INACTIVE_SYSTEM_T;
		
		//Criamos arcos de sa�da para todos os m�todos que n�o dependem de nenhum outro (s�o paralelos inicialmente).
		for(int k = 0; k<this.methods.size(); k++){
			if(this.methods.get(k).getDependsOn().size() == 0){

				//Output Arcs
				SPNModel.Place p1 = this.model.getPlace("p1_" + this.methods.get(k).getName());
				SPNModel.Arc a5 = new SPNModel.Arc(p1.getName());
				initialTransition.addOutputArc(a5);
			}
		}
		
		//E um arco de entrada partindo do place INACTIVE_SYSTEM_P
		SPNModel.Place pInitial = this.INACTIVE_SYSTEM_P;
		SPNModel.Arc a6 = new SPNModel.Arc(pInitial.getName());
		initialTransition.addInputArc(a6);
		
		//Se o quantidade de m�todos no final for maior que 1 precisamos de uma estrutura (transition e place) extra no final.
		if(this.finalParalelism > 1){
			//Final Transition
			SPNModel.ImmediateTransition finalTransition = new SPNModel.ImmediateTransition("FINAL", this.model);
			this.model.addTransition(finalTransition);
			
			//E ligamos os pFinal_ desses m�todos na transi��o FINAL
			for(int k = 0; k<this.methods.size(); k++){
				if(this.methods.get(k).getDependents().size() == 0){
					//Input Arcs

					SPNModel.Place pFinal = this.model.getPlace("pFinal_" + this.methods.get(k).getName());
					SPNModel.Arc a7 = new SPNModel.Arc(pFinal.getName());
					finalTransition.addInputArc(a7);

				}
			}
			
			SPNModel.Place placeFinal = new SPNModel.Place("FINAL_PLACE");
			this.model.addPlace(placeFinal);
			
			//E ligamos com a transi��o FINAL ao FINAL_PLACE
			SPNModel.Arc a7 = new SPNModel.Arc("FINAL_PLACE");
			finalTransition.addOutputArc(a7);

		}

	}
	
	//Liga os arcos para formar a estrutura para um calculo de an�lise estacion�ria.
	public void createArcsStationayAnalisys (){

		SPNModel.Place resourcePlace = this.model.getPlace("ResourcePool");

		Object transitionsName [] = this.model.getTransitions().keySet().toArray();

		for (int i = 0; i < transitionsName.length; i++) {//For each Transitions...

			if(this.model.getTransition(transitionsName[i].toString()).getName().substring(0,2).equals("RI")){ //If is a Immediate Transition


				String transitionCompleteName = transitionsName[i].toString();
				String methodName = transitionCompleteName.substring(3,transitionCompleteName.length());


				//Se a transi��o for imediata criamos arcos de entrada partindo do place p1_... e do Resource Pool
				SPNModel.Place p1 = this.model.getPlace("p1_" + methodName);

				if(p1 == null){ //Se p1 for nulo quer dizer que o m�todo depende de outro, ent�o sua estrutura no modelo s� ter� p2_ e pFinal_.

					Method mAtual = getMethod(methodName); //M�todo atual
					
					//Ent�o, criamos com arcos de entrada os places pFinal_ dos m�todos que dependem dele a transi��o imediata dele, e depois o Resource Pool.
					for(int j = 0; j<mAtual.getDependsOn().size(); j++){
						SPNModel.Place pFinalA = this.model.getPlace("pFinal_" + mAtual.getDependsOn().get(j).getName());

						SPNModel.Arc a1 = new SPNModel.Arc(pFinalA.getName());
						this.model.getTransition(transitionsName[i].toString()).addInputArc(a1);
					}
					
					SPNModel.Arc a2 = new SPNModel.Arc(resourcePlace.getName());
					this.model.getTransition(transitionsName[i].toString()).addInputArc(a2);

				} else{
					
					//Mas se na representa��o do m�todo possuir p1, ent�o criamos arcos de entrada partindo do place p1_... e do Resource Pool
					SPNModel.Arc a1 = new SPNModel.Arc(p1.getName());
					this.model.getTransition(transitionsName[i].toString()).addInputArc(a1);

					SPNModel.Arc a2 = new SPNModel.Arc(resourcePlace.getName());
					this.model.getTransition(transitionsName[i].toString()).addInputArc(a2);

				}

				//E um arco de sa�da para o place p2_...
				SPNModel.Place p2 = this.model.getPlace("p2_" + methodName);

				SPNModel.Arc a3 = new SPNModel.Arc(p2.getName());
				this.model.getTransition(transitionsName[i].toString()).addOutputArc(a3);

			}else if(this.model.getTransition(transitionsName[i].toString()).getName().substring(0,2).equals("RE")){ //If is a Exponential Transition

				String transitionCompleteName = transitionsName[i].toString();
				String methodName = transitionCompleteName.substring(3,transitionCompleteName.length());

				Method m = getMethod(methodName);

				//Se a transi��o for exponencial criamos arcos de entrada partindo do place p2_...
				SPNModel.Place p2 = this.model.getPlace("p2_" + methodName);
				SPNModel.Arc a3 = new SPNModel.Arc(p2.getName());
				this.model.getTransition(transitionsName[i].toString()).addInputArc(a3);


				//E um arco de sa�da para o place pFinal_ e para o Resource Pool...
				SPNModel.Place pFinal = this.model.getPlace("pFinal_" + m.getName());
				SPNModel.Arc a5 = new SPNModel.Arc(pFinal.getName());
				this.model.getTransition(transitionsName[i].toString()).addOutputArc(a5);


				SPNModel.Arc a6 = new SPNModel.Arc(resourcePlace.getName());
				this.model.getTransition(transitionsName[i].toString()).addOutputArc(a6);
			}
		}

		//Para a transi��o INACTIVE_SYSTEM_T
		SPNModel.TimedTransition initialTransition = this.INACTIVE_SYSTEM_T;
		
		//Criamos arcos de sa�da para todos os m�todos que n�o dependem de nenhum outro (s�o paralelos inicialmente).
		for(int k = 0; k<this.methods.size(); k++){
			if(this.methods.get(k).getDependsOn().size() == 0){

				//Output Arcs
				SPNModel.Place p1 = this.model.getPlace("p1_" + this.methods.get(k).getName());
				SPNModel.Arc a5 = new SPNModel.Arc(p1.getName());
				initialTransition.addOutputArc(a5);
			}
		}
		
		//E um arco de entrada partindo do place INACTIVE_SYSTEM_P
		SPNModel.Place pInitial = this.INACTIVE_SYSTEM_P;
		SPNModel.Arc a6 = new SPNModel.Arc(pInitial.getName());
		initialTransition.addInputArc(a6);

		//Transi��o final que retorna ao place inicial (INACTIVE_SYSTEM_P)
		SPNModel.ImmediateTransition finalTransition = new SPNModel.ImmediateTransition("FINAL", this.model);
		this.model.addTransition(finalTransition);
		
		//Criamos arcos de entrada dos pFinal_ dos m�todos que n�o possuem 'dependentes' na transi��o FINAL
		for(int k = 0; k<this.methods.size(); k++){
			if(this.methods.get(k).getDependents().size() == 0){
				//Input Arcs

				SPNModel.Place pFinal = this.model.getPlace("pFinal_" + this.methods.get(k).getName());
				SPNModel.Arc a7 = new SPNModel.Arc(pFinal.getName());
				finalTransition.addInputArc(a7);

			}
		}
		
		//E ligamos com a transi��o FINAL ao INACTIVE_SYSTEM_P
		SPNModel.Arc a7 = new SPNModel.Arc(this.INACTIVE_SYSTEM_P.getName());
		finalTransition.addOutputArc(a7);

	}
	
	//Realiza a An�lise Estacion�ria
	public double doStationayAnalisys(){
		
		//Define a m�trica, adiciona ao modelo e executa.
		String metric = "P{#INACTIVE_PLACE>0}*(1/0.0001)";
		Metric m = SPNStationaryAnalysisMetric.getMetric(this.model, metric);
		this.model.addMetric(m);
		double result = m.solve();

		return result;
	}
	
	//Realiza a An�lise Transiente
	public ResultTime[] doTransientAnalysis(double tempoTotal, ResultTime[] resultados){
		
		
		double LTS = tempoTotal + (tempoTotal * 0.1); //Define o tempo limite de execu��o
		double result = 0;

		String metric = "P{#FINAL_PLACE>0}"; //Define a m�trica
		double i = 0; //Representa o tempo para o qual est� ocorrendo a execu��o.
		int index = 0; //Index para armazenar o resultado no local certo do array de resultados.
		
		//Enquanto o tempo da execu��o n�o atinge o tempo limite
		while(i<LTS){

			Metric m =  SPNTransientAnalysisMetric.getMetric(this.model, metric, i);
			this.model.addMetric(m);
			result = m.solve();
			
			resultados[index] = new ResultTime(i, result);
			i += (LTS * 0.001);
			index++;
		}

		return resultados;
	}

}
