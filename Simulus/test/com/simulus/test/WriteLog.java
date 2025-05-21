package com.simulus.test;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;


/**
 * {@code WriteLog} is a standard way to write to a file
 * 
 */
public class WriteLog {
	
	private String filePath;
	private boolean append_to_file = false;
	
	/**
	 * Specify file path
	 * @param filePath
	 */
	public WriteLog(String filePath){
		this.filePath = filePath;
	}
	
	/**
	 * 
	 * @param filePath File PATH
	 * @param append_to_file Boolean whether to write log to existing file
	 */
	public WriteLog(String filePath, boolean append_to_file){
		this.filePath = filePath;
		this.append_to_file = append_to_file;
		
	}
	
	/**
	 * Write log to a file 
	 * @param text String message to be logged
	 * @throws IOException
	 */
	public void WriteToLog(String text) throws IOException {
		FileWriter write = new FileWriter (filePath, append_to_file);
		
		PrintWriter printLine = new PrintWriter(write);
	
		printLine.printf("%s" + "%n", text);
		
		printLine.close();
		
	}
	
	/**
	 * Clear an existing file 
	 */
	public void flush(){
		PrintWriter writer;
		try {
			writer = new PrintWriter(filePath);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
}
