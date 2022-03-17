package com.company.aggregation;

import java.util.ArrayList;
import java.util.List;

/**
 * value - the represent value of the date / e.g: '03-05-2022' - Transaction Node of Year TransactionNode will be 2022.
 * 																 Transaction Node of Month TransactionNode will be 5.
 *   															 Transaction Node of Day TransactionNode will be 3.				
 * datePart - the type to the TransactionNode
 * dateCount - count the entries of the current TransactionNode by Type datePart.
 * riskAverage -hold the update riskAvg for each TransactionNode by Type datePatr.
 * 	nextList - hold the nextList from type nextList /  e.g: years -> list of months.
 * 															month -> list of days.
 * 															days -> the list will be NULL.
 * 
 *
 */
public class TransactionNode {
	int value;
	DatePart datePart;
	int dateCount;
	double riskAverage = 0;
	List<TransactionNode> nextList = new ArrayList<>();
	
	
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public DatePart getDatePart() {
		return datePart;
	}
	public void setDatePart(DatePart datePart) {
		this.datePart = datePart;
	}
	public int getDateCount() {
		return dateCount;
	}
	public void setDateCount(int dateCount) {
		this.dateCount = dateCount;
	}
	public double getRiskAverage() {
		return riskAverage;
	}
	public void setRiskAverage(double riskAverage) {
		this.riskAverage = riskAverage;
	}
	public List<TransactionNode> getNextList() {
		return nextList;
	}
	public void setNextList(List<TransactionNode> nextList) {
		this.nextList = nextList;
	}
	
	

}
