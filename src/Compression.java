/**
 * 	@author Devon Tyson
 */

import java.io.*;
import java.util.Scanner;

public class Compression {
	
	public static int sum = 0;
	public static double numBytes = 0.0;
	public static byte sigBytes;
	public static OutputStream outFile;
	public static File gapsFile;
	public static int fileNum = 1;
	public static int count = 0;
	public static byte indicator = 0;
	
	public static String fileIn = "india"; // file used for testing
	public static String dat = ".dat";
	public static String txt = ".txt";
	public static String b = "_b";
	public static String gaps = "_gaps";
	
	public static void main(String[] args) {
		
		try {
			
			File inFile = new File(fileIn + txt);
			findGaps(inFile);
			File gapsFile = new File(fileIn + gaps + txt);

			Scanner scan = new Scanner(gapsFile);
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

			double compression_ratio = 0.0;
			if(sum > 0) {
				compression_ratio = ((double)count*32)/(double)sum; // where count = number of 32 bit ints in a file
													// and sum = total bits after compression
			}

			System.out.print("Compression ratio: ");
			System.out.printf("%.2f", compression_ratio);
			System.out.println();
			outFile.close();
			scan.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void writeNum(int num) throws IOException {
		count++;
		numBytes+=4; // 4 bytes per original integer
				
		if(num < 256) {
			sum += 10;
			sigBytes = 0; // 1 sigByte
			//numBytes+=(10.0/8.0);
					
		} else if(num >= 256 && num <= (Math.pow(2, 16)-1)) {
			sigBytes = 1; // 2 sigBytes
			sum += 18;
			//numBytes+=(18.0/8.0);
					
		} else if(num >= (Math.pow(2, 16)) && num <= (Math.pow(2, 24)-1) ) {
			sigBytes = 2; // 3 sigBytes
			sum += 26;
			//numBytes+=(26.0/8.0);
					
		} else {
			sigBytes = 3; // 4 sigBytes
			sum += 34;
			//numBytes+=(34.0/8.0);
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

	public static void findGaps(File inFile) throws IOException {

		PrintWriter writer = new PrintWriter(new FileWriter(fileIn + gaps + txt));

		Scanner scan = new Scanner(inFile);
		int firstNum = 0;
		int temp = 0;

		// write the first number for reference
		if(scan.hasNextInt()) {
			firstNum = scan.nextInt();
			writer.println(firstNum);
		}

		while(scan.hasNextInt()) {
			temp = scan.nextInt();
			int gapNum = temp - firstNum;
			writer.println(gapNum);
			firstNum = temp;
		}
	}
}