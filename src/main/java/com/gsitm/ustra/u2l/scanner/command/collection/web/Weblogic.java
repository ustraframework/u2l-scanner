package com.gsitm.ustra.u2l.scanner.command.collection.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.gsitm.ustra.u2l.scanner.command.collection.Middleware.ServerCollectionData;
import com.gsitm.ustra.u2l.scanner.domains.JNDIData;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;
import com.gsitm.ustra.u2l.scanner.utils.CommonFileUtils;
import com.gsitm.ustra.u2l.scanner.utils.CommonXmlUtils;
import com.gsitm.ustra.u2l.scanner.utils.ReportUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Weblogic implements MiddlewareScanner {

	private boolean isServerDirectory(File pathname) {

		if (new File(CommonFileUtils.join(pathname, "wlserver")).exists() &&
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
		// JNDI 추출
		log.info("Find JNDI Configuration : {}", targetDirPath);

		List<JNDIData> jndiDatas = new ArrayList<JNDIData>();
		for (File subDir : targetDirPath.listFiles()) {
			if (subDir.isDirectory()) {
				File jdbcConfigDir = new File(CommonFileUtils.join(subDir, "config", "jdbc"));

				// if jdbc config directory is exists
				if (jdbcConfigDir.exists() && jdbcConfigDir.isDirectory()) {
					for(File f : CommonFileUtils.scanFile(jdbcConfigDir.getPath(), "*.xml")) {
						try {
							// String content = FileUtils.readFileToString(f, "UTF-8");
							JNDIData jndiData = this.getJNDIInfo(CommonXmlUtils.readXmlDocument(f));

							if (jndiData != null) {
								log.info("Find JNDI Configuration : {}", jndiData);
								jndiDatas.add(jndiData);
							}

						} catch (Exception e) {
							log.warn(e.getMessage());
						}
					}
				}
			}
		}

		List<List<String>> jndiDataCsvInfo = new ArrayList<List<String>>();

		// make header
		List<String> header = new ArrayList<String>();
		header.add("name");
		header.add("url");
		header.add("user");
		header.add("driverName");

		jndiDataCsvInfo.add(header);

		for(JNDIData jndiData : jndiDatas) {
			List<String> body = new ArrayList<String>();
			body.add(jndiData.getName());
			body.add(jndiData.getUrl());
			body.add(jndiData.getUserName());
			body.add(jndiData.getDriverName());

			jndiDataCsvInfo.add(body);
		}
		File reportFile = new File(CommonFileUtils.join(targetDirPath.getPath(), "jndi.csv"));
		try {
			ReportUtils.createCsvFile(reportFile, jndiDataCsvInfo);

			log.info("Make JNDI Configuration File : {}", reportFile.toString());
		} catch (IOException e) {
			log.warn(e.getMessage());
		}

	}

	private JNDIData getJNDIInfo(Document doc) {

		JNDIData jndiData = new JNDIData();
		NodeList childNodes = doc.getDocumentElement().getChildNodes();

		for(int i=0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeName().equals("name")) {
				jndiData.setName(childNodes.item(i).getTextContent());
			}

			if (childNodes.item(i).getNodeName().equals("jdbc-driver-params")) {

				NodeList paramNodes = childNodes.item(i).getChildNodes();

				for(int k=0; k < paramNodes.getLength(); k++) {
					if (paramNodes.item(k).getNodeName().equals("url")) {
						jndiData.setUrl(paramNodes.item(k).getTextContent());
					}

					if (paramNodes.item(k).getNodeName().equals("driver-name")) {
						jndiData.setDriverName(paramNodes.item(k).getTextContent());
					}

					if (paramNodes.item(k).getNodeName().equals("properties")) {

						NodeList propertiesNodes = paramNodes.item(k).getChildNodes();

						for(int j=0; j < propertiesNodes.getLength(); j++) {
							if (propertiesNodes.item(j).hasChildNodes() && propertiesNodes.item(j).getFirstChild().getTextContent().equals("name")) {
								jndiData.setUserName(propertiesNodes.item(j).getLastChild().getTextContent());
							}
						}
					}
				}


			}

		}

		return StringUtils.isEmpty(jndiData.getName()) ? null : jndiData;

	}

}
