package com.javafx.printclient.api.jasper;

import com.javafx.printclient.service.Printer;
import com.javafx.printclient.utils.PrintUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.type.OrientationEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.print.PageFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

public class JasperReportGenerator {

    protected final static Log log = LogFactory.getLog(JasperReportGenerator.class);

    /**
     * 功能描述
     *
     * @param label 标签文件
     * @param xml   数据文件
     * @param baos  返回字节流
     * @return java.io.ByteArrayOutputStream
     */
    public ByteArrayOutputStream generate(File label, File xml, ByteArrayOutputStream baos, PageFormat pageFormat) throws Exception {
        Printer.converXML(xml);
        JasperPrint jasperPrint = null;
        try {
            JRXmlDataSource xmlDS = new JRXmlDataSource(xml, "/LABELS/LABEL/HEADER");
            jasperPrint = JasperFillManager.fillReport(label.getAbsolutePath(), (Map) null, xmlDS);
            PrintUtil.setPageFormat(pageFormat, jasperPrint.getPageWidth(), jasperPrint.getPageHeight(),
                    OrientationEnum.PORTRAIT.getName().equalsIgnoreCase(jasperPrint.getOrientationValue().getName()) ? 1 : 0);
        } catch (JRException var9) {
            var9.printStackTrace();
            return null;
        }

        if (jasperPrint == null) {
            return null;
        } else {
            try {
                JRAbstractExporter exporter = new JRPdfExporter();
                //创建jasperPrint
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                //生成输出流
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
                //屏蔽copy功能
                exporter.setParameter(JRPdfExporterParameter.IS_ENCRYPTED, Boolean.TRUE);
                //加密
                exporter.setParameter(JRPdfExporterParameter.IS_128_BIT_KEY, Boolean.TRUE);
                exporter.exportReport();
                return baos;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
