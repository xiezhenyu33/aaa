package com.parko.zkcenter.entity.back;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.parko.system.entity.BasisEntity;

import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * 轮播图实体
 * @author Administrator
 *
 */
@Entity
@Table(name = "T_ROTATION_CHART")
@Data
@DynamicInsert
@DynamicUpdate
public class TRotationChart extends BasisEntity{

	@ApiParam("宣传语")
	@Column(length=90,nullable=false)
	private String slogan;//
	
	@ApiParam("跳转链接")
	@Column(length=300)
	private String imageUrl;//
	
}
