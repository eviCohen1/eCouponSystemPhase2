package com.evicohen.Logs;

import java.sql.Date;
import java.time.LocalDateTime;

import com.evicohen.Main.Utils;




public class Log {
	
	private static LocalDateTime timestamp; 
	public enum logType { Error,Info } ;
	
	/** This method get a description and return Log.info
	 * @param description
	 * @return
	 */
	public static String info(String description) { 
		
		String log;
		
		timestamp = Utils.timeStamp();
		log =  "["+ logType.Info +"]  Timestamp: " + timestamp +  ", Description: " + description   ; 
		
		return log ; 	
		
	}
	
	/**This method get description and ExceptionMassage and return Log.Error
	 * @param description
	 * @param throwable
	 * @return
	 */
	public static String Error(String description, StackTraceElement[] stackTraceElements) { 
		
		String log;
		
		timestamp = Utils.timeStamp();
		log =  "["  + logType.Error + "]  Timestamp: " + timestamp +  ", Description: " + description + ", Stack Trace: " + stackTraceElements ; 
		
		return log ; 	
		
	}

	

}
