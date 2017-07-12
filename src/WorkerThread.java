import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;

public class WorkerThread implements Callable<BigDecimal> {

	static final BigDecimal common= new BigDecimal((2 * Math.sqrt(2) )/ 9801);
	
	private int threadNumber;
	private int tasksNumber;
	private int threads;
	private int totalNumberOfTasks;
	private boolean quietMode;
	
	private long workTime;
	
	private BigDecimal myPi = BigDecimal.ONE;
	
	public WorkerThread(int threadNumber, int tasksNumber, int threads, int totalNumberOfTasks, boolean quietMode) {
		this.threadNumber = threadNumber;
		this.tasksNumber = tasksNumber;
		this.threads = threads;
		this.totalNumberOfTasks = totalNumberOfTasks;
		this.quietMode = quietMode;
		
	}
	@Override
	public BigDecimal call() {
		if (!quietMode) {
			System.out.println("Thread " + threadNumber + " started.");			
		}
		long start = System.currentTimeMillis();
    	BigDecimal sum = BigDecimal.ZERO;
		for (int i = 0; i < tasksNumber; i++) {
			int k = threadNumber + i * threads;
			if (k > totalNumberOfTasks) {
				break;
			}
//main logic - formula
	    		BigDecimal fact, fourFact;
	    		Integer I = new Integer(k);
	    		//look if it's already calculated (i)!
	    		if(Main.factorials.containsKey(I)) { //i found it
	    			fact = Main.factorials.get(I); //use it 
	    		}
	    		else { //i didnt find it
					NavigableSet<Integer> values = new TreeSet<>(Main.factorials.keySet());
					    Integer closest = values.floor(I); //find the closest smaller one
					if(closest != null) { //map is not empty
	 /*!!!! */			fact = myFact(closest, I);
						fact  = fact.multiply(Main.factorials.get(closest));
					}
					else { //map is still empty
						fact = factorial(I);
					}
					Main.factorials.put(I, fact);
	    		}
	    		
	    		//look if it's already calculated (4i)!
	    		I = new Integer(4*k);
	    		if(Main.factorials.containsKey(I)) {//i found it
	    			fourFact = Main.factorials.get(I); //use it
	    		}
	    		else {//i didnt find it
	    			NavigableSet<Integer> values =new TreeSet<>( Main.factorials.keySet());
	    			    Integer closest = values.floor(I); //find the closest smaller one
	    			   if(closest != null) {
	   	 /*!!!! */			 fourFact = myFact(closest, I); 
	    				 fourFact = fourFact.multiply(Main.factorials.get(closest));
	    			   }
	    			   else {
	    				 fourFact = factorial(I);
	    			   }
	    			   Main.factorials.put(I, fourFact);
	    		}
	    		BigDecimal numerator = new BigDecimal(1103 + 26390*k);
	    		numerator = numerator.multiply(fourFact);
	    		BigDecimal denumerator = pow(fact,new BigInteger(Integer.toString(k))).
	    				multiply(pow(new BigDecimal(396), new BigInteger(Integer.toString(4*k)))); //(i!)^4 * (396)^4i
	    		sum = sum.add(numerator.divide(denumerator, totalNumberOfTasks, RoundingMode.HALF_UP));
	    	}
	    	myPi = common.multiply(sum);
	    	long finish = System.currentTimeMillis();
	        workTime = finish - start;
		if (!quietMode) {
			System.out.println("Thread " + threadNumber + " stopped.");
			System.out.println("Thread " + threadNumber + " execution time was: " + workTime + " milliseconds");			
		}
		return myPi;
	}


private static BigDecimal pow(BigDecimal b, BigInteger e) {
    BigDecimal result = BigDecimal.ONE;
    BigDecimal p = b;
    int skipped = 0;
    while (e.compareTo(BigInteger.ZERO) > 0) {
        if (e.and(BigInteger.ONE).equals(BigInteger.ONE)) {
            if (skipped > 0) {

                if (skipped > 29) {
                    p = pow(p, BigInteger.ONE.shiftLeft(skipped));
                } else {
                    p = p.pow(1 << skipped);
                }
                skipped = 0;
            }
            result = result.multiply(p);
        }
        skipped++;
        e = e.shiftRight(1);
    }
    return result;
}

public static BigDecimal myFact(long start,long end) {
	BigDecimal result =BigDecimal.ONE;
		for(long i = start+1; i <= end; i++) {
			result = result.multiply(new BigDecimal(i));
		}
	return result;
}

private BigDecimal factorial(long n) {
	return recfact(1, n); 
}

private BigDecimal recfact(long start, long n) {
    long i;
    if (n <= 16) { 
        BigDecimal r = new BigDecimal(start);
        for (i = start + 1; i < start + n; i++) 
        	r = r.multiply(new BigDecimal(i));
        return r;
    }
    i = n / 2;
    return recfact(start, i).multiply(recfact(start + i, n - i));
}

}
