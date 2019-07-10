package com.parko.zkcenter.entity.cond;

import com.parko.zkcenter.entity.report.TQualityControlReport;

import lombok.Data;

/**
 * 质控报表保存条件
 * @author Administrator
 *
 */
@Data
public class SaveQualityControlReportCond  extends FileUrlsCond{

	private TQualityControlReport tQualityControlReport;//质控报表对象
	
}
