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

import com.javafx.printclient.api.jasper.JasperReportGenerator;
import com.javafx.printclient.utils.IoUtil;
import com.javafx.printclient.utils.PrintUtil;

import java.awt.print.PageFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * jasper操作
 *
 * @author hxy
 */
public class JasperPreviewStrategy extends PreviewStrategy {

	private static JasperReportGenerator jasperReportGenerator = new JasperReportGenerator();

    @Override
    public boolean executeReport(File label, File xml, int copies, String osPrinterName) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			PageFormat pageFormat = new PageFormat();
			jasperReportGenerator.generate(label, xml, baos, pageFormat);
			return PrintUtil.printPdf(baos, label, xml, copies, osPrinterName, pageFormat);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			IoUtil.closeQuietly(baos);
		}
    }
}
