package testandormi;
import java.rmi.Remote;
import java.rmi.RemoteException;
 
public interface Calculator extends Remote
{
    public long addition(long a,long b) throws RemoteException;
    public long subtraction(long a,long b) throws RemoteException;
    public long multiplication(long a,long b) throws RemoteException;
    public long division(long a,long b) throws RemoteException;
    public long fib(long a) throws RemoteException;
    public long fibSeq(long a, int qnt) throws RemoteException;
    public void nqueens(String n, int s, int[] b) throws RemoteException;
	public boolean checkDiagonal1(byte[][] board, int row, int col) throws RemoteException;
	public boolean checkDiagonal2(byte[][] board, int row, int col) throws RemoteException;
}

