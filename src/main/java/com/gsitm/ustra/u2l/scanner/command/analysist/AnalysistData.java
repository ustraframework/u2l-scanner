package com.gsitm.ustra.u2l.scanner.command.analysist;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalysistData {

	private String sourceCodePath;
	private String code;
	private Integer lineNo;
	private String comment;

}
