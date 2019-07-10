package com.parko.zkcenter.service.report;

import java.util.List;
import java.util.Map;

import com.parko.system.entity.pojo.PageBean;
import com.parko.zkcenter.entity.cond.GetReportsOnPageCond;
import com.parko.zkcenter.entity.report.TQualityControlReport;

/**
 * 质控报表数据业务处理接口
 * @author Administrator
 *
 */
public interface TQualityControlReportService {

	/**
	 * 保存广东省综合医院摸底调查表
	 * @param tQualityControlReport
	 * @param id
	 * @return
	 * @throws IllegalAccessException 
	 */
	TQualityControlReport saveQualityControlReport(TQualityControlReport tQualityControlReport, int id) throws IllegalAccessException;
   /**
    * 获取用户个人的广东省综合医院摸底调查表数据列表
    * @param id
    * @return
    */
	List<TQualityControlReport> getQlyCtlReports(TQualityControlReport tQualityControlReport);
	/**
	 * 获取表详情数据
	 * @param id
	 * @return
	 */
    TQualityControlReport findById(int id);
    /**
     * 分页查询质控报表数据列表
     * @param getReportsOnPageCond
     * @param userId 
     * @return
     */
	PageBean<Map<String, Object>> getReportsOnPage(GetReportsOnPageCond getReportsOnPageCond, int userId);

}
