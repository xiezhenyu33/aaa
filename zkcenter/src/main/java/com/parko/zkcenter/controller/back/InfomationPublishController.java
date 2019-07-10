package com.parko.zkcenter.controller.back;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.MultipartConfig;

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
import com.parko.zkcenter.entity.TFileUrlRel;
import com.parko.zkcenter.entity.back.TInformationPublish;
import com.parko.zkcenter.entity.back.TNewsManage;
import com.parko.zkcenter.entity.cond.AddOneInfoCond;
import com.parko.zkcenter.entity.cond.FileCond;
import com.parko.zkcenter.entity.cond.GetInfosOnPageCond;
import com.parko.zkcenter.service.back.TFileUrlRelService;
import com.parko.zkcenter.service.back.TInformationPublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 后台管理-信息资料发布业务控制层
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/qualityControl/back")
@Api(tags = "信息资料发布业务控制层")
public class InfomationPublishController {
      
	@Autowired
	private TInformationPublishService tInformationPublishService;//信息资料发布数据业务处理接口
	
	@Autowired
	private TFileUrlRelService tFileUrlRelService;//附件路径关联数据业务处理接口
	
	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	 @Autowired
	 RedisUtil redisUtil;
	 
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 添加信息资料发布数据
	 * @param addOneInfoCond
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "添加信息资料发布数据")
	  @RequestMapping(value = "addOneInfo", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData addOneInfo(@RequestBody AddOneInfoCond addOneInfoCond,@RequestHeader String token) {
		
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  TInformationPublish tInformationPublish=addOneInfoCond.getTInformationPublish();
		  int infoId=tInformationPublish.getId();
		  if(infoId==0) {//新增操作
			  tInformationPublish.setCreateUserName(curUser.getUserName());
		  }else {//更新，需要组织更新数据
			  TInformationPublish tInformationPublish2=tInformationPublishService.findById(infoId);
			  tInformationPublish.setCreateUser(tInformationPublish2.getCreateUser());
			  tInformationPublish.setCreateUserName(tInformationPublish2.getCreateUserName());
			  tInformationPublish.setCreateTime(tInformationPublish2.getCreateTime());
		  }
		  List<TFileUrlRel> tFileUrlRels=new ArrayList<>();
		  try {
			  tInformationPublish=tInformationPublishService.saveTInformationPublish(tInformationPublish,curUser.getId());
			  if(tInformationPublish.getId()>0) {//信息添加成功，添加对应的信息-附件路径关联数据
				  List<FileCond> fileUrls=addOneInfoCond.getFileUrls();
				  String infoMetType=tInformationPublish.getInfoMetType();// 0 指南/规范 1特医食品
				  String urlType="";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理
				  if("0".equals(infoMetType)) {
					  urlType="0";
				  }else if ("1".equals(infoMetType)) {
					  urlType="1";
				  }
				  tFileUrlRels=tFileUrlRelService.dealTFileUrlRels(fileUrls,tInformationPublish.getId(),urlType);
			  }
			  Map<String, Object> map=new HashMap<>();
			  map.put("tInformationPublish", tInformationPublish);
			  map.put("tFileUrlRels", tFileUrlRels);
			  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", map);
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
		/**
		 * 删除资料信息发布数据
		 * @param iDcond
		 * @param token
		 * @return
		 */
		  @ApiOperation(value = "删除资料信息发布数据")
		  @RequestMapping(value = "deleteOneInfo", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData deleteOneInfo(@RequestBody IDcond iDcond,@RequestHeader String token) {
			 TSysUser curUser=(TSysUser) redisUtil.get(token);
			  try {
				  int infoId=iDcond.getId();
				  TInformationPublish tInformationPublish=tInformationPublishService.findById(iDcond.getId());
				  //获取对应的附件url列表
				  String infoMetType=tInformationPublish.getInfoMetType();// 0 指南/规范 1特医食品
				  String urlType="";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理
				  if("0".equals(infoMetType)) {
					  urlType="0";
				  }else if ("1".equals(infoMetType)) {
					  urlType="1";
				  }
				  //先将信息-附件路径关联删除
					 tFileUrlRelService.deleteTSysRoleMenus(infoId,urlType);
				 //删除对应的资料信息发布数据
					 tInformationPublishService.deleteOneInfo(infoId,curUser.getId() );
				 appResultData=new AppResultData(APPResultCommom.SUCCESS, APPResultCommom.DELETE_SUCCESS, null);
			} catch (Exception e) {
				e.printStackTrace();
				 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
				 return appResultData;
			}
			  return appResultData;
		    }
	/**
	 * 获取单个资料发布信息
	 * @param iDcond
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "获取单个资料发布信息")
	  @RequestMapping(value = "findOneInfo", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData findOneInfo(@RequestBody IDcond iDcond,@RequestHeader String token) {
		 Map<String, Object> map=new HashMap<>();
		  try {
			  TInformationPublish tInformationPublish=tInformationPublishService.findById(iDcond.getId());
			  //获取对应的附件url列表
			  String infoMetType=tInformationPublish.getInfoMetType();// 0 指南/规范 1特医食品
			  String urlType="";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理
			  if("0".equals(infoMetType)) {
				  urlType="0";
			  }else if ("1".equals(infoMetType)) {
				  urlType="1";
			  }
			  List<FileCond> fileUrls= tFileUrlRelService.getFileUrlsByRelId(tInformationPublish.getId(),urlType);
			  map.put("tInformationPublish", tInformationPublish);
			  map.put("tFileUrlRels", fileUrls);
			  if(tInformationPublish.getId()>0) {
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
	   * 分页查询资料信息发布数据列表
	   * @param getInfosOnPageCond
	   * @param token
	   * @return
	   */
		  @ApiOperation(value = "分页查询资料信息发布数据列表")
		  @RequestMapping(value = "getInfosOnPage", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData getInfosOnPage(@RequestBody GetInfosOnPageCond getInfosOnPageCond ,@RequestHeader String token) {
			
			  TSysUser curUser=(TSysUser) redisUtil.get(token);
			  try {
				  TSysUser tSysUser=tSysUserService.findById(curUser.getId());
				  String rightSql=tSysUserService.getUsersByRightOnAffiliateIdId(tSysUser.getAffiliateId());
				  PageBean<Map<String, Object>> pageBean=tInformationPublishService.getInfosOnPage(getInfosOnPageCond,rightSql);
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
