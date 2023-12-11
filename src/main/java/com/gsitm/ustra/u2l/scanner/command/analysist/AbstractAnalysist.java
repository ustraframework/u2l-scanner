package com.gsitm.ustra.u2l.scanner.command.analysist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;
import com.gsitm.ustra.u2l.scanner.utils.CommonFileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAnalysist {

	// protected Pattern IP_ADDRESS_PATTERN = Pattern.compile(".*(?!0)(?!.*\\.$)((1?\\d?\\d|25[0-5]|2[0-4]\\d)(\\.|$)){4}(?).*");
	// protected Pattern IP_ADDRESS_PATTERN = Pattern.compile("(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}");
	protected Pattern IP_ADDRESS_PATTERN = Pattern.compile(
			".*(?<!\\d|\\d\\.)" +
			"(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])" +
			"(?!\\d|\\.\\d).*"
	);


	/**
	 * 검색할 패턴
	 * @return
	 */
	abstract protected String[] filePatterns();

	/**
	 * 분석 커스톰 룰
	 * @param f
	 * @param lineNo
	 * @param line
	 * @return
	 */
	abstract protected List<AnalysistData> checkCustomRule(File f, Integer lineNo, String line);

	/**
	 * 파일 필터링
	 * @param files
	 * @return
	 */
	protected List<File> filterFiles(List<File> files) {
		return files;
	}

	/**
	 * 포함되어야 할 코드인지 확인
	 * @param code
	 * @return
	 */
	abstract protected boolean isIncludeCode(String code);


	public List<AnalysistData> analysis(ContextData contextData) throws IOException {

		List<AnalysistData> dataList = new ArrayList<AnalysistData>();

		log.info("Start find files");

		List<File> files = this.filterFiles(CommonFileUtils.scanFile(contextData.getRootPath(), this.filePatterns()));

		log.info("Found target files : {}", files.size());

		for(File f : files) {
			log.info("check file : {}", f.toString());

			List<String> lines = FileUtils.readLines(f);

			int index = 0;
			for(String line: lines) {
				index++;

				dataList.addAll(this.checkIpAddress(f, index, line));
				dataList.addAll(this.checkUrlPattern(f, index, line));
				dataList.addAll(this.checkCustomRule(f, index, line));
			}

		}

		return dataList;

	}

	protected List<AnalysistData> checkIpAddress(File f, Integer lineNo, String line) {

		List<AnalysistData> dataList = new ArrayList<AnalysistData>();

		if (IP_ADDRESS_PATTERN.matcher(line).find() && this.isIncludeCode(line)) {
			dataList.add(AnalysistData.builder()
					.code(line.trim())
					.lineNo(lineNo)
					.sourceCodePath(f.getPath())
					.comment("IP Address Pattern Detected.")
					.build()
			);
		}

		return dataList;
	}


	protected List<AnalysistData> checkUrlPattern(File f, Integer lineNo, String line) {

		List<AnalysistData> dataList = new ArrayList<AnalysistData>();

		if ("xml".equals(FilenameUtils.getExtension(f.getPath())) || "jsp".equals(FilenameUtils.getExtension(f.getPath()))) {
			return dataList;
		}

		if (line.contains("://") && this.isIncludeCode(line)) {
			dataList.add(AnalysistData.builder()
					.code(line.trim())
					.lineNo(lineNo)
					.sourceCodePath(f.getPath())
					.comment("Url Pattern Detected.")
					.build()
			);
		}

		return dataList;
	}

}
