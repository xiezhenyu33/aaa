package com.parko.zkcenter.controller.back;

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
import com.parko.system.entity.pojo.PageBean;
import com.parko.system.entity.sys.TSysUser;
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.JwtTokenUtil;
import com.parko.zkcenter.entity.TFileUrlRel;
import com.parko.zkcenter.entity.back.TNewsManage;
import com.parko.zkcenter.entity.cond.AddOneNewsCond;
import com.parko.zkcenter.entity.cond.FileCond;
import com.parko.zkcenter.entity.cond.GetNewsOnPageCond;
import com.parko.zkcenter.service.back.TFileUrlRelService;
import com.parko.zkcenter.service.back.TNewsManageService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 后台新闻管理业务控制层
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/qualityControl/news")
@Api(tags = "后台新闻管理业务控制层")
public class NewsManageController {

	@Autowired
	private TNewsManageService tNewsManageService;//新闻管理数据业务处理接口
	
	@Autowired
	private TFileUrlRelService tFileUrlRelService;//附件路径关联数据业务处理接口
	
	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	 @Autowired
	 RedisUtil redisUtil;
	 
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 添加新闻发布数据
	 * @param addOneNewsCond
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "添加新闻发布数据")
	  @RequestMapping(value = "addOneNews", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData addOneNews(@RequestBody AddOneNewsCond addOneNewsCond,@RequestHeader String token) {
		
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  TNewsManage tNewsManage=addOneNewsCond.getTNewsManage();
		  int newId=tNewsManage.getId();
		  if(newId==0) {//新增操作
			  tNewsManage.setCreateUserName(curUser.getUserName());
		  }else {//更新，需要组织更新数据
			  TNewsManage tNewsManage2=tNewsManageService.findById(newId);
			  tNewsManage.setCreateUser(tNewsManage2.getCreateUser());
			  tNewsManage.setCreateUserName(tNewsManage2.getCreateUserName());
			  tNewsManage.setCreateTime(tNewsManage2.getCreateTime());
		  }
		  List<TFileUrlRel> tFileUrlRels=new ArrayList<>();
		  try {
			  tNewsManage=tNewsManageService.saveTNewsManage(tNewsManage,curUser.getId());
			  if(tNewsManage.getId()>0) {//信息添加成功，添加对应的信息-附件路径关联数据
				  List<FileCond> fileUrls=addOneNewsCond.getFileUrls();
				  String urlType="2";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理
				  tFileUrlRels=tFileUrlRelService.dealTFileUrlRels(fileUrls,tNewsManage.getId(),urlType);
			  }
			  Map<String, Object> map=new HashMap<>();
			  map.put("tNewsManage", tNewsManage);
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
		 * 删除新闻数据
		 * @param iDcond
		 * @param token
		 * @return
		 */
		  @ApiOperation(value = "删除新闻数据")
		  @RequestMapping(value = "deleteOneNews", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData deleteOneNews(@RequestBody IDcond iDcond,@RequestHeader String token) {
			 TSysUser curUser=(TSysUser) redisUtil.get(token);
			  try {
				  int newsId=iDcond.getId();
				  //先将信息-附件路径关联删除
					  String urlType="2";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理
					 tFileUrlRelService.deleteTSysRoleMenus(newsId,urlType);
				 //删除对应的资料信息发布数据
					 tNewsManageService.deleteById(newsId,curUser.getId());
				 appResultData=new AppResultData(APPResultCommom.SUCCESS, APPResultCommom.DELETE_SUCCESS, null);
			} catch (Exception e) {
				e.printStackTrace();
				 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
				 return appResultData;
			}
			  return appResultData;
		    }
		 /**
		  * 获取单个新闻信息
		  * @param iDcond
		  * @param token
		  * @return
		  */
			  @ApiOperation(value = "获取单个新闻信息")
			  @RequestMapping(value = "findOneNews", method = RequestMethod.POST)
			  @JwtTokenUtil.UserLoginToken
			   public AppResultData findOneNews(@RequestBody IDcond iDcond,@RequestHeader String token) {
				 Map<String, Object> map=new HashMap<>();
				  try {
					  int newsId=iDcond.getId();
					  TNewsManage tNewsManage=tNewsManageService.findById(newsId);
					  //获取对应的附件url列表
					  String urlType="2";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理
					  List<FileCond> fileUrls= tFileUrlRelService.getFileUrlsByRelId(newsId,urlType);
					  map.put("tNewsManage", tNewsManage);
					  map.put("tFileUrlRels", fileUrls);
					  if(tNewsManage.getId()>0) {
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
	   * 分页查询新闻数据列表
	   * @param getNewsOnPageCond
	   * @param token
	   * @return
	   */
	  @ApiOperation(value = "分页查询新闻数据列表")
	  @RequestMapping(value = "getNewsOnPage", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData getNewsOnPage(@RequestBody GetNewsOnPageCond getNewsOnPageCond ,@RequestHeader String token) {
		
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  try {
			  TSysUser tSysUser=tSysUserService.findById(curUser.getId());
			  List<Integer> curUserIds=tSysUserService.getUsersByRightListOnAffiliateIdId(tSysUser.getAffiliateId());
			  PageBean<TNewsManage> pageBean=tNewsManageService.getNewsOnPage(getNewsOnPageCond,curUserIds);
			  List<TNewsManage> list=pageBean.getPageData();
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
