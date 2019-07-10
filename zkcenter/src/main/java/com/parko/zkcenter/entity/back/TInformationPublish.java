package com.parko.zkcenter.entity.back;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.parko.system.entity.BasisEntity;

import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * 信息资料库实体类
 * @author 19634
 *
 */
@Entity
@Table(name = "T_INFORMATION_PUBLISH")
@Data
@DynamicInsert
@DynamicUpdate
public class TInformationPublish extends BasisEntity {
	

	@Column(length=110,nullable=false)
	@ApiParam("资料名称")
	private String infoMetName;//资料名称

	@ApiParam("资料内容类型代码")
	private int infoType;//资料内容类型代码 表T_PARAM_CLASS_MANAGE主键 id

	@ApiParam("资料类型")
	@Column(length=1,nullable=false)
	private String infoMetType;//资料类型 0 指南/规范 1 

    @ApiParam("资料内容-富文本")
	@Lob
	private String infoMetDesc;//资料内容-富文本
}