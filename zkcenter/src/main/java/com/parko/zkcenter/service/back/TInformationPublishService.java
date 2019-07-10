package com.parko.zkcenter.service.back;

import java.util.List;
import java.util.Map;

import com.parko.system.entity.pojo.PageBean;
import com.parko.system.entity.sys.TSysRole;
import com.parko.zkcenter.entity.back.TInformationPublish;
import com.parko.zkcenter.entity.cond.GetInfosOnPageCond;

/**
 * 信息资料发布数据业务处理接口
 * @author Administrator
 *
 */
public interface TInformationPublishService {

	/**
	 * 保存信息资料发布数据
	 * @param tInformationPublish
	 * @param id
	 * @return
	 * @throws IllegalAccessException 
	 */
	TInformationPublish saveTInformationPublish(TInformationPublish tInformationPublish, int id) throws IllegalAccessException;
    /**
     * 删除对应的资料信息发布数据
     * @param infoId
     * @param id
     * @throws IllegalAccessException 
     */
	TInformationPublish deleteOneInfo(int infoId, int id) throws IllegalAccessException;
/**
 * 根据id获取对象数据
 * @param infoId
 * @return
 */
	TInformationPublish findById(int infoId);
	/**
	 * 分页查询资料信息发布数据列表
	 * @param getInfosOnPageCond
	 * @param curUserIds 
	 * @return
	 */
PageBean<Map<String, Object>> getInfosOnPage(GetInfosOnPageCond getInfosOnPageCond,String rightSql);
}
