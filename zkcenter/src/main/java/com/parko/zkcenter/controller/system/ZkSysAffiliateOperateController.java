package com.parko.zkcenter.controller.system;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.parko.system.entity.cond.sys.AddDeptCond;
import com.parko.system.entity.cond.sys.AddOneAffiliateCond;
import com.parko.system.entity.cond.sys.GetTAffiliatesOnPageCond;
import com.parko.system.entity.pojo.APPResultCommom;
import com.parko.system.entity.pojo.AppResultData;
import com.parko.system.entity.pojo.PageBean;
import com.parko.system.entity.sys.TAffiliate;
import com.parko.system.entity.sys.TAffiliateDept;
import com.parko.system.entity.sys.TSysUser;
import com.parko.system.service.sys.TAffiliateDeptService;
import com.parko.system.service.sys.TAffiliateService;
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.JwtTokenUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 后台机构系统相关业务接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/zk/sys/affiliate")
@Api(tags = "后台机构系统相关业务接口")
public class ZkSysAffiliateOperateController {

	@Autowired
	private TAffiliateService tAffiliateService;//系统机构业务处理接口
	
	@Autowired
	private TAffiliateDeptService tAffiliateDeptService;//机构科室关系数据业务处理接口
	
	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	 @Autowired
	 RedisUtil redisUtil;
	 
	 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 添加机构数据
	 * @param tAffiliate
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "添加机构数据")
	  @RequestMapping(value = "addOneAffiliate", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData addOneAffiliate(@RequestBody AddOneAffiliateCond addOneAffiliateCond,@RequestHeader String token) {
		
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  TAffiliate tAffiliate=addOneAffiliateCond.getTaffiliate();
		  int parentAffiliateId=tAffiliate.getParentAffiliateId();
		  int oldAffiliateId=tAffiliate.getId();
		  String affiliateName=tAffiliate.getAffiliateName();
		  TAffiliate tAffiliateParam=new TAffiliate();
		  tAffiliateParam.setAffiliateName(affiliateName);
		  TAffiliate tAffiliate2=tAffiliateService.getOneAffiliate(tAffiliateParam);
		  if(oldAffiliateId==0) {//新增操作，校验机构名称是否重复
			  if(tAffiliate2.getId()>0) {//已经存在相同的机构名称，提示已存在
				  appResultData=new AppResultData(APPResultCommom.AFFILIATE_REPEAT_ERROR, APPResultCommom.AFFILIATE_REPEAT_ERROR_MSG, affiliateName);
				  return appResultData;
			  }
			  tAffiliate.setCreateUserName(curUser.getUserName());
		  }else {//更新操作，判断两个机构名相同的数据id是否一致
			  if(tAffiliate.getId()!=tAffiliate2.getId()) {//id不一致，则存在重复数据
				  appResultData=new AppResultData(APPResultCommom.AFFILIATE_REPEAT_ERROR, APPResultCommom.AFFILIATE_REPEAT_ERROR_MSG, affiliateName);
				  return appResultData;
			  }
		  }
		  List<TAffiliateDept> tAffiliateDepts=new ArrayList<>();
		  try {
			  tAffiliate=tAffiliateService.saveAffiliate(tAffiliate,curUser.getId());
			  if(tAffiliate.getId()>0) {//机构添加成功，添加对应的科室数据
				  List<AddDeptCond> addDeptConds=addOneAffiliateCond.getAddDeptConds();
				 if(addDeptConds.size()>0) {//存在需要保存的科室数据
					 //现将机构科室中机构所有的科室数据删除
					 int affiliateId=tAffiliate.getId();
					 TAffiliateDept tAffiliateDept=new TAffiliateDept();
					 tAffiliateDept.setAffiliateId(affiliateId);
					 tAffiliateDeptService.deleteTAffiliateDepts(tAffiliateDept);
					 //保存机构科室数据关系
					 for(AddDeptCond addDeptCond:addDeptConds) {
						 TAffiliateDept tAffiliateDeptParam=new TAffiliateDept();
						 tAffiliateDeptParam.setAffiliateId(affiliateId);
						 tAffiliateDeptParam.setDeptId(addDeptCond.getDeptId());
						 tAffiliateDeptParam.setDeptName(addDeptCond.getDeptName());
						 tAffiliateDepts.add(tAffiliateDeptParam);
					 }
					 if(tAffiliateDepts.size()>0) {
						 tAffiliateDepts=tAffiliateDeptService.saveAll(tAffiliateDepts);
					 }
				 }
			  }
			  Map<String, Object> map=new HashMap<>();
			  map.put("tAffiliate", tAffiliate);
			  map.put("AffiliateDept", tAffiliateDepts);
			  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", map);
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
	/**
	 * 获取所有的机构数据列表（不分页）
	 * @param token
	 * @return
	 */
		  @ApiOperation(value = "获取所有的机构数据列表（不分页）")
		  @RequestMapping(value = "geTAffiliates", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData geTAffiliates(@RequestHeader String token) {
			
			  Map<String, Object> map=new HashMap<>();
			  TSysUser tSysUser=(TSysUser) redisUtil.get(token);
			  try {
				  int userId=tSysUser.getId();
				  tSysUser=tSysUserService.findById(userId);
				  TAffiliate tAffiliate=new TAffiliate();
				  tAffiliate.setId(tSysUser.getAffiliateId());
				  List<TAffiliate> affiliates=tAffiliateService.geTAffiliates(tAffiliate);
				  map.put("list", affiliates);
				  if(affiliates!=null&&affiliates.size()>0) {
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
		/**
		 * 获取单条机构详情数据
		 * @param iDcond
		 * @param token
		 * @return
		 */
		  @ApiOperation(value = "获取单条机构详情数据")
		  @RequestMapping(value = "geTAffiliateDetails", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData geTAffiliateDetails(@RequestBody IDcond iDcond ,@RequestHeader String token) {
			
			  Map<String, Object> map=new HashMap<>();
			  try {
				  int affiliateId=iDcond.getId();
				  Map<String, Object> tAffiliateMap=tAffiliateService.findByIdOnSql(affiliateId);
				  TAffiliateDept tAffiliateDept=new TAffiliateDept();
					 tAffiliateDept.setAffiliateId(affiliateId);
				  List<TAffiliateDept> tAffiliateDepts=tAffiliateDeptService.getTAffiliateDepts(tAffiliateDept);	 
				  map.put("affiliate", tAffiliateMap);
				  map.put("depts", tAffiliateDepts);
				  if(tAffiliateMap!=null) {
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
		  /**
			 * 获取单条机构详情数据
			 * @param iDcond
			 * @param token
			 * @return
			 */
			  @ApiOperation(value = "删除单条机构数据")
			  @RequestMapping(value = "deleteTAffiliate", method = RequestMethod.POST)
			  @JwtTokenUtil.UserLoginToken
			   public AppResultData deleteTAffiliate(@RequestBody IDcond iDcond ,@RequestHeader String token) {
				
				  TSysUser tSysUser=(TSysUser) redisUtil.get(token);
				  try {
					  int affiliateId=iDcond.getId();
					  //首先判断该机构下是否还存在用户
					  TSysUser tSysUserParam=new TSysUser();
					  tSysUserParam.setAffiliateId(affiliateId);
					  List<TSysUser> tSysUsers=tSysUserService.geTSysUsers(tSysUserParam);
					  if(tSysUsers!=null&&tSysUsers.size()>0) {//已经存在对应的用户数据，提示不能删除
						  appResultData=new AppResultData(APPResultCommom.AFFILIATE_CANNOT_DELETE_ERROR,APPResultCommom.AFFILIATE_CANNOT_DELETE_ERROR_MSG, null);
						 return appResultData;
					  }
					  //执行删除机构-科室对应关系操作
					  TAffiliateDept tAffiliateDept=new TAffiliateDept();
					  tAffiliateDept.setAffiliateId(affiliateId);
					  tAffiliateDeptService.deleteTAffiliateDepts(tAffiliateDept);
					  //根据主键删除机构数据
					  tAffiliateService.deleteAffiliateById(affiliateId,tSysUser.getId());
					  appResultData=new AppResultData(APPResultCommom.SUCCESS, APPResultCommom.DELETE_SUCCESS, null);
				} catch (Exception e) {
					e.printStackTrace();
					 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
					 return appResultData;
				}
				  return appResultData;
			    }  
			 /**
			  * 分页查询机构数据列表
			  * @param getTAffiliatesOnPageCond
			  * @param token
			  * @return
			  */
				  @ApiOperation(value = "分页查询机构数据列表")
				  @RequestMapping(value = "getTAffiliatesOnPage", method = RequestMethod.POST)
				  @JwtTokenUtil.UserLoginToken
				   public AppResultData getTAffiliatesOnPage(@RequestBody GetTAffiliatesOnPageCond getTAffiliatesOnPageCond ,@RequestHeader String token) {
					TSysUser tSysUser=(TSysUser) redisUtil.get(token);
					  try {
						  int userId=tSysUser.getId();
						  tSysUser=tSysUserService.findById(userId);
						  getTAffiliatesOnPageCond.setCurAffiliateId(tSysUser.getAffiliateId());
						  PageBean<Map<String, Object>> pageBean=tAffiliateService.getTAffiliatesOnPage(getTAffiliatesOnPageCond);
						  List<Map<String, Object>> list=pageBean.getPageData();
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
