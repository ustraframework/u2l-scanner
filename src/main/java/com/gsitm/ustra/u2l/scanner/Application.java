package com.gsitm.ustra.u2l.scanner;

import java.util.Arrays;

import com.gsitm.ustra.u2l.scanner.command.Analysist;
import com.gsitm.ustra.u2l.scanner.command.Collection;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
public class Application {
	public static void main(String[] args) {

		if (args.length < 1) {
			exitWithErrorMessage();
		}

		try {
			CommandContextHolder.create();
			String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

			if ("collect".equals(args[0])) {
				new CommandLine(new Collection()).execute(commandArgs);
			}

			if ("analysis".equals(args[0])) {
				new CommandLine(new Analysist()).execute(commandArgs);
			}

		} finally {
			CommandContextHolder.clear();
		}
    }

	private static void exitWithErrorMessage() {
		log.warn("Wrong Command. Please use collection or analysis command");
		System.exit(-1);
	}
}
