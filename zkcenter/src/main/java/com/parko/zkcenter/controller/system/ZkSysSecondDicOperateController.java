package com.parko.zkcenter.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.parko.system.entity.pojo.APPResultCommom;
import com.parko.system.entity.pojo.AppResultData;
import com.parko.system.entity.sys.TSysArea;
import com.parko.system.entity.sys.TSysSecondDic;
import com.parko.system.service.sys.TSysSecondDicService;
import com.parko.system.utils.JwtTokenUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 后台码表相关业务接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/zk/sys/dic")
@Api(tags = "后台码表相关业务接口")
public class ZkSysSecondDicOperateController {

	@Autowired
	private TSysSecondDicService tSysSecondDicService;//码表数据业务接口
	
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 获取码值数据列表
	 * @param tSysSecondDic
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "获取码值数据列表")
	  @RequestMapping(value = "geTSysSecondDics", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData geTSysSecondDics(@RequestBody TSysSecondDic tSysSecondDic,@RequestHeader String token) {
		
		  Map<String, Object> map=new HashMap<>();
		  try {
			  List<TSysSecondDic> tSysSecondDics=tSysSecondDicService.geTSysSecondDics(tSysSecondDic);
			  map.put("list", tSysSecondDics);
			  if(tSysSecondDics!=null&&tSysSecondDics.size()>0) {
				  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", map);
			  }else {
				  appResultData=new AppResultData(APPResultCommom.GETLIST_FAIL, APPResultCommom.GETLIST_FAIL_ERROR_MSG, map);
				  return appResultData;
			  }
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
	  
}
