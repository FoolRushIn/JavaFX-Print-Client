//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package com.javafx.printclient.common;

import com.javafx.printclient.httputil.HttpManager;
import com.javafx.printclient.mina.MinaClient;
import com.javafx.printclient.service.Printer;
import com.javafx.printclient.utils.StringUtil;
import com.javafx.printclient.utils.ZipUtil;
import com.sie.infor.message.FileTask;
import com.sie.infor.message.SocketMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LabelService {
    private final String PRINTERCONFIGFILE = "printer.properties";
    private final String SYSCONFIGFILE = "sys.properties";
    private final String XMLPATH = "xmlpath";
    private final String RPTPATH = "rptpath";
    private final String LOGPATH = "logpath";
    private final String FTPADDR = "ftpaddr";
    private final String FTPUSER = "fptuser";
    private final String FTPPWD = "ftppwd";
    private final String FTPREMOTE = "ftpremote";
    private final String REMOTEMETHOD = "remotemethod";
    private final String REMOTERPTPATH = "remoterptpath";
    public static Map<String, Printer> allPrinter = new HashMap();
    private boolean active = false;
    private String xmlPath = "";
    private String rptPath = "";
    private String logPath = "";
    private String ftpAddr = "";
    private String ftpUser = "";
    private String ftpPwd = "";
    private String ftpRemote = "";
    private String remotemethod = "";
    private String rptRemote = "";
    private String ip = "";
    private int port = 0;
    private static LabelService instance = null;

    private static MinaClient minaClient = null;

    private LabelService() {
        this.loadConfig();
        this.loadPrinter();
        this.setActive(true);
        this.startBackThread();

        this.loadMinaClient();

        try {
            this.downloadrpt();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void downloadrpt() throws IOException {
        if (this.rptRemote != null && this.rptRemote.trim().length() != 0) {
            Properties properties = System.getProperties();
            String sep = properties.getProperty("file.separator");
            String rptPath = this.rptPath;
            File dir = new File(rptPath);
            if (dir.exists()) {
                if (dir.isDirectory()) {
                    String rptRemote = this.rptRemote;
                    if (rptRemote.lastIndexOf("\\") + 1 != rptRemote.length()) {
                        rptRemote = rptRemote + "\\";
                    }

                    if (rptPath.lastIndexOf(sep) + 1 != rptPath.length()) {
                        rptPath = rptPath + sep;
                    }

                    File remoteDir = new File(rptRemote);
                    File[] allfiles = remoteDir.listFiles(new FileFilter() {
                        public boolean accept(File pathname) {
                            return pathname.isFile() && pathname.canRead() && (pathname.getName().toLowerCase().endsWith(".jasper") || pathname.getName().toLowerCase().endsWith(".jxml"));
                        }
                    });
                    File[] var11 = allfiles;
                    int var10 = allfiles.length;

                    for (int var9 = 0; var9 < var10; ++var9) {
                        File thefile = var11[var9];
                        File tofile = new File(rptPath + thefile.getName());
                        Files.copy(thefile.toPath(), tofile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }

                }
            }
        } else if ("4".equalsIgnoreCase(this.getRemotemethod())) {
            //20200331 如果未配置远程rpt目录，并且是http请求下载文件，则使用http的方式获取打印模板文件
            downloadRptFromUrl();
        }
    }

    public void downloadRptFromUrl() throws IOException {
        String ftpaddr = this.getFtpAddr();
        if (!StringUtil.isNullOrBlank(ftpaddr)) {
            Properties properties = System.getProperties();
            String sep = properties.getProperty("file.separator");
            String rptPath = this.getRptPath();
            File dir = new File(rptPath);
            if (dir.exists()) {
                dir.mkdirs();
            }
            if (dir.isDirectory()) {
                String zipFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                        + ".zip";

                if (rptPath.lastIndexOf(sep) + 1 != rptPath.length()) {
                    rptPath = rptPath + sep;
                }

                /**
                 * 从http获取文件,文件名自己命名
                 */
                try {
                    String paramStr = "printer=XXXXXX&operator=" + Constant.OPERATOR_DOWNLOAD_RPT;

                    String downloadUrl = ftpaddr + "&" + paramStr;

                    System.out.println("开始下载报表文件" + zipFileName + " " + new Date());
                    HttpManager.downLoadFromUrl(downloadUrl, zipFileName, rptPath);
                    System.out.println("报表文件结束下载" + zipFileName + " " + new Date());
                    File zipfile = new File(rptPath + sep + zipFileName);
                    System.out.println("获取报表文件:" + zipfile.getName() + ",大小:" + zipfile.length() + " " + new Date());
                    if (zipfile.length() > 0) {
                        ZipUtil.unZipFiles(rptPath + sep + zipFileName, rptPath);
                    }
                    if (zipfile.canRead()) {
                        log.debug("删除6");
                        zipfile.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadConfig() {
        Properties prop = new Properties();
        FileInputStream in = null;
        File directory = new File(System.getProperty("user.dir"));

        try {
            String currentpath = directory.getAbsolutePath();
            File f = new File("sys.properties");
            if (!f.exists()) {
                this.xmlPath = currentpath + "\\xml";
                this.rptPath = currentpath + "\\label";
                this.logPath = currentpath + "\\log";
                this.ftpAddr = "";
                this.ftpUser = "";
                this.ftpPwd = "";
                this.ftpRemote = "";
                this.remotemethod = "0";
                this.rptRemote = "";
                this.ip = "";
                this.port = 0;
            } else {
                in = new FileInputStream("sys.properties");
                prop.load(in);
                this.xmlPath = prop.getProperty("xmlpath", currentpath + "\\xml");
                this.rptPath = prop.getProperty("rptpath", currentpath + "\\label");
                this.logPath = prop.getProperty("logpath", currentpath + "\\log");
                this.ftpAddr = prop.getProperty("ftpaddr", "");
                this.ftpUser = prop.getProperty("fptuser", "");
                this.ftpPwd = prop.getProperty("ftppwd", "");
                this.ftpRemote = prop.getProperty("ftpremote", "");
                this.remotemethod = prop.getProperty("remotemethod", "0");
                this.rptRemote = prop.getProperty("remoterptpath", "");
                this.ip = prop.getProperty("ip", "");
                this.port = Integer.parseInt(prop.getProperty("port", "0"));
            }
        } catch (IOException var14) {
            var14.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var13) {
                    var13.printStackTrace();
                }
            }

        }

    }

    public String getRptRemote() {
        return this.rptRemote;
    }

    public void setRptRemote(String rptRemote) {
        this.rptRemote = rptRemote;
    }

    public void loadPrinter() {
        Properties prop = new Properties();
        FileInputStream in = null;

        try {
            File f = new File("printer.properties");
            if (f.exists()) {
                in = new FileInputStream("printer.properties");
                prop.load(in);
                Iterator it = prop.stringPropertyNames().iterator();

                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = prop.getProperty(key);
                    this.addPrinter(key, value);
                }
            }
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }

        }

    }


    public void loadMinaClient() {
        Map<Byte, FileTask> fileTasks = new HashMap<>();

//        FileTask fileTask1 = new FileTask(
//                "F:\\client_download1", new String[] {"PRINT01","PRINT02"}, new String[] {}, 5,  TimeUnit.SECONDS);
//        FileTask fileTask2 =new FileTask(
//                "F:\\client_download2", new String[] {"PRINT01","PRINT02"}, new String[] {}, 10,  TimeUnit.SECONDS);

        //下载打印文件
        FileTask fileTask1 = new FileTask(
                this.getXmlPath(),
                allPrinter.values().stream().map(p -> p.getPrinterName()).toArray(String[]::new),
                new String[]{}, Constant.LABEL_FILE_DOWNLOAD_PERIOD, TimeUnit.SECONDS);

        //下载报表文件
        FileTask fileTask2 = new FileTask(
                this.getRptPath(), new String[]{},
                new String[]{Constant.FILE_TYPE_BIRT, Constant.FILE_TYPE_JASPER}, Constant.REPORT_FILE_DOWNLOAD_PERIOD, TimeUnit.SECONDS);

        fileTasks.put(SocketMessage.TYPE_CLIENT_NOTIFY_SERVER_LABEL, fileTask1);
        fileTasks.put(SocketMessage.TYPE_CLIENT_NOTIFY_SERVER_REPORT, fileTask2);

        minaClient = new MinaClient(getIp(), getPort(), fileTasks);
        //判断是否使用的socket
        if ("5".equals(remotemethod)) {
            minaClient.initConnector();
        } else {
            minaClient.closeConnect();
        }
    }

    public String getFtpUser() {
        return this.ftpUser;
    }

    public void setFtpUser(String ftpUser) {
        this.ftpUser = ftpUser;
    }

    public String getFtpPwd() {
        return this.ftpPwd;
    }

    public void setFtpPwd(String ftpPwd) {
        this.ftpPwd = ftpPwd;
    }

    public String getFtpRemote() {
        return this.ftpRemote;
    }

    public void setFtpRemote(String ftpRemote) {
        this.ftpRemote = ftpRemote;
    }

    public String getFtpAddr() {
        return this.ftpAddr;
    }

    public void setFtpAddr(String ftpAddr) {
        this.ftpAddr = ftpAddr;
    }

    public String getXmlPath() {
        return this.xmlPath;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
        File xmlPathFile = new File(xmlPath);
        if (!xmlPathFile.exists()) {
            xmlPathFile.mkdir();
        }

    }

    public String getRptPath() {
        return this.rptPath;
    }

    public void setRptPath(String rptPath) {
        this.rptPath = rptPath;
        File rptPathFile = new File(rptPath);
        if (!rptPathFile.exists()) {
            rptPathFile.mkdir();
        }

    }

    public String getLogPath() {
        return this.logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
        File logPathFile = new File(logPath);
        if (!logPathFile.exists()) {
            logPathFile.mkdir();
        }

    }

    public void addPrinter(String printerName, String osprintername) {
        if (!allPrinter.containsKey(printerName.toUpperCase())) {
            Printer printer = new Printer(printerName, this);
            printer.setOsPrinterName(osprintername);
            allPrinter.put(printerName, printer);
            printer.startThread();
        }

    }

    public boolean addPrinterAction(String printerName, String osprintername) {
        Properties prop = new Properties();
        File f = new File("printer.properties");
        FileInputStream in = null;
        FileOutputStream oFile = null;

        try {
            if (!f.exists()) {
                f.createNewFile();
            }

            in = new FileInputStream(f);
            prop.load(in);
            prop.setProperty(printerName, osprintername);
            oFile = new FileOutputStream("printer.properties", false);
            prop.store(oFile, "");
            oFile.close();
            this.addPrinter(printerName, osprintername);
            return true;
        } catch (IOException var16) {
            var16.printStackTrace();
        } finally {
            if (oFile != null) {
                try {
                    oFile.close();
                } catch (IOException var15) {
                    var15.printStackTrace();
                }
            }

        }

        return false;
    }

    public void removePrinter(String printerName) {
        if (allPrinter.containsKey(printerName)) {
            Printer printer = (Printer) allPrinter.get(printerName);
            printer.setStopThread(true);
            allPrinter.remove(printerName);
        }

    }

    public boolean removePrinterAction(String printerName) {
        Properties prop = new Properties();
        File f = new File("printer.properties");
        FileInputStream in = null;
        FileOutputStream oFile = null;

        try {
            if (!f.exists()) {
                f.createNewFile();
            }

            in = new FileInputStream(f);
            prop.load(in);
            prop.remove(printerName);
            oFile = new FileOutputStream("printer.properties", false);
            prop.store(oFile, "");
            oFile.close();
            this.removePrinter(printerName);
            return true;
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            if (oFile != null) {
                try {
                    oFile.close();
                } catch (IOException var14) {
                    var14.printStackTrace();
                }
            }

        }

        return false;
    }

    public static LabelService getInstance() {
        if (instance == null) {
            instance = new LabelService();
        }

        return instance;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRemotemethod() {
        return this.remotemethod;
    }

    public void setRemotemethod(String remotemethod) {
        this.remotemethod = remotemethod;
    }

    public void saveConfigPath(String xmlPath, String labelPath, String logPath, String ftpAddr, String ftpUser,
                               String ftpPwd, String ftpRemote, String remotemethod, String rptremote, String ip, int port) {
        this.setXmlPath(xmlPath);
        this.setRptPath(labelPath);
        this.setLogPath(logPath);
        this.setFtpAddr(ftpAddr);
        this.setFtpUser(ftpUser);
        this.setFtpPwd(ftpPwd);
        this.setFtpRemote(ftpRemote);
        this.setRemotemethod(remotemethod);
        this.setRptRemote(rptremote);
        this.setIp(ip);
        this.setPort(port);
        Properties prop = new Properties();
        File f = new File("sys.properties");
        FileInputStream in = null;
        FileOutputStream oFile = null;

        try {
            if (!f.exists()) {
                f.createNewFile();
            }

            in = new FileInputStream(f);
            prop.load(in);
            prop.setProperty("xmlpath", xmlPath);
            prop.setProperty("rptpath", labelPath);
            prop.setProperty("logpath", logPath);
            prop.setProperty("ftpaddr", ftpAddr);
            prop.setProperty("fptuser", ftpUser);
            prop.setProperty("ftppwd", ftpPwd);
            prop.setProperty("ftpremote", ftpRemote);
            prop.setProperty("remotemethod", remotemethod);
            prop.setProperty("remoterptpath", rptremote);
            prop.setProperty("ip", ip);
            prop.setProperty("port", port + "");
            oFile = new FileOutputStream("sys.properties", false);
            prop.store(oFile, "");
            oFile.close();

            //判断是否使用的socket
            if ("5".equals(remotemethod)) {
                minaClient.initConnector();
                minaClient.setIp(getIp());
                minaClient.setPort(getPort());
            } else {
                minaClient.closeConnect();
            }

        } catch (IOException var23) {
            var23.printStackTrace();
        } finally {
            if (oFile != null) {
                try {
                    oFile.close();
                } catch (IOException var22) {
                    var22.printStackTrace();
                }
            }

        }

    }

    public boolean savePrinter(String printerName, String osprintername) {
        return allPrinter.containsKey(printerName) ? true : this.addPrinterAction(printerName, osprintername);
    }

    public Object[][] getPrinterModel() {
        Set<String> keys = allPrinter.keySet();
        Object[][] obj = new Object[keys.size()][4];
        int i = 0;
        Printer p = null;

        for (Iterator var6 = keys.iterator(); var6.hasNext(); ++i) {
            String key = (String) var6.next();
            obj[i][0] = i + 1;
            obj[i][1] = key;
            p = (Printer) allPrinter.get(key);
            obj[i][2] = p.getOsPrinterName();
            obj[i][3] = p.isPrinterActive() ? "Y" : "N";
        }

        return obj;
    }

    public Object[][] getPrinterStatusModel() {
        Set<String> keys = allPrinter.keySet();
        Object[][] obj = new Object[keys.size() == 0 ? 1 : keys.size()][4];
        int i = 0;
        Printer p = null;

        for (Iterator var6 = keys.iterator(); var6.hasNext(); ++i) {
            String key = (String) var6.next();
            obj[i][0] = i + 1;
            obj[i][1] = key;
            p = (Printer) allPrinter.get(key);
            obj[i][2] = p.getStatus();
            Map<String, Integer> m = p.getStatusTotal();
            Set<String> mk = m.keySet();
            String memo = "";

            String k;
            for (Iterator var11 = mk.iterator(); var11.hasNext(); memo = memo + k + "=" + (Integer) m.get(k) + ";") {
                k = (String) var11.next();
            }

            obj[i][3] = memo;
        }

        if (i == 0) {
            obj[0][0] = "";
            obj[0][1] = "";
            obj[0][2] = "";
            obj[0][3] = "";
        }

        return obj;
    }

    public void setActive(String printerName, boolean b) {
        if (allPrinter.containsKey(printerName)) {
            Printer p = (Printer) allPrinter.get(printerName);
            p.setPrinterActive(b);
        }

    }

    public void startBackThread() {
        _BackThread backThread = new _BackThread();
        backThread.start();
    }

    public synchronized void deleteOldFile() {
        String xmlPath = this.getXmlPath();
        File dir = new File(xmlPath);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File[] allFile = dir.listFiles();
                Calendar currentDate = Calendar.getInstance();
                currentDate.add(5, -2);
                File[] var8 = allFile;
                int var7 = allFile.length;

                for (int var6 = 0; var6 < var7; ++var6) {
                    File f = var8[var6];
                    Calendar createDate = Calendar.getInstance();
                    createDate.setTimeInMillis(f.lastModified());
                    if (currentDate.after(createDate)) {
                        while (f.exists()) {
                            try {
                                log.debug("删除1");
                                f.delete();
                            } catch (Exception var11) {
                                var11.printStackTrace();
                            }
                        }
                    }
                }

            }
        }
    }

//    public static void main(String[] args) {
//        Calendar currentDate = Calendar.getInstance();
//        System.out.println(currentDate);
//        currentDate.add(5, -300);
//        System.out.println(currentDate);
//        File f = new File("d:\\infor_sce_administration_console_build_219_linux.iso");
//        Calendar createDate = Calendar.getInstance();
//        createDate.setTimeInMillis(f.lastModified());
//        System.out.println(createDate);
//        if (currentDate.after(createDate)) {
//            System.out.println("OK");
//        } else {
//            System.out.println("FAIL");
//        }
//
//    }

    public class _BackThread extends Thread {
        public _BackThread() {
        }

        public void run() {
            while (true) {
                try {
                    LabelService.this.deleteOldFile();
                } catch (Exception var3) {
                    var3.printStackTrace();
                }

                try {
                    Thread.sleep(600000L);
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
                }
            }
        }
    }
}
