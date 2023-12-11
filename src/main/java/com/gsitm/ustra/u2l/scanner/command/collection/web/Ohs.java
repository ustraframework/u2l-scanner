package com.gsitm.ustra.u2l.scanner.command.collection.web;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.gsitm.ustra.u2l.scanner.command.collection.Middleware.ServerCollectionData;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;
import com.gsitm.ustra.u2l.scanner.utils.CommonFileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ohs implements MiddlewareScanner {

	private boolean isServerDirectory(File pathname) {

		if (
				new File(CommonFileUtils.join(pathname, "ohs")).exists() &&
				new File(CommonFileUtils.join(pathname, "wlserver")).exists() &&
				new File(CommonFileUtils.join(pathname, "domains")).exists()) {
			return true;
		}

		return false;
	}

	public ServerCollectionData collect(ContextData contextData, File targetPath) throws IOException {

		if (isServerDirectory(targetPath)) {
			ServerCollectionData collectionData = new ServerCollectionData();
			collectionData.setPysicalPath(targetPath.toString());
			// collectionData.getCollectionPaths().put("domains", CommonFileUtils.join(targetPath, "domains"));
			return collectionData;
		}

		return null;

	}

	@Override
	public void onCopyConfiguration(File targetDirPath, File originPath) {

		File domainFile = new File(CommonFileUtils.join(originPath, "domain-registry.xml"));

		if (domainFile.exists()) {
			try {
				FileUtils.copyFileToDirectory(domainFile, targetDirPath);
			} catch (IOException e) {
				log.warn(e.getMessage());
			}
		}

		File domainDirs = new File(CommonFileUtils.join(originPath, "domains"));

		if (domainDirs.exists()) {
			for(File subDomainDir : domainDirs.listFiles()) {
				File configDir = new File(CommonFileUtils.join(subDomainDir, "config"));
				File targetConfigDir = new File(CommonFileUtils.join(targetDirPath, subDomainDir.getName(), "config"));
				if (configDir.exists()) {
					try {
						FileUtils.copyDirectory(configDir, targetConfigDir);
					} catch (IOException e) {
						log.warn(e.getMessage());
					}
				}

				for(File f : CommonFileUtils.scanFile(subDomainDir.getPath(), "*.sh")) {
					try {
						FileUtils.copyFileToDirectory(f, new File(CommonFileUtils.join(targetDirPath, subDomainDir.getName())));
					} catch (IOException e) {
						log.warn(e.getMessage());
					}
				}
			}
		}

	}

	@Override
	public void onAfterCopyConfiguration(File targetDirPath) {

	}

}
