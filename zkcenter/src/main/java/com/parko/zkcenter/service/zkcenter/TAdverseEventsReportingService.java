package com.parko.zkcenter.service.zkcenter;

import java.util.List;
import java.util.Map;

import com.parko.system.entity.pojo.PageBean;
import com.parko.zkcenter.entity.cond.GetEventsOnPageCond;
import com.parko.zkcenter.entity.cond.GetReportsOnPageCond;
import com.parko.zkcenter.entity.zkcenter.TAdverseEventsReporting;

/**
 * 不良事件上报数据业务处理接口
 * @author Administrator
 *
 */
public interface TAdverseEventsReportingService {

	/**
	 * 保存不良事件上报数据
	 * @param tAdverseEventsReporting
	 * @param curUserId
	 * @return
	 * @throws IllegalAccessException 
	 */
	TAdverseEventsReporting saveTAdverseEventsReporting(TAdverseEventsReporting  tAdverseEventsReporting,int curUserId) throws IllegalAccessException;
   
	/**
	 * 获取不良事件对象根据id
	 * @param eventId
	 * @return
	 */
	TAdverseEventsReporting findById(int eventId);
	
	/**
	 * 获取不良事件数据列表
	 * @param tAdverseEventsReporting
	 * @return
	 */
	List<TAdverseEventsReporting> geTAdverseEventsReportings(TAdverseEventsReporting tAdverseEventsReporting);

	/**
	 * 分页不良事件上报数据列表
	 * @param getEventsOnPageCond
	 * @param id
	 * @return
	 */
	PageBean<Map<String, Object>> getEventsOnPage(GetEventsOnPageCond getEventsOnPageCond, int id);
}
