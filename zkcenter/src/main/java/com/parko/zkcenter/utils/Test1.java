package com.parko.zkcenter.utils;

import com.alibaba.fastjson.JSONObject;

public class Test1 {

	public static void main(String[] args) {
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("11", "11");
		String aa=JSONObject.toJSONString(jsonObject);
       System.out.println(aa);
	}

}
