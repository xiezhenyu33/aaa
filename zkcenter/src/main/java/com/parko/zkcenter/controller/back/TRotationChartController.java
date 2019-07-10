package com.parko.zkcenter.controller.back;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.JwtTokenUtil;
import com.parko.zkcenter.entity.TFileUrlRel;
import com.parko.zkcenter.entity.back.TRotationChart;
import com.parko.zkcenter.entity.cond.AddOneChartCond;
import com.parko.zkcenter.entity.cond.FileCond;
import com.parko.zkcenter.service.back.TFileUrlRelService;
import com.parko.zkcenter.service.back.TRotationChartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 轮播图相关业务处理接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/qualityControl/chart")
@Api(tags = "轮播图相关业务处理接口")
public class TRotationChartController {

	@Autowired
	private TRotationChartService tRotationChartService;//轮播图数据业务处理接口
	
	@Autowired
	private TFileUrlRelService tFileUrlRelService;//附件路径关联数据业务处理接口
	
	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	@Autowired
	 RedisUtil redisUtil;
	 
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 添加轮播图数据
	 * @param tRotationChart
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "添加轮播图数据")
	  @RequestMapping(value = "addOneChart", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData addOneChart(@RequestBody AddOneChartCond addOneChartCond,@RequestHeader String token) {
		
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  Map<String, Object> map=new HashMap<>();
		  TRotationChart tRotationChart=addOneChartCond.getTRotationChart();
		  int chartId=tRotationChart.getId();
		  if(chartId==0) {//新增操作
			  tRotationChart.setCreateUserName(curUser.getUserName());
		  }else {//更新，需要组织更新数据
			  TRotationChart tRotationChart2=tRotationChartService.findById(chartId);
			  tRotationChart.setCreateUser(tRotationChart2.getCreateUser());
			  tRotationChart.setCreateUserName(tRotationChart2.getCreateUserName()); 
			  tRotationChart.setCreateTime(tRotationChart2.getCreateTime());
		  }
		  List<TFileUrlRel> tFileUrlRels=new ArrayList<>();
		  try {
			  tRotationChart=tRotationChartService.saveTRotationChart(tRotationChart, curUser.getId());
			  if(tRotationChart.getId()>0) {
				  List<FileCond> fileUrls=addOneChartCond.getFileUrls();
				  String urlType="3";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理 3 轮播图
				  tFileUrlRels=tFileUrlRelService.dealTFileUrlRels(fileUrls,tRotationChart.getId(),urlType);
			  }
			  map.put("tRotationChart", tRotationChart);
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
	  * 删除轮播图数据
	  * @param iDcond
	  * @param token
	  * @return
	  */
		  @ApiOperation(value = "删除轮播图数据")
		  @RequestMapping(value = "deleteOneChart", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
	   public AppResultData deleteOneChart(@RequestBody IDcond iDcond,@RequestHeader String token) {
		 TSysUser curUser=(TSysUser) redisUtil.get(token);
		  try {
			  int chartId=iDcond.getId();
			  //先将信息-附件路径关联删除
				  String urlType="3";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理 3 轮播图
				 tFileUrlRelService.deleteTSysRoleMenus(chartId,urlType);
			 //删除对应的资料信息发布数据
				 tRotationChartService.deleteTRotationChartById(chartId,curUser.getId());
			 appResultData=new AppResultData(APPResultCommom.SUCCESS, APPResultCommom.DELETE_SUCCESS, null);
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
	 /**
	  * 获取单个轮播图信息
	  * @param iDcond
	  * @param token
	  * @return
	  */
			  @ApiOperation(value = "获取单个轮播图信息")
			  @RequestMapping(value = "findOneChart", method = RequestMethod.POST)
			  @JwtTokenUtil.UserLoginToken
			   public AppResultData findOneChart(@RequestBody IDcond iDcond,@RequestHeader String token) {
				 Map<String, Object> map=new HashMap<>();
				  try {
					  int chartId=iDcond.getId();
					  TRotationChart tRotationChart=tRotationChartService.findById(chartId);
					  //获取对应的附件url列表
					  String urlType="3";//附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理 3 轮播图
					  List<FileCond> fileUrls= tFileUrlRelService.getFileUrlsByRelId(chartId,urlType);
					  map.put("tRotationChart", tRotationChart);
					  map.put("tFileUrlRels", fileUrls);
					  if(tRotationChart.getId()>0) {
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
		  * 分页查询轮播图数据列表
		  * @param getNewsOnPageCond
		  * @param token
		  * @return
		  */
		  @ApiOperation(value = "分页查询轮播图数据列表")
		  @RequestMapping(value = "getChartsOnPage", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData getChartsOnPage(@RequestBody CommomPage commomPage ,@RequestHeader String token) {
				PageBean<Map<String, Object>> pageBean=new PageBean<>();
				List<Map<String, Object>> list=new ArrayList<>();
				  TSysUser curUser=(TSysUser) redisUtil.get(token);
			  try {
				  TSysUser tSysUser=tSysUserService.findById(curUser.getId());
				  List<Integer> curUserIds=tSysUserService.getUsersByRightListOnAffiliateIdId(tSysUser.getAffiliateId());
				  Page<TRotationChart> tRotationChartPages=tRotationChartService.getChartsOnPage(commomPage,curUserIds);
				  List<TRotationChart> tRotationCharts=tRotationChartPages.getContent();
				  if(tRotationCharts!=null&&tRotationCharts.size()>0) {//处理部分图片数据信息
					  for(TRotationChart tRotationChart:tRotationCharts){
						  Map<String, Object> map=new HashMap<>();
						  map.put("id", tRotationChart.getId());
						  map.put("slogan", tRotationChart.getSlogan());
						  map.put("imageUrl", tRotationChart.getImageUrl());
						  map.put("createTime", tRotationChart.getCreateTime()==null?"":tRotationChart.getCreateTime().substring(0, 10));
						  List<FileCond> fileUrls= tFileUrlRelService.getFileUrlsByRelId(tRotationChart.getId(),"3");
						  map.put("fileUrls", fileUrls);
						  list.add(map);
					  }
					  pageBean.setCurrentPage(tRotationChartPages.getNumber());
					  pageBean.setPageData(list);
					  pageBean.setPageSize(tRotationChartPages.getSize());
					  pageBean.setTotalCount(tRotationChartPages.getTotalElements());
					  pageBean.setTotalPage(tRotationChartPages.getTotalPages());
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
			 * 获取所有的机构数据列表（不分页）
			 * @param token
			 * @return
			 */
		  @ApiOperation(value = "获取所有的轮播图数据列表（不分页）")
		  @RequestMapping(value = "geTCharts", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData geTCharts(@RequestHeader String token) {
			
			  Map<String, Object> map=new HashMap<>();
			  TSysUser tSysUser=(TSysUser) redisUtil.get(token);
			  List<TRotationChart> returnList=new ArrayList<>();
			  try {
				  int userId=tSysUser.getId();
				  tSysUser=tSysUserService.findById(userId);
				  TRotationChart tRotationChartParam=new TRotationChart();
				  List<Integer> curUserIds=tSysUserService.getUsersByRightListOnAffiliateIdId(tSysUser.getAffiliateId());
				  List<TRotationChart> tRotationCharts=tRotationChartService.geTRotationCharts(tRotationChartParam,curUserIds);
				  if(tRotationCharts!=null&&tRotationCharts.size()>0) {
					  for(TRotationChart tRotationChart:tRotationCharts) {//获取本机构下的数据
						  TSysUser tSysUser2=tSysUserService.findById(tRotationChart.getCreateUser());
						  if(tSysUser.getAffiliateId()==tSysUser2.getAffiliateId()) {
							  returnList.add(tRotationChart);
						  }
					  }
					  map.put("list", returnList);
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
