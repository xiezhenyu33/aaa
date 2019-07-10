package com.parko.zkcenter.controller.zkcenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.parko.zkcenter.entity.cond.AddOneEventCond;
import com.parko.zkcenter.entity.cond.FileCond;
import com.parko.zkcenter.entity.cond.GetEventsOnPageCond;
import com.parko.zkcenter.entity.cond.UpdateReportStatusCond;
import com.parko.zkcenter.entity.zkcenter.TAdverseEventsReporting;
import com.parko.zkcenter.service.back.TFileUrlRelService;
import com.parko.zkcenter.service.zkcenter.TAdverseEventsReportingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 不良事件上报相关业务处理接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/qualityControl/events")
@Api(tags = "不良事件上报相关业务处理接口")
public class AdverseEventsReportingController  {

	@Autowired
	private TAdverseEventsReportingService tAdverseEventsReportingService;// 不良事件上报数据业务处理接口
	
	@Autowired
	private TFileUrlRelService tFileUrlRelService;//附件路径关联数据业务处理接口
	
	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	@Autowired
	 RedisUtil redisUtil;
	 
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 生成发送通知数据
	 * @param addChecksCond
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "保存不良事件上报数据")
	  @RequestMapping(value = "addOneEvent", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData addoneEvent(@RequestBody AddOneEventCond addOneEventCond,@RequestHeader String token) {
		
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  TAdverseEventsReporting tAdverseEventsReporting=addOneEventCond.getTAdverseEventsReporting();
		  int eventId=tAdverseEventsReporting.getId();
		  if(eventId==0) {//新增操作
			  tAdverseEventsReporting.setCreateUserName(curUser.getUserName());
		  }else {
			//组织更新数据
			  TAdverseEventsReporting tAdverseEventsReporting2=tAdverseEventsReportingService.findById(eventId);
			  tAdverseEventsReporting.setCreateUser(tAdverseEventsReporting2.getCreateUser());
			  tAdverseEventsReporting.setCreateUserName(tAdverseEventsReporting2.getCreateUserName()); 
			  String reportStatus=tAdverseEventsReporting.getReportStatus();
			  if("0".equals(reportStatus)) {//待公示状态数据，意味着完成上报提交，更改上报时间为当前时间
				  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				  tAdverseEventsReporting.setCreateTime(df.format(new Date()));
			  }else {
				  tAdverseEventsReporting.setCreateTime(tAdverseEventsReporting2.getCreateTime());
			  }
		  }
		  List<TFileUrlRel> tFileUrlRels=new ArrayList<>();
		  try {
			  //处理机构id
			 curUser=tSysUserService.findById(curUser.getId());
			  tAdverseEventsReporting.setAffiliateId(curUser.getAffiliateId());
			  tAdverseEventsReporting=tAdverseEventsReportingService.saveTAdverseEventsReporting(tAdverseEventsReporting, curUser.getId());
			   if(tAdverseEventsReporting.getId()>0) {
					  //处理对应的路径数据
				       List<FileCond> fileUrls=addOneEventCond.getFileUrls();
				     //附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理 3 轮播图 4 抽检通知 5 评分细则 6 不良事件
					  String urlType="6";
					  tFileUrlRels=tFileUrlRelService.dealTFileUrlRels(fileUrls,tAdverseEventsReporting.getId(),urlType);
			   }
			   appResultData=new AppResultData(APPResultCommom.SUCCESS, "", tAdverseEventsReporting);
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
	  /**
		 * 获取表详情数据
		 * @param iDcond
		 * @param token
		 * @return
		 */
	  @ApiOperation(value = "获取不良事件详情数据")
	  @RequestMapping(value = "getOneEvent", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData getOneEvent(@RequestBody IDcond iDcond, @RequestHeader String token) {
		  Map<String, Object> map=new HashMap<>();
		  try {
			  TAdverseEventsReporting tAdverseEventsReporting=tAdverseEventsReportingService.findById(iDcond.getId());
			  if(tAdverseEventsReporting.getId()>0) {
				  //附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理 3 轮播图 4 抽检通知 5 评分细则 6 不良事件
				  String urlType="6";
				  List<FileCond> fileUrls= tFileUrlRelService.getFileUrlsByRelId(tAdverseEventsReporting.getId(),urlType);
				  map.put("tAdverseEventsReporting", tAdverseEventsReporting);
				  map.put("tFileUrlRels", fileUrls);
				  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", map);
			  }else {
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
	  * 更改不良事件状态
	  * @param updateReportStatusCond
	  * @param token
	  * @return
	  */
	  @ApiOperation(value = "更改不良事件状态")
	  @RequestMapping(value = "updateEventStatus", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData updateEventStatus(@RequestBody UpdateReportStatusCond updateReportStatusCond,@RequestHeader String token) {
		 TSysUser curUser=(TSysUser) redisUtil.get(token);
		  try {
			  int eventId=updateReportStatusCond.getReportId();
			  TAdverseEventsReporting tAdverseEventsReporting=tAdverseEventsReportingService.findById(eventId);
			  tAdverseEventsReporting.setReportStatus(updateReportStatusCond.getReportStatus());
			  tAdverseEventsReporting=tAdverseEventsReportingService.saveTAdverseEventsReporting(tAdverseEventsReporting, curUser.getId());
			 appResultData=new AppResultData(APPResultCommom.SUCCESS, "", tAdverseEventsReporting);
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
	/**
	 * 分页查询不良事件上报数据列表
	 * @param getReportsOnPageCond
	 * @param token
	 * @return
	 */
		  @ApiOperation(value = "分页查询不良事件上报数据列表")
		  @RequestMapping(value = "getEventsOnPage", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData getEventsOnPage(@RequestBody GetEventsOnPageCond getEventsOnPageCond ,@RequestHeader String token) {
			
			  TSysUser curUser=(TSysUser) redisUtil.get(token);  
			  try {
				  PageBean<Map<String, Object>> pageBean=tAdverseEventsReportingService.getEventsOnPage(getEventsOnPageCond,curUser.getId());
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
