package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.sun.org.apache.regexp.internal.recompile;

public class Configuration {

	private Properties properties;
	private String filename = "config-cloudlet.properties";
	private static Configuration configuration = null;
	
	private Configuration() throws FileNotFoundException, IOException{
		this.properties = new Properties();
		InputStream in = new FileInputStream("./"+this.filename);
		this.properties.load(in);
	}
	
	public static Configuration getConfiguration() throws FileNotFoundException, IOException{
		if(configuration == null) {
			configuration = new Configuration();
		}
		return configuration;
	}
	
	public String getProperty(String key){
		return this.properties.getProperty(key);
	}
	
	public String getProperty(String key, String defaultValue){
		return this.properties.getProperty(key, defaultValue);
	}
	
	public List<String> getFirstServersGroupNames() {
		return Arrays.asList(this.getProperty("first-group-server-names").split(","));
	}
	
	public List<String> getSecondServersGroupNames() {
		return Arrays.asList(this.getProperty("second-group-server-names").split(","));
	}
	

	public boolean isSmartStrategy() {
		if(this.getProperty("smart-strategy").equals("yes")){
			return true;
		}else {
			return false;
		}
	}
	

	public boolean isReplicatedStrategy() {
		if(this.getProperty("replicated-strategy").equals("yes")){
			return true;
		}else {
			return false;
		}
	}
	
	public String getRabbitMQServer() {
		return this.getProperty("rabbitmq-server");
	}
	
	public double getWeightLoad() {
		return Double.parseDouble(this.getProperty("weight-load"));
	}
	
	public double getWeightRTT() {
		return Double.parseDouble(this.getProperty("weight-rtt"));
	}
	
	public static void main(String[] args) {
		try {
			Configuration configuration = Configuration.getConfiguration();
			System.out.println(configuration.getProperty("testKey"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
