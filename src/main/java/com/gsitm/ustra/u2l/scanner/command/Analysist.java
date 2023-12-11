package com.gsitm.ustra.u2l.scanner.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.gsitm.ustra.u2l.scanner.command.analysist.AnalysistData;
import com.gsitm.ustra.u2l.scanner.command.analysist.Clang;
import com.gsitm.ustra.u2l.scanner.command.analysist.Java;
import com.gsitm.ustra.u2l.scanner.command.analysist.XPlatform;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;
import com.gsitm.ustra.u2l.scanner.utils.CommonFileUtils;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Slf4j
@Command(
  name = "analysist",
  description = "Analysis source code",
  version = "1.0"
)
public class Analysist implements Runnable {

	@Option(names = {"-r", "--rootpath"}, required = true, description = "rootPath")
    private String rootPath;

	@Option(names = {"-t", "--type"}, required = true, description = "language type")
    private String type;

    @Override
    public void run() {

    	// 서버 설정 조회
    	try {

    		log.info("Start anaysis : {},{}", this.rootPath, this.type);
    		ContextData contextData = CommandContextHolder.get();
    		contextData.setRootPath(rootPath);

    		List<AnalysistData> dataList = null;

    		if ("java".equals(this.type)) {
    			dataList = new Java().analysis(contextData);
    		}

    		if ("c".equals(this.type)) {
    			dataList = new Clang().analysis(contextData);
    		}

    		if ("xplatform".equals(this.type)) {
    			dataList = new XPlatform().analysis(contextData);
    		}

    		if (dataList != null) {
    			this.createReport(contextData, dataList);
    		}

		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
    }

    private void createReport(ContextData contextData, List<AnalysistData> dataList) throws IOException {

    	File targetFile = new File(CommonFileUtils.join(contextData.getCwd(), FilenameUtils.getName(contextData.getRootPath()) + ".csv"));

    	if (targetFile.exists()) {
    		targetFile.delete();
    	}

    	// crate directory
		targetFile.getParentFile().mkdirs();


		BufferedWriter fw = new BufferedWriter(new FileWriter(targetFile));

		try {

			fw.write("Path,LineNo,Code,Comment");
			fw.newLine();

			for(AnalysistData data : dataList) {
				fw.write(data.getSourceCodePath());
				fw.write(",");
				fw.write(data.getLineNo().toString());
				fw.write(",");
				fw.write(data.getCode().replace(",", " ").trim());
				fw.write(",");
				fw.write(data.getComment());
				fw.newLine();
			}

			fw.flush();

		} catch(Exception e) {
			log.warn(e.getMessage(), e);
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
		}

		log.info("Created report : {}, {}", targetFile.getPath(), dataList.size());

    }


}