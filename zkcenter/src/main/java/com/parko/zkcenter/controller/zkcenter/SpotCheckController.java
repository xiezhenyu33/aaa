package com.parko.zkcenter.controller.zkcenter;

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
import com.parko.system.entity.pojo.APPResultCommom;
import com.parko.system.entity.pojo.AppResultData;
import com.parko.system.entity.pojo.CommomPage;
import com.parko.system.entity.pojo.PageBean;
import com.parko.system.entity.sys.TAffiliate;
import com.parko.system.entity.sys.TSysUser;
import com.parko.system.service.sys.TAffiliateService;
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.JwtTokenUtil;
import com.parko.zkcenter.entity.TFileUrlRel;
import com.parko.zkcenter.entity.cond.AddChecksCond;
import com.parko.zkcenter.entity.cond.FileCond;
import com.parko.zkcenter.entity.zkcenter.TSpotCheck;
import com.parko.zkcenter.service.back.TFileUrlRelService;
import com.parko.zkcenter.service.zkcenter.TSpotCheckService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 医院抽检相关业务处理接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/qualityControl/checks")
@Api(tags = "医院抽检相关业务处理接口")
public class SpotCheckController {

	@Autowired
	private TSpotCheckService tSpotCheckService;//医院抽检实体（消息）业务处理接口
	
	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	@Autowired
	private TFileUrlRelService tFileUrlRelService;//附件路径关联数据业务处理接口
	
	@Autowired
	private TAffiliateService tAffiliateService;//系统机构业务处理接口
	
	@Autowired
	 RedisUtil redisUtil;
	 
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 生成发送通知数据
	 * @param addChecksCond
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "生成发送通知数据")
	  @RequestMapping(value = "addChecks", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData addChecks(@RequestBody AddChecksCond addChecksCond,@RequestHeader String token) {
		
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  TSpotCheck tSpotCheck=addChecksCond.getTSpotCheck();
		  int chekId=tSpotCheck.getId();
		  if(chekId==0) {//新增操作
			  tSpotCheck.setCreateUserName(curUser.getUserName());
		  }
		  List<TSpotCheck> tSpotChecks=new ArrayList<>();
		  List<TFileUrlRel> tFileUrlRels=new ArrayList<>();
		  String affliateNames="";
		  List<Integer > failAffiliateIds=new ArrayList<>();
		  try {
			  //首先获取该通知对应的机构id，获取该机构下所有的医生用户数据
			  List<Integer> affliateIds=addChecksCond.getAffliateIds();
			  if(affliateIds!=null&&affliateIds.size()>0) {//医院id是批量的，通知也是批量的 
				  for(int affiliateId:affliateIds){
					  TSysUser tSysUserParam=new TSysUser();
					  tSysUserParam.setAffiliateId(affiliateId);
					  List<TSysUser> tSysUsers=tSysUserService.geTSysUsers(tSysUserParam);
					  if(tSysUsers!=null&&tSysUsers.size()>0) {//该机构下存在相对应的用户数据
						  for(TSysUser tSysUser:tSysUsers) {
							  TSpotCheck tSpotCheckParam=new TSpotCheck();
							  tSpotCheckParam.setCheckStatus("1");
							  tSpotCheckParam.setUserId(tSysUser.getId());
							  tSpotCheckParam.setAffiliateId(affiliateId);
							  tSpotCheckParam.setTitle(tSpotCheck.getTitle());
							  tSpotCheckParam.setContext(tSpotCheck.getContext());
							  tSpotChecks.add(tSpotCheckParam);
						  }
						if(tSpotChecks.size()>0) {//批量保存用户数据
							tSpotChecks=tSpotCheckService.saveAll(tSpotChecks,curUser.getId());
							//处理对应的路径数据
							for (TSpotCheck t:tSpotChecks) {
								if(t.getId()>0) {
									  List<FileCond> fileUrls=addChecksCond.getFileUrls();
									  String urlType="4";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理 3 轮播图 4 抽检通知 5 评分细则
									  tFileUrlRels=tFileUrlRelService.dealTFileUrlRels(fileUrls,t.getId(),urlType);
								}
							}
						}  
						
					  }else {//该机构下还未存在相应的用户数据
						  failAffiliateIds.add(affiliateId);
					  }
				  }
				  String error="";
                 if(failAffiliateIds.size()>0) {//处理还未存在相应的用户数据的机构信息
                	 for(int affiliateId:failAffiliateIds) {
                		 TAffiliate tAffiliate=tAffiliateService.findById(affiliateId);
                		 affliateNames=affliateNames+tAffiliate.getAffiliateName()+", ";
                	 }
                	 error=affliateNames+"等"+APPResultCommom.AFFILIATE_NOT_USER_ERROR_MSG;
                 }
                 appResultData=new AppResultData(APPResultCommom.SUCCESS, error, null);
			  }else {//参数传递异常
				  appResultData=new AppResultData(APPResultCommom.PARAM_FAIL,APPResultCommom.PARAM_FAIL_MSG, null);
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
	  * 获取用户未读的抽检消息数据条数
	  * @param token
	  * @return
	  */
	  @ApiOperation(value = "获取用户未读的抽检消息数据条数")
	  @RequestMapping(value = "findChecksCount", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData findChecksCount(@RequestHeader String token) {
		 Map<String, Object> map=new HashMap<>();
		 TSysUser tSysUser=(TSysUser) redisUtil.get(token);
		 int count=0;
		  try {
			  int userId=tSysUser.getId();
			  TSpotCheck tSpotCheckParam=new TSpotCheck();
			  tSpotCheckParam.setCheckStatus("1");
			  tSpotCheckParam.setUserId(userId);
			  List<TSpotCheck> tSpotChecks=tSpotCheckService.geTSpotChecks(tSpotCheckParam);
				if(tSpotChecks!=null&&tSpotChecks.size()>0) {
					count=tSpotChecks.size();
				}
				map.put("count", count);
				 appResultData=new AppResultData(APPResultCommom.SUCCESS, "", map);
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
	 /**
	  * 分页查询用户医院抽检消数据列表
	  * @param commomPage
	  * @param token
	  * @return
	  */
			  @ApiOperation(value = "分页查询用户医院抽检消数据列表")
			  @RequestMapping(value = "getUsersSpotCheckOnPage", method = RequestMethod.POST)
			  @JwtTokenUtil.UserLoginToken
		   public AppResultData getUsersSpotCheckOnPage(@RequestBody CommomPage commomPage ,@RequestHeader String token) {
				  TSysUser tSysUser=(TSysUser) redisUtil.get(token);
			  try {
				  PageBean<Map<String, Object>> pageBean=tSpotCheckService.getUsersSpotCheckOnPage(commomPage,tSysUser.getId());
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
		 /**
		  * 获取单个抽检信息
		  * @param iDcond
		  * @param token
		  * @return
		  */
			  @ApiOperation(value = "获取单个抽检信息")
			  @RequestMapping(value = "findOnechecks", method = RequestMethod.POST)
			  @JwtTokenUtil.UserLoginToken
			   public AppResultData findOnechecks(@RequestBody IDcond iDcond,@RequestHeader String token) {
				
				  TSysUser tSysUser=(TSysUser) redisUtil.get(token);
				  Map<String, Object> map=new HashMap<>();
				  try {
					  int checkId=iDcond.getId();
					  TSpotCheck tSpotCheck=tSpotCheckService.findById(checkId);
					  //更新抽检消息为已读
					  tSpotCheck.setCheckStatus("0");
					  tSpotCheck=tSpotCheckService.saveTSpotCheck(tSpotCheck, tSysUser.getId());
					  //获取对应的附件url列表
					  String urlType="4";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理 3 轮播图 4 抽检通知
					  List<FileCond> fileUrls= tFileUrlRelService.getFileUrlsByRelId(checkId,urlType);
					  map.put("tSpotCheck", tSpotCheck);
					  map.put("tFileUrlRels", fileUrls);
					  if(tSpotCheck.getId()>0) {
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
}
