package com.javafx.printclient.utils;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	
	/**
	 * when the string is null return true,otherwise false
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str){
		return str==null;
	}
	
	/***
	 * this method only return true when the str is not null and empty,to be note that a null string will return false
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str){
		return (!isNull(str))&&(isEmpty(str));
	}

	/**
	 * when a string is null or empty will return true
	 * @param str
	 * @return
	 */
	public static boolean isNullOrBlank(String str){
		return (isNull(str))||(isEmpty(str));
	}
	/**
	 * check a string trim A need: A>=minLen and A<=maxLen 
	 * @param str
	 * @param minLen
	 * @param maxLen
	 * @return
	 */
	public static boolean isRightLength(String str, int minLen, int maxLen){
		if(str==null || minLen > maxLen )
		{
			return false;
		}
		
		int len = str.trim().length();
		
		return len>=minLen && len<=maxLen;
	}
	
	private static boolean isEmpty(String str){
		return (str.trim().length()==0);
	}
	
	public static String getUUID(){
		return UUID.randomUUID().toString();
	}
	
	/**
	 * 
	 */
	public static String lpad(String s, int n, String replace) {
		while (s.length() < n) {
			s = replace+s;
		}
		return s;
	}
	
	/*
	 * 第一步：扫描进去的SKU编码需要用系统中的相应单据中SKU编码去掉前两位后进行比对
	 * 第二步：将扫描的SKU去掉后4位与系统中去掉前两位的SKU进行匹配 add by phoebe 20150717
	 */
	
	public static String skuScanMatching(String sku,List<String> skuS){
		for (int i = 0; i < skuS.size(); i++) {
			if(skuS.get(i).substring(2).equals(sku)||skuS.get(i).substring(2).equals(sku.substring(0,sku.length()-4))){
				sku = skuS.get(i);
				break;
			}
		}  
		return sku;
		
	}
	/**
	 * 判断数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
	}
	/**
	 * 判断特殊字符
	 * @param str
	 * @return
	 */
	 public static  boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
	 /**
	 * 将字符串LIST转换为符合,,,)字符串
	 */
	public static String changeListToStr(List<String> list){
		StringBuffer idkey=new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			if(i!=list.size()-1){
				idkey.append(list.get(i));
				idkey.append(",");
			}else{
				idkey.append(list.get(i));
			}
		}
		return idkey.toString();
	}
	 /**
	 * 将字符串LIST转换为符合 sql IN('','','','')字符串
	 */
	public static String changeListToSqlStr(List<String> list){
		StringBuffer idkey=new StringBuffer();
		idkey.append("'");
		for (int i = 0; i < list.size(); i++) {
			if(i!=list.size()-1){
				idkey.append(list.get(i));
				idkey.append("','");
			}else{
				idkey.append(list.get(i));
			}
		}
		idkey.append("'");
		return idkey.toString();
	}
}
