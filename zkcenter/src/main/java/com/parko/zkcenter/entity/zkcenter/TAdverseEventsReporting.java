package com.parko.zkcenter.entity.zkcenter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.parko.system.entity.BasisEntity;

import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * 不良事件上报实体类
 * @author 19634
 *
 */
@Entity
@Table(name = "T_ADVERSE_EVENTS_REPORTING")
@Data
@DynamicInsert
@DynamicUpdate
public class TAdverseEventsReporting extends BasisEntity {
	

	@ApiParam("报表编号 ")
	@Column(length=20)
	private String reportNum;//
	
	@ApiParam("上报机构id ")
	private int affiliateId;//
	
    @ApiParam("数据上报状态")
	@Column(length=20)
	private String reportStatus;//见码表D006

	@ApiParam("调表人名称")
	@Column(length=50,nullable=false)
	private String fillFormName;//调表人名称

	@ApiParam("联系电话")
	@Column(length=11,nullable=false)
	private String phone;//联系电话

	@ApiParam("填写时间")
	@Column(length=20,nullable=false)
	private String fillTime;//填写时间

	@ApiParam("上报类型")
	@Column(length=20)
	private String reportType;//上报类型 见码表 D008

	@ApiParam("上报标题")
	@Column(length=100,nullable=false)
	private String reportTitle;//上报标题

    @ApiParam("影响范围")
	@Column(length=1200,nullable=false)
	private String scopeOfInfluence;//影响范围

	@ApiParam("患者情况")
	@Column(length=2200,nullable=false)
	private String patientCondition;//患者情况

	@ApiParam("膳食营养支持治疗情况内容json对象字符串")
	@Column(length=2000)
	private String reportContext;//
	
	@ApiParam("不良反应内容json对象字符串")
	@Column(length=3000)
	private String badEffects;//不良反应内容


	@ApiParam("临床用药内容json对象字符串")
	@Column(length=500)
	private String clinicalMedication;//临床用药内容

	@ApiParam("实验室指标内容json对象字符串")
	@Column(length=200)
	private String labIndic;//实验室指标

	@ApiParam("补充说明")
	@Column(length=700)
	private String otherRemak;//补充说明

}