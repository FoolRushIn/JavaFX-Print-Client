//package com.javafx.printclient.utils;
//
//import com.javafx.printclient.common.LabelService;
//import com.javafx.printclient.service.Printer;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
///**
// * @ClassName PrinterServiceImpl
// * @Description TODO
// * @Author sue
// * @Date 2022/1/14 11:12
// * @Version 1.0
// **/
//
//@Component
//public class PrinterUtil {
//
//    public static Map<String, Printer> allPrinter = new HashMap();
//
////    public static void addPrinter(String printerName, String osprintername) {
////        if (!allPrinter.containsKey(printerName.toUpperCase())) {
////            Printer printer = new Printer(printerName);
////            printer.setOsPrinterName(osprintername);
////            allPrinter.put(printerName, printer);
////
////            //todo
//////            printer.startThread();
////        }
////    }
//
//    public boolean savePrinter(String printerName, String osprintername) {
//        Properties prop = new Properties();
//        File f = new File("printer.properties");
//        FileInputStream in = null;
//        FileOutputStream oFile = null;
//
//        try {
//            if (!f.exists()) {
//                f.createNewFile();
//            }
//
//            in = new FileInputStream(f);
//            prop.load(in);
//            prop.setProperty(printerName, osprintername);
//            oFile = new FileOutputStream("printer.properties", false);
//            prop.store(oFile, "");
//            oFile.close();
//            LabelService.addPrinter(printerName, osprintername);
//            return true;
//        } catch (IOException var16) {
//            var16.printStackTrace();
//        } finally {
//            if (oFile != null) {
//                try {
//                    oFile.close();
//                } catch (IOException var15) {
//                    var15.printStackTrace();
//                }
//            }
//
//        }
//
//        return false;
//    }
//
//    public void removePrinter(String printerName) {
//        if (allPrinter.containsKey(printerName)) {
//            Printer printer = (Printer) allPrinter.get(printerName);
//            printer.setStopThread(true);
//            allPrinter.remove(printerName);
//        }
//
//    }
//
//    public boolean removePrinterAction(String printerName) {
//        Properties prop = new Properties();
//        File f = new File("printer.properties");
//        FileInputStream in = null;
//        FileOutputStream oFile = null;
//
//        try {
//            if (!f.exists()) {
//                f.createNewFile();
//            }
//
//            in = new FileInputStream(f);
//            prop.load(in);
//            prop.remove(printerName);
//            oFile = new FileOutputStream("printer.properties", false);
//            prop.store(oFile, "");
//            oFile.close();
//            removePrinter(printerName);
//            return true;
//        } catch (IOException var15) {
//            var15.printStackTrace();
//        } finally {
//            if (oFile != null) {
//                try {
//                    oFile.close();
//                } catch (IOException var14) {
//                    var14.printStackTrace();
//                }
//            }
//
//        }
//        return false;
//    }
//
//
//}
