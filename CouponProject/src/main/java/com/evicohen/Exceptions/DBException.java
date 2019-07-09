package com.evicohen.Exceptions;



import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.Arrays;

import com.evicohen.Logs.*;





@SuppressWarnings("serial")
public class DBException extends Exception{

	public DBException(String message) throws IOException {
		super(message);
		loggerMassage();
		// TODO Auto-generated constructor stub
	}


	public void loggerMassage() throws IOException {
		
		Log log = new Log(); 
		Logger.log(Log.Error(getMessage(), getStackTrace()));

	}



	

}
