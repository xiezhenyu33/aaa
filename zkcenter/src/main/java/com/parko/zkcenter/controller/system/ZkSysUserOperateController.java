package com.parko.zkcenter.controller.system;

import static org.hamcrest.CoreMatchers.nullValue;

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
import com.parko.system.entity.cond.LoginCond;
import com.parko.system.entity.cond.PhoneCond;
import com.parko.system.entity.cond.UpdatePwdCond;
import com.parko.system.entity.cond.sys.AddOneUserCond;
import com.parko.system.entity.cond.sys.GetUsersOnPageCond;
import com.parko.system.entity.cond.sys.UpdateUserStatus;
import com.parko.system.entity.pojo.APPResultCommom;
import com.parko.system.entity.pojo.AppResultData;
import com.parko.system.entity.pojo.PageBean;
import com.parko.system.entity.pojo.ParamCommom;
import com.parko.system.entity.sys.TSysUser;
import com.parko.system.entity.sys.TSysUserRole;
import com.parko.system.service.sys.TSysUserRoleService;
import com.parko.system.service.sys.TSysUserService;
import com.parko.system.utils.AliyunMessageUtil;
import com.parko.system.utils.JwtTokenUtil;
import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 后台用户系统相关业务接口
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/zk/sys/user")
@Api(tags = "后台用户系统相关业务接口")
public class ZkSysUserOperateController {

	@Autowired
	private TSysUserService tSysUserService;//系统用户业务处理接口
	
	@Autowired
	private TSysUserRoleService tSysUserRoleService;// 用户角色关联数据业务处理接口
	
	 @Autowired
	 RedisUtil redisUtil;
	 
	 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	AppResultData appResultData;//公共返回数据对象
	
	/**
	 * 获取单个用户对象数据（包含角色信息）
	 * @param iDcond
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "获取单个用户对象数据（包含角色信息）")
	  @RequestMapping(value = "findOneUser", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData findOneUser(@RequestBody IDcond iDcond,@RequestHeader String token) {
		 Map<String, Object> map=new HashMap<>();
		  try {
			  TSysUser tSysUser=tSysUserService.findById(iDcond.getId());
			  //获取对应的角色数组
			  TSysUserRole tSysUserRole=new TSysUserRole();
			  tSysUserRole.setUserId(tSysUser.getId());
			  List<TSysUserRole> tSysUserRoles=tSysUserRoleService.geTSysUserRoles(tSysUserRole);
			  List<Integer> roleIds=new ArrayList<>();
			  if(tSysUserRoles!=null&&tSysUserRoles.size()>0) {
				  for(TSysUserRole t:tSysUserRoles) {
					  roleIds.add(t.getRoleId());
				  }
			  }
			  map.put("user", tSysUser);
			  map.put("roleIds", roleIds);
			  if(tSysUser.getId()>0) {
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
	   * 添加用户数据
	   * @param tSysUser
	   * @param token
	   * @return
	   */
	  @ApiOperation(value = "添加用户数据")
	  @RequestMapping(value = "addOneUser", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData addOneUser(@RequestBody AddOneUserCond addOneUserCond,@RequestHeader String token) {
		
		  TSysUser curUser=(TSysUser) redisUtil.get(token);
		  if(curUser==null) {
			  appResultData=new AppResultData(APPResultCommom.TOKEN_FAIL, APPResultCommom.TOKEN_FAIL_MSG, null);
			  return appResultData;
		  }
		  System.out.println("=============");
		  try {
			  TSysUser tSysUser=addOneUserCond.getTSysUser();
			  int userId=tSysUser.getId();
			  String userLoginid=tSysUser.getUserLoginid();
			  TSysUser tSysUserParam=new TSysUser();
			  tSysUserParam.setUserLoginid(userLoginid);
			  TSysUser tSysUser2=tSysUserService.findOneByUser(tSysUserParam);
			  if(userId==0) {
				  if(tSysUser2.getId()>0) {
					  appResultData=new AppResultData(APPResultCommom.PHONE_BEING_USED_CODE, APPResultCommom.PHONE_BEING_USED_MSG, userLoginid);
					  return appResultData;
				  }
				  tSysUser.setCreateUserName(curUser.getUserName());
			  }else {
				  if(userId!=tSysUser2.getId()) {
					  appResultData=new AppResultData(APPResultCommom.PHONE_BEING_USED_CODE, APPResultCommom.PHONE_BEING_USED_MSG, userLoginid);
					  return appResultData;
				  }
				  //组织更新数据
				  tSysUser=buildUpdateData(tSysUser2,tSysUser);
			  }
			  tSysUser=tSysUserService.saveUser(tSysUser,curUser.getId());
			  List<TSysUserRole> tSysUserRoles=new ArrayList<>();
			  if(tSysUser.getId()>0) {//用户保存成功，处理角色数据
				  List<Integer> roleIds=addOneUserCond.getRoleIds();
				  if(roleIds.size()>0) {//存在需要处理的角色数据
					  //首先将原本的关联数据删除
					  TSysUserRole tSysUserRole=new TSysUserRole();
					  tSysUserRole.setUserId(tSysUser.getId());
					  tSysUserRoleService.deleteTSysUserRoles(tSysUserRole);
					  //保存用户-角色关联数据
					  for(int roleId:roleIds) {
						  TSysUserRole tSysUserRoleParam=new TSysUserRole();
						  tSysUserRoleParam.setRoleId(roleId);
						  tSysUserRoleParam.setUserId(tSysUser.getId());
						  tSysUserRoles.add(tSysUserRoleParam);
					  }
					  if(tSysUserRoles.size()>0) {
						  tSysUserRoleService.saveAll(tSysUserRoles);
					  }
				  }
			  }
			  appResultData=new AppResultData(APPResultCommom.SUCCESS, "", tSysUser);
		} catch (Exception e) {
			e.printStackTrace();
			 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
			 return appResultData;
		}
		  return appResultData;
	    }
	/**
	   * 系统用户登录
	   * @param loginCond
	   * @return
	   */
	  @ApiOperation(value = "系统用户登录")
	  @RequestMapping(value = "login", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData login(@RequestBody LoginCond loginCond) {
		
			TSysUser sysUserParam=new TSysUser();
			sysUserParam.setUserLoginid(loginCond.getUserLoginid());
			sysUserParam.setUserLoginpassword(loginCond.getUserLoginpassword());
			TSysUser sysUser=new TSysUser();
			try {
				 sysUser=tSysUserService.findOneByUser(sysUserParam);
				 if(sysUser!=null&&sysUser.getId()>0) {
					 String userStatus=sysUser.getUserStatus();
				     if("1".equals(userStatus)) {
				    	 appResultData=new AppResultData(APPResultCommom.PAT_LOGIN_FOBIT_ERROR, APPResultCommom.PAT_LOGIN_FOBIT_ERROR_MSG, sysUser);
						    return appResultData;
				     }else {
				    		sysUser.setToken(JwtTokenUtil.createTokenByCurrentUser(sysUser));
							sysUser.setLastLoginTime(df.format(new Date()));
							//更新当前用户登录信息
							tSysUserService.saveUser(sysUser, sysUser.getId());
							redisUtil.set(sysUser.getToken(),sysUser);
				     }
				 }else {
					 appResultData=new AppResultData(APPResultCommom.USER_ERROR, APPResultCommom.USER_ERROR_MSG, null);
				    return appResultData;
				 }
			} catch (Exception e) {
				e.printStackTrace();
				appResultData=new AppResultData(APPResultCommom.FAIL, APPResultCommom.FAIL_MSG+e.getMessage(), null);
				return appResultData;
			}
			appResultData=new AppResultData(APPResultCommom.SUCCESS, "用户登录成功！", sysUser);
			return appResultData;
      }
	/**
	 * 获取用户详细数据（不包含角色信息）
	 * @param iDcond
	 * @param token
	 * @return
	 */
	  @ApiOperation(value = "获取用户详细数据（不包含角色信息）")
	  @RequestMapping(value = "getUserDetailById", method = RequestMethod.POST)
	  @JwtTokenUtil.UserLoginToken
	   public AppResultData getUserDetailById(@RequestBody IDcond iDcond,@RequestHeader String token) {
		
			Map<String, Object> map=new HashMap<>();
			try {
				map=tSysUserService.getUserDetailById(iDcond.getId());
			} catch (Exception e) {
				e.printStackTrace();
				appResultData=new AppResultData(APPResultCommom.FAIL, APPResultCommom.FAIL_MSG+e.getMessage(), null);
				return appResultData;
			}
			appResultData=new AppResultData(APPResultCommom.SUCCESS, "获取用户详细数据成功！", map);
			return appResultData;
      }
	 /**
	  * 根据手机号获取对应的验证码
	  * @param phoneCond
	  * @param token
	  * @return
	  */
		  @ApiOperation(value = "根据手机号获取对应的验证码")
		  @RequestMapping(value = "getAuthenticationCodeOnPwd", method = RequestMethod.POST)
		   public AppResultData getAuthenticationCode(@RequestBody PhoneCond phoneCond,@RequestHeader(required=false) String token) {
			
				Map<String, Object> map=new HashMap<>();
				try {
					//首先根据手机号获取对应的用户数据，判断该用户是否存在
					String phone=phoneCond.getPhone();
					TSysUser tSysUserParam=new TSysUser();
					tSysUserParam.setUserLoginid(phone);
					TSysUser tSysUser=tSysUserService.findOneByUser(tSysUserParam);
					if(tSysUser.getId()>0) {//该用户存在，则执行验证码发送操作
						 map=tSysUserService.getAuthenticationCode(phone,AliyunMessageUtil.UPDATEPASSWORDIDENTITYTEMPLATECODE);
						 boolean status=(boolean) map.get("status");
						 String data=(String) map.get("data");
						 if(status) {//验证码发送成功
					          appResultData=new  AppResultData(APPResultCommom.SUCCESS, "", APPResultCommom.YAZ_SEND_SUCCESS_MSG);
						 }else {//验证码发送失败
							 return new AppResultData(APPResultCommom.FAIL, APPResultCommom.ALI_MSG_YZMS_ERROR_MSG, data);
						 } 
					}else {//该用户不存在，则提示手机号码有误
						appResultData=new AppResultData(APPResultCommom.USER_PHONE_ERROR, APPResultCommom.USER_PHONE_ERROR_MSG,phone);
						return appResultData;
					}
				} catch (Exception e) {
					e.printStackTrace();
					appResultData=new AppResultData(APPResultCommom.FAIL, APPResultCommom.FAIL_MSG+e.getMessage(), null);
					return appResultData;
				}
				return appResultData;
	      }
		/**
		 * 修改用户密码
		 * @param updatePwdCond
		 * @param token
		 * @return
		 */
	  @ApiOperation(value = "修改用户密码")
	  @RequestMapping(value = "updatePwd", method = RequestMethod.POST)
	   public AppResultData updatePwd(@RequestBody UpdatePwdCond updatePwdCond,@RequestHeader(required=false) String token) {
		
			try {
				//首先判断手机号码是否存在
				String phone=updatePwdCond.getPhone();
				TSysUser tSysUserParam=new TSysUser();
				tSysUserParam.setUserLoginid(phone);
				TSysUser tSysUser=tSysUserService.findOneByUser(tSysUserParam);
				if(tSysUser.getId()>0) {//该用户存在，则下一步校验操作
					String authenticationCode=updatePwdCond.getAuthenticationCode();//传入的验证码
					String authenticationCodeRedis=(String) redisUtil.get(ParamCommom.pwdRedisPrefix+phone);//缓存的验证码
					if(authenticationCodeRedis==null) {//获取缓存的验证码失败，可能是验证码已失效
						appResultData=new AppResultData(APPResultCommom.ALI_MSG_YZME_ERROR, APPResultCommom.ALI_MSG_YZME_ERROR_MSG,authenticationCodeRedis);
						return appResultData;
					}else {//判断两个验证码是否匹配
						if(authenticationCode.equals(authenticationCodeRedis)) {//传入的验证码与缓存的验证码一致，可以执行修改密码操作
							String pwd=updatePwdCond.getPwd();
							tSysUser.setUserLoginpassword(pwd);
							//修改密码之后对应的token也会改变，需要重新设置
							tSysUser.setToken(JwtTokenUtil.createTokenByCurrentUser(tSysUser));
							tSysUser=tSysUserService.saveUser(tSysUser, tSysUser.getId());
							appResultData=new AppResultData(APPResultCommom.SUCCESS, APPResultCommom.PASS_WORD_SUCCESS, null);
						}else {//传入的验证码与缓存的验证码不一致
							appResultData=new AppResultData(APPResultCommom.ALI_MSG_ERROR, APPResultCommom.ALI_MSG_ERROR_MSG,authenticationCodeRedis);
							return appResultData;
						}
					}
				} else{
					//该用户不存在，则提示手机号码有误
					appResultData=new AppResultData(APPResultCommom.USER_PHONE_ERROR, APPResultCommom.USER_PHONE_ERROR_MSG,phone);
					return appResultData;
				}	
			} catch (Exception e) {
				e.printStackTrace();
				appResultData=new AppResultData(APPResultCommom.FAIL, APPResultCommom.FAIL_MSG+e.getMessage(), null);
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
		  @ApiOperation(value = "删除用户数据")
		  @RequestMapping(value = "deleteOneUser", method = RequestMethod.POST)
		  @JwtTokenUtil.UserLoginToken
		   public AppResultData deleteOneUser(@RequestBody IDcond iDcond,@RequestHeader String token) {
			 TSysUser curUser=(TSysUser) redisUtil.get(token);
			  try {
				  int userId=iDcond.getId();
				  //获取对应的角色数组，删除掉关联数据
				  TSysUserRole tSysUserRole=new TSysUserRole();
				  tSysUserRole.setUserId(userId);
				 tSysUserRoleService.deleteTSysUserRoles(tSysUserRole);
				 //删除对应的用户数据
				 tSysUserService.deleteOneUser(userId,curUser.getId() );
				 appResultData=new AppResultData(APPResultCommom.SUCCESS, APPResultCommom.DELETE_SUCCESS, null);
			} catch (Exception e) {
				e.printStackTrace();
				 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
				 return appResultData;
			}
			  return appResultData;
		    }
			/**
			 *冻结/解冻用户数据
			 * @param iDcond
			 * @param token
			 * @return
			 */
			  @ApiOperation(value = "冻结/解冻用户数据")
			  @RequestMapping(value = "updateUserStatus", method = RequestMethod.POST)
			  @JwtTokenUtil.UserLoginToken
			   public AppResultData updateUserStatus(@RequestBody UpdateUserStatus updateUserStatus,@RequestHeader String token) {
				 TSysUser curUser=(TSysUser) redisUtil.get(token);
				  try {
					  int userId=updateUserStatus.getUserId();
				     TSysUser tSysUser=tSysUserService.findById(userId);
				     tSysUser.setUserStatus(updateUserStatus.getUserStatus());
				     tSysUser=tSysUserService.saveUser(tSysUser, curUser.getId());
					 appResultData=new AppResultData(APPResultCommom.SUCCESS, "", tSysUser);
				} catch (Exception e) {
					e.printStackTrace();
					 appResultData=new AppResultData(APPResultCommom.FAIL,APPResultCommom.FAIL_MSG, null);
					 return appResultData;
				}
				  return appResultData;
			    }
			  
		 /**
		  * 分页查询用户数据列表
		  * @param tSysUser
		  * @param token
		  * @return
		  */
			  @ApiOperation(value = "分页查询用户数据列表")
			  @RequestMapping(value = "getUsersOnPage", method = RequestMethod.POST)
			  @JwtTokenUtil.UserLoginToken
		   public AppResultData getUsersOnPage(@RequestBody GetUsersOnPageCond getUsersOnPageCond ,@RequestHeader String token) {
					TSysUser tSysUser=(TSysUser) redisUtil.get(token);
			  try {
				  int userId=tSysUser.getId();
				  tSysUser=tSysUserService.findById(userId);
				  getUsersOnPageCond.setCurAffiliateId(tSysUser.getAffiliateId());
				  PageBean<Map<String, Object>> pageBean=tSysUserService.getUsersOnPage(getUsersOnPageCond);
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
		   *组织更新数据
		   * @param tSysUser2 待更新数据
		   * @param tSysUser 入参
		   * @return
		   */
		  public  TSysUser buildUpdateData(TSysUser tSysUser2, TSysUser tSysUser) {
			   if(!StringUtil.isNullOrEmpty(tSysUser.getUserName())){
				   tSysUser2.setUserName(tSysUser.getUserName());
			   }
			   if(tSysUser.getAffiliateId()>0){
				   tSysUser2.setAffiliateId(tSysUser.getAffiliateId());
			   }
			   if(tSysUser.getDeptId()>0){
				   tSysUser2.setDeptId(tSysUser.getDeptId());
			   }
			   if(!StringUtil.isNullOrEmpty(tSysUser.getTitleId())){
				   tSysUser2.setTitleId(tSysUser.getTitleId());
			   }
			return tSysUser2;
		}
			  
}
