/**
 * 	@author Devon Tyson
 */

import java.io.*;
import java.util.Scanner;

public class Compression {
	
	public static int sum = 0;
	public static float numBytes = 0;
	public static byte sigBytes;
	public static OutputStream outFile;
	public static int fileNum = 1;
	public static int count = 0;
	public static byte indicator = 0;
	
	public static String fileIn = "Data_GPDF_0.01"; // file used for testing
	public static String dat = ".dat";
	public static String b = "_b";
	
	public static void main(String[] args) {
		
		try {
			
			File inFile = new File(fileIn + ".txt");
			Scanner scan = new Scanner(inFile);
			outFile = new FileOutputStream(fileIn + fileNum + b + dat);
			
			int num = 0;
			while(scan.hasNextInt()) { // !eof
				
				// write 4 numbers at a time
				for(int i=0; i < 4; i++) {
					
					if(scan.hasNext()) {
						num = scan.nextInt();
						writeNum(num); 
						
						// adjust the indicator for the 4 numbers depending on index
						if(i==0) {
							indicator = (byte) (indicator | (sigBytes));
						} else if(i==1) {
							indicator = (byte) (indicator | (sigBytes << 2));
						} else if(i==2) {
							indicator = (byte) (indicator | (sigBytes << 4));
						} else if(i==3) {
							indicator = (byte) (indicator | (sigBytes << 6));
						}
						
						//testing
						//System.out.println("ind: " + indicator);
					}
				}
				outFile.write(sigBytes); // write the indicator byte
			//	numBytes += 1;
				indicator = 0;
			}
				
			double compression_ratio = (count*32)/sum; // where count = number of 32 bit ints in a file
													   // and sum = total bits after compression
			System.out.println("Compression ratio: " + compression_ratio);
			outFile.close();
			scan.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void writeNum(int num) throws IOException {
		count++;
				
		if(num < 256) {
			sum += 10;
			sigBytes = 1;
			numBytes+=(10/8);
					
		} else if(num >= 256 && num <= (Math.pow(2, 16)-1)) {
			sigBytes = 2;
			sum += 18;
			numBytes+=(18/8);
					
		} else if(num >= (Math.pow(2, 16)) && num <= (Math.pow(2, 24)-1) ) {
			sigBytes = 3;
			sum += 26;
			numBytes+=(26/8);
					
		} else {
			sigBytes = 4;
			sum += 34;
			numBytes+=(34/8);
		}
				
		if(numBytes >= 65536) { // 64k bytes per file
			fileNum++;
			outFile = new FileOutputStream(fileIn + fileNum + b + dat);			
			numBytes = 0;
		}
				
		byte fourth = (byte) (num & 0x000000ff);
		byte third = (byte) ((num & 0x0000ff00) >> 8);
		byte second = (byte) ((num & 0x00ff0000) >> 16);
		byte first = (byte) ((num & 0xff000000) >> 24);
				
				
		if(first > 0)
			outFile.write(first);
				
		if(second > 0)
			outFile.write(second);
				
		if(third > 0)
			outFile.write(third);
				
		if(fourth > 0)
			outFile.write(fourth);
		
		//testing
		//System.out.println("sig: " + sigBytes);
	}
}