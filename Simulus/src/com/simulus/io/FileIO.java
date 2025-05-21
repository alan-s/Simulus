package com.simulus.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.simulus.MainApp;


/**
 * Used by {@link MainApp} to copy/delete the temporary CSV file created in {@link CsvExport} to a new location.
 *
 */
public class FileIO {
	
	/**
	 * Copies a file to a destination location from the source.
	 * @param source The full path of the source file
	 * @param dest The full destination path including filename
	 * @throws IOException If an error occurs while copying the CSV file.
	 */
	public void copyFile(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	
	
	/**
	 * delete a file given it's full path
	 * @param toDelete the file to be deleted
	 */
	public void deleteFile(File toDelete){
			
		try{ 
			toDelete.delete();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	
	}

}
