package com.parko.zkcenter.entity.cond;

import com.parko.zkcenter.entity.back.TRotationChart;

import lombok.Data;

@Data
public class AddOneChartCond extends FileUrlsCond{

	private TRotationChart tRotationChart;//轮播图对象
}
