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
package com.javafx.printclient.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 类描述
 *
 * @author hxy
 */
public class PrintUtil {

    public static boolean printPdf(ByteArrayOutputStream baos, File label, File xml, int copies, String osPrinterName, PageFormat pageFormat) {
        PDDocument document = null;
        FileOutputStream fops = null;
        try {
            String filename = xml.getParentFile() + File.separator + FileUtil.getNameWithoutExtension(label.getName()) + System.currentTimeMillis() + ".pdf";
            fops = new FileOutputStream(filename);
            fops.write(baos.toByteArray());

            File pdfFile = new File(filename);
            document = PDDocument.load(pdfFile);
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setJobName(pdfFile.getName());
            if (osPrinterName != null) {
                // 查找并设置打印机
                //获得本台电脑连接的所有打印机
                PrintService[] printServices = PrinterJob.lookupPrintServices();
                if (printServices == null || printServices.length == 0) {
                    System.out.print("打印失败，未找到可用打印机，请检查。");
                    return false;
                }
                PrintService printService = null;
                //匹配指定打印机
                for (int i = 0; i < printServices.length; i++) {
                    System.out.println(printServices[i].getName());
                    if (printServices[i].getName().contains(osPrinterName)) {
                        printService = printServices[i];
                        break;
                    }
                }
                if (printService != null) {
                    printJob.setPrintService(printService);
                } else {
                    System.out.print("打印失败，未找到名称为" + osPrinterName + "的打印机，请检查。");
                    return false;
                }
            }

            //打印本地文件
            printJob.setPageable(new PDFPageable(document));
            printJob.setCopies(copies);//设置打印份数
            //添加打印属性
            HashPrintRequestAttributeSet pars = new HashPrintRequestAttributeSet();
            pars.add(Sides.ONE_SIDED); //设置单双页打印
            pars.add(SheetCollate.COLLATED);//设置逐份打印COLLATED（123,123,123） 不逐份打印 UNCOLLATED（111,222,333）
//            if (printJob.printDialog()){
                printJob.print(pars);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.closeQuietly(document);
            IoUtil.closeQuietly(fops);
        }
        return true;
    }

    public static void setPageFormat(PageFormat pageFormat, double width, double height, int orientation) {
//        pageFormat.setOrientation(orientation);//纵向横向
//        pageFormat.setPaper(PrintUtil.getPaper(width, height));//设置纸张
    }

    public static Paper getPaper(double width, double height) {
        Paper paper = new Paper();
        // 默认为A4纸张，对应像素宽和高分别为 595, 842
        width = width <= 0 ? 595 :  width;
        height = height <= 0 ? 842: height;
        // 设置边距，单位是像素，10mm边距，对应 28px
        double marginLeft = 0;
        double marginRight = 0;
        double marginTop = 0;
        double marginBottom = 0;
        paper.setSize(width, height);
        // 下面一行代码，解决了打印内容为空的问题
        paper.setImageableArea(marginLeft, marginRight, width - (marginLeft + marginRight), height - (marginTop + marginBottom));
        return paper;
    }
}