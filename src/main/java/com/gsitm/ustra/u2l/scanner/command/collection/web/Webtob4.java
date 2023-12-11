package com.gsitm.ustra.u2l.scanner.command.collection.web;

import java.io.File;
import java.io.IOException;

import com.gsitm.ustra.u2l.scanner.command.collection.Middleware.ServerCollectionData;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;
import com.gsitm.ustra.u2l.scanner.utils.CommonFileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Webtob4 implements MiddlewareScanner {

	private boolean isServerDirectory(File pathname) {


		if (CommonFileUtils.scanFile(pathname.toString(), "**/config/http.m").size() > 0 &&
				CommonFileUtils.scanFile(pathname.toString(), "**/config/wsconfig").size() > 0) {
			return true;
		}


		return false;
	}

	public ServerCollectionData collect(ContextData contextData, File targetPath) throws IOException {

		if (isServerDirectory(targetPath)) {
			ServerCollectionData collectionData = new ServerCollectionData();
			collectionData.setPysicalPath(targetPath.toString());
			collectionData.getCollectionPaths().put("config", CommonFileUtils.join(targetPath, "config"));
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
