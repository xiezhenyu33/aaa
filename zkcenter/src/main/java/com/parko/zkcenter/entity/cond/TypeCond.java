package com.parko.zkcenter.entity.cond;

import java.io.Serializable;

import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * 类型条件
 * @author Administrator
 *
 */
@Data
public class TypeCond implements Serializable{

	@ApiParam("类型")
	private String type;//分类类型 0 规范指南分类 1 特医食品分类
}
