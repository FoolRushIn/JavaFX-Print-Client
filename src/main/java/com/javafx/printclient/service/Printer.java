//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package com.javafx.printclient.service;

import com.enterprisedt.net.ftp.FTPClient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor
public class Printer {
    private String printerName = "";
    private String osPrinterName;
    private FTPClient ftpserver = null;
    private boolean printerActive = false;
    //    private LabelService labelService = null;
    private boolean stopThread = false;
    private String status = "0";
    public static Map<String, String> statusMap = new HashMap();
    private Map<String, Integer> statusTotal = new HashMap();

    static {
        statusMap.put("-999", "打印机停止");
        statusMap.put("-888", "服务停止");
        statusMap.put("-777", "失败");
        statusMap.put("0", "等待");
        statusMap.put("1", "获取XML文件");
        statusMap.put("-1", "获取XML文件失败");
        statusMap.put("2", "获取报表文件");
        statusMap.put("-2", "获取报表文件失败");
        statusMap.put("3", "打印报表");
        statusMap.put("-3", "打印报表失败");
        statusMap.put("9", "打印完成");
        statusMap.put("-9", "备份文件失败");
    }

    public boolean isStopThread() {
        return this.stopThread;
    }

    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

    public Map<String, Integer> getStatusTotal() {
        return this.statusTotal;
    }

    public String getOsPrinterName() {
        return this.osPrinterName;
    }

    public void setOsPrinterName(String osPrinterName) {
        this.osPrinterName = osPrinterName;
    }

    public void setStatusTotal(Map<String, Integer> statusTotal) {
        this.statusTotal = statusTotal;
    }

    public Printer(String printerName) {
        this.printerName = printerName;
        this.printerActive = true;
    }

    public Printer(String printerName, boolean printerActive) {
        this.printerName = printerName;
        this.printerActive = printerActive;
    }

    public boolean isPrinterActive() {
        return this.printerActive;
    }

    public void setPrinterActive(boolean printerActive) {
        this.printerActive = printerActive;
    }

    public String getStatus() {
        return statusMap.containsKey(this.status) ? (String) statusMap.get(this.status) : this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrinterName() {
        return this.printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public static void converXML(File xml) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        FileInputStream in = null;

        try {
            in = new FileInputStream(xml);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(in);
            NodeList nodeList = document.getElementsByTagName("LABELS");
            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    Node labels = nodeList.item(i);
                    NodeList alllabel = labels.getChildNodes();

                    for (int j = 0; j < alllabel.getLength(); ++j) {
                        Node label = alllabel.item(j);
                        NodeList labelFields = label.getChildNodes();
                        List<String> existNodeName = new ArrayList();

                        int k;
                        Node labelField;
                        String nodeName;
                        for (k = 0; k < labelFields.getLength(); ++k) {
                            labelField = labelFields.item(k);
                            nodeName = labelField.getNodeName();
                            if (!"variable".equalsIgnoreCase(nodeName)) {
                                existNodeName.add(nodeName);
                            }
                        }

                        for (k = 0; k < labelFields.getLength(); ++k) {
                            labelField = labelFields.item(k);
                            nodeName = labelField.getNodeName();
                            String nodeValue = "";
                            if ("variable".equalsIgnoreCase(nodeName)) {
                                NamedNodeMap attrs = labelField.getAttributes();

                                for (int z = 0; z < attrs.getLength(); ++z) {
                                    Node attrNode = attrs.item(z);
                                    if ("name".equalsIgnoreCase(attrNode.getNodeName())) {
                                        nodeName = attrNode.getNodeValue();
                                        if (labelField.getFirstChild() == null) {
                                            nodeValue = "";
                                        } else {
                                            nodeValue = labelField.getFirstChild().getNodeValue();
                                        }
                                        break;
                                    }
                                }

                                if (!existNodeName.contains(nodeName)) {
                                    Element addnode = document.createElement(nodeName);
                                    addnode.setTextContent(nodeValue);
                                    label.appendChild(addnode);
                                }
                            }
                        }
                    }
                }
            }

            StreamResult strResult = new StreamResult(xml);
            TransformerFactory tfac = TransformerFactory.newInstance();

            try {
                Transformer t = tfac.newTransformer();
                t.setOutputProperty("encoding", "UTF-8");
                t.setOutputProperty("indent", "yes");
                t.setOutputProperty("method", "xml");
                t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                t.transform(new DOMSource(document.getDocumentElement()), strResult);
            } catch (Exception var34) {
                System.err.println("XML.toString(Document): " + var34);
            }
        } catch (ParserConfigurationException var35) {
            var35.printStackTrace();
        } catch (SAXException var36) {
            var36.printStackTrace();
        } catch (IOException var37) {
            var37.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var33) {
                    var33.printStackTrace();
                }
            }

        }

    }

//    public void startThread() {
//        _PrinterThread printerThread = new _PrinterThread(this);
//        printerThread.start();
//    }


    public class PrintXMLParam {
        private String labelNameJasper = "";
        private String labelNameBirt = "";
        private String printerName = "";
        private int copies = 1;
        private File xml = null;

        public PrintXMLParam() {
        }

        public void setLabelNameBirt(String labelNameBirt) {
            this.labelNameBirt = labelNameBirt;
        }

        public void setLabelNameJasper(String labelNameJasper) {
            this.labelNameJasper = labelNameJasper;
        }

        public String getLabelNameBirt() {
            return labelNameBirt;
        }

        public String getLabelNameJasper() {
            return labelNameJasper;
        }

        public String getPrinterName() {
            return this.printerName;
        }

        public void setPrinterName(String printerName) {
            this.printerName = printerName;
        }

        public int getCopies() {
            return this.copies;
        }

        public void setCopies(int copies) {
            this.copies = copies;
        }

        public File getXml() {
            return this.xml;
        }

        public void setXml(File xml) {
            this.xml = xml;
        }
    }

//    public class _PrinterThread extends Thread {
//        private Printer printer = null;
////        private LabelService service = null;
//
//        public _PrinterThread(Printer printer) {
//            this.printer = printer;
////            this.service = printer.getLabelService();
//        }
//
//        public void run() {
//            while(true) {
//                try {
//                    if (this.printer.isStopThread()) {
//                        return;
//                    }
//
//                    if (this.service.isActive() && this.printer.isPrinterActive()) {
//                        this.printer.setStatus("0");
//                        if ("1".equalsIgnoreCase(Printer.this.labelService.getRemotemethod())) {
//                            this.printer.downloadFromFtp();
//                        } else if ("2".equalsIgnoreCase(Printer.this.labelService.getRemotemethod())) {
//                            this.printer.downloadFromSMB();
//                        } else if ("3".equalsIgnoreCase(Printer.this.labelService.getRemotemethod())) {
//                            this.printer.downloadFromSharedFile();
//                        } else if ("4".equalsIgnoreCase(Printer.this.labelService.getRemotemethod())) {
//                            this.printer.downloadFromUrl();
//                        }
//
//                        File[] xmlFile = this.printer.checkAndFindTask();
//                        if (xmlFile != null && xmlFile.length > 0) {
//                            File[] var5 = xmlFile;
//                            int var4 = xmlFile.length;
//
//                            for(int var3 = 0; var3 < var4; ++var3) {
//                                File eachFile = var5[var3];
//
//                                try {
//                                    this.print(eachFile);
//                                    Thread.sleep(1000L);
//                                } finally {
//                                    while(eachFile.exists()) {
//                                        try {
//                                            log.debug("删除7");
//                                            eachFile.delete();
//                                        } catch (Exception var13) {
//                                            var13.printStackTrace();
//                                        }
//                                    }
//
//                                }
//                            }
//                        }
//                    } else if (this.service.isActive()) {
//                        this.printer.setStatus("-999");
//                    } else {
//                        this.printer.setStatus("-888");
//                    }
//
//                    try {
//                        Thread.sleep(3000L);
//                    } catch (InterruptedException var15) {
//                        var15.printStackTrace();
//                    }
//                } catch (Exception var16) {
//                    var16.printStackTrace();
//                }
//            }
//        }
//
//        public void print(File xmlFile) {
//            this.printer.setStatus("1");
//            if (xmlFile.exists()) {
//                this.printer.setStatus("2");
//                PrintXMLParam label = this.printer.findLabel(xmlFile);
//                if (label != null && label.labelNameJasper.trim().length() != 0) {
//                    File labelFile = null;
//                    try {
//                        labelFile = new File(label.labelNameJasper);
//                        System.out.println(label.labelNameJasper);
//                        if(labelFile == null || labelFile.length() <=0) {
//                            labelFile = new File(label.labelNameBirt);
//                            System.out.println(label.labelNameBirt);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (labelFile.exists()) {
//                        this.printer.setStatus("3");
//                        if (this.printer.printLabel(labelFile, xmlFile, label.copies)) {
//                            this.printer.setStatus("9");
//                            if (!Printer.this.backupLabel(xmlFile)) {
//                                this.printer.setStatus("-9");
//                            }
//                        } else {
//                            this.printer.setStatus("-3");
//                        }
//                    } else {
//                        this.printer.setStatus("-2");
//                    }
//                } else {
//                    this.printer.setStatus("-2");
//                }
//            } else {
//                this.printer.setStatus("-1");
//            }
//
//            if (this.printer.getStatusTotal().containsKey(this.printer.getStatus())) {
//                Integer cnt = (Integer)this.printer.getStatusTotal().get(this.printer.getStatus());
//                this.printer.getStatusTotal().put(this.printer.getStatus(), new Integer(cnt + 1));
//            } else {
//                this.printer.getStatusTotal().put(this.printer.getStatus(), new Integer(1));
//            }
//
//        }
//    }
}
