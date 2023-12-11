package com.gsitm.ustra.u2l.scanner.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SystemUtils {

	public String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "";
		}
	}

	public String getCurrentUser() {
		return System.getProperty("user.name");
	}

}
