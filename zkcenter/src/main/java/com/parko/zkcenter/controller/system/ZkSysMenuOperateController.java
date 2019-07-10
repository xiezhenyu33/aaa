package com.parko.zkcenter.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.parko.redis.utils.RedisUtil;
import com.parko.system.entity.pojo.APPResultCommom;
import com.parko.system.entity.pojo.AppResultData;
import com.parko.system.entity.pojo.TSysMenuCond;
import com.parko.system.entity.sys.TSysMenu;
import com.parko.system.entity.sys.TSysRole;
import com.parko.system.entity.sys.TSysRoleMenu;
import com.parko.system.entity.sys.TSysUser;
import com.parko.system.entity.sys.TSysUserRole;
import com.parko.system.service.sys.TSysMenuService;
import com.parko.system.service.sys.TSysRoleMenuService;
import com.parko.system.service.sys.TSysUserRoleService;
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.JwtTokenUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 后台菜单系统相关业务接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/zk/sys/menus")
@Api(tags = "后台菜单系统相关业务接口")
public class ZkSysMenuOperateController {

	@Autowired
	private TSysMenuService tSysMenuService;// 菜单数据业务处理接口
	
	@Autowired
	private TSysUserRoleService tSysUserRoleService;// 用户角色关联数据业务处理接口
	
	@Autowired
	private TSysRoleMenuService tSysRoleMenuService;//角色菜单关联数据业务处理接口
	
	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	 @Autowired
	 RedisUtil redisUtil;
	 
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 获取所有的菜单数据列表（不分页）
	 * @param token
	 * @return
	 */
		  @ApiOperation(value = "获取所有的菜单数据列表（不分页）")
		  @RequestMapping(value = "getMenus", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData getMenus(@RequestHeader String token) {
			
			  Map<String, Object> map=new HashMap<>();
			  try {
				  List<TSysMenu> tSysMenus=tSysMenuService.getTSysMenus();
				  map.put("list", tSysMenus);
				  if(tSysMenus!=null&&tSysMenus.size()>0) {
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
		   *  用户登录，获取菜单树
		   * @param token
		   * @return
		   */
			@ApiOperation(value = " 用户登录，获取菜单树",httpMethod = "POST")
			@RequestMapping(value = "getMenusTree.int.do",method = RequestMethod.POST)
			@JwtTokenUtil.UserLoginToken
			public AppResultData getMenusTree( @RequestHeader String token) {

				   List<TSysMenuCond> rootMenu = new ArrayList<TSysMenuCond>();
			      List<TSysMenu> allMenu=new ArrayList<TSysMenu>();
				try {
					TSysUser curSysUser=(TSysUser) redisUtil.get(token);
					if(curSysUser==null) {
						  appResultData=new AppResultData(APPResultCommom.TOKEN_FAIL, APPResultCommom.TOKEN_FAIL_MSG, null);
					     return appResultData;
					}
					curSysUser=tSysUserService.findById(curSysUser.getId());
					String userType=curSysUser.getUserType();
					int userId=curSysUser.getId();
					if("A".equals(userType)){//如果是系统超级管理员，则获取全部的用户菜单数据
						  allMenu =tSysMenuService.getTSysMenus();
						  //组织并获取菜单节点数据
						  tSysMenuService.setTreeMenu(allMenu,rootMenu);
					}else {//其余为普通用户
						//根据用户-角色获取对应的用户-角色关联数据列表
						  TSysUserRole tSysUserRole=new TSysUserRole();
						  tSysUserRole.setUserId(userId);
						  List<TSysUserRole> tSysUserRoles=tSysUserRoleService.geTSysUserRoles(tSysUserRole);
						  if(tSysUserRoles!=null&&tSysUserRoles.size()>0) {
						  //获取角色对应的菜单数据列表
						  List<TSysRoleMenu> tSysRoleMenusAll=new ArrayList<>();
							 for(TSysUserRole tySysUserRole:tSysUserRoles) {
								  TSysRoleMenu tSysRoleMenu=new TSysRoleMenu();
								  tSysRoleMenu.setRoleId(tySysUserRole.getRoleId());
								List<TSysRoleMenu> tSysRoleMenus=tSysRoleMenuService.getTSysRoleMenus(tSysRoleMenu);	 	 
								tSysRoleMenusAll.addAll(tSysRoleMenus);
							 }
						  if(tSysRoleMenusAll.size()>0) {//获取所有角色对应的菜单数据列表
							  List<Integer> listMenuIds=new ArrayList<Integer>();
								 for(int i=0;i<tSysRoleMenusAll.size();i++) {
									 TSysRoleMenu sysRoleMenuRel=tSysRoleMenusAll.get(i);
									 listMenuIds.add(sysRoleMenuRel.getMenuId());
								 }
								 //根据id数组获取对应的对象数据
								 allMenu=tSysMenuService.getMenusByIds(listMenuIds);
								 tSysMenuService.setTreeMenu(allMenu,rootMenu);
						  }else {//用户对应的角色未设置对应的菜单数据，提醒设置
							  appResultData=new AppResultData(APPResultCommom.USER_NO_MENUS_ERROR, APPResultCommom.USER_NO_MENUS_ERROR_MSG, null);
							  return appResultData;
						  }
						  }else {//该用户未设置对应的角色数据，应该提示前往设置
							  appResultData=new AppResultData(APPResultCommom.USER_NO_ROLES_ERROR, APPResultCommom.USER_NO_ROLES_ERROR_MSG, null);
							  return appResultData;
							  
						  }
					}
					  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", rootMenu);
				} catch (Exception e) {
					e.printStackTrace();
					 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
					 return appResultData;
				}
               return appResultData;
			}

}
