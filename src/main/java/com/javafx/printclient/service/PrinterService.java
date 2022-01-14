package com.javafx.printclient.service;

public interface PrinterService {
    public void addPrinter(String printerName, String osprintername);

    public boolean savePrinter(String printerName, String osprintername);

    public void removePrinter(String printerName);

    public boolean removePrinterAction(String printerName);
}
