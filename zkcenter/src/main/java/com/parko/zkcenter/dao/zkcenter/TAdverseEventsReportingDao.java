package com.parko.zkcenter.dao.zkcenter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.parko.zkcenter.entity.zkcenter.TAdverseEventsReporting;

/**
 * 不良事件上报数据交互
 * @author Administrator
 *
 */
public interface TAdverseEventsReportingDao extends CrudRepository<TAdverseEventsReporting, Integer>, JpaRepository<TAdverseEventsReporting, Integer>, JpaSpecificationExecutor<TAdverseEventsReporting>{

}
