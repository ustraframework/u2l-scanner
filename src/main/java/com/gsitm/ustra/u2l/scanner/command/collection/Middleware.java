package com.gsitm.ustra.u2l.scanner.command.collection;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.gsitm.ustra.u2l.scanner.command.collection.web.Jeus6;
import com.gsitm.ustra.u2l.scanner.command.collection.web.Jeus7;
import com.gsitm.ustra.u2l.scanner.command.collection.web.MiddlewareScanner;
import com.gsitm.ustra.u2l.scanner.command.collection.web.Nginx;
import com.gsitm.ustra.u2l.scanner.command.collection.web.Ohs;
import com.gsitm.ustra.u2l.scanner.command.collection.web.Ohs10;
import com.gsitm.ustra.u2l.scanner.command.collection.web.Weblogic;
import com.gsitm.ustra.u2l.scanner.command.collection.web.Weblogic10;
import com.gsitm.ustra.u2l.scanner.command.collection.web.Webtob4;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;
import com.gsitm.ustra.u2l.scanner.utils.CommonFileUtils;

import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class Middleware {

	private Map<ServerType, List<ServerCollectionData>> serverCollectionData = new HashMap<ServerType, List<ServerCollectionData>>();


	private boolean isExcludeDirectory(File pathname, ContextData contextData) {

		// log.info("excludePath : {}/{}", contextData.getExcludePaths(), pathname.getPath());

		if (contextData.getExcludePaths() != null) {
			for(String excludePath : contextData.getExcludePaths()) {
				if (pathname.getPath().startsWith(excludePath)) {
					return true;
				}
			}

		}

		if (contextData.getExcludeSymlink()&& CommonFileUtils.isSymlink(pathname)) {
			return true;
		}


		String name = FilenameUtils.getName(pathname.toString());

		if ("node_modules".equals(name)) {
			return true;
		}

		if (name.startsWith(".")) {
			return true;
		}

		return false;
	}


	public void collect(final ContextData contextData) throws IOException {

		log.info("Start scanning web/was server installation path");

		final List<ServerCollectionData> serverCollectionDatas = new ArrayList<ServerCollectionData>();

		serverCollectionData.put(ServerType.NGINX, new ArrayList<ServerCollectionData>());
		serverCollectionData.put(ServerType.JEUS6, new ArrayList<ServerCollectionData>());

		for(int i = 0; i<contextData.getRootPaths().length; i++) {
			try {
				CommonFileUtils.traverseDirectories(contextData.getRootPaths()[i], new FileFilter() {
					@Override
					public boolean accept(File pathname) {

						if (isExcludeDirectory(pathname, contextData)) {
							return false;
						}

						log.info("Scan directory : {}", pathname);

						for(ServerType serverType : ServerType.values()) {
							try {
								ServerCollectionData collectionData = serverType.getScanner().collect(contextData, pathname);
								if (collectionData != null) {
									collectionData.setServerType(serverType);
									serverCollectionDatas.add(collectionData);
									return false;
								}
							} catch(Exception e) {
								log.warn("Cannot access directory : {}", pathname);
								return false;
							}
						}


						return true;
					}

				});
			} catch(Exception e) {
				log.warn("Cannot access directory : {}", contextData.getRootPaths()[i]);
			}
		}

		log.info("------------------------------------------------------------------------------------");
		for(ServerCollectionData c : serverCollectionDatas) {
			log.info("found server {} : {}", c.getServerType().name(), c.getPysicalPath());
		}

		for(ServerCollectionData c : serverCollectionDatas) {
			// log.info("found server {} : {}", c.getServerType().name(), c.getPysicalPath());

			log.info("collect server data : {}", c.getPysicalPath());

			File targetRootPath = new File(CommonFileUtils.join(contextData.getOutputDir(), FilenameUtils.getName(c.getPysicalPath())));

			for(Entry<String, String> e : c.getCollectionPaths().entrySet()) {


				File targetPath = new File(CommonFileUtils.join(contextData.getOutputDir(), FilenameUtils.getName(c.getPysicalPath()), e.getKey()));
				File reportFilePath = new File(CommonFileUtils.join(contextData.getOutputDir(), FilenameUtils.getName(c.getPysicalPath()), "README.md"));

				if (!targetPath.exists()) {
					targetPath.mkdirs();
				}

				String report = new StringBuilder()
					.append("- Type :")
					.append(c.getServerType().name())
					.append("\n")
					.append("- Path :")
					.append(c.getPysicalPath())
					.toString();

				FileUtils.write(reportFilePath, report, "UTF-8");


				try {
					FileUtils.copyDirectory(new File(e.getValue()), targetPath);
				} catch(Exception e2) {
					log.warn(e2.getMessage());
				}

				CommonFileUtils.removeHiddenDirectories(targetPath.getPath());
				CommonFileUtils.removeCompressFiles(targetPath.getPath());
				CommonFileUtils.removeLogFiles(targetPath.getPath());


			}

			c.getServerType().getScanner().onCopyConfiguration(targetRootPath, new File(c.getPysicalPath()));
			c.getServerType().getScanner().onAfterCopyConfiguration(targetRootPath);
		}
		log.info("------------------------------------------------------------------------------------");

	}


	@Data
	public static class ServerCollectionData {
		private ServerType serverType;
		private String pysicalPath;
		private String version;
		private String name;
		private Map<String, String> collectionPaths = new HashMap<String, String>();
	}


	public enum ServerType {
		NGINX(new Nginx()),
		JEUS6(new Jeus6()),
		Jeus7(new Jeus7()),
		WEBTOB4(new Webtob4()),
		OHS(new Ohs()),
		OHS10(new Ohs10()),
		WEBLOGIC(new Weblogic()),
		WEBLOGIC10(new Weblogic10());

		private MiddlewareScanner scanner;

		ServerType(MiddlewareScanner scanner) {
			this.scanner = scanner;
		}

		public MiddlewareScanner getScanner() {
			return this.scanner;
		}
	}
}
