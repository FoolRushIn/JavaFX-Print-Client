package com.javafx.printclient.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 异步请求返回对象封装类
 * @author shi_zujun 
 * @date 2017年8月18日 下午4:53:02
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnData implements Serializable{

	private static final long serialVersionUID = 830238967474370032L;

	/** 返回码 */
	private String key;
	
	/** 返回值 */
	private Object value;
	
	/** 返回信息 */
	private String msg;

}
