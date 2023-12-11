package com.gsitm.ustra.u2l.scanner.command.collection.web;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;

import com.gsitm.ustra.u2l.scanner.command.collection.Middleware.ServerCollectionData;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;
import com.gsitm.ustra.u2l.scanner.utils.CommonFileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Jeus {

	private boolean isServerDirectory(File pathname) {

		if (FilenameUtils.getName(pathname.toString()).contains("jeus")) {
			if (CommonFileUtils.scanFile(pathname.toString(), "**/config/**/JEUSMain.xml").size() > 0) {
				return true;
			}
		}

		return false;
	}

	public ServerCollectionData collect(ContextData contextData, File targetPath) throws IOException {

		if (isServerDirectory(targetPath)) {
			ServerCollectionData collectionData = new ServerCollectionData();
			collectionData.setPysicalPath(targetPath.toString());
			return collectionData;
		}

		return null;

	}

}
