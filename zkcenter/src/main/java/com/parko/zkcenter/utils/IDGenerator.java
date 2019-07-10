package com.parko.zkcenter.utils;

import java.util.Random;

public class IDGenerator {
	/**
	 * @return 生成主键id（23位），生成规则:20位时间戳（精确到微妙）+3位随机数。
	 */
	public static String getId() {
		StringBuffer retValue = new StringBuffer();
		Random rd = new Random();
		//获取20位时间戳
		String tempString = TimestampConvert.currentTimeString().trim();
		for(int i = 0; i < tempString.length(); i++) {
			try {
				if(Integer.parseInt(tempString.substring(i, i + 1)) < 10)
					retValue.append(tempString.substring(i, i + 1));
			} catch(Exception e) {
			}
		}
		retValue.append(100+rd.nextInt(900));
		return retValue.toString();
	}
	  public static void main(String[] args) {
		  System.out.println(TimestampConvert.currentTimeString().trim());
		  System.out.println(IDGenerator.getId());
		  System.out.println(TimestampConvert.currentTimeString().trim());
	
		  }
}