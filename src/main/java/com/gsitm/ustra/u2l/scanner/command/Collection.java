package com.gsitm.ustra.u2l.scanner.command;

import java.io.IOException;

import com.gsitm.ustra.u2l.scanner.command.collection.Middleware;
import com.gsitm.ustra.u2l.scanner.command.collection.ServerEnv;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Slf4j
@Command(
  name = "collect",
  description = "Collect configurations",
  version = "1.0"
)
public class Collection implements Runnable {

	@Option(names = {"-r", "--rootpath"}, description = "rootPath (default. values is '/')", split = ",")
    private String[] rootPath = new String[] {"/"};

	@Option(names = {"-e", "--excludePath"}, description = "excludePath", split = ",")
    private String[] excludePath = new String[] {};

    @Override
    public void run() {

    	// 서버 설정 조회
    	try {

    		ContextData contextData = CommandContextHolder.get();
    		contextData.setRootPaths(this.rootPath);
    		contextData.setExcludePaths(this.excludePath);

    		log.info("rootPath : {}", this.rootPath);
    		log.info("excludePath : {}/{}", this.excludePath.length, this.excludePath);

			ServerEnv.collect(contextData);
			Middleware.collect(contextData);

		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
    }


}