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
import com.parko.redis.utils.RedisUtil;
import com.parko.system.entity.pojo.APPResultCommom;
import com.parko.system.entity.pojo.AppResultData;
import com.parko.system.entity.sys.TSysArea;
import com.parko.system.service.sys.TSysAreaService;
import com.parko.system.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 后台区域相关业务接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/zk/sys/area")
@Api(tags = "后台区域相关业务接口")
public class ZkSysAreaOperateController {

	@Autowired
	private TSysAreaService tSysAreaService;//区域数据业务处理接口
	
	 @Autowired
	 RedisUtil redisUtil;
	 
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 获取省市县等数据列表
	 * @param tSysArea
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "获取省市县等数据列表")
	  @RequestMapping(value = "getTSysAreas", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData getTSysAreas(@RequestBody TSysArea tSysArea,@RequestHeader String token) {
		
		  Map<String, Object> map=new HashMap<>();
		  try {
			  List<TSysArea> tSysAreas=tSysAreaService.getTSysAreas(tSysArea);
			  map.put("list", tSysAreas);
			  if(tSysAreas!=null&&tSysAreas.size()>0) {
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
