/**
 * 	@author Devon Tyson
 */

import java.io.*;
import java.util.Scanner;

public class Compression {
	
	public static void main(String[] args) {

		int fileNum = 1;
		int numBytes = 0;
		int sum = 0;
		int count = 0;
		byte sigBytes = 0;
		String fileIn = "Data_GPDF_0.01"; // file used for testing
		String dat = ".dat";
		String b = "_b";
		
		try {
			
			File inFile = new File(fileIn + ".txt");
			Scanner scan = new Scanner(inFile);
			OutputStream outFile = new FileOutputStream(fileIn + fileNum + b + dat);
			
			int num = 0;
			while(scan.hasNextInt()) { // !eof
				
				num = scan.nextInt();
				numBytes+=4;
				count++;
				
				if(num < 256) {
					sum += 10;
					sigBytes = 1;
					
				} else if(num >= 256 && num <= (Math.pow(2, 16)-1)) {
					sigBytes = 2;
					sum += 18;
					
				} else if(num >= (Math.pow(2, 16)) && num <= (Math.pow(2, 24)-1) ) {
					sigBytes = 3;
					sum += 26;
					
				} else {
					sigBytes = 4;
					sum += 34;
				}
				
				if(numBytes >= 65536) { // 64k bytes per file
					fileNum++;
					outFile = new FileOutputStream(fileIn + fileNum + b);			
					numBytes = 0;
				}
				
				byte fourth = (byte) (num & 0x000000ff);
				byte third = (byte) ((num & 0x0000ff00) >> 8);
				byte second = (byte) ((num & 0x00ff0000) >> 16);
				byte first = (byte) ((num & 0xff000000) >> 24);
				
				outFile.write(sigBytes); // write the indicator byte
				
				if(first > 0)
					outFile.write(first);
				
				if(second > 0)
					outFile.write(second);
				
				if(third > 0)
					outFile.write(third);
				
				if(fourth > 0)
					outFile.write(fourth);
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
}
