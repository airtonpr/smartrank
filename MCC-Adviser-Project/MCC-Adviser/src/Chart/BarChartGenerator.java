package Chart;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jfree.chart.ChartFactory;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PlotOrientation;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;


public class BarChartGenerator{

	private double results [];
	private String columns [];
	private XYSeriesCollection chartData;
	private JFreeChart chart;
	
	public BarChartGenerator(double results []) {
		// TODO Auto-generated constructor stub
		this.results = results;
		this.columns = new String [this.results.length];
		this.chartData = new XYSeriesCollection();
		this.chart = ChartFactory.createBarChart("Performance Model Evaluation", "Number of VMs", "Throughput", createDataset(), PlotOrientation.VERTICAL, false, false, false);
		
		createDataset();
		exportChart();
	}
	private CategoryDataset createDataset() {

		// row keys...
		final String series1 = "Throughput";

		// column keys...
		for(int i = 1; i<=this.results.length; i++){
			if(i>1){
				final String category = i + " VMs";
				this.columns[i-1] = category;
			}else{
				final String category = "1 VM";
				this.columns[i-1] = category;
			}
			
		}
		

		// create the dataset...
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for(int j = 0; j<this.columns.length; j ++){
			dataset.addValue(this.results[j], series1, this.columns[j]);
		}

		return dataset;

	}
	
	//Gera o arquivo com o gráfico.
	public void exportChart(){

		try {
			OutputStream image = new FileOutputStream("barchart.png");
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
