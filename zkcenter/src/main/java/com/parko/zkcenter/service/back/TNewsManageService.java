package com.parko.zkcenter.service.back;

import java.util.List;

import com.parko.system.entity.pojo.PageBean;
import com.parko.zkcenter.entity.back.TNewsManage;
import com.parko.zkcenter.entity.cond.GetNewsOnPageCond;

/**
 * 新闻管理数据业务处理接口
 * @author Administrator
 *
 */
public interface TNewsManageService {

	/**
	 * 保存单个新闻管理实体数据
	 * @param tNewsManage
	 * @return
	 * @throws IllegalAccessException 
	 */
	TNewsManage  saveTNewsManage(TNewsManage tNewsManage,int curUserId) throws IllegalAccessException;
	
	/**
	 * 根据主键id获取单个数据对象
	 * @param newId
	 * @return
	 */
	TNewsManage  findById(int  newId);
	
	/**
	 * 根据主键id删除单个数据对象
	 * @param newId
	 * @return
	 * @throws IllegalAccessException 
	 */
	TNewsManage  deleteById(int  newId,int curUserId) throws IllegalAccessException;
     /**
      * 分页查询新闻数据列表
      * @param getNewsOnPageCond
     * @param affiliateId 
      * @return
      */
	PageBean<TNewsManage> getNewsOnPage(GetNewsOnPageCond getNewsOnPageCond, List<Integer> curUserIds);
}
