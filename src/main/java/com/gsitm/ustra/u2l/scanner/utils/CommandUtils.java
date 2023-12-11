package com.gsitm.ustra.u2l.scanner.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SystemUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class CommandUtils {

	public String executeAndGetOutput(String[] command) {

		StringBuilder outputBuilder = new StringBuilder();
		try {
	    	Runtime rt = Runtime.getRuntime();
	    	String[] commands = (String[]) ArrayUtils.addAll(getOsCommands(), command);
	    	Process proc = rt.exec(commands);

	    	BufferedReader stdInput = new BufferedReader(new
	    	     InputStreamReader(proc.getInputStream()));

	    	BufferedReader stdError = new BufferedReader(new
	    	     InputStreamReader(proc.getErrorStream()));

	    	// Read the output from the command
	    	String s = null;
	    	while ((s = stdInput.readLine()) != null) {
	    		outputBuilder.append(s).append(CommandContextHolder.get().getNewLineStr());
	    	}

	    	// Read any errors from the attempted command
	    	// System.out.println("Here is the standard error of the command (if any):\n");
	    	while ((s = stdError.readLine()) != null) {
	    	    log.warn(s);
	    	}
    	} catch(Exception e) {
    		log.warn(e.getMessage(), e);
    	}

		return outputBuilder.toString();
	}


	public String[] getOsCommands() {

		if (SystemUtils.IS_OS_WINDOWS) {
			return new String[]{ "cmd.exe", "/c" };
		}

		if (SystemUtils.IS_OS_LINUX) {
			return new String[]{ "/bin/bash", "-c" };
		}

		return new String[]{ "/bin/sh", "-c" };

	}

}
