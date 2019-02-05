package com.itc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;

public class EmployeeTimeProcessor {
	private FileReader fr = null;
	private BufferedReader br = null;
	private Hashtable<Integer, String> empData = new Hashtable<>(); 
	private Hashtable<Integer, Integer> empGoodData = new Hashtable<>();
	private Hashtable<Integer, Integer> empBadData = new Hashtable<>();
	private Hashtable<Integer, String> consolidatedData = new Hashtable<>();
	
	public static void main(String[] args) {
		EmployeeTimeProcessor etp = new EmployeeTimeProcessor();
		
		try {
			etp.readTimeSheet("C:\\Users\\training\\eclipse-workspace\\employeeDetails\\resources\\EmpTimeSheet.txt");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void readTimeSheet(String fileName) throws Exception {
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);

			String line;
			while((line = br.readLine())!= null) {
				processTime(line);
			}
				
			Set<Integer> gKeys = empGoodData.keySet();
	        for(Integer key: gKeys){
	        		consolidatedData.put(key, ""+empData.get(key)+",GoodData-"+empGoodData.get(key));     		
	        }
	        
	        Set<Integer> bKeys = empBadData.keySet();
	        for(Integer key: bKeys){
	        	if(consolidatedData.get(key) != null)
	        		consolidatedData.put(key, consolidatedData.get(key)+",BadData-"+empBadData.get(key));
	        	else
	        		consolidatedData.put(key, empData.get(key)+",BadData-"+empBadData.get(key));
	        }
	        
	        Set<Integer> conKeys = consolidatedData.keySet();
	        for(Integer key: conKeys){
	        	System.out.println(""+key+","+consolidatedData.get(key));
	        }
	        
		} catch (FileNotFoundException fne) {
			System.out.println(fne.getMessage());
			throw fne;
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			throw ioe;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			fr.close();
			br.close();
		}
	}
	
	private void processTime(String line) throws ParseException {
		StringTokenizer token = new StringTokenizer(line,",");
		
		try {
			while(token.hasMoreElements()) {
				String empID = token.nextToken();
				String empName = token.nextToken();
				String empLoginTime = token.nextToken();
				String empLogoutTime = token.nextToken();
				
				DateFormat df = new SimpleDateFormat("hh:mm:ss");
				Date inTime = df.parse(empLoginTime);
				Date outTime = df.parse(empLogoutTime);
				
				long diff = outTime.getTime() - inTime.getTime();
				long diffHrs = diff/(60*60*1000)%24;
				long diffMin= diff/(60*1000)%60;
				long diffSec=diff/1000%60;
				
//				System.out.println("Time in office for "+empName+" is "+diffHrs+" Hours");
				
				int eId = Integer.parseInt(empID);
				
				empData.put(eId, empName);
				
				if(diffHrs >= 8) {
					Integer empGoodDataAvlbl = empGoodData.get(eId);
					if(empGoodDataAvlbl != null) {
						empGoodDataAvlbl++;
						empGoodData.put(eId, empGoodDataAvlbl);
					} else {
						empGoodData.put(eId, 1);
					}					
				} else {
					Integer empBadDataAvlbl = empBadData.get(eId);
					if(empBadDataAvlbl != null) {
						empBadDataAvlbl++;
						empBadData.put(eId, empBadDataAvlbl);
					} else {
						empBadData.put(eId, 1);
					}					
				}
			}    
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			throw e;
		}

	}
}
