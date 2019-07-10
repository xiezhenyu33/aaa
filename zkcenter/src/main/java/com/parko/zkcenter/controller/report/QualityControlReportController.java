package com.parko.zkcenter.controller.report;

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
import com.parko.zkcenter.entity.cond.FileCond;
import com.parko.zkcenter.entity.cond.GetReportsOnPageCond;
import com.parko.zkcenter.entity.cond.SaveQualityControlReportCond;
import com.parko.zkcenter.entity.cond.UpdateReportStatusCond;
import com.parko.zkcenter.entity.report.TQualityControlReport;
import com.parko.zkcenter.service.back.TFileUrlRelService;
import com.parko.zkcenter.service.report.TQualityControlReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 质控报表相关业务处理控制
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/qualityControl/report")
@Api(tags = "质控报表相关业务处理控制")
public class QualityControlReportController {

	@Autowired
	private TQualityControlReportService tQualityControlReportService;// 质控报表数据业务处理接口
	
	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	@Autowired
	private TFileUrlRelService tFileUrlRelService;//附件路径关联数据业务处理接口
	
	 @Autowired
	 RedisUtil redisUtil;
	 
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 保存广东省综合医院摸底调查表
	 * @param tQualityControlReport
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "保存广东省综合医院摸底调查表")
	  @RequestMapping(value = "saveQualityControlReport", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData saveQualityControlReport(@RequestBody SaveQualityControlReportCond saveQualityControlReportCond ,@RequestHeader String token) {
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  TQualityControlReport tQualityControlReport=saveQualityControlReportCond.getTQualityControlReport();
		  int reportId=tQualityControlReport.getId();
		  if(reportId==0) {//新增
			  tQualityControlReport.setCreateUserName(curUser.getUserName());
			  String reportName="";
			  String reportType=tQualityControlReport.getReportType();
			  if("0".equals(reportType)) {
				  reportName=APPResultCommom.REPORTNAME_0;
			  }else  if("1".equals(reportType)) {
				  reportName=APPResultCommom.REPORTNAME_1;
			  }else  if("2".equals(reportType)) {
				  reportName=APPResultCommom.REPORTNAME_2;
			  }else  if("3".equals(reportType)) {
				  reportName=APPResultCommom.REPORTNAME_3;
			  }
			  tQualityControlReport.setReportName(reportName);
		  }else {//组织更新数据
			  TQualityControlReport tQualityControlReport2=tQualityControlReportService.findById(reportId);
			  tQualityControlReport.setReportName(tQualityControlReport2.getReportName());
			  tQualityControlReport.setCreateUser(tQualityControlReport2.getCreateUser());
			  tQualityControlReport.setCreateUserName(tQualityControlReport2.getCreateUserName()); 
			  String reportStatus=tQualityControlReport.getReportStatus();
			  if("0".equals(reportStatus)) {//待公示状态数据，意味着完成上报提交，更改上报时间为当前时间
				  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				  tQualityControlReport.setCreateTime(df.format(new Date()));
			  }else {
				  tQualityControlReport.setCreateTime(tQualityControlReport2.getCreateTime());
			  }
		  }
		  List<TFileUrlRel> tFileUrlRels=new ArrayList<>();
		  try {
			  tQualityControlReport=tQualityControlReportService.saveQualityControlReport(tQualityControlReport,curUser.getId());
			  if(tQualityControlReport.getId()>0) {
				  List<FileCond> fileUrls=saveQualityControlReportCond.getFileUrls();
				  if(fileUrls!=null) {
					  String urlType="5";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理 3 轮播图 4 抽检通知 5 评分细则
					  tFileUrlRels=tFileUrlRelService.dealTFileUrlRels(fileUrls,tQualityControlReport.getId(),urlType); 
				  }
			  }
			  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", tQualityControlReport);
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
		/**
		 * 获取用户个人的广东省综合医院摸底调查表数据列表（小程序）
		 * @param token
		 * @return
		 */
		  @ApiOperation(value = "获取用户个人的广东省综合医院摸底调查表数据列表(小程序)")
		  @RequestMapping(value = "getQlyCtlReportsByUserId", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData getQlyCtlReportsByUserId(@RequestHeader String token) {
			  TSysUser curUser=(TSysUser) redisUtil.get(token);
			  Map<String, Object> map=new HashMap<>();
			  List<Map<String, Object>> list=new ArrayList<>();
			  try {
				  TQualityControlReport tQualityControlReport=new TQualityControlReport();
				  tQualityControlReport.setCreateUser(curUser.getId());
				  List<TQualityControlReport> tQualityControlReports=tQualityControlReportService.getQlyCtlReports(tQualityControlReport);
				  if(tQualityControlReports!=null&&tQualityControlReports.size()>0) {
					  for(TQualityControlReport tQualityControlReport2:tQualityControlReports){
						  //获取表用户数据
						  Map<String, Object> userMap=tSysUserService.getUserDetailById(tQualityControlReport2.getCreateUser());
						 Map<String, Object> reportMap=new HashMap<>();
						 reportMap.put("report", tQualityControlReport2);
						 reportMap.put("user", userMap);
						  list.add(reportMap);
					  }
					  map.put("list", list);
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
		 * 获取表详情数据
		 * @param iDcond
		 * @param token
		 * @return
		 */
			  @ApiOperation(value = "获取表详情数据")
			  @RequestMapping(value = "getOneQlyCtlReport", method = RequestMethod.POST)
			  @JwtTokenUtil.UserLoginToken
			   public AppResultData getOneQlyCtlReport(@RequestBody IDcond iDcond, @RequestHeader String token) {
				  Map<String, Object> map=new HashMap<>();
				  try {
					  TQualityControlReport tQualityControlReport=tQualityControlReportService.findById(iDcond.getId());
					  if(tQualityControlReport.getId()>0) {
						  //获取对应的附件url列表
						  String urlType="5";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理 3 轮播图 4 抽检通知 5 评分细则
						  List<FileCond> fileUrls= tFileUrlRelService.getFileUrlsByRelId(tQualityControlReport.getId(),urlType);
						  map.put("tQualityControlReport", tQualityControlReport);
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
		  * 分页查询质控报表数据列表
		  * @param getReportsOnPageCond
		  * @param token
		  * @return
		  */
		  @ApiOperation(value = "分页查询质控报表数据列表")
		  @RequestMapping(value = "getReportsOnPage", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData getReportsOnPage(@RequestBody GetReportsOnPageCond getReportsOnPageCond ,@RequestHeader String token) {
			
			  TSysUser curUser=(TSysUser) redisUtil.get(token);  
			  try {
				  PageBean<Map<String, Object>> pageBean=tQualityControlReportService.getReportsOnPage(getReportsOnPageCond,curUser.getId());
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
		 *更改数据表状态
		 * @param iDcond
		 * @param token
		 * @return
		 */
		  @ApiOperation(value = "更改数据表状态")
		  @RequestMapping(value = "updateReportStatus", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData updateReportStatus(@RequestBody UpdateReportStatusCond updateReportStatusCond,@RequestHeader String token) {
			 TSysUser curUser=(TSysUser) redisUtil.get(token);
			  try {
				  int reportId=updateReportStatusCond.getReportId();
				  TQualityControlReport tQualityControlReport=tQualityControlReportService.findById(reportId);
				  tQualityControlReport.setReportStatus(updateReportStatusCond.getReportStatus());
				  tQualityControlReport=tQualityControlReportService.saveQualityControlReport(tQualityControlReport, curUser.getId());
				 appResultData=new AppResultData(APPResultCommom.SUCCESS, "", tQualityControlReport);
			} catch (Exception e) {
				e.printStackTrace();
				 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
				 return appResultData;
			}
			  return appResultData;
		    }
}
