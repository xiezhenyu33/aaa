package com.parko.zkcenter.entity.cond;



import com.parko.system.entity.pojo.CommomPage;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * 信息资料发布查询条件
 * @author Administrator
 *
 */
@Data
public class GetInfosOnPageCond extends CommomPage{

	@ApiParam("资料类型")
	private String infoMetType;//资料类型 0 指南/规范 1 
	
	@ApiParam("资料名称")
	private String infoMetName;//资料名称
	
	@ApiParam("资料内容类型代码")
	private String infoType;//资料内容类型代码 D006
}
