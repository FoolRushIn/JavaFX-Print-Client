/**
 * Copyright (c) 2019-2099, Raptor
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javafx.printclient.printstrategy;


import com.javafx.printclient.common.Constant;

import java.io.File;

/**
 * 创建预览策略
 *
 * @author hxy
 */
public class PreviewContext {

	private PreviewStrategy previewStrategy;

	//通过构造方法，传入预览策略再生产具体的策略类
	public PreviewContext(String fileType) {
		switch (fileType) {
			case Constant.FILE_TYPE_BIRT:
				previewStrategy = new BirtPreviewStrategy();
				break;

			case Constant.FILE_TYPE_JASPER:
				previewStrategy = new JasperPreviewStrategy();
				break;

			default:
				break;
		}
	}

	public boolean executeReport(File label, File xml, int copies, String osPrinterName) throws Exception {
		return previewStrategy.executeReport(label, xml, copies, osPrinterName);
	}
}
