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
import com.parko.system.entity.cond.IDcond;
import com.parko.system.entity.cond.sys.AffiliateIdCond;
import com.parko.system.entity.pojo.APPResultCommom;
import com.parko.system.entity.pojo.AppResultData;
import com.parko.system.entity.sys.TAffiliateDept;
import com.parko.system.entity.sys.TDept;
import com.parko.system.entity.sys.TSysUser;
import com.parko.system.service.sys.TAffiliateDeptService;
import com.parko.system.service.sys.TDeptService;
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.JwtTokenUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 后台科室系统相关业务接口
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/zk/sys/dept")
@Api(tags = "后台科室系统相关业务接口")
public class ZkSysDeptOperateController {

	@Autowired
	private TDeptService tDeptService;// 系统用户业务处理接口

	@Autowired
	private TSysUserService tSysUserService;// 系统用户业务处理接口

	@Autowired
	private TAffiliateDeptService tAffiliateDeptService;// 机构科室关系数据业务处理接口

	@Autowired
	RedisUtil redisUtil;

	AppResultData appResultData;// 公共返回数据对象

	/**
	 * 添加科室数据
	 * 
	 * @param tDept
	 * @param token
	 * @return
	 */
	@ApiOperation(value = "添加科室数据")
	@RequestMapping(value = "addOneDept", method = RequestMethod.POST)
	@JwtTokenUtil.UserLoginToken
	public AppResultData addOneDept(@RequestBody TDept tDept, @RequestHeader String token) {

		TSysUser curUser = (TSysUser) redisUtil.get(token);
		tDept.setCreateUserName(curUser.getUserName());
		try {
			tDept = tDeptService.saveDept(tDept, curUser.getId());
			appResultData = new AppResultData(APPResultCommom.SUCCESS, "", tDept);
		} catch (Exception e) {
			e.printStackTrace();
			appResultData = new AppResultData(APPResultCommom.FAIL, APPResultCommom.FAIL_MSG, null);
			return appResultData;
		}
		return appResultData;
	}

	/**
	 * 删除科室数据
	 * 
	 * @param iDcond
	 * @param token
	 * @return
	 */
	@ApiOperation(value = "删除科室数据")
	@RequestMapping(value = "deleteOneDept", method = RequestMethod.POST)
	@JwtTokenUtil.UserLoginToken
	public AppResultData deleteOneDept(@RequestBody IDcond iDcond, @RequestHeader String token) {
		try {
			// 首先判断该科室下是否已经存在对应的用户数据
			int deptId = iDcond.getId();
			TSysUser tSysUserParam = new TSysUser();
			tSysUserParam.setDeptId(deptId);
			List<TSysUser> tSysUsers = tSysUserService.geTSysUsers(tSysUserParam);
			if (tSysUsers != null && tSysUsers.size() > 0) {// 已经存在对应的用户数据，提示不能删除
				appResultData = new AppResultData(APPResultCommom.DEPT_CANNOT_DELETE_ERROR,
						APPResultCommom.DEPT_CANNOT_DELETE_ERROR_MSG, null);
				return appResultData;
			}
			// 执行删除机构-科室对应关系操作
			TAffiliateDept tAffiliateDept = new TAffiliateDept();
			tAffiliateDept.setDeptId(deptId);
			tAffiliateDeptService.deleteTAffiliateDepts(tAffiliateDept);
			// 删除对应的科室数据
			tDeptService.deleteOneDept(deptId);
			appResultData = new AppResultData(APPResultCommom.SUCCESS, APPResultCommom.DELETE_SUCCESS, null);
		} catch (Exception e) {
			e.printStackTrace();
			appResultData = new AppResultData(APPResultCommom.FAIL, APPResultCommom.FAIL_MSG, null);
			return appResultData;
		}
		return appResultData;
	}

	/**
	 * 获取所有的机构数据列表
	 * 
	 * @param token
	 * @return
	 */
	@ApiOperation(value = "根据机构id获取对应的科室数据列表")
	@RequestMapping(value = "getDeptsByAffiliateId", method = RequestMethod.POST)
	@JwtTokenUtil.UserLoginToken
	public AppResultData getDeptsByAffiliateId(@RequestBody AffiliateIdCond affiliateIdCond,
			@RequestHeader String token) {

		Map<String, Object> map = new HashMap<>();
		try {
			int affiliateId = affiliateIdCond.getAffiliateId();
			TAffiliateDept tAffiliateDept = new TAffiliateDept();
			tAffiliateDept.setAffiliateId(affiliateId);
			List<TAffiliateDept> tAffiliateDepts = tAffiliateDeptService.getTAffiliateDepts(tAffiliateDept);
			map.put("list", tAffiliateDepts);
			if (tAffiliateDepts != null && tAffiliateDepts.size() > 0) {
				appResultData = new AppResultData(APPResultCommom.SUCCESS, "", map);
			} else {
				appResultData = new AppResultData(APPResultCommom.GETLIST_FAIL, APPResultCommom.GETLIST_FAIL_ERROR_MSG,
						map);
				return appResultData;
			}
		} catch (Exception e) {
			e.printStackTrace();
			appResultData = new AppResultData(APPResultCommom.FAIL, APPResultCommom.FAIL_MSG, null);
			return appResultData;
		}
		return appResultData;
	}
}
