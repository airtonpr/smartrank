package testandormi;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorImpl extends UnicastRemoteObject implements Calculator
{
	public CalculatorImpl() throws RemoteException
	{

		super();
	}
	public long addition(long a, long b) throws RemoteException
	{
		return a+b;
	}
	public long subtraction(long a, long b) throws RemoteException
	{
		return a-b;
	}
	public long multiplication(long a, long b) throws RemoteException
	{
		return a*b;
	}
	public long division(long a, long b) throws RemoteException
	{
		return a/b;
	}


	public long fib(long a) throws RemoteException
	{
		long result = 0;

		//number of elements to generate in a series
		int limit = (int) a;

		long[] series = new long[limit + 1];


		if(limit > 1){
			//create first 2 series elements
			series[0] = 0;
			series[1] = 1;

			//create the Fibonacci series and store it in an array
			for(int i=2; i < (limit + 1); i++){
				
				series[i] = series[i-1] + series[i-2];
				
			}

			
			return series[limit];

		}else if(limit == 1){
			return 1;
		}else{
			return 0;
		}
	}
	
	private long tempoInicial = 0;
	private long tempoFinal = 0;
	
	public long fibSeq(long n, int qnt) throws RemoteException{
		tempoInicial = System.currentTimeMillis();
		
		long result = 0;
		long novon = n;

		for(int i = 0; i < qnt; i++){
			
			result = fib(novon);
			System.out.println("Resultado do fib" + i + ": " + result);
			novon = result;

		}
		
		tempoFinal = System.currentTimeMillis();
		
		return result;
	}

	
	public void nqueens(String nome, int s, int[] b) throws RemoteException{
		long tempoI = System.currentTimeMillis();
		String n = nome;
		int sol = s;
		int y = 0;
		b[0] = -1;
		while (y >= 0) {
			do {
				b[y]++;
			} while ((b[y] < 8) && unsafe(y, b));
			if (b[y] < 8) {
				if (y < 7) {
					b[++y] = -1;
				} else {
					sol = putboard(nome, sol, b);
				}
			} else {
				y--;
			}
		}

		System.out.println("Tempo total nqueens"+ n + ": " + (System.currentTimeMillis() - tempoI));
	}



	static boolean unsafe(int y, int[] b) {
		int x = b[y];
		for (int i = 1; i <= y; i++) {
			int t = b[y - i];
			if (t == x ||
					t == x - i ||
					t == x + i) {
				return true;
			}
		}

		return false;
	}

	public static int putboard(String n, int s, int[] b) {
		ManageFile m = new ManageFile("NQueens" + n);
		try {
			m.WriteFile("\n\nSolution " + (++s));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				try {
					m.WriteFile((b[y] == x) ? "|Q" : "|_");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				m.WriteFile("|");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return s;
	}
	
	private static int N = 8;
	
	public boolean checkDiagonal1(byte[][] board, int row, int col) {
		int sum = 0;
		int i = row;
		int j = col;
		while (i < N && j < N) {
			sum += board[i][j];
			i++;
			j++;
		}
		return sum <= 1;
	}

	public boolean checkDiagonal2(byte[][] board, int row, int col) {
		int sum = 0;
		int i = row;
		int j = col;
		while (i < N && j >=0) {
			sum += board[i][j];
			i++;
			j--;
		}
		return sum <= 1;
	}
	
	
}

