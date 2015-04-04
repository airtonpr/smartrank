package testandormi;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

public class TransmissionClient implements Runnable {

	private final CountDownLatch signalToServersNumber;
	public static Calculator c = null;

	private long result;

	private Operation type;
	private String nomeNQ;
	private int qnt;
	private long time;
	private long n1;
	private long n2;
	private static int N;

	public TransmissionClient(Operation type, long n1, long n2,
			CountDownLatch stsn, int qnt) {
		// signalToAllProcess = stap;
		signalToServersNumber = stsn;
		this.type = type; // add, mult, div or sub
		this.n1 = n1;
		this.n2 = n2;
		this.qnt = qnt;
		this.N = 8;

		try {
			c = new CalculatorImpl();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public TransmissionClient(Operation type,
			CountDownLatch stsn, String nomeNQ, int qnt) {
		// signalToAllProcess = stap;
		signalToServersNumber = stsn;
		this.type = type; // add, mult, div or sub
		this.nomeNQ = nomeNQ;
		this.qnt = qnt;
		this.N = 8;
		
		try {
			c = new CalculatorImpl();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public long getTime() {
		return time;
	}

	public long getResult() {
		return this.result;
	}

	public void executeMethods() throws RemoteException {
		switch (type) {
		case SUM:
			result = c.addition(this.n1, this.n2);
			break;

		case DIV:
			result = c.division(this.n1, this.n2);
			break;

		case MULT:
			result = c.multiplication(this.n1, this.n2);
			break;

		case SUB:
			result = c.subtraction(this.n1, this.n2);
			break;
			
		case FIB:
			result = c.fib(this.n1);
			break;
		
		case FIBSEQ:
			result = c.fibSeq(this.n1, this.qnt);
			break;
		
		case NQ:
			int[] b = new int[8];
			c.nqueens(this.nomeNQ, 0, b);
			result = 0;
			break;
		
		case NQSEQ:
			int[] bb = new int[8];
			for(int i = 0; i < this.qnt; i++){
				c.nqueens(this.nomeNQ, 0, bb);
			}
			result = 0;
			break;
		
		case NEWNQ:
			
			break;
		}
	}
	

	public void run() {
		long startTime = System.currentTimeMillis();
		try {
			executeMethods();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		signalToServersNumber.countDown();
		long endTime = System.currentTimeMillis();
		time = endTime - startTime;
	}

}
