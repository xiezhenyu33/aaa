package com.parko.zkcenter.entity.cond;


import com.parko.system.entity.pojo.CommomPage;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * 质控报表分页查询
 * @author Administrator
 *
 */
@Data
public class GetReportsOnPageCond extends CommomPage{
	
	@ApiParam("机构名称")
	private String affiliateName;//机构名称
	
	@ApiParam("报表类型 0 评分标准 1建设管理 2 工作开展调查 3 摸底调查 ")
	private String reportType;//报表类型 0 评分标准 1建设管理 2 工作开展调查 3 摸底调查
	
	@ApiParam("开始时间 ")
	private String startTime;//
	
	@ApiParam("结束时间 ")
	private String endTime;//
	
	private String reportStatus;//报表状态 见码表D006
	
}
