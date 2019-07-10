package com.parko.zkcenter.entity.cond;

import javax.persistence.Column;

import com.parko.system.entity.pojo.CommomPage;

import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * 分页查询不良事件上报数据条件
 * @author Administrator
 *
 */
@Data
public class GetEventsOnPageCond extends CommomPage{
  
	@ApiParam("上报机构id ")
	private int affiliateId;//
	
	@ApiParam("上报类型")
	private String reportType;//上报类型 见码表 D008
	
	@ApiParam("上报标题")
	private String reportTitle;//上报标题
	
	@ApiParam("不良反应")
	private String badEffects;//不良反应内容
	
	@ApiParam("开始时间 ")
	private String startTime;//
	
	@ApiParam("结束时间 ")
	private String endTime;//
	
	@ApiParam("所属省代码")
	private String provinceCode;//所属省代码

	@ApiParam("所属市代码")
	private String cityCode;//所属市代码

	@ApiParam("所属县代码")
	private String countyCode;//所属县代码
}
