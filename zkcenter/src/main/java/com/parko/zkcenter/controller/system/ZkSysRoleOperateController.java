package com.parko.zkcenter.controller.system;

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
import com.parko.system.entity.cond.sys.AddOneRoleCond;
import com.parko.system.entity.cond.sys.NameCond;
import com.parko.system.entity.pojo.APPResultCommom;
import com.parko.system.entity.pojo.AppResultData;
import com.parko.system.entity.pojo.PageBean;
import com.parko.system.entity.sys.TSysRole;
import com.parko.system.entity.sys.TSysRoleMenu;
import com.parko.system.entity.sys.TSysUser;
import com.parko.system.entity.sys.TSysUserRole;
import com.parko.system.service.sys.TSysRoleMenuService;
import com.parko.system.service.sys.TSysRoleService;
import com.parko.system.service.sys.TSysUserRoleService;
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.JwtTokenUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 后台角色系统相关业务接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/zk/sys/roles")
@Api(tags = "后台角色系统相关业务接口")
public class ZkSysRoleOperateController {

	@Autowired
	private TSysRoleService tSysRoleService;//角色数据业务处理接口
	
	@Autowired
	private TSysRoleMenuService tSysRoleMenuService;//角色菜单关联数据业务处理接口
	
	@Autowired
	private TSysUserRoleService tSysUserRoleService;// 用户角色关联数据业务处理接口
	
	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	 @Autowired
	 RedisUtil redisUtil;
	 
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 添加机构数据
	 * @param tAffiliate
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "添加角色数据")
	  @RequestMapping(value = "addOneRole", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData addOneRole(@RequestBody AddOneRoleCond addOneRoleCond,@RequestHeader String token) {
		
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  TSysRole tSysRole=addOneRoleCond.getTSysRole();
		  String roleName=tSysRole.getRoleName();
		  int roleId=tSysRole.getId();
		  TSysRole tSysRoleParam=new TSysRole();
		  tSysRoleParam.setRoleName(roleName);
		  TSysUser tSysUser=tSysUserService.findById(curUser.getId());
		  List<Integer> curUserIds=tSysUserService.getUsersByRightListOnAffiliateIdId(tSysUser.getAffiliateId());
		 List<TSysRole> tSysRoles=tSysRoleService.geTSysRoles(tSysRoleParam,curUserIds);
		  if(roleId==0) {//新增操作，校验角色名称是否重复
			  if(tSysRoles!=null&&tSysRoles.size()>0) {//已经存在相同的角色名称，提示已存在
				  appResultData=new AppResultData(APPResultCommom.ROLE_REPEAT_ERROR, APPResultCommom.ROLE_REPEAT_ERROR_MSG, roleName);
				  return appResultData;
			  }
			  tSysRole.setCreateUserName(curUser.getUserName());
		  }else {//更新操作，判断两个角色名相同的数据id是否一致
			  if(tSysRoles!=null&&tSysRoles.size()>0) {
				  TSysRole tSysRoleOld=tSysRoles.get(0);
				  if(roleId!=tSysRoleOld.getId()) {//id不一致，则存在重复数据
					  appResultData=new AppResultData(APPResultCommom.ROLE_REPEAT_ERROR, APPResultCommom.ROLE_REPEAT_ERROR_MSG, roleName);
					  return appResultData;
				  }
			  }
		  }
		  List<TSysRoleMenu> tSysRoleMenus=new ArrayList<>();
		  try {
			  tSysRole=tSysRoleService.saveTSysRole(tSysRole,curUser.getId());
			  if(tSysRole.getId()>0) {//角色添加成功，添加对应的角色菜单关联数据
				  List<Integer> menuIds=addOneRoleCond.getMenuIds();
				 if(menuIds.size()>0) {//存在需要保存的角色菜单关联数据
					 //先将角色菜单关联数据删除
					 TSysRoleMenu tSysRoleMenu=new TSysRoleMenu();
					 tSysRoleMenu.setRoleId(tSysRole.getId());
					 tSysRoleMenuService.deleteTSysRoleMenus(tSysRoleMenu);
					 //保存角色菜单关联数据
					 for(int menuId:menuIds) {
						 TSysRoleMenu tSysRoleMenuParam=new TSysRoleMenu();
						 tSysRoleMenuParam.setRoleId(tSysRole.getId());
						 tSysRoleMenuParam.setMenuId(menuId);
						tSysRoleMenus.add(tSysRoleMenuParam);
					 }
					 if(tSysRoleMenus.size()>0) {
						 tSysRoleMenus=tSysRoleMenuService.saveAll(tSysRoleMenus);
					 }
				 }
			  }
			  Map<String, Object> map=new HashMap<>();
			  map.put("role", tSysRole);
			  map.put("menus", tSysRoleMenus);
			  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", map);
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
	 /**
	  * 获取所有的角色数据列表（不分页）
	  * @param token
	  * @return
	  */
		  @ApiOperation(value = "获取所有的角色数据列表（不分页）")
		  @RequestMapping(value = "getRoles", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData getRoles(@RequestHeader String token) {
			
			  Map<String, Object> map=new HashMap<>();
			  TSysUser curUser=(TSysUser) redisUtil.get(token);
			  try {
				  TSysUser tSysUser=tSysUserService.findById(curUser.getId());
				  List<Integer> curUserIds=tSysUserService.getUsersByRightListOnAffiliateIdId(tSysUser.getAffiliateId());
				  TSysRole tSysRoleParam=new TSysRole();
				  List<TSysRole> tSysRoles=tSysRoleService.geTSysRoles(tSysRoleParam,curUserIds);
				  map.put("list", tSysRoles);
				  if(tSysRoles!=null&&tSysRoles.size()>0) {
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
			  @ApiOperation(value = "获取单条角色详情数据")
			  @RequestMapping(value = "getRoleDetails", method = RequestMethod.POST)
			  @JwtTokenUtil.UserLoginToken
			   public AppResultData getRoleDetails(@RequestBody IDcond iDcond ,@RequestHeader String token) {
				
				  Map<String, Object> map=new HashMap<>();
				  try {
					  int roleId=iDcond.getId();
					  TSysRole tSysRole=tSysRoleService.findById(roleId);
					  TSysRoleMenu tSysRoleMenu=new TSysRoleMenu();
						 tSysRoleMenu.setRoleId(roleId);
						 List<TSysRoleMenu> tSysRoleMenus=tSysRoleMenuService.getTSysRoleMenus(tSysRoleMenu);	 	 
						 List<Integer> menuIds=new ArrayList<>();
						 if(tSysRoleMenus!=null&&tSysRoleMenus.size()>0) {
							 for(TSysRoleMenu t:tSysRoleMenus) {
								 menuIds.add(t.getMenuId());
							 }
						 }
						 map.put("role", tSysRole);
						 map.put("menuIds", menuIds);
					  if(tSysRole.getId()>0) {
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
				 * 删除用户数据
				 * @param iDcond
				 * @param token
				 * @return
				 */
				  @ApiOperation(value = "删除角色数据")
				  @RequestMapping(value = "deleteOneRole", method = RequestMethod.POST)
				  @JwtTokenUtil.UserLoginToken
				   public AppResultData deleteOneRole(@RequestBody IDcond iDcond,@RequestHeader String token) {
					 TSysUser curUser=(TSysUser) redisUtil.get(token);
					  try {
						  int roleId=iDcond.getId();
						  //首先判断对应的角色下是否存在用户数据
						  TSysUserRole tSysUserRole=new TSysUserRole();
						  tSysUserRole.setRoleId(roleId);
						 List<TSysUserRole> tSysUserRoles= tSysUserRoleService.geTSysUserRoles(tSysUserRole);
						  if(tSysUserRoles!=null&&tSysUserRoles.size()>0) {
							  appResultData=new AppResultData(APPResultCommom.ROLE_CANNOT_DELETE_ERROR,APPResultCommom.ROLE_CANNOT_DELETE_ERROR_MSG, null);
								 return appResultData;
						  }
						  //删除对应的角色-菜单关联数据
						  TSysRoleMenu tSysRoleMenu=new TSysRoleMenu();
						 tSysRoleMenu.setRoleId(roleId);
						 tSysRoleMenuService.deleteTSysRoleMenus(tSysRoleMenu);
						 //删除对应的角色数据
						 tSysRoleService.deleteOneRole(roleId,curUser.getId());
						 appResultData=new AppResultData(APPResultCommom.SUCCESS, APPResultCommom.DELETE_SUCCESS, null);
					} catch (Exception e) {
						e.printStackTrace();
						 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
						 return appResultData;
					}
					  return appResultData;
				    }
			/**
			 * 分页查询角色数据列表
			 * @param nameCond
			 * @param token
			 * @return
			 */
			  @ApiOperation(value = "分页查询角色数据列表")
			  @RequestMapping(value = "getRolesOnPage", method = RequestMethod.POST)
			  @JwtTokenUtil.UserLoginToken
			   public AppResultData getRolesOnPage(@RequestBody NameCond nameCond ,@RequestHeader String token) {
				
				  TSysUser curUser=(TSysUser) redisUtil.get(token);
				  try {
					  TSysUser tSysUser=tSysUserService.findById(curUser.getId());
					  List<Integer> curUserIds=tSysUserService.getUsersByRightListOnAffiliateIdId(tSysUser.getAffiliateId());
					  PageBean<TSysRole> pageBean=tSysRoleService.getRolesOnPage(nameCond,curUserIds);
					  List<TSysRole> list=pageBean.getPageData();
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
