package com.javafx.printclient.common;

public class Constant {
	
	/**
	 * HTTP请求操作方式
	 * 下载XML打印文件
	 */
	public static final String OPERATOR_DOWNLOAD_XML = "downloadXml";

	/**
	 * HTTP请求操作方式
	 * 下载RPT打印文件
	 */
	public static final String OPERATOR_DOWNLOAD_RPT = "downloadRpt";

	/**
	 * file type birt
	 */
	public static final String FILE_TYPE_BIRT = "rptdesign";

	/**
	 * file type jasper
	 */
	public static final String FILE_TYPE_JASPER = "jasper";




	/**mina
	 *  LABEL_XML_PERIOD  设置标签xml文件请求间隔时间，单位为秒
	 */
	public static final int LABEL_FILE_DOWNLOAD_PERIOD = 3;

	/**mina
	 * REPORT_FILE_DOWNLOAD_PERIOD 设置报表文件文件请求间隔时间，单位为秒
	 */
	public static final int REPORT_FILE_DOWNLOAD_PERIOD = 310;

	/**mina
	 * READ_IDLE_TIMEOUT 设置会话读空闲时间
	 */
	public static final int READ_IDLE_TIMEOUT = 60; //{@link READ_IDLE_TIMEOUT}

	/**mina
	 * WRITE_IDLE_TIMEOUT 设置会话写空闲时间，当会话写空闲发送心跳包给服务器
	 */
	public static final int WRITE_IDLE_TIMEOUT = 45;

}
