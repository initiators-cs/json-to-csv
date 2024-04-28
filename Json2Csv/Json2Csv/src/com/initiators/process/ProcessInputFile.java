package com.initiators.process;

import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.initiators.constants.Constants;

public class ProcessInputFile {
	private String inputFileLocation = "";
	private String outputFileLocation = "";

	public ProcessInputFile(String inputFileLocation, String outputFileLocation) {
		this.inputFileLocation = inputFileLocation;
		this.outputFileLocation = outputFileLocation;
	}

	public void processInputFile() throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		// create header row in output file
		List<String> csvLines = new ArrayList<>();
		String[] allHeaders = Constants.BASE_HEADERS;
		for (String s : Constants.childList) {
			allHeaders = ArrayUtils.addAll(allHeaders, Constants.childHeaderMap.get(s));
		}
		String headerLine = String.join(String.valueOf(Constants.PIPE_CHAR), allHeaders);
		csvLines.add(headerLine);
		Constants.CSV_HEADER_COUNT = allHeaders.length;

		try (FileReader jsonReader = new FileReader(inputFileLocation)) {
			JsonNode rootNode = mapper.readTree(jsonReader);
			if (rootNode.isArray()) {
				for (JsonNode element : rootNode) {
					processJsonElement(element, csvLines);
				}
			}

			try (PrintWriter out = new PrintWriter(outputFileLocation)) {
				csvLines.forEach(out::println);
			}
		}
	}

	private void processJsonElement(JsonNode element, List<String> csvLines) {
		List<String> baseValues = extractValuesFromJson(element, Constants.BASE_HEADERS); // Base fields
		String baseValuesString = String.join(String.valueOf(Constants.PIPE_CHAR), baseValues);
		
		// process all child records
		Map<String, List<String>> childKeyValueMap = new HashMap<String, List<String>>();
		for (String childField : Constants.childList) {
			JsonNode nodeDetails = element.get(childField);
			if (nodeDetails != null && nodeDetails.isArray()) {
				List<String> listOfLineValue = new ArrayList<String>();
				for (JsonNode nodeDetail : nodeDetails) {
					List<String> lineValues = new ArrayList<>();
					lineValues.addAll(extractValuesFromJson(nodeDetail, Constants.childHeaderMap.get(childField)));
					listOfLineValue.add(String.join(String.valueOf(Constants.PIPE_CHAR), lineValues));
				}
				childKeyValueMap.put(childField, listOfLineValue);
			}
		}
		
		// add blanks where header field is not in child record
		for (String childField : Constants.childList) {
			List<String> listOfLineValue = childKeyValueMap.get(childField);
			int before = 0;
			int after = 0;
			int count = 0;
			for (String childField1 : Constants.childList) {
				if (!childField.equals(childField1)) {
					count += Constants.childHeaderMap.get(childField1).length;
				}
				else {
					before = count;
					count = 0;
					continue;
				}
			}
			after = count;
			count = 0;
			String temp = baseValuesString;
			for (String s : listOfLineValue) {
				for (int i = 0; i<before ; i++) {
					temp += String.valueOf('|');
				}
				temp += s;
				for (int i = 0; i<after ; i++) {
					temp += String.valueOf('|');
				}
				csvLines.add(temp);
			}
		}
	}

	private List<String> extractValuesFromJson(JsonNode jsonNode, String[] headerArray) {
		List<String> values = new ArrayList<>();
		for (String header : headerArray) {
			JsonNode valueNode = jsonNode.get(header);
			String textValue = valueNode != null ? valueNode.asText() : "";
			values.add(textValue);
		}
		return values;
	}

}
