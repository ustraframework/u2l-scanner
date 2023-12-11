package com.gsitm.ustra.u2l.scanner.command.analysist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XPlatform extends AbstractAnalysist {

	@Override
	protected String[] filePatterns() {
		return new String[] {
			"**/*.xfdl",
			"**/*.xadl",
			"**/*.xprj",
			"**/*.xjs",
			"**/*.xml"
		};
	}

	@Override
	protected List<AnalysistData> checkCustomRule(File f, Integer lineNo, String line) {
		List<AnalysistData> dataList = new ArrayList<AnalysistData>();
		String ext = FilenameUtils.getExtension(f.getPath());


		if (!this.isIncludeCode(line)) {
			return dataList;
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

		return true;
	}

}
