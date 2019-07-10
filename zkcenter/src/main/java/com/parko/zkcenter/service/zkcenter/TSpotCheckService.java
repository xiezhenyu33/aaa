package com.parko.zkcenter.service.zkcenter;

import java.util.List;
import java.util.Map;

import com.parko.system.entity.pojo.CommomPage;
import com.parko.system.entity.pojo.PageBean;
import com.parko.zkcenter.entity.zkcenter.TSpotCheck;

/**
 * 医院抽检实体（消息）业务处理接口
 * @author Administrator
 *
 */
public interface TSpotCheckService {

	/**
	 * 保存医生抽检信息数据
	 * @param tSpotCheck
	 * @param curUserId
	 * @return
	 * @throws IllegalAccessException 
	 */
	TSpotCheck saveTSpotCheck(TSpotCheck tSpotCheck ,int curUserId) throws IllegalAccessException;
	
	/**
	 * 根据主键id获取对象数据
	 * @param checkId
	 * @return
	 */
	TSpotCheck findById(int checkId);
	
	/**
	 * 根据id删除对应的数据
	 * @param checkId
	 * @param curUserId
	 * @return
	 * @throws IllegalAccessException 
	 */
	TSpotCheck deleteById(int checkId,int curUserId) throws IllegalAccessException;
	
	/**
	 * 根据对象获取对应的数据列表
	 * @param tSpotCheck
	 * @return
	 */
	List<TSpotCheck> geTSpotChecks(TSpotCheck tSpotCheck);

	/**
	 * 批量保存用户数据
	 * @param tSpotChecks
	 * @param curUserId 
	 * @throws IllegalAccessException 
	 */
	List<TSpotCheck> saveAll(List<TSpotCheck> tSpotChecks, int curUserId) throws IllegalAccessException;

	/**
	 * 分页查询用户医院抽检消数据列表
	 * @param commomPage
	 * @param id
	 * @return
	 */
	PageBean<Map<String, Object>> getUsersSpotCheckOnPage(CommomPage commomPage, int id);
}
