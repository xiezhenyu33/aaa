package com.parko.zkcenter.service.back;

import java.util.List;

import com.parko.system.entity.pojo.PageBean;
import com.parko.zkcenter.entity.back.TParamClassManage;
import com.parko.zkcenter.entity.cond.GetParamClasssOnPageCond;

/**
 * 参数分类数据业务处理接口
 * @author Administrator
 *
 */
public interface TParamClassManageService {

	/**
	 * 保存对应的数据
	 * @param tParamClassManage
	 * @return
	 * @throws IllegalAccessException 
	 */
	TParamClassManage saveTParamClassManage(TParamClassManage  tParamClassManage,int curUserId) throws IllegalAccessException;
	
	/**
	 * 根据主键id获取对应的数据
	 * @param classId
	 * @return
	 */
	TParamClassManage findById(int classId);
	/**
	 * 根据主键id删除对应的数据
	 * @param classId
	 * @return
	 * @throws IllegalAccessException 
	 */
	TParamClassManage deleteTParamClassManage(int classId,int curUserId) throws IllegalAccessException;
	
	/**
	 * 根据对象获取所有的列表数据
	 * @param tParamClassManage
	 * @return
	 */
	List<TParamClassManage> geTParamClassManages(TParamClassManage tParamClassManage);

	/**
	 * 分页查询参数分类(特医食品分类/规范指南分类)信息列表
	 * @param tParamClassManage
	 * @return
	 */
	PageBean<TParamClassManage> getParamClasssOnPage(GetParamClasssOnPageCond getParamClasssOnPageCond,List<Integer> curUserIds);
	
}
