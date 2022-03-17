package com.company.aggregation;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


// 1) Implement Methods
// 2) The data from the file should be kept in memory after serialization (each row as Transaction)
// 3) Add main function that calls functions (use all DatePart types)
// 4) write main method that will send files with few lines


public class AggregationService {
	
	Map<String, List<TransactionNode>> map = new ConcurrentHashMap<>();
	
	/**
	 * Create aggregationService as Singelton
	 */
	private static AggregationService instance;
	
	private AggregationService() {}
	
	public static AggregationService getInstance(){
		if(instance == null) {
			synchronized (AggregationService.class) {
				if(instance == null) {
					instance = new AggregationService();
				}
			}
		}
		return instance;
	}
	

    /**
     * .
     * This method gets file holding transactions, executes aggregations and save it for next calls
     * you should expect to have multiple files processed during the run
     *
     * @param file csv file contains transactions comma separated.
     *    file ex: id   username    organization    eventTime   eventType   risk
     *             123  momo        BOFA            01-01-2022  payment     870
     *             252  coco        UBS             02-02-2022  transfer    200
     */
    public synchronized void processFile(File file) {
        // 1. save data in memory
        // 2. aggregate transactions by day, week, month calculate the avg of risk
        //    in a way that you could get right avg anytime.

    	
    	try {
        	if(file.canRead()) {
        		
    			List<String> allLines = Files.readAllLines(Paths.get(file.getPath()));
    			for (String line : allLines) {
					String[] commaSpliterLine = line.split(",");
					Transaction trans = createTransactionFromLine(commaSpliterLine);
					
					String key = trans.getUserName().toLowerCase();
					if(map.containsKey(key)) {
						handleExistTransNode(trans, key);
					}else {
						handleNewTransNode(trans, key);
						
					}
				}
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Handle exist YEAR Transaction Node and start process it.
     * In addition we calculate the new riskAvg for each related YEAR,MONTH,DAY Transaction Node 
     * @param trans
     * @param key
     */

	private void handleExistTransNode(Transaction trans, String key) {
		List<TransactionNode> yearsList = map.get(key);
		Integer curYear = DateUtil.getYear(trans.getEventTime());
		try {
		
			Optional<TransactionNode> yearNodeOp = findTransactionNode(curYear, yearsList);
			if(yearNodeOp.isPresent()) {
				TransactionNode yearNode = yearNodeOp.get();
				setRiskAverageDeatils(trans, yearNode);
				handleExistMonthTransNode(trans, yearsList, yearNode);
			}else {
				TransactionNode yearNodeNew = createNewYearTransNode(trans);
				handleExistMonthTransNode(trans, yearsList, yearNodeNew);
				yearsList.add(yearNodeNew);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Handle new Transaction Node and add related YEAR,MONTH, DAY Transaction Node 
	 *  
	 * @param trans
	 * @param key
	 * @throws Exception 
	 */
	private void handleNewTransNode(Transaction trans, String key) throws Exception {
		// day
		TransactionNode dayTransactionNode = createDayTransactionNode(trans);
		
		// month
		TransactionNode monthTransactionNode = createNewMonthTransNode(trans);
		monthTransactionNode.getNextList().add(dayTransactionNode);
		
		// year
		TransactionNode yearTransactionNode = createNewYearTransNode(trans);
		yearTransactionNode.getNextList().add(monthTransactionNode);

		// list of years add to user
		List<TransactionNode> listForYear = new ArrayList<>();
		listForYear.add(yearTransactionNode);
		
		map.put(key, listForYear);
	}

	/**
	 * Handle exist MONTH and re-calculate riskAverage and add is related DAYS by the date;
	 * @param trans
	 * @param yearsList
	 * @param yearNode
	 */
	private void handleExistMonthTransNode(Transaction trans, List<TransactionNode> yearsList, TransactionNode yearNode) {
		Integer curMonth = DateUtil.getMonth(trans.getEventTime());
		List<TransactionNode> monthList = yearNode.getNextList();
		Optional<TransactionNode> monthNodeOptional = findTransactionNode(curMonth, monthList);
		if(monthNodeOptional.isPresent()) {
			TransactionNode monthNode = monthNodeOptional.get();
			setRiskAverageDeatils(trans, monthNode);
			handleExistDayTransNode(trans, monthList, monthNode);
		}else {
			TransactionNode monthNewNode = createNewMonthTransNode(trans);
//			monthList.add(monthNewNode);
			yearNode.getNextList().add(monthNewNode);
			handleExistDayTransNode(trans, yearNode.getNextList(), monthNewNode);
			
			yearsList.add(monthNewNode);
		}

	}

	/**
	 * Handle exist DAY and re-calculate riskAverage. 
	 * For the TrnasactionNode from DAY type there is not nextList (= NULL).
	 * @param trans
	 * @param monthList
	 * @param monthNode
	 */
	
	private void handleExistDayTransNode(Transaction trans, List<TransactionNode> monthList, TransactionNode monthNode) {
		try {
			Integer curDay = DateUtil.getDay(trans.getEventTime());

			List<TransactionNode> dayList = monthNode.getNextList();
			Optional<TransactionNode> dayNodeOpional = findTransactionNode(curDay, dayList);
			if(dayNodeOpional.isPresent()) {
				TransactionNode dayNode = dayNodeOpional.get();
				setRiskAverageDeatils(trans, dayNode);
			}else {
				TransactionNode dayNewNode = createDayTransactionNode(trans);
				monthNode.getNextList().add(dayNewNode);
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Create new TransactionNode of YEAR type
	 * @param trans
	 * @return
	 */
	private TransactionNode createNewYearTransNode(Transaction trans) {
		Integer curYear = DateUtil.getYear(trans.getEventTime());
		TransactionNode yearTransactionNode = new TransactionNode();
		yearTransactionNode.setValue(curYear);
		yearTransactionNode.setDatePart(DatePart.Yearly);
		setRiskAverageDeatils(trans, yearTransactionNode);

		return yearTransactionNode;
	}
	
	/**
	 * Create new TransactionNode of MONTH type
	 * @param trans
	 * @return
	 */

	private TransactionNode createNewMonthTransNode(Transaction trans) {
		
		Integer curMonth = DateUtil.getMonth(trans.getEventTime());
		TransactionNode monthTransactionNode = new TransactionNode();
		monthTransactionNode.setValue(curMonth);
		monthTransactionNode.setDatePart(DatePart.Monthly);
		setRiskAverageDeatils(trans, monthTransactionNode);
		return monthTransactionNode;
	}

	
	/**
	 * Create new TransactionNode of DAY type
	 * @param trans
	 * @return
	 * @throws Exception 
	 */
	private TransactionNode createDayTransactionNode(Transaction trans) throws Exception {
		Integer curDay = DateUtil.getDay(trans.getEventTime());

		TransactionNode dayTransactionNode = new TransactionNode();
		dayTransactionNode.setValue(curDay);
		dayTransactionNode.setDatePart(DatePart.Daily);
		setRiskAverageDeatils(trans, dayTransactionNode);
		return dayTransactionNode;
	}
	
	/**
	 * set the new riskAvg for current Transaction Node
	 * @param trans
	 * @param transactionNode
	 */

	private void setRiskAverageDeatils(Transaction trans, TransactionNode transactionNode) {
		double newAverageDay = calculateNewRiskAverage(transactionNode, trans.getRisk());
		transactionNode.setDateCount(transactionNode.getDateCount() + 1);
		transactionNode.setRiskAverage(newAverageDay);
	}

	/**
	 * Re-calculate the new risk by: ((previous #avg * previous #entries) + current #risk ) \ (previous #entries + 1)
	 * @param transactionNode
	 * @param risk
	 * @return
	 */
	private double calculateNewRiskAverage(TransactionNode transactionNode, int risk) {		
		double newAverage = ((transactionNode.getRiskAverage() * transactionNode.getDateCount()) + risk) / (transactionNode.getDateCount() +1);
		return newAverage;
	}

	/**
	 * create array split by COMMA and serialize it to Transaction entity
	 * @param commaSpliterLine
	 * @return
	 */
	private Transaction createTransactionFromLine(String[] commaSpliterLine) {
		Transaction trans = new Transaction();
		try {
			trans.setId(commaSpliterLine[0].trim());
			trans.setUserName(commaSpliterLine[1].trim());
			trans.setOrganization(commaSpliterLine[2].trim());
			trans.setEventTime(DateUtil.convertStringToDate(commaSpliterLine[3].trim()));
			trans.setEventType(commaSpliterLine[4].trim());
			trans.setRisk(Integer.valueOf(commaSpliterLine[5].trim()));
		} catch (Exception e) {
			System.err.println("Error while trying to serialize lint to transaction entity");
		}

		return trans;
	}

    /**
     * This function returns the average of risk for the given user for the given date part in the date
     * e.g. if date is 15/3/2022 then for Daily you will return avg of 15/3/2022 for Monthly for 3/2022 and for yearly for all 2022.
     * The result need to reflect currently processed files and therefor might change depending on the point it was called.
     * the date can be current day or month or year
     *
     * @return
     */
	
    public synchronized double getAvg(String user, Date date, DatePart datePart) {
    	TransactionNode res = null;
    	try {
        	List<TransactionNode> yearsList = map.get(user.toLowerCase());
        	switch (datePart) {
			case Yearly:
				res = getTransactionNode(DateUtil.getYear(date), yearsList);
				break;
				
			case Monthly:
				res = getMonthExpectedTransNode(date, yearsList);
				break;
				
			case Daily:
				TransactionNode monthTransNode = getMonthExpectedTransNode(date, yearsList);
				List<TransactionNode> daysList = monthTransNode.getNextList();
				res = getTransactionNode(DateUtil.getDay(date), daysList);
				break;
			default:
				break;
			}
        	
		} catch (Exception e) {
			System.err.println("ERROR: input date/user NOT EXIT ! [user: " + user + ", datePart: " + datePart + ", date: " + date + "]");
		}
    	
    	return res != null ? res.getRiskAverage() : Double.NaN;
    }

    /**
     * Get month expected TransactionNode.
     * @param date
     * @param yearsList
     * @return
     */
	private TransactionNode getMonthExpectedTransNode(Date date, List<TransactionNode> yearsList) {
		TransactionNode monthTransNode = null;
		TransactionNode yearTranNode = getTransactionNode(DateUtil.getYear(date), yearsList);
		if(yearTranNode != null) {
			List<TransactionNode> monthList = yearTranNode.getNextList();
			 monthTransNode = getTransactionNode(DateUtil.getMonth(date), monthList);
		}
		return monthTransNode;
	}

	/**
	 * Get expected TransactionNode by DatePart
	 * @param datePartNumber
	 * @param list
	 * @return
	 */
	private TransactionNode getTransactionNode(int datePartNumber, List<TransactionNode> list) {
		TransactionNode res = null;
		Optional<TransactionNode> yearNodeOpional = findTransactionNode(datePartNumber, list);
		if(yearNodeOpional.isPresent()) {
			res = yearNodeOpional.get();
		}
		return res;
	}

	/**
	 * Find the Transaction Node by list and value
	 * @param value
	 * @param list
	 * @return Optional<TransactionNode>
	 */
	private Optional<TransactionNode> findTransactionNode(Integer value, List<TransactionNode> list) {
		return list.stream().filter(n -> n.getValue() == value).findFirst();
	}

    

}