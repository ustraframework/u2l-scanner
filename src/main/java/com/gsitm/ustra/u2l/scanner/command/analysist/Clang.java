package com.gsitm.ustra.u2l.scanner.command.analysist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Clang extends AbstractAnalysist {

	@Override
	protected String[] filePatterns() {
		return new String[] {
			"**/*.c",
			"**/*.h",
			"**/*.pc"
		};
	}

	@Override
	protected List<File> filterFiles(List<File> files) {
		List<File> rfiles = new ArrayList<File>();

		for(File f : files) {
			if (f.toString().contains(".metadata") || f.toString().contains(".gradle") || f.toString().contains(".idea")) {
				continue;
			}

			rfiles.add(f);
		}


		return rfiles;
	}

	@Override
	protected List<AnalysistData> checkCustomRule(File f, Integer lineNo, String line) {
		List<AnalysistData> dataList = new ArrayList<AnalysistData>();

		if (StringUtils.containsIgnoreCase(line, "hostname")) {
			dataList.add(AnalysistData.builder()
					.code(line.trim())
					.lineNo(lineNo)
					.sourceCodePath(f.getPath())
					.comment("Detected hostname dependency.")
					.build()
			);
		}

		return dataList;
	}

	@Override
	protected boolean isIncludeCode(String code) {
		if (code.trim().startsWith("//")) {
			return false;
		}

		if (code.trim().startsWith("*")) {
			return false;
		}

		if (code.trim().startsWith("/*")) {
			return false;
		}

		return true;
	}

}
