package com.initiators.process;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.initiators.constants.Constants;
import com.initiators.constants.Messages;

public class LoadHeaderFile {
	public static void loadHeaderFile(String headerFile) throws Exception {

        Properties prop = new Properties();
        try (FileReader reader = new FileReader(headerFile)) {
            prop.load(reader);
            String headers = prop.getProperty(Constants.BASE_HEADER);
            if (headers != null) {
                Constants.BASE_HEADERS = headers.split(Constants.PIPE_STRING);
            } else {
                throw new Exception(Messages.INVALID_HEADER_FILE_FORMAT);
            }
            
            String childListString = prop.getProperty(Constants.CHILD_HEADER);
            Map<String, String[]> childHeaderMapValue = new HashMap<String, String[]>();
            if (childListString != null) {
                Constants.childList = Arrays.asList(childListString.split(Constants.PIPE_STRING));
                for (String s: Constants.childList) {
                	 String childHeaders = prop.getProperty(s);
                	 childHeaderMapValue.put(s,childHeaders.split(Constants.PIPE_STRING));
                }
                Constants.childHeaderMap = childHeaderMapValue;
            } else {
                throw new Exception(Messages.INVALID_HEADER_FILE_FORMAT);
            }
        } catch (IOException e) {
            throw new Exception(Messages.INVALID_FILE_LOCATION + headerFile, e);
        }

	}

}
