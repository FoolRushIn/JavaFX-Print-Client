//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package com.javafx.printclient.service;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.javafx.printclient.common.Constant;
import com.javafx.printclient.common.LabelService;
import com.javafx.printclient.httputil.HttpManager;
import com.javafx.printclient.printstrategy.PreviewContext;
import com.javafx.printclient.utils.FileUtil;
import com.javafx.printclient.utils.StringUtil;
import com.javafx.printclient.utils.ZipUtil;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class Printer {
    private String printerName = "";
    private String osPrinterName;
    private FTPClient ftpserver = null;
    private boolean printerActive = false;
    private LabelService labelService = null;
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

    public Printer(String printerName, LabelService labelService) {
        this.printerName = printerName;
        this.printerActive = true;
        this.labelService = labelService;
    }

    public Printer(String printerName, LabelService labelService, boolean printerActive) {
        this.printerName = printerName;
        this.printerActive = printerActive;
        this.labelService = labelService;
    }

    public boolean isPrinterActive() {
        return this.printerActive;
    }

    public void setPrinterActive(boolean printerActive) {
        this.printerActive = printerActive;
    }

    public boolean getPrinterActive() {
        return this.printerActive;
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

    public LabelService getLabelService() {
        return this.labelService;
    }

    public void setLabelService(LabelService labelService) {
        this.labelService = labelService;
    }

    public File[] checkAndFindTask() {
        String xmlPath = this.labelService.getXmlPath();
        File dir = new File(xmlPath);
        if (!dir.exists()) {
            return null;
        } else if (!dir.isDirectory()) {
            return null;
        } else {
            File[] findFile = dir.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    if (pathname.isFile() && pathname.canRead() && pathname.getName().toLowerCase().endsWith(".xml")) {
                        String filename = pathname.getName();
                        if (filename.indexOf("_") > 0) {
                            String printnameinfile = filename.substring(0, filename.indexOf("_"));
                            return Printer.this.printerName.equalsIgnoreCase(printnameinfile);
                        } else {
                            PrintXMLParam printParam = Printer.this.findLabel(pathname);
                            return Printer.this.printerName.equalsIgnoreCase(printParam.printerName);
                        }
                    } else {
                        if (pathname.exists()) {
                            try {
                                log.debug("删除1");

                                //刪除10分鐘以前是文件
                                Path path = Paths.get(pathname.getPath());
                                BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
                                BasicFileAttributes attr = basicview.readAttributes();
                                Long l = attr.creationTime().toMillis();
                                if ((System.currentTimeMillis() - l) / 1000 > 600) {
                                    pathname.delete();
                                }
                            } catch (Exception var4) {
                                var4.printStackTrace();
                            }
                        }

                        return false;
                    }
                }
            });
            if (findFile != null && findFile.length > 0) {
                Arrays.sort(findFile, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return f1.getName().compareTo(f2.getName());
                    }
                });
                return findFile;
            } else {
                return null;
            }
        }
    }

    public PrintXMLParam findLabel(File xml) {
        PrintXMLParam printParam = new PrintXMLParam();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        FileInputStream in = null;

        try {
            in = new FileInputStream(xml);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(in);
            NodeList nodeList = document.getElementsByTagName("LABELS");
            if (nodeList.getLength() > 0) {
                NamedNodeMap attrs = nodeList.item(0).getAttributes();
                String nodevalue = "";
                String nodename = "";

                for (int j = 0; j < attrs.getLength(); ++j) {
                    Node attrNode = attrs.item(j);
                    nodename = attrNode.getNodeName();
                    nodevalue = attrNode.getNodeValue();
                    if ("_FORMAT".equalsIgnoreCase(nodename)) {
                        if (nodevalue != null && nodevalue.trim().length() != 0) {
                            printParam.labelNameJasper = this.labelService.getRptPath() + "//" + nodevalue + ".jasper";
                            printParam.labelNameBirt = this.labelService.getRptPath() + "//" + nodevalue + ".rptdesign";
                        }
                    } else if ("_PRINTERNAME".equalsIgnoreCase(nodename)) {
                        printParam.printerName = nodevalue;
                    } else if ("_QUANTITY".equalsIgnoreCase(attrNode.getNodeName())) {
                        try {
                            printParam.copies = Integer.parseInt(attrNode.getNodeValue());
                        } catch (Exception var27) {
                            printParam.copies = 1;
                        }
                    }
                }
            }
        } catch (ParserConfigurationException var28) {
            var28.printStackTrace();
        } catch (SAXException var29) {
            var29.printStackTrace();
        } catch (IOException var30) {
            var30.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var26) {
                    var26.printStackTrace();
                }
            }

        }

        return printParam;
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

    public boolean printLabel(File label, File xml, int copies) {
        PreviewContext previewContext = new PreviewContext(FileUtil.getFileExtension(label.getName()));
        try {
            return previewContext.executeReport(label, xml, copies,  this.osPrinterName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean backupLabel(File xml) {
        String logPath = this.labelService.getLogPath();
        InputStream inStream = null;
        FileOutputStream fs = null;

        try {
            int byteread = 0;
            if (xml.exists()) {
                inStream = new FileInputStream(xml);
                File desFile = new File(logPath + "//" + xml.getName());
                if (desFile.exists()) {
                    log.debug("删除2");
                    desFile.delete();
                }

                if (!desFile.getParentFile().exists()) {
                    desFile.getParentFile().mkdirs();
                }

                fs = new FileOutputStream(logPath + "//" + xml.getName());
                byte[] buffer = new byte[1444];

                byteread = 0;
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }

                inStream.close();
            }

            return true;
        } catch (Exception var17) {
            var17.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }

                if (fs != null) {
                    fs.close();
                }
            } catch (IOException var16) {
                var16.printStackTrace();
                return false;
            }

        }

        return false;
    }

    public void startThread() {
        _PrinterThread printerThread = new _PrinterThread(this);
        printerThread.start();
    }
//todo
//    public static void main(String[] args) {
//        String dir = "F:\\work";
//        File fdir = new File(dir);
//        String[] var6;
//        int var5 = (var6 = fdir.list()).length;
//
//        String xml;
//        for(int var4 = 0; var4 < var5; ++var4) {
//            xml = var6[var4];
//            System.out.println(xml);
//        }
//
//        xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><!--Type:ASUSCASE_CM01_169 ORDERKEY=0000115630--><labels _FORMAT=\"SKU\" _PRINTERNAME=\"AAA\" _QUANTITY=\"1\"><label><OrderDate>0000115630</OrderDate><Order1>测试</Order1><variable name=\"COMPANY\">ACI-BIZCOM</variable><variable name=\"EXTERNORDERKEY\">155215081002446</variable><variable name=\"STORERKEY\">ASUST01B</variable><variable name=\"SKU\">17604-00011800</variable><variable name=\"SUSR2\">15150357326</variable><variable name=\"SKUDESC1\">DVD S-MULTI DL 8X/6X/8X6X/5X</variable><variable name=\"QTY\">30</variable><variable name=\"ITEM\">14</variable><variable name=\"DROPID\">P201508150031</variable><variable name=\"LOT11\"> </variable><variable name=\"OrderDate\">0000115630</variable><variable name=\"SUSR3\"/></label><label><OrderDate>00001156301111</OrderDate><Order1>测试111</Order1><variable name=\"COMPANY\">ACI-BIZCOM</variable><variable name=\"EXTERNORDERKEY\">155215081002446</variable><variable name=\"STORERKEY\">ASUST01B</variable><variable name=\"SKU\">17604-00011800</variable><variable name=\"SUSR2\">15150357326</variable><variable name=\"SKUDESC1\">DVD S-MULTI DL 8X/6X/8X6X/5X</variable><variable name=\"QTY\">30</variable><variable name=\"ITEM\">14</variable><variable name=\"DROPID\">P201508150031</variable><variable name=\"LOT11\">China</variable><variable name=\"OrderDate\">00001156301111</variable><variable name=\"SUSR3\"/></label></labels>";
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        ByteArrayInputStream in = null;
//
//        try {
//            in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            Document document = db.parse(in);
//            NodeList nodeList = document.getElementsByTagName("LABELS");
//            if (nodeList.getLength() > 0) {
//                for(int i = 0; i < nodeList.getLength(); ++i) {
//                    Node labels = nodeList.item(i);
//                    NodeList alllabel = labels.getChildNodes();
//
//                    for(int j = 0; j < alllabel.getLength(); ++j) {
//                        Node label = alllabel.item(j);
//                        NodeList labelFields = label.getChildNodes();
//                        List<String> existNodeName = new ArrayList();
//
//                        int k;
//                        Node labelField;
//                        String nodeName;
//                        for(k = 0; k < labelFields.getLength(); ++k) {
//                            labelField = labelFields.item(k);
//                            nodeName = labelField.getNodeName();
//                            if (!"variable".equalsIgnoreCase(nodeName)) {
//                                existNodeName.add(nodeName);
//                            }
//                        }
//
//                        for(k = 0; k < labelFields.getLength(); ++k) {
//                            labelField = labelFields.item(k);
//                            nodeName = labelField.getNodeName();
//                            String nodeValue = "";
//                            if ("variable".equalsIgnoreCase(nodeName)) {
//                                NamedNodeMap attrs = labelField.getAttributes();
//
//                                for(int z = 0; z < attrs.getLength(); ++z) {
//                                    Node attrNode = attrs.item(z);
//                                    if ("name".equalsIgnoreCase(attrNode.getNodeName())) {
//                                        nodeName = attrNode.getNodeValue();
//                                        if (labelField.getFirstChild() == null) {
//                                            nodeValue = " ";
//                                        } else {
//                                            nodeValue = labelField.getFirstChild().getNodeValue();
//                                        }
//                                        break;
//                                    }
//                                }
//
//                                if (!existNodeName.contains(nodeName)) {
//                                    Element addnode = document.createElement(nodeName);
//                                    addnode.setTextContent(nodeValue);
//                                    label.appendChild(addnode);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            File strWtr = new File("e:\\abc.xml");
//            StreamResult strResult = new StreamResult(strWtr);
//            TransformerFactory tfac = TransformerFactory.newInstance();
//
//            try {
//                Transformer t = tfac.newTransformer();
//                t.setOutputProperty("encoding", "UTF-8");
//                t.setOutputProperty("indent", "yes");
//                t.setOutputProperty("method", "xml");
//                t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//                t.transform(new DOMSource(document.getDocumentElement()), strResult);
//            } catch (Exception var36) {
//                System.err.println("XML.toString(Document): " + var36);
//            }
//        } catch (ParserConfigurationException var37) {
//            var37.printStackTrace();
//        } catch (SAXException var38) {
//            var38.printStackTrace();
//        } catch (IOException var39) {
//            var39.printStackTrace();
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException var35) {
//                    var35.printStackTrace();
//                }
//            }
//
//        }
//
//    }

    public void downloadFromSMB() throws MalformedURLException, SmbException {
        String smbHost = this.labelService.getFtpAddr();
        String user = this.labelService.getFtpUser();
        String pwd = this.labelService.getFtpPwd();
        String remotePath = this.labelService.getFtpRemote();
        if (remotePath == null || remotePath.trim().length() == 0) {
            remotePath = "/";
        }

        Properties properties = System.getProperties();
        String sep = properties.getProperty("file.separator");
        if (smbHost != null && smbHost.trim().length() != 0) {
            String xmlPath = this.labelService.getXmlPath();
            File dir = new File(xmlPath);
            if (dir.exists()) {
                if (dir.isDirectory()) {
                    if (remotePath.lastIndexOf("/") + 1 != remotePath.length()) {
                        remotePath = remotePath + "/";
                    }

                    if (xmlPath.lastIndexOf(sep) + 1 != xmlPath.length()) {
                        xmlPath = xmlPath + sep;
                    }

                    String smbUrl = "smb://" + user + ":" + pwd + "@" + smbHost + remotePath;
                    SmbFile sfile = new SmbFile(smbUrl);
                    SmbFile[] allSFile = sfile.listFiles();
                    if (allSFile != null) {
                        InputStream in = null;
                        OutputStream out = null;
                        SmbFile[] var17 = allSFile;
                        int var16 = allSFile.length;

                        for (int var15 = 0; var15 < var16; ++var15) {
                            SmbFile theFile = var17[var15];
                            String nf = theFile.getName();
                            int dirindex = nf.lastIndexOf(47);
                            if (dirindex >= 0) {
                                nf = nf.substring(dirindex + 1);
                            }

                            if (nf.indexOf("_") > 0) {
                                String printnameinfile = nf.substring(0, nf.indexOf("_"));
                                if (this.printerName.equalsIgnoreCase(printnameinfile)) {
                                    File localFile = new File(xmlPath + nf);

                                    try {
                                        in = new BufferedInputStream(new SmbFileInputStream(theFile));
                                        out = new BufferedOutputStream(new FileOutputStream(localFile));

                                        for (byte[] buffer = new byte[1024]; in.read(buffer) != -1; buffer = new byte[1024]) {
                                            out.write(buffer);
                                        }
                                        log.debug("删除2");
                                        theFile.delete();
                                    } catch (Exception var31) {
                                        var31.printStackTrace();
                                    } finally {
                                        try {
                                            out.close();
                                            in.close();
                                        } catch (IOException var30) {
                                            var30.printStackTrace();
                                        }

                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    public void downloadFromFtp() throws IOException, FTPException {
        String ftpHost = this.labelService.getFtpAddr();
        String user = this.labelService.getFtpUser();
        String pwd = this.labelService.getFtpPwd();
        String remotePath = this.labelService.getFtpRemote();
        if (remotePath == null || remotePath.trim().length() == 0) {
            remotePath = "/";
        }

        Properties properties = System.getProperties();
        String sep = properties.getProperty("file.separator");
        if (ftpHost != null && ftpHost.trim().length() != 0) {
            String xmlPath = this.labelService.getXmlPath();
            File dir = new File(xmlPath);
            if (dir.exists()) {
                if (dir.isDirectory()) {
                    if (remotePath.lastIndexOf("/") + 1 != remotePath.length()) {
                        remotePath = remotePath + "/";
                    }

                    if (xmlPath.lastIndexOf(sep) + 1 != xmlPath.length()) {
                        xmlPath = xmlPath + sep;
                    }

                    String[] allFTPFiles = null;
                    if (this.ftpserver == null) {
                        this.ftpserver = new FTPClient();
                        this.ftpserver.setRemoteHost(ftpHost);
                        this.ftpserver.connect();
                        this.ftpserver.login(user, pwd);
                        this.ftpserver.setTransferBufferSize(10240000);
                        this.ftpserver.setType(FTPTransferType.BINARY);
                    }

                    try {
                        allFTPFiles = this.ftpserver.dir(remotePath);
                    } catch (Exception var17) {
                        this.ftpserver = new FTPClient();
                        this.ftpserver.setRemoteHost(ftpHost);
                        this.ftpserver.connect();
                        this.ftpserver.login(user, pwd);
                        this.ftpserver.setTransferBufferSize(10240000);
                        this.ftpserver.setType(FTPTransferType.BINARY);
                        allFTPFiles = this.ftpserver.dir(remotePath);
                    }

                    this.ftpserver.chdir(remotePath);
                    if (allFTPFiles != null) {
                        String[] var13 = allFTPFiles;
                        int var12 = allFTPFiles.length;

                        for (int var11 = 0; var11 < var12; ++var11) {
                            String f = var13[var11];
                            String nf = f;
                            int dirindex = f.lastIndexOf(47);
                            if (dirindex >= 0) {
                                nf = f.substring(dirindex + 1);
                            }

                            if (nf.indexOf("_") > 0) {
                                String printnameinfile = nf.substring(0, nf.indexOf("_"));
                                if (this.printerName.equalsIgnoreCase(printnameinfile)) {
                                    this.ftpserver.get(xmlPath + nf, f);
                                    log.debug("删除4");
                                    this.ftpserver.delete(f);
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    public void downloadFromSharedFile() throws IOException {
        String remotePath = this.labelService.getFtpRemote();
        if (remotePath != null && remotePath.trim().length() != 0) {
            Properties properties = System.getProperties();
            String sep = properties.getProperty("file.separator");
            String xmlPath = this.labelService.getXmlPath();
            File dir = new File(xmlPath);
            if (dir.exists()) {
                if (dir.isDirectory()) {
                    if (remotePath.lastIndexOf("\\") + 1 != remotePath.length()) {
                        remotePath = remotePath + "\\";
                    }

                    if (xmlPath.lastIndexOf(sep) + 1 != xmlPath.length()) {
                        xmlPath = xmlPath + sep;
                    }

                    File remoteDir = new File(remotePath);
                    String[] allfiles = remoteDir.list();
                    if (allfiles != null) {
                        String[] var11 = allfiles;
                        int var10 = allfiles.length;

                        for (int var9 = 0; var9 < var10; ++var9) {
                            String thefile = var11[var9];
                            if (thefile.indexOf("_") > 0) {
                                String printnameinfile = thefile.substring(0, thefile.indexOf("_"));
                                if (this.printerName.equalsIgnoreCase(printnameinfile)) {
                                    File fromfile = new File(remotePath + thefile);
                                    File tofile = new File(xmlPath + thefile);
                                    Files.copy(fromfile.toPath(), tofile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                    log.debug("删除5");
                                    fromfile.delete();
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    public void downloadFromUrl() throws IOException {
        String ftpaddr = this.labelService.getFtpAddr();
        if (!StringUtil.isNullOrBlank(ftpaddr)) {
            Properties properties = System.getProperties();
            String sep = properties.getProperty("file.separator");
            String xmlPath = this.labelService.getXmlPath();
            File dir = new File(xmlPath);
            if (dir.exists()) {
                if (dir.isDirectory()) {
                    String zipFileName = this.printerName + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                            + ".zip";

                    if (xmlPath.lastIndexOf(sep) + 1 != xmlPath.length()) {
                        xmlPath = xmlPath + sep;
                    }

                    /**
                     * 从http获取文件,文件名自己命名
                     */
                    try {
                        String paramStr = "printer=" + this.printerName + "&operator=" + Constant.OPERATOR_DOWNLOAD_XML;

                        String downloadUrl = ftpaddr + "&" + paramStr;

                        System.out.println("打印机:" + this.printerName + ",开始下载" + zipFileName + " " + new Date());
                        boolean isAvailable = HttpManager.downLoadFromUrl(downloadUrl, zipFileName, xmlPath);

                        if (isAvailable) {
                            System.out.println("打印机:" + this.printerName + ",结束下载" + zipFileName + " " + new Date());
                            File zipfile = new File(xmlPath + sep + zipFileName);
                            System.out.println("打印机:" + this.printerName + ",获取打印机文件:" + zipfile.getName() + ",大小:" + zipfile.length() + " " + new Date());
                            if (zipfile.length() > 0) {
                                ZipUtil.unZipFiles(xmlPath + sep + zipFileName, xmlPath);
                            }
                            if (zipfile.canRead()) {
                                log.debug("删除6");
                                zipfile.delete();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

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

    public class _PrinterThread extends Thread {
        private Printer printer = null;
        private LabelService service = null;

        public _PrinterThread(Printer printer) {
            this.printer = printer;
            this.service = printer.getLabelService();
        }

        public void run() {
            while(true) {
                try {
                    if (this.printer.isStopThread()) {
                        return;
                    }

                    if (this.service.isActive() && this.printer.isPrinterActive()) {
                        this.printer.setStatus("0");

                        //todo 下载方式
//                        if ("1".equalsIgnoreCase(Printer.this.labelService.getRemotemethod())) {
//                            this.printer.downloadFromFtp();
//                        } else if ("2".equalsIgnoreCase(Printer.this.labelService.getRemotemethod())) {
//                            this.printer.downloadFromSMB();
//                        } else if ("3".equalsIgnoreCase(Printer.this.labelService.getRemotemethod())) {
//                            this.printer.downloadFromSharedFile();
//                        } else if ("4".equalsIgnoreCase(Printer.this.labelService.getRemotemethod())) {
//                            this.printer.downloadFromUrl();
//                        }

                        //暂时用本地


                        File[] xmlFile = this.printer.checkAndFindTask();
                        if (xmlFile != null && xmlFile.length > 0) {
                            File[] var5 = xmlFile;
                            int var4 = xmlFile.length;

                            for(int var3 = 0; var3 < var4; ++var3) {
                                File eachFile = var5[var3];

                                try {
                                    this.print(eachFile);
                                    Thread.sleep(1000L);
                                } finally {
                                    while(eachFile.exists()) {
                                        try {
                                            log.debug("删除7");
                                            eachFile.delete();
                                        } catch (Exception var13) {
                                            var13.printStackTrace();
                                        }
                                    }

                                }
                            }
                        }
                    } else if (this.service.isActive()) {
                        this.printer.setStatus("-999");
                    } else {
                        this.printer.setStatus("-888");
                    }

                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException var15) {
                        var15.printStackTrace();
                    }
                } catch (Exception var16) {
                    var16.printStackTrace();
                }
            }
        }

        public void print(File xmlFile) {
            this.printer.setStatus("1");
            if (xmlFile.exists()) {
                this.printer.setStatus("2");
                PrintXMLParam label = this.printer.findLabel(xmlFile);
                if (label != null && label.labelNameJasper.trim().length() != 0) {
                    File labelFile = null;
                    try {
                        labelFile = new File(label.labelNameJasper);
                        System.out.println(label.labelNameJasper);
                        if(labelFile == null || labelFile.length() <=0) {
                            labelFile = new File(label.labelNameBirt);
                            System.out.println(label.labelNameBirt);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (labelFile.exists()) {
                        this.printer.setStatus("3");
                        if (this.printer.printLabel(labelFile, xmlFile, label.copies)) {
                            this.printer.setStatus("9");
                            if (!Printer.this.backupLabel(xmlFile)) {
                                this.printer.setStatus("-9");
                            }
                        } else {
                            this.printer.setStatus("-3");
                        }
                    } else {
                        this.printer.setStatus("-2");
                    }
                } else {
                    this.printer.setStatus("-2");
                }
            } else {
                this.printer.setStatus("-1");
            }

            if (this.printer.getStatusTotal().containsKey(this.printer.getStatus())) {
                Integer cnt = (Integer)this.printer.getStatusTotal().get(this.printer.getStatus());
                this.printer.getStatusTotal().put(this.printer.getStatus(), new Integer(cnt + 1));
            } else {
                this.printer.getStatusTotal().put(this.printer.getStatus(), new Integer(1));
            }

        }
    }
}
