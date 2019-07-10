package com.parko.zkcenter.entity.cond;

import com.parko.zkcenter.entity.zkcenter.TAdverseEventsReporting;

import lombok.Data;

/**
 * 不良事件·上报保存条件
 * @author Administrator
 *
 */
@Data
public class AddOneEventCond extends FileUrlsCond {
      
	private TAdverseEventsReporting tAdverseEventsReporting;//不良事件对象
	
}
