package com.parko.zkcenter.controller.back;

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
import com.parko.system.entity.cond.IDcond;
import com.parko.system.entity.pojo.APPResultCommom;
import com.parko.system.entity.pojo.AppResultData;
import com.parko.system.entity.pojo.PageBean;
import com.parko.system.entity.sys.TSysUser;
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.JwtTokenUtil;
import com.parko.zkcenter.entity.back.TParamClassManage;
import com.parko.zkcenter.entity.cond.GetParamClasssOnPageCond;
import com.parko.zkcenter.entity.cond.TypeCond;
import com.parko.zkcenter.service.back.TParamClassManageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 参数分类管理相关业务处理接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/qualityControl/class")
@Api(tags = "参数分类管理相关业务处理接口")
public class ParamClassManageController {

	@Autowired
	private TParamClassManageService tParamClassManageService;//参数分类数据业务处理接口
	
	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	@Autowired
	 RedisUtil redisUtil;
	 
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 添加参数分类(特医食品分类/规范指南分类)数据
	 * @param tParamClassManage
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "添加参数分类(特医食品分类/规范指南分类)数据")
	  @RequestMapping(value = "addOneParamClass", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData addOneParamClass(@RequestBody TParamClassManage tParamClassManage,@RequestHeader String token) {
		
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  int classId=tParamClassManage.getId();
		  String className=tParamClassManage.getClassName();
		  TParamClassManage tParamClassManageParam=new TParamClassManage();
		  tParamClassManageParam.setClassName(className);
		  List<TParamClassManage> tParamClassManages=tParamClassManageService.geTParamClassManages(tParamClassManageParam);	
		  if(classId==0) {//新增操作
			  if(tParamClassManages!=null&&tParamClassManages.size()>0) {//判重，分类名称不雷同
				  appResultData=new AppResultData(APPResultCommom.CLASS_REPEAT_ERROR,APPResultCommom.CLASS_REPEAT_ERROR_MSG, className);
					 return appResultData;
			  }
			  tParamClassManage.setCreateUserName(curUser.getUserName());
		  }else {//更新，需要组织更新数据
			  if(tParamClassManages!=null&&tParamClassManages.size()>0) {
				  TParamClassManage tParamClassManage2=tParamClassManages.get(0);
				  if(classId!=tParamClassManage2.getId()) {//判重，分类名称不雷同
					  appResultData=new AppResultData(APPResultCommom.CLASS_REPEAT_ERROR,APPResultCommom.CLASS_REPEAT_ERROR_MSG, className);
					 return appResultData;
				  } 
			  }
			  TParamClassManage tParamClassManage2=tParamClassManageService.findById(classId);
			  tParamClassManage.setCreateUser(tParamClassManage2.getCreateUser());
			  tParamClassManage.setCreateUserName(tParamClassManage2.getCreateUserName()); 
			  tParamClassManage.setCreateTime(tParamClassManage2.getCreateTime());
		  }
		  try {
			  tParamClassManage=tParamClassManageService.saveTParamClassManage(tParamClassManage, curUser.getId());
			  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", tParamClassManage);
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
	 /**
	  * 删除参数分类(特医食品分类/规范指南分类)数据
	  * @param iDcond
	  * @param token
	  * @return
	  */
	  @ApiOperation(value = "删除参数分类(特医食品分类/规范指南分类)数据")
	  @RequestMapping(value = "deleteOneParamClass", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData deleteOneParamClass(@RequestBody IDcond iDcond,@RequestHeader String token) {
		 TSysUser curUser=(TSysUser) redisUtil.get(token);
		  try {
			  int classId=iDcond.getId();
			  tParamClassManageService.deleteTParamClassManage(classId,curUser.getId());
			 appResultData=new AppResultData(APPResultCommom.SUCCESS, APPResultCommom.DELETE_SUCCESS, null);
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
	 /**
	  * 获取参数分类(特医食品分类/规范指南分类)信息
	  * @param iDcond
	  * @param token
	  * @return
	  */
	  @ApiOperation(value = "获取参数分类(特医食品分类/规范指南分类)信息")
	  @RequestMapping(value = "findOneParamClass", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData findOneParamClass(@RequestBody IDcond iDcond,@RequestHeader String token) {
		  try {
			  int newsId=iDcond.getId();
			  TParamClassManage tParamClassManage=tParamClassManageService.findById(newsId);	
			  if(tParamClassManage.getId()>0) {
				  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", tParamClassManage);
			  }else{
				  appResultData=new AppResultData(APPResultCommom.GETLIST_FAIL, APPResultCommom.GETLIST_FAIL_ERROR_MSG, null); 
			     return appResultData;
			  }
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
		 /**
		  * 获取参数分类(特医食品分类/规范指南分类)信息
		  * @param iDcond
		  * @param token
		  * @return
		  */
	  @ApiOperation(value = "获取对应类别的参数分类(特医食品分类/规范指南分类)信息（不分页）")
	  @RequestMapping(value = "getParamClasss", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData getParamClasss(@RequestBody TypeCond typeCond,@RequestHeader String token) {
		Map<String, Object> map=new HashMap<>();
		  try {
			  String classType=typeCond.getType();
			  TParamClassManage tParamClassManageParam=new TParamClassManage();
			  tParamClassManageParam.setClassType(classType);
			  List<TParamClassManage> tParamClassManages=tParamClassManageService.geTParamClassManages(tParamClassManageParam);	
			  map.put("list", tParamClassManages);
			  if(tParamClassManages!=null&&tParamClassManages.size()>0) {
				  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", map);
			  }else{
				  appResultData=new AppResultData(APPResultCommom.GETLIST_FAIL, APPResultCommom.GETLIST_FAIL_ERROR_MSG, null); 
			     return appResultData;
			  }
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
	/**
	 * 分页查询参数分类(特医食品分类/规范指南分类)信息列表
	 * @param nameCond
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "分页查询参数分类(特医食品分类/规范指南分类)信息列表")
	  @RequestMapping(value = "getParamClasssOnPage", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData getParamClasssOnPage(@RequestBody GetParamClasssOnPageCond getParamClasssOnPageCond ,@RequestHeader String token) {
		
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  try {
			  TSysUser tSysUser=tSysUserService.findById(curUser.getId());
			  List<Integer> curUserIds=tSysUserService.getUsersByRightListOnAffiliateIdId(tSysUser.getAffiliateId());
			  PageBean<TParamClassManage> pageBean=tParamClassManageService.getParamClasssOnPage(getParamClasssOnPageCond,curUserIds);
			  List<TParamClassManage> list=pageBean.getPageData();
			  if(list.size()>0) {
				  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", pageBean);
			  }else{
				  appResultData=new AppResultData(APPResultCommom.GETLIST_FAIL, APPResultCommom.GETLIST_FAIL_ERROR_MSG, pageBean);
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
