package com.gsitm.ustra.u2l.scanner.command.analysist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Java extends AbstractAnalysist {

	@Override
	protected String[] filePatterns() {
		return new String[] {
			"**/*.java",
			"**/*.properties",
			"**/*.xml",
			"**/*.yml",
			"**/*.yaml",
			"**/*.jsp",
			"**/*.ftl"
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
		String ext = FilenameUtils.getExtension(f.getPath());


		if (!this.isIncludeCode(line)) {
			return dataList;
		}

		if (line.contains(".renameTo")) {
			dataList.add(AnalysistData.builder()
					.code(line.trim())
					.lineNo(lineNo)
					.sourceCodePath(f.getPath())
					.comment("use FileUtils.moveFile method instead of File.renameTo")
					.build()
			);
		}


		if ("xml".equals(ext)) {
			if (line.contains("WM_CONCAT")) {
				dataList.add(AnalysistData.builder()
						.code(line.trim())
						.lineNo(lineNo)
						.sourceCodePath(f.getPath())
						.comment("use LIST_AGG instead of WM_CONCAT")
						.build()
				);
			}
		}

		if ("java".equals(ext)) {

//			if (line.contains("new Socket(")) {
//				dataList.add(AnalysistData.builder()
//						.code(line.trim())
//						.lineNo(lineNo)
//						.sourceCodePath(f.getPath())
//						.comment("Detected socket client code.")
//						.build()
//				);
//			}

			if (line.contains("new Socket(")) {
				dataList.add(AnalysistData.builder()
						.code(line.trim())
						.lineNo(lineNo)
						.sourceCodePath(f.getPath())
						.comment("Detected socket client code.")
						.build()
				);
			}

			if (line.contains("new HttpClient(")) {
				dataList.add(AnalysistData.builder()
						.code(line.trim())
						.lineNo(lineNo)
						.sourceCodePath(f.getPath())
						.comment("Detected http client code.")
						.build()
				);
			}


			if (StringUtils.containsIgnoreCase(line, "hostname")) {
				dataList.add(AnalysistData.builder()
						.code(line.trim())
						.lineNo(lineNo)
						.sourceCodePath(f.getPath())
						.comment("Detected hostname dependency.")
						.build()
				);
			}

			if (StringUtils.containsIgnoreCase(line, "https.protocols")) {
				dataList.add(AnalysistData.builder()
						.code(line.trim())
						.lineNo(lineNo)
						.sourceCodePath(f.getPath())
						.comment("Deteced https protocols setting.")
						.build()
				);
			}
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
