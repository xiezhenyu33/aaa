package com.parko.zkcenter.dao.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import com.parko.zkcenter.entity.report.TQualityControlReport;

/**
 * *质控报表数据交互
 * @author Administrator
 *
 */
public interface TQualityControlReportDao extends CrudRepository<TQualityControlReport, Integer>, JpaRepository<TQualityControlReport, Integer>, JpaSpecificationExecutor<TQualityControlReport>{

}
