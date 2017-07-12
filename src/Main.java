import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

	private static int n = 100; //number of digits
	private static int  t =8; //number of threads
	
	private static String filename = "result.txt";
	
	private static BigDecimal pi = BigDecimal.ZERO;
	private static boolean quiet = false;
	
	public static Map<Integer, BigDecimal> factorials = new ConcurrentHashMap<>();
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		checkInput(args);
		long start = System.currentTimeMillis();
		 ExecutorService executor = Executors.newFixedThreadPool(t);
		 List<Future<BigDecimal>> list = new ArrayList<>();
		 
	 	boolean remainder = (n % t != 0);
		int tasksNumber = n / t + (remainder ? 1 : 0);
		for (int i = 0; i < t; i++) {
			Callable<BigDecimal> callable = new WorkerThread(i, tasksNumber, t, n, quiet);
			Future<BigDecimal> future = executor.submit(callable);
			
			list.add(future);
		}
		 for (Future<BigDecimal> future : list) {
		       pi =  pi.add(future.get());
		 }
		 
	    pi  = new BigDecimal(1).divide(pi, n, RoundingMode.HALF_UP);
	    executor.shutdown();
	    long finish = System.currentTimeMillis();

	    while (!executor.isTerminated()) {
	    }
	    System.out.println("Total execution time for current run (millis): "+ (finish-start));
	    writeResult();
	}

	private static void checkInput(String[] strings) {
		for(int i = 0; i < strings.length; i++) {
			if(strings[i].equals("-p")) {
				n = Integer.parseInt(strings[++i]); continue;
			}
			 if(strings[i].equals("-t") || strings[i].equals("-tasks")) {
				t = Integer.parseInt(strings[++i]); continue;
			}
			if(strings[i].equals("-o")) {
				filename = strings[++i];
				continue;
			} 
			if(strings[i].equals("-q")) {
				quiet =true; continue;		
			} 
		}
	}

	private static void writeResult() {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream(filename), "utf-8"))) {
	   writer.write(pi+"\n");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
