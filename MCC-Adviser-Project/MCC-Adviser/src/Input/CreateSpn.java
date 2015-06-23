package Input;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import advisor.main.MethodAdvisor;
import method.advisor.ClassNode;
import method.advisor.Dependence;
import method.advisor.MethodNode;
import SPN.Method;
import SPN.SpnStructure;
import Chart.BarChartGenerator;
import Chart.LineChartGenerator;
import Chart.QuickSort;
import Chart.ResultTime;

public class CreateSpn {

	public InputStreamReader isr;
	public BufferedReader br;
	public ArrayList<Method> methodList; //Lista de Method (estrutura que representam os métodos).
	private int countParalellism; //Grau de paralelismo da entrada.
	private ResultTime resultsTransient [][]; //Lista de resultados da análise transiente.
	private double results []; //Lista de resultados da análise estacionária.
	private double totalTime; //Soma dos tempos das chamadas.
	

	public CreateSpn(){

		this.methodList = new ArrayList<Method>();
		this.countParalellism = 0;
		this.totalTime =  0;
		
		paralellism();
	}
	
	//Lê e interpreta o arquivo input_methods.java criando uma lista de Method (estrutura que representa um métodos) e suas dependências.
	public void createMethods(ClassNode cn) throws IOException{
		
		for(MethodNode mn : cn.getMethodNodes()){
			for(Dependence d : mn.getDependences()){

				Method method1 = null;
				Method method2 = null;
				if(d.getAssign() != null){
					method1 = searchMethod(d.getAssign().toString());
					if(method1 == null){
						method1 = new Method();
						method1.setName(d.getAssign().toString());
						this.methodList.add(method1);
					}
				}

				if(d.getCall() != null){
					method2 = searchMethod(d.getCall().toString());
					if(method2 == null){
						method2 = new Method();
						method2.setName(d.getCall().toString());
						if(method1 != null){
							method2.setDependsOn(method1);
						}
						this.methodList.add(method2);
					}else{
						if(method1 != null){
							method2.setDependsOn(method1);
						}

					}
					if(method1 != null){
						method1.setDependents(method2);
					}
				}
			}
		}
		paralellism();
	}

	
	//Coloca o tempo das chamadas nos Methods, que posteriormente serão usados nas transições.
	public void readInputTimeFile() throws IOException, NumberFormatException, ArrayIndexOutOfBoundsException{
		//try{
			FileInputStream fis = new FileInputStream(new File("inputTimeFile.txt"));
			InputStreamReader isrTime = new InputStreamReader(fis);
			BufferedReader brTime = new BufferedReader(isrTime);

			String line = brTime.readLine();
			line = brTime.readLine();
			line = brTime.readLine();
			
			int i = 0;
			while(line != null){

				String[] tabSeparatedArray = line.split("=");

				this.methodList.get(i).setTime(Double.parseDouble(tabSeparatedArray[1]));
				
				this.totalTime += Double.parseDouble(tabSeparatedArray[1]);
				
				i++;
				line = brTime.readLine();
			}
			
			brTime.close();
		
	}
	
	//Verifica qual o nível de paralelismo dos métodos do arquivo de entrada.
	public void paralellism(){
		for(int i = 0; i<this.methodList.size(); i++){
			if(this.methodList.get(i).getDependsOn().size() == 0){
				this.countParalellism++;
			}
		}
	}
	
	//Busca um Method pelo nome na lista de método.
	public Method searchMethod(String methodName){
		for(int i = 0; i < this.methodList.size(); i++){
			if(this.methodList.get(i).getName().equals(methodName)){
				return this.methodList.get(i);
			}
		}
		return null;
	}
	
	
	//Cria uma SPN e executa uma Análise Estacionária.
	public void createSpnStationayAnalisys(CreateSpn ir) throws Exception{
		
		this.results = new double [this.countParalellism+2];
		
		for(int i = 1; i<=this.countParalellism+2; i++){
			SpnStructure ger = new SpnStructure(ir.methodList, i); 
			
			ger.methodStructure(); //Create the structure representating a Method on SPN 
			ger.createResourcePool(); //Create the Resource Pool Place
			ger.createArcsStationayAnalisys(); //Create and connect Arcs
			
			DecimalFormat df = new DecimalFormat("0.000000"); //Para formatar a saída da análise para a criação do gráfico.
			double result_r1 = ger.doStationayAnalisys();
			
			result_r1 = Double.valueOf((df.format(result_r1).replace(',', '.')));
			results[i-1] = result_r1;
			System.out.println(result_r1);

		}
		
		//Testando para 100 chamadas paralelas
/*		paralellism();
		this.results = new double [11];
		
		File arquivo = new File("stationary.txt");
		FileWriter fw = new FileWriter( arquivo, true );
		BufferedWriter bw = new BufferedWriter( fw );
		
		bw.write( "Nº VMs | Resultados" );
		bw.newLine();
		
		int a = 1;
		for(int i = 0; i <= 11; i++){
			
			SpnStructure ger = new SpnStructure(ir.methodList, a);
			
			ger.methodStructure(); //Create the structure representating a Method on SPN 
			ger.createResourcePool(); //Create the Resource Pool Place
			ger.createArcsStationayAnalisys(); //Create and connect Arcs
			
			DecimalFormat df = new DecimalFormat("0.000000");
			double result_r1 = ger.doStationayAnalisys();
			
			result_r1 = Double.valueOf((df.format(result_r1).replace(',', '.')));
			results[i] = result_r1;
			
			bw.write( a + " " + results[i]);
			bw.newLine();
			
			if(a == 1){
				a = a + 9;
			}else{
				a = a + 10;
			}
			System.out.println(a);
		}
		bw.close();
		fw.close();*/
		
	}
	
	//Cria uma SPN e executa uma Análise Transiente.
	public void createSpnTransientAnalysis(CreateSpn ir) throws Exception{
		
		
		this.resultsTransient = new ResultTime [this.countParalellism+2][];
		
		for(int i = 1; i<=this.countParalellism+2; i++){
			
			ResultTime resultsTransient_2 [] = new ResultTime [1000];
			
			//Para a criação da SPN, passamos a lista de Method e a quantidade de recursos no Resource Pool
			SpnStructure ger = new SpnStructure(ir.methodList, i); 
			
			ger.methodStructure(); //Create the structure representating a Method on SPN 
			ger.createResourcePool(); //Create the Resource Pool Place
			ger.createArcsTransientAnalysis(); //Create and connect Arcs
			
			//Executa a Análise Transiente.
			ResultTime result_r1[] = ger.doTransientAnalysis(this.totalTime, resultsTransient_2);
			
			resultsTransient[i-1] = result_r1;
		}
		
	}
	
	//Gera um gráfico de barras
	public void generateBarChart (){
		BarChartGenerator bar = new BarChartGenerator(this.results);
	}
	
	//Gera um gráfico de linhas
	
	//Gera um gráfico de linha
	public void generateLineChart (){

		LineChartGenerator line = new LineChartGenerator(this.resultsTransient);
	}
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub


		
		/*for(MethodNode mn : cn.getMethodNodes()){
			for(Dependence d : mn.getDependences()){
				System.out.println("Método que chama: " + d.getAssign() + " Método chamado: " + d.getCall());
			}
		}*/
		ClassNode cn = MethodAdvisor.runMethodAdvisor("input_methods.java");
		
		CreateSpn ir = new CreateSpn();
		ir.createMethods(cn);
		ir.readInputTimeFile();
		ir.createSpnTransientAnalysis(ir);
		ir.generateLineChart();
		
		/*
		ir.createSpnStationayAnalisys(ir);
		ir.generateBarChart();
		*/
		
	}


}
