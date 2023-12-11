package com.gsitm.ustra.u2l.scanner.command.collection.web;
//package com.gsitm.ustra.java.autotool.command.collection.web;
//
//import java.io.File;
//import java.io.IOException;
//
//import com.gsitm.ustra.java.autotool.command.collection.WebServer.ServerCollectionData;
//import com.gsitm.ustra.java.autotool.utils.CommandContextHolder.ContextData;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class Tomcat {
//
//	private boolean isServerDirectory(File pathname) {
//		if (pathname.toPath().resolve("nginx.exe").toFile().exists()) {
//			return true;
//		}
//
//		if (pathname.toPath().resolve("nginx.conf").toFile().exists()) {
//			return true;
//		}
//
//		if (pathname.toPath().resolve("sbin/nginx").toFile().exists()) {
//			return true;
//		}
//
//		return false;
//	}
//
//	public ServerCollectionData collect(ContextData contextData, File targetPath) throws IOException {
//
//		if (isServerDirectory(targetPath)) {
//			ServerCollectionData collectionData = new ServerCollectionData();
//			collectionData.setPysicalPath(targetPath.toString());
//			return collectionData;
//		}
//
//
//		return null;
//
////		log.info("found nginx webserver : {}", this.serverDirectories.size());
////		log.info("{}", this.serverDirectories);
//
//	}
//
//}
