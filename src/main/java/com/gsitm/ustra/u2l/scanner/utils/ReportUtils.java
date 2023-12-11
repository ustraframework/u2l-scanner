package com.gsitm.ustra.u2l.scanner.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ReportUtils {

	public void createCsvFile(File targetFile, Map<String, String> map) throws IOException {

		// crate directory
		targetFile.getParentFile().mkdirs();


		BufferedWriter fw = new BufferedWriter(new FileWriter(targetFile));

		try {
			for(Entry<String, String> e: map.entrySet()) {
				fw.write(e.getKey() + "," + e.getValue());
				fw.newLine();
			}

			fw.flush();

		} catch(Exception e) {
			log.warn(e.getMessage(), e);
		} finally {
			if (fw != null) {
				fw.close();
			}
		}

	}

	public void createCsvFile(File targetFile, List<List<String>> lists) throws IOException {

		// crate directory
		targetFile.getParentFile().mkdirs();


		BufferedWriter fw = new BufferedWriter(new FileWriter(targetFile));

		try {

			for(List<String> list: lists) {
				for(String v: list) {
					fw.write(v + ",");
				}
				fw.newLine();
			}

			fw.flush();

		} catch(Exception e) {
			log.warn(e.getMessage(), e);
		} finally {
			if (fw != null) {
				fw.close();
			}
		}

	}


}
