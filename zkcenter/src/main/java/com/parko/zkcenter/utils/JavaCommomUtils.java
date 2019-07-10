package com.parko.zkcenter.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class JavaCommomUtils {

	/**
	 * 生成任意长度的数字0串
	 * @param length
	 * @return
	 */
	public static String randomNumData(int length ) {
	      String val = "";
		    for (int i = 0; i < length; i++) {
		    	 val +="0";
		    }
		    System.out.println("===val=="+val);
		    return val;
	}
	 /**
     * 根据浏览器对传入的参数进行编码处理
     * @param request
     * @param paramName
     * @return
     */
    public  static String setParamName(HttpServletRequest request, String paramName) {
    	   final String userAgent = request.getHeader("USER-AGENT");
           String finalFileName = "";
           try {
              
               if(StringUtils.contains(userAgent, "Edge")||StringUtils.contains(userAgent, "MSIE")){//IE浏览器
                   finalFileName = URLEncoder.encode(paramName, "UTF8");
                   
               }else if(StringUtils.contains(userAgent, "Mozilla")||StringUtils.contains(userAgent, "Chrome")){//google,火狐浏览器
            	   
            	   finalFileName = new String(paramName.getBytes(),"ISO-8859-1" );

               }else{

                   finalFileName = URLEncoder.encode(paramName, "UTF-8");
               }
           } catch (UnsupportedEncodingException e) {
           }
           return finalFileName;
       }
}
