package testandormi;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

public class CalculatorClient{
	private static int N = 8;
	public static void main(String[] args){
		try	{
			new CalculatorClient();

			Naming.lookup("//127.0.0.1/CalculatorService");

			long startTime2 = System.currentTimeMillis();

			CountDownLatch signalProcess = new CountDownLatch(1);
			/*
			  Teste fib na sequencia
			  TransmissionClient fibseq = new TransmissionClient(Operation.FIBSEQ, 7, 2, signalProcess, 2);

			  Teste fib paralelo
			  TransmissionClient fib = new TransmissionClient(Operation.FIB, 7, 2, signalProcess, 1);
			  TransmissionClient fib2 = new TransmissionClient(Operation.FIB, 13, 2, signalProcess, 1);

			  Teste NQueens sequencial
			  TransmissionClient nq = new TransmissionClient(Operation.NQSEQ, 4, 2, signalProcess, 2);
			 */
			//TransmissionClient nq = new TransmissionClient(Operation.NQSEQ, 4, 2, signalProcess, 2);

			TransmissionClient fibseq = new TransmissionClient(Operation.FIBSEQ, 9, 2, signalProcess, 2);

			TransmissionClient fib = new TransmissionClient(Operation.FIB, 9, 2, signalProcess, 1);
			TransmissionClient fib2 = new TransmissionClient(Operation.FIB, 34, 2, signalProcess, 1);

			TransmissionClient nqSEQ = new TransmissionClient(Operation.NQSEQ, signalProcess, " Sequencial", 3);
			

			//new Thread(fibseq).start();
			//new Thread(fib).start();
			//new Thread(fib2).start();

			System.out.println("Waiting for threads to wait");

			signalProcess.await();

			long endTime2 = System.currentTimeMillis();
			long totalTime2 = endTime2 - startTime2;
			System.out.println("Thread Principal: "+totalTime2);
			//System.out.println("Tempo de operação fibseq: " + fibseq.getTime());
			//System.out.println("Tempo de operação fib: " + fib.getTime());
			//System.out.println("Tempo de operação fib: " + fib2.getTime());

		}
		catch (Exception e)
		{
			System.out.println("Exception is : "+e);
		}
	}



}
