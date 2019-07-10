package com.parko.zkcenter.entity.cond;

import java.util.List;

import com.parko.zkcenter.entity.zkcenter.TSpotCheck;

import lombok.Data;

/**
 * 医院抽检添加条件
 * @author Administrator
 *
 */
@Data
public class AddChecksCond  extends FileUrlsCond{

	private TSpotCheck tSpotCheck;//医院抽检实体
	
   private List<Integer> affliateIds;//医院id数据
}
