package com.parko.zkcenter.dao.back;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.parko.zkcenter.entity.back.TRotationChart;

/**
 * 轮播图数据库交互
 * @author Administrator
 *
 */
public interface TRotationChartDao extends CrudRepository<TRotationChart, Integer>, JpaRepository<TRotationChart, Integer>, JpaSpecificationExecutor<TRotationChart> {

}
