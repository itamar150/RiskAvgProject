package com.company.aggregation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ParallelMain {

	// need to change path by your directory
	// private static final String DIR_PATH = "C:\\Users\\migdal\\Desktop\\nice_files";
	private static final int NUMBER_OF_THREADS = 2;
	private static final String REQUEST_USER = "momo";
	private static final String REQUEST_DATE = "05-08-2022";
	
	AggregationService aggregationService = AggregationService.getInstance();

	public static void main(String[] args) {

        ParallelMain main = new ParallelMain();
		ExecutorService executorService = null;
		
		try {
	        System.out.println("Creating Executor Service with a thread pool of Size: " + NUMBER_OF_THREADS);
	        executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	        // get all files from resource folder 
            List<File> files = main.getAllFilesFromResource("files");

	        if(!files.isEmpty()) {
				Set<Callable<File>> callables = new HashSet<Callable<File>>();
				for (File f : files) {
					if(f.isFile()) {
						Callable<File> callable = main.createCallabelFile(f);
						callables.add(callable);
					}
				}
	
				// invoke all and after it done shutDoun the executeService
				executorService.invokeAll(callables);  
		        executorService.shutdown();
		        
			    if(executorService.isShutdown()) {
			    	main.getAverage();
		        }
			    
	        }else {
	        	System.err.println("Your Directory/File is not exist. Please check the property 'DIR_PATH'");
	        }
		     

		} catch (Exception e) {
			if(executorService != null) {
				executorService.shutdown();
			}
			System.err.println("ERROR:  failed execute processFile");
		}

    }

	
	/**
	 * create a callable object for parallel run
	 * @param file
	 * @return
	 */
	private Callable<File> createCallabelFile(File file) {
		Callable<File> callable = new Callable<File>() {
		    public File call() throws Exception {
		        System.out.println("Executing File : " + file.getName() + ", thread: "+ Thread.currentThread().getName());
		    	aggregationService.processFile(file);
		        return file;
		    }
		};
		return callable;
	}

	/**
	 * get Average by date and User
	 * @param aggregationService
	 */
	private void getAverage() {
		Date  date = DateUtil.convertStringToDate(REQUEST_DATE);
		double yearly = aggregationService.getAvg(REQUEST_USER, date, DatePart.Yearly);
		double monthlty = aggregationService.getAvg(REQUEST_USER, date, DatePart.Monthly);
		double daily = aggregationService.getAvg(REQUEST_USER, date, DatePart.Daily);
		
		System.out.println("yearly avg: " + yearly);
		System.out.println("monthlty  avg: " + monthlty);
		System.out.println("daily avg: " + daily);
	}
	
	/**
	 * get all files from the resources/{folder} 
	 * @param folderName
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException
	 */
    private List<File> getAllFilesFromResource(String folderName) throws URISyntaxException, IOException {

	    ClassLoader classLoader = this.getClass().getClassLoader();
	
	    URL resource = classLoader.getResource(folderName);
	
	    List<File> collect = Files.walk(Paths.get(resource.toURI()))
	            .filter(Files::isRegularFile)
	            .map(f -> f.toFile())
	            .collect(Collectors.toList());
	
	    return collect;
    }

}
