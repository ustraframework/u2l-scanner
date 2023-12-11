package com.gsitm.ustra.u2l.scanner.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommandContextHolder {

	private static ThreadLocal<ContextData> contextHolder = new ThreadLocal<ContextData>();

	/**
	 * 생성
	 */
	public static void create() {
		ContextData data = new ContextData();
		data.setNewLineStr(System.getProperty("line.separator"));
		data.setCwd(System.getProperty("user.dir"));
		// data.setOutputDir(new File(CommonFileUtils.join(data.getCwd(), ".output")));
		data.setOutputDir(new File(CommonFileUtils.join(data.getCwd(), SystemUtils.getHostName() + "-" + SystemUtils.getCurrentUser())));

		if (data.getOutputDir().exists()) {
			try {
				log.info("Please wait. remove directory : {}", data.getOutputDir().toString());
				FileUtils.deleteDirectory(data.getOutputDir());
			} catch (IOException e) {
				log.warn(e.getMessage());
			}
		}

		contextHolder.set(data);
	}

	/**
	 * 조회
	 * @return
	 */
	public static ContextData get() {
		return contextHolder.get();
	}

	/**
	 * 초기화
	 */
	public static void clear() {
		contextHolder.remove();
	}

	@Data
	public static class ContextData {
		private String newLineStr;
		private String cwd;
		private File outputDir;
		private String[] rootPaths;
		private String rootPath;
		private String[] excludePaths;
	}

}
