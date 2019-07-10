package com.parko.zkcenter.service.back;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.parko.system.entity.pojo.CommomPage;
import com.parko.system.entity.pojo.PageBean;
import com.parko.system.utils.CriteriaUtil;
import com.parko.system.utils.EntityUtil;
import com.parko.system.utils.RestrictionsUtil;
import com.parko.zkcenter.dao.back.TRotationChartDao;
import com.parko.zkcenter.entity.back.TRotationChart;

/**
 * 轮播图数据业务处理接口实现类
 * @author Administrator
 *
 */
@Service
public class TRotationChartServiceImpl implements TRotationChartService {

	@Autowired
	private TRotationChartDao tRotationChartDao;//轮播图数据库交互

	/**
	 * 保存轮播图数据
	 * @throws IllegalAccessException 
	 */
	@Override
	public TRotationChart saveTRotationChart(TRotationChart tRotationChart, int curUserId) throws IllegalAccessException {
		tRotationChart.setDataRemoveType(0);
		if(tRotationChart.getId()==0) {//新增
			tRotationChart= (TRotationChart) EntityUtil.entityFormatField(EntityUtil.createOperation,curUserId,tRotationChart);
		}else {//修改
			tRotationChart= (TRotationChart) EntityUtil.entityFormatField(EntityUtil.updateOperation,curUserId,tRotationChart);
		}
		tRotationChart=tRotationChartDao.save(tRotationChart);
		return tRotationChart;
	}
/**
 *  根据id获取对应的数据信息
 */
	@Override
	public TRotationChart findById(int chartId) {
		TRotationChart tRotationChart =new TRotationChart();
		Optional<TRotationChart> tOptional=tRotationChartDao.findById(chartId);
		if(tOptional.isPresent()) {
			tRotationChart=tOptional.get();
		}
		return tRotationChart;
	}
/**
 *  根据id删除对应的数据信息
 * @throws IllegalAccessException 
 */
	@Override
	public TRotationChart deleteTRotationChartById(int chartId, int curUserId) throws IllegalAccessException {
		TRotationChart tRotationChart=findById(chartId);
		tRotationChart= (TRotationChart) EntityUtil.entityFormatField(EntityUtil.deleteOperation,curUserId,tRotationChart);
		tRotationChart=tRotationChartDao.save(tRotationChart);
		return tRotationChart;
	}
	
	/**
	 * 分页查询轮播图数据列表
	 */
@SuppressWarnings("deprecation")
@Override
public Page<TRotationChart> getChartsOnPage(CommomPage commomPage,List<Integer> curUserIds) {
	 
	int pageSize=commomPage.getPageSize();
	  int pageNum=commomPage.getPageNum();
   Pageable pageable = new PageRequest(pageNum,pageSize, Sort.Direction.ASC,"createTime");
     CriteriaUtil<TRotationChart> criteriaUtil = new CriteriaUtil<TRotationChart>();
     //当前机构的用户
     if(curUserIds!=null&&curUserIds.size()>0) {
   	   criteriaUtil.add(RestrictionsUtil.in("createUser",curUserIds,true));
     }
     criteriaUtil.add(RestrictionsUtil.eq("dataRemoveType",0,true));
	  Page<TRotationChart>  tRotationChartPages=tRotationChartDao.findAll(criteriaUtil, pageable);
    return tRotationChartPages;
}
/**
 * 获取对应的轮播图数据列表
 */
	@Override
	public List<TRotationChart> geTRotationCharts(TRotationChart tRotationChart,List<Integer> curUserIds) {
		CriteriaUtil<TRotationChart> criteriaUtil = new CriteriaUtil<TRotationChart>();
	     criteriaUtil.add(RestrictionsUtil.eq("dataRemoveType",0,true));
	     //当前机构的用户
	      if(curUserIds!=null&&curUserIds.size()>0) {
	    	   criteriaUtil.add(RestrictionsUtil.in("createUser",curUserIds,true));
	      }
	     List<TRotationChart> tRotationCharts=tRotationChartDao.findAll(criteriaUtil);
		return tRotationCharts;
	}
	
	
	
}
