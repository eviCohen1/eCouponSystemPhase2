package com.evicohen.Exceptions;



import java.io.IOException;

import com.evicohen.Logs.*;




public class RemoveException extends Exception {
	
	
	public RemoveException(String massage) throws IOException { 
		
		super(massage); 
		loggerMassage();
		
	}
	
	public void loggerMassage() throws IOException {

		Log log = new Log();
		Logger.log(Log.Error(getMessage(), getStackTrace()));

	}

}
