package com.parko.zkcenter.entity.cond;


import com.parko.system.entity.pojo.CommomPage;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * 查询参数分类条件
 * @author Administrator
 *
 */
@Data
public class GetParamClasssOnPageCond extends CommomPage{

	@ApiParam("分类名称")
	private String className;
	
	@ApiParam("分类类型 0 指南规范 1 特医食品")
	private String classType;
	
}
