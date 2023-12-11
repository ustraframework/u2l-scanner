package com.gsitm.ustra.u2l.scanner.command.collection.web;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import com.gsitm.ustra.u2l.scanner.command.collection.Middleware.ServerCollectionData;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;
import com.gsitm.ustra.u2l.scanner.utils.CommonFileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Jeus7 implements MiddlewareScanner {

	private boolean isServerDirectory(File pathname) {

		if (FilenameUtils.getName(pathname.toString()).contains("jeus")) {
			if (CommonFileUtils.scanFile(pathname.toString(), "**/bin/jeusadmin").size() > 0) {
				return true;
			}

			if (CommonFileUtils.scanFile(pathname.toString(), "**/domains/nodes.xml").size() > 0) {
				return true;
			}
		}

		return false;
	}

	public ServerCollectionData collect(ContextData contextData, File targetPath) throws IOException {

		if (isServerDirectory(targetPath)) {
			ServerCollectionData collectionData = new ServerCollectionData();
			collectionData.setPysicalPath(targetPath.toString());
			collectionData.getCollectionPaths().put("config", CommonFileUtils.join(targetPath, "domains"));
			collectionData.getCollectionPaths().put("bin", CommonFileUtils.join(targetPath, "bin"));
			return collectionData;
		}

		return null;

	}

	@Override
	public void onCopyConfiguration(File targetDirPath, File originPath) {

	}

	@Override
	public void onAfterCopyConfiguration(File targetDirPath) {

	}

}
