package com.gsitm.ustra.u2l.scanner.command.collection.web;

import java.io.File;
import java.io.IOException;

import com.gsitm.ustra.u2l.scanner.command.collection.Middleware.ServerCollectionData;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;
import com.gsitm.ustra.u2l.scanner.utils.CommonFileUtils;

public class Ohs10 extends Ohs {

	private boolean isServerDirectory(File pathname) {

		if (
				new File(CommonFileUtils.join(pathname, "ohs")).exists() &&
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


}
