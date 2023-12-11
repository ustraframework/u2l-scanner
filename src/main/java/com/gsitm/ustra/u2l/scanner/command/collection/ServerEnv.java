package com.gsitm.ustra.u2l.scanner.command.collection;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;
import com.gsitm.ustra.u2l.scanner.utils.CommandUtils;
import com.gsitm.ustra.u2l.scanner.utils.CommonFileUtils;
import com.gsitm.ustra.u2l.scanner.utils.ReportUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerEnv {

	public static void collect(ContextData contextData) throws IOException {


		log.info("Start scanning server variables");

		String output = CommandUtils.executeAndGetOutput(new String[] { SystemUtils.IS_OS_WINDOWS ? "set" : "env" });
		Map<String, String> envMap = new LinkedHashMap<String, String>();
		envMap.put("Key", "Value");

		if (StringUtils.isNotEmpty(output)) {

			String[] strLines = output.split(contextData.getNewLineStr());

			for (int i = 0; i < strLines.length; i++) {

				String line = strLines[i];
				line = line.startsWith("declare -x ") ? line.replace("declare -x ", "")
						: line.startsWith("export ") ? line.replace("export ", "") : line;

				if (line.indexOf('=') < 1) {
					continue;
				}

				String key = line.substring(0, line.indexOf('='));
				String value = line.substring(line.indexOf('=') + 1);

				// extract double quotation
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length() - 1);
				}

				envMap.put(key.trim(), value);

			}

		}

		log.info("Detected {} Server Variables....", envMap.size() - 1);


		if (!SystemUtils.IS_OS_WINDOWS) {


			// crontab
			envMap.put("", "");
			envMap.put("", "");
			output = CommandUtils.executeAndGetOutput(new String[] { "crontab -l" });

			if (StringUtils.isNotEmpty(output)) {

				String[] strLines = output.split(contextData.getNewLineStr());

				log.info("Detected {} Cron Schedule....", strLines.length);

				for (int i = 0; i < strLines.length; i++) {

					String line = strLines[i];

					if (line.indexOf("/") >= 0) {

						envMap.put(line.substring(0, line.indexOf("/")), line.substring(line.indexOf("/")));

					} else {
						envMap.put(line, "");
					}

				}

			}
		}




		File reportFile = new File(CommonFileUtils.join(contextData.getOutputDir(), "server-env.csv"));
		ReportUtils.createCsvFile(reportFile, envMap);

		log.info("Complete collection. You can see result at {}", reportFile);
	}

}
