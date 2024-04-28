package com.initiators.main;

import com.initiators.constants.Messages;
import com.initiators.process.LoadHeaderFile;
import com.initiators.process.ProcessInputFile;

public class Json2Csv {

	public static void main(String[] args) {
		System.out.println("----------------------");
		System.out.println("Processing... Started");
		System.out.println("----------------------");
		try {
			if (args.length < 4) {
				throw new Exception(Messages.INVALID_PARAMTERS);
			}
			if (!("-i").equals(args[0]) || !("-o").equals(args[2])) {
				throw new Exception(Messages.INVALID_PARAMTERS);
			}
			String inputFileLocation = args[1];
			String outputFileLocation = args[3];
			String headerFileLocation = ".\\header.properties";
			// process header file first
			System.out.println("Header File Loading... Started");
			LoadHeaderFile.loadHeaderFile(headerFileLocation);
			System.out.println("Header File Loaded... Successfully");
			System.out.println("Input File Processing... Started");
			// process input file
			new ProcessInputFile(inputFileLocation,outputFileLocation).processInputFile();
			System.out.println("Input File Processed... Successfully");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("------------------");
		System.out.println("System... Exiting");
		System.out.println("------------------");
	}

}
