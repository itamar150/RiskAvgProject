package com.company.aggregation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class NotParallelMain {

	
	private static final String REQUEST_USER = "momo";
	private static final String REQUEST_DATE = "05-08-2022";
	
	AggregationService aggregationService = AggregationService.getInstance();

	public static void main(String[] args) {
		
		NotParallelMain main = new NotParallelMain();

		try {

	        List<File> files = main.getAllFilesFromResource("files");
			
	        if(!files.isEmpty()) {
		        for (File curFile : files) {
		        	if(curFile.isFile()) {
		        		main.processFile(curFile);
		        	}
				}
		        
				main.getAverage();

	        }else {
	        	System.err.println("Your Directory/File is not exist. Please check the property 'DIR_PATH'");

	        }
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		

	}

	/**
	 * process file
	 * @param curFile
	 */
	private void processFile(File curFile) {
		aggregationService.processFile(curFile);
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
