package com.parko.zkcenter.entity.cond;


import com.parko.system.entity.pojo.CommomPage;
import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * 新闻；列表查询条件
 * @author Administrator
 *
 */
@Data
public class GetNewsOnPageCond extends CommomPage{
	
	@ApiParam("新闻类别")
	private String newsType;//新闻类别代码 D007
	
	@ApiParam("新闻标题")
	private String title;//新闻标题
	
	@ApiParam("开始时间")
	private String startTime;//开始时间
	
	@ApiParam("结束时间")
	private String endTime;//结束时间
}
