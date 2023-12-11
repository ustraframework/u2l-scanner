package com.gsitm.ustra.u2l.scanner.command.collection.web;

import java.io.File;
import java.io.IOException;

import com.gsitm.ustra.u2l.scanner.command.collection.Middleware.ServerCollectionData;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;
import com.gsitm.ustra.u2l.scanner.utils.CommonFileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Webtob {

	private boolean isServerDirectory(File pathname) {

		if (new File(CommonFileUtils.join(pathname, "nginx.exe")).exists()) {
			return true;
		}

		if (new File(CommonFileUtils.join(pathname, "nginx.conf")).exists()) {
			return true;
		}

		if (new File(CommonFileUtils.join(pathname, "sbin/nginx")).exists()) {
			return true;
		}

		if (new File(CommonFileUtils.join(pathname, "conf/nginx.conf")).exists()) {
			return true;
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

//		log.info("found nginx webserver : {}", this.serverDirectories.size());
//		log.info("{}", this.serverDirectories);

	}

}
