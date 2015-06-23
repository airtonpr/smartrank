package Chart;
import java.util.ArrayList;


public class QuickSort {
	
	private static long comparisons = 0;
	private static long exchanges   = 0;

	public static void quicksort(ArrayList<ResultTime> a) {
		shuffle(a);                        // to guard against worst-case
		quicksort(a, 0, a.size() - 1); //////////////////////////
	}

	// quicksort a[left] to a[right]
	public static void quicksort(ArrayList<ResultTime> a, int left, int right) {
		if (right <= left) return;
		int i = partition(a, left, right);
		quicksort(a, left, i-1);
		quicksort(a, i+1, right);
	}

	// partition a[left] to a[right], assumes left < right
	private static int partition(ArrayList<ResultTime> a, int left, int right) {
		int i = left - 1;
		int j = right;
		while (true) {
			while (less(a.get(++i).getProb(), a.get(right).getProb()))      // find item on left to swap
				;                               // a[right] acts as sentinel
			while (less(a.get(right).getProb(), a.get(--j).getProb()))      // find item on right to swap
				if (j == left) break;           // don't go out-of-bounds
			if (i >= j) break;                  // check if pointers cross
			exch(a, i, j);                      // swap two elements into place
		}
		exch(a, i, right);                      // swap with partition element
		return i;
	}

	// is x < y ?
	private static boolean less(double x, double y) {
		comparisons++;
		return (x < y);
	}

	// exchange a[i] and a[j]
	private static void exch(ArrayList<ResultTime> a, int i, int j) {
		exchanges++;
		double swap = a.get(i).getProb();
		a.get(i).setProb(a.get(j).getProb());
		a.get(j).setProb(swap);
	}

	// shuffle the array a[]
	private static void shuffle(ArrayList<ResultTime> a) {
		int N = a.size();
		for (int i = 0; i < N; i++) {
			int r = i + (int) (Math.random() * (N-i));   // between i and N-1
			exch(a, i, r);
		}
	}
}
