package com.gsitm.ustra.u2l.scanner.command.collection.web;

import java.io.File;
import java.io.IOException;

import com.gsitm.ustra.u2l.scanner.command.collection.Middleware.ServerCollectionData;
import com.gsitm.ustra.u2l.scanner.utils.CommandContextHolder.ContextData;

public interface MiddlewareScanner {

	/**
	 * 미들웨어 정보 수집 처리
	 * @param contextData
	 * @param targetPath
	 * @return
	 * @throws IOException
	 */
	ServerCollectionData collect(ContextData contextData, File targetPath) throws IOException;

	/**
	 * Configuration 복사
	 * @param targetDirPath 복사 대상 디렉토리
	 * @param originPath 원본 디렉토리
	 */
	void onCopyConfiguration(File targetDirPath, File originPath);

	/**
	 * 설정 복사 후 처리 작업
	 * @param targetDirPath 설정 정보 복사 디렉토리
	 */
	void onAfterCopyConfiguration(File targetDirPath);
}
