package com.parko.zkcenter.entity.back;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.rmi.CORBA.ClassDesc;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.parko.system.entity.BasisEntity;

import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * 参数管理-分类管理
 * @author Administrator
 *
 */
@Entity
@Table(name = "T_PARAM_CLASS_MANAG")
@Data
@DynamicInsert
@DynamicUpdate
public class TParamClassManage extends BasisEntity{
     
	@ApiParam("分类名称")
	@Column(length=50,nullable=false)
	private String className;
	
	@ApiParam("分类描述")
	@Column(length=620)
	private String  classDesc;
	
	@ApiParam("分类类型 0 指南规范 1 特医食品")
	@Column(length=1)
	private String classType;
}
