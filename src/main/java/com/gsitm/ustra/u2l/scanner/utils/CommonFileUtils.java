package com.gsitm.ustra.u2l.scanner.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.tools.ant.DirectoryScanner;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class CommonFileUtils {

	/**
	 * 디렉토리 탐색
	 * @param path
	 * @param filter
	 */
	public void traverseDirectories(String path, final FileFilter filter) {

		File dir = new File(path);
		File[] listFiles = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && !pathname.getName().startsWith(".");
			}

		});

		if (listFiles == null) {
			return;
		}

		for(int i=0; i < listFiles.length; i++) {
			boolean result = filter.accept(listFiles[i]);

			if (result) {
				traverseDirectories(listFiles[i].getPath(), filter);
			}
		}

	}

	/**
	 * 디렉토리의 파일을 pattern으로 검색
	 * @param targetDirPath
	 * @param pattern
	 * @return
	 */
	public List<File> scanFile(String targetDirPath, String... patterns) {

		try {
			if (!new File(targetDirPath).exists() || !new File(targetDirPath).isDirectory()) {
				return new ArrayList<File>();
			}

			DirectoryScanner ds = new DirectoryScanner();
			ds.setBasedir(targetDirPath);
			ds.setIncludes(patterns);
			ds.scan();

			String[] matches = ds.getIncludedFiles();
			List<File> files = new ArrayList<File>(matches.length);

			for(int i=0; i < matches.length; i++) {
				files.add(new File(join(new File(targetDirPath), matches[i])));
			}

			return files;
		} catch(Exception e) {
			return new ArrayList<File>();
		}
	}


	public String join(File file, String...paths) {
		String path = file.getPath();

		for(String p : paths) {
			path += (SystemUtils.IS_OS_WINDOWS ? "\\" : "/") + p.replaceAll("/", SystemUtils.IS_OS_WINDOWS ? "\\" : "/");
		}

		return path;
	}

	public String join(String file, String...paths) {
		return join(new File(file), paths);
	}

	public void removeHiddenDirectories(String targetDirPath) {
		DirectoryScanner ds = new DirectoryScanner();
		ds.setBasedir(targetDirPath);
		ds.setIncludes(new String[] { "**/.*", "**/tmp/**" });
		ds.scan();

		String[] matches = ds.getIncludedDirectories();

		for(String path : matches) {
			if (new File(join(new File(targetDirPath), path)).exists()) {
				try {
					FileUtils.deleteDirectory(new File(join(new File(targetDirPath), path)));
				} catch (IOException e) {
				}
			}

		}
	}

	public void removeCompressFiles(String targetDirPath) {
		for(File file : scanFile(targetDirPath, "**/*.tar", "**/*.tar.gz", "**/*.zip")) {
			file.delete();
		}
	}

	public void removeLogFiles(String targetDirPath) {
		for(File file : scanFile(targetDirPath, "**/*.log", "**/*log/**", "**/*LOG/**")) {
			file.delete();
		}
	}


}
