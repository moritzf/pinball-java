package pinball.helper;

import java.util.Comparator;

public class HighscoreComparator implements Comparator<Object[]> {
    @Override
    public int compare(Object[] array1, Object[] array2) {
	// get the second element of each array, andtransform it into a Double
	Integer d1 = new Integer((int) array1[1]);
	Integer d2 = new Integer((int) array2[2]);
	// since you want a descending order, you need to negate the
	// comparison of the double
	return -d1.compareTo(d2);
	// or : return d2.compareTo(d1);
    }
}
