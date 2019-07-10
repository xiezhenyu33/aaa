package com.parko.zkcenter.service.back;

import java.util.List;

import org.springframework.data.domain.Page;

import com.parko.system.entity.pojo.CommomPage;
import com.parko.system.entity.pojo.PageBean;
import com.parko.zkcenter.entity.back.TRotationChart;

/**
 * 轮播图数据业务处理接口
 * @author Administrator
 *
 */
public interface TRotationChartService {

	/**
	 * 保存轮播图数据
	 * @param tRotationChart
	 * @param curUserId
	 * @return
	 * @throws IllegalAccessException 
	 */
	TRotationChart saveTRotationChart(TRotationChart tRotationChart,int curUserId) throws IllegalAccessException;
	
	/**
	 * 根据id获取对应的数据信息
	 * @param chartId
	 * @return
	 */
	TRotationChart findById(int chartId);
	/**
	 * 根据id删除对应的数据信息
	 * @param chartId
	 * @param curUserId
	 * @return
	 * @throws IllegalAccessException 
	 */
	TRotationChart deleteTRotationChartById(int chartId,int curUserId) throws IllegalAccessException;

	/**
	 * 分页查询轮播图数据列表
	 * @param commomPage
	 * @return
	 */
	Page<TRotationChart> getChartsOnPage(CommomPage commomPage,List<Integer> curUserIds);
	
	/**
	 * 获取对应的轮播图数据列表
	 * @param tRotationChart
	 * @return
	 */
	List<TRotationChart> geTRotationCharts(TRotationChart tRotationChart,List<Integer> curUserIds);
	
	
}
