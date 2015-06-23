package Chart;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChartGenerator {

	
	private JFreeChart chart;
	private ResultTime[][] resultsTransient;
	private XYSeries [] series;
	private XYSeriesCollection dataset;

	public LineChartGenerator(ResultTime[][] resultsTransient){
		this.dataset = new XYSeriesCollection();
		this.resultsTransient = resultsTransient;
		this.series = new XYSeries [resultsTransient.length];
		
		//this.chart = ChartFactory.createBarChart("Test Chart", "Executions", "Time", chartData, PlotOrientation.VERTICAL, false, false, false);
		this.chart = ChartFactory.createXYLineChart("Performance Model Evaluation", "Execution Time", "Probability", dataset, PlotOrientation.VERTICAL, true, true, false);
		addDataToChart();
		exportChart();
	}
	
	//Adiciona os dados no dataset
	public void addDataToChart (){

		for(int i = 0; i<this.series.length; i++){
			
			if(i == 1){
				this.series[i] = new XYSeries(1 + "VM");
			}else{
				this.series[i] = new XYSeries(i+1 + "VMs");
			}
			
			for(int j = 0; j<1000; j++){
				this.series[i].add(this.resultsTransient[i][j].getTime(), this.resultsTransient[i][j].getProb());
			}

			dataset.addSeries(series[i]);
		}	

	}
	//Gera o arquivo com o gráfico
	public void exportChart(){

		XYPlot xyPlot = (XYPlot) this.chart.getPlot();
		xyPlot.setDomainCrosshairVisible(true);
		xyPlot.setRangeCrosshairVisible(true);

		NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
		range.setTickUnit(new NumberTickUnit(0.2));

		try {
			OutputStream image = new FileOutputStream("grafico.png");
			ChartUtilities.writeChartAsPNG(image, this.chart, 500, 500);
			image.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
