package com.operasoft.snowboard.queue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileFifo {
	  private File file;
	  private long position = 0;
	  private String cache = null;

	  public FileFifo(File file) {
	    this.file = file;
	  }

	  public long size() {
		  return this.file.length();
	  }
	  
	  public void add(String data){
		  try {
              BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
              bw.write(data);
              bw.newLine();
              bw.close();
	      } catch (Exception e) {
	      }		  
	  }
	  
	  public String peek() throws IOException {
	    if (cache != null) {
	      return cache;
	    }

	    BufferedReader r = new BufferedReader(new FileReader(file));
	    try {
	      r.skip(position);
	      cache = r.readLine();
	      return cache;
	    } finally {
	      r.close();
	    }
	  }

	  private void removeFirstLine() throws IOException {  
		    RandomAccessFile raf = new RandomAccessFile(file,"rw");         
		     //Initial write position                                             
		    long writePosition = raf.getFilePointer();                            
		    raf.readLine();                                                       
		    // Shift the next lines upwards.                                      
		    long readPosition = raf.getFilePointer();                             
		    byte[] buff = new byte[1024];                                         
		    int n;                                                                
		    while (-1 != (n = raf.read(buff))) {                                  
		        raf.seek(writePosition);                                          
		        raf.write(buff, 0, n);                                            
		        readPosition += n;                                                
		        writePosition += n;                                               
		        raf.seek(readPosition);                                           
		    }                                                                     
		    raf.setLength(writePosition);                                         
		    raf.close();                                                          
		}         	  
	  
	  public void remove() throws IOException {
		  removeFirstLine();
	  }
	  
	  public void close(){
	  }
}
