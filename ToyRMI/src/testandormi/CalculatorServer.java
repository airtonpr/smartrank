package testandormi;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
 
public class CalculatorServer
{
	CalculatorServer()
	{
		try
		{
			LocateRegistry.createRegistry(1099);
			Calculator c = new CalculatorImpl();
			Naming.rebind("rmi://localhost:1099/CalculatorService", c);
		}
		catch (Exception e)
		{
			System.out.println("Exception is : "+e);
		}
	}
	
	public static void main(String[] args)
	{
		new CalculatorServer();
	}
}
