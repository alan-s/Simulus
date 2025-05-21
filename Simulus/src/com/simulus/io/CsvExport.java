package com.simulus.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Used by {@code MainApp} for exporting simulation analytical data to a Comma Separated Value (CSV) file.
 *
 */
public class CsvExport{

	private static File outputFile;
	
	
	public CsvExport(File saveFile){
		outputFile = saveFile;
	}
	/**
	 * Generates an empty CSV file with up to five user-defined column headers
	 * @param header01 first column header
	 * @param header02 second column header
	 * @param header03 third column header
	 * @param header04 fourth column header
	 * @param header05 fifth column header
	 * @param header06 sixth column header
	 */
	public void generateCsvFile(String header01, String header02,
		   String header03, String header04, String header05, String header06){
	   
	try
	{
	    FileWriter writer = new FileWriter(outputFile);
 
	    writer.append(header01);
	    writer.append(',');
	    writer.append(header02);
	    writer.append(',');
	    writer.append(header03);
	    writer.append(',');
	    writer.append(header04);
	    writer.append(',');
	    writer.append(header05);
	    writer.append(',');
	    writer.append(header06);
	    writer.append('\n');
	    
	    writer.flush();
	    writer.close();
	}
	catch(IOException e)
	{
	     e.printStackTrace();
	} 
    
}
   /**
    * Appends a new row with parameter values to the generated CSV file
    * from {@link #generateCsvFile(String, String, String, String, String, String)}
 * @param value01 first value in the appended row
 * @param value02 second value in the appended row
 * @param value03 third value in the appended row
 * @param value04 fourth value in the appended row
 * @param value05 fifth value in the appended row
 * @param value06 sixth value in the appended row
 */
public void appendRow(String value01, String value02,
		   String value03, String value04, String value05, String value06){
	   
		try
		{
			if (outputFile.exists()){
			    FileWriter writer = new FileWriter(outputFile, true);
				 
			    writer.append(value01);
			    writer.append(',');
			    writer.append(value02);
			    writer.append(',');
			    writer.append(value03);
			    writer.append(',');
			    writer.append(value04);
			    writer.append(',');
			    writer.append(value05);
			    writer.append(',');
			    writer.append(value06);
			    writer.append('\n');
			    
			    writer.flush();
			    writer.close();
			}

		}
		catch(IOException e)
		{
		     e.printStackTrace();
		} 
	
   }

}