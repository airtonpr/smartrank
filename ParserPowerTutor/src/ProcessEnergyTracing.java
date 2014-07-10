import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcessEnergyTracing{  
   public static void main (String[]args){  
      try{  
         BufferedReader br = new BufferedReader(new FileReader("PowerTrace1403021152965.log"));  
  
         while(br.ready()){  
            String linha = br.readLine();  
            if (linha.contains("CPU-10085")) {
				double energyInWatts =  Long.parseLong(linha.substring(9).trim());
				if (energyInWatts != 0) {
					//Transforming in J/s
					System.out.println(energyInWatts/1000);
				}
			}
         }  
         br.close();  
      }catch(IOException ioe){  
         ioe.printStackTrace();  
      }  
   }  
}  