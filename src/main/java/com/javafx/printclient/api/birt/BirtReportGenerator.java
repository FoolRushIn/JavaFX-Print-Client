package com.javafx.printclient.api.birt;

import com.javafx.printclient.utils.PrintUtil;
import net.sf.jasperreports.engine.type.OrientationEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.report.engine.api.*;
import org.eclipse.birt.report.engine.api.script.element.IMasterPage;
import org.eclipse.birt.report.model.api.*;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.metadata.DimensionValue;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.awt.print.PageFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class BirtReportGenerator {
    private static IReportEngine birtEngine = new BirtEngineFactory().getObject();

    protected final static Log log = LogFactory.getLog(BirtReportGenerator.class);

    private static String DATASOURCE_NAME = "DataSource";
    private static String DATASET_NAME = "DataSet";
    private static String QueryText = "";

    /**
     * 功能描述
     *
     * @param rptParam 报表参数
     * @param label    模板文件
     * @param xml      xml数据源文件
     * @return java.io.ByteArrayOutputStream
     */
    public ByteArrayOutputStream generate(ReportParameter rptParam, File label, File xml, ByteArrayOutputStream baos, PageFormat pageFormat) throws Exception {
        //ByteArrayOutputStream 底层维护了一个byte[]，可以自动扩容
        IReportRunnable runnable = null;
        Resource rs = new FileSystemResource(label.getAbsolutePath());
        runnable = birtEngine.openReportDesign(rs.getInputStream());

        IMasterPage simpleMasterPage = runnable.getDesignInstance().getReport().getMasterPage("Simple MasterPage");
        String pageType = (String) simpleMasterPage.getUserProperty("type");//纸张类型
        Double height = 0.0;
        Double width = 0.0;
        DimensionValue heightDV = (DimensionValue) simpleMasterPage.getUserProperty("height");
        DimensionValue widthDV = (DimensionValue) simpleMasterPage.getUserProperty("width");
        String orientation = simpleMasterPage.getUserProperty("orientation").toString();
        height = heightDV.getMeasure();
        width = widthDV.getMeasure();

        //设置打印属性
        PrintUtil.setPageFormat(pageFormat, width, height,
                OrientationEnum.PORTRAIT.getName().equalsIgnoreCase(orientation) ? 1 : 0);

        IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(runnable);

        try {
            // create task to run and render report
            ReportDesignHandle report = (ReportDesignHandle) runnable.getDesignHandle();
            buildReport(report, xml.getAbsolutePath());

            IRenderOption options = new RenderOption();
            if (rptParam.getFormat().equalsIgnoreCase("pdf")) {
                PDFRenderOption pdfOptions = new PDFRenderOption(options);
                pdfOptions.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
                pdfOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.FIT_TO_PAGE_SIZE);
                pdfOptions.setOutputStream(baos);
                runAndRenderTask.setRenderOption(pdfOptions);
            }
            if (rptParam.getFormat().equalsIgnoreCase("html")) {
                HTMLRenderOption htmlOptions = new HTMLRenderOption();
                htmlOptions.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
                htmlOptions.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption.FIT_TO_PAGE_SIZE);
                htmlOptions.setOutputStream(baos);
                runAndRenderTask.setRenderOption(htmlOptions);
            }
            runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, this.getClass().getClassLoader());
            runAndRenderTask.run();
        } finally {
            if (runAndRenderTask != null) {
                runAndRenderTask.close();
            }
        }
        return baos;
    }


    protected void buildReport(ReportDesignHandle designHandle, String xmlPath) {
        try {
            ElementFactory designFactory = designHandle.getElementFactory();
            buildDataSource(designFactory, designHandle, xmlPath);
            buildDataSet(designFactory, designHandle, xmlPath);
            TableHandle table = (TableHandle) designHandle.getBody().get(0);
            table.setDataSet(designHandle.findDataSet(DATASET_NAME));

        } catch (SemanticException e) {
            log.error(e);
            e.printStackTrace();
        }

    }

    protected void buildDataSource(ElementFactory designFactory,
                                   ReportDesignHandle designHandle, String xmlPath) throws SemanticException {
        clearDataSource(designHandle);
        OdaDataSourceHandle dsHandle = designFactory.newOdaDataSource(
                DATASOURCE_NAME,
                "org.eclipse.birt.report.data.oda.xml");
        /*dsHandle.setProperty( "odaDriverClass",
        "com.mysql.jdbc.Driver" );
        dsHandle.setProperty( "odaURL", "jdbc:mysql://localhost/stat" );
        dsHandle.setProperty( "odaUser", "root" );
        dsHandle.setProperty( "odaPassword", "" );*/
        dsHandle.setProperty("FILELIST", xmlPath);
        designHandle.getDataSources().add(dsHandle);
    }

    protected void buildDataSet(ElementFactory designFactory,
                                ReportDesignHandle designHandle, String xmlPath) throws SemanticException {

        clearDataSet(designHandle);
        OdaDataSetHandle dsHandle = designFactory.newOdaDataSet(DATASET_NAME,
                //"org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet" );
                /*  OdaDataSetHandle dsHandle = designFactory.newOdaDataSet(DATASET_NAME,*/
                "org.eclipse.birt.report.data.oda.xml.dataSet");
        dsHandle.setPrivateDriverProperty("XML_FILE", xmlPath);
        dsHandle.setPrivateDriverProperty("MAX_ROW", "-1");
        dsHandle.setQueryText(QueryText);
        dsHandle.setDataSource(DATASOURCE_NAME);
        designHandle.getDataSets().add(dsHandle);

    }


    /**
     * 清除原有的数据源
     */
    protected void clearDataSource(ReportDesignHandle designHandle) {
        //DataSourceHandle dsh = designHandle.findDataSource(DATASOURCE_NAME);
        //dsh.drop();
        int count = designHandle.getDataSources().getCount();

        try {
            for (int i = 0; i < count; i++)
                designHandle.getDataSources().drop(i);
        } catch (SemanticException e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    /**
     * 清除原有的数据集
     */
    protected void clearDataSet(ReportDesignHandle designHandle) {
        getQueryText(designHandle);
        int count = designHandle.getDataSets().getCount();
        try {
            for (int i = count - 1; i >= 0; i--) {
                designHandle.getDataSets().drop(i);
            }
        } catch (SemanticException e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    /**
     *
     */
    protected void getQueryText(ReportDesignHandle designHandle) {
        QueryText = (String) designHandle.getDataSets().get(0).getProperty(
                "queryText");
    }
}
