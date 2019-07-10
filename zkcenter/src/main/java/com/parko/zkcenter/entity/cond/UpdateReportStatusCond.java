package com.parko.zkcenter.entity.cond;

import java.io.Serializable;

import lombok.Data;

/**
 * 更新报表状态接口
 * @author Administrator
 *
 */
@Data
public class UpdateReportStatusCond implements Serializable{
         
	private int reportId;//报表id
	
	private String reportStatus;//报表状态 码表 D006
	
}
