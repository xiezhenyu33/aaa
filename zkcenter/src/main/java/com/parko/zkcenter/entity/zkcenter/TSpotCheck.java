package com.parko.zkcenter.entity.zkcenter;

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
 * 医院抽检实体
 * @author Administrator
 *
 */
@Entity
@Table(name = "T_SPOT_CHECK")
@Data
@DynamicInsert
@DynamicUpdate
public class TSpotCheck extends BasisEntity{

	@ApiParam("医院id（机构id）")
	private int affiliateId;
	
	@ApiParam("医生用户id")
	private int userId;
	
	@ApiParam("标题")
	@Column(length=70)
	private String title;
	
	@ApiParam("消息阅读状态 0 已读 1 未读（默认）")
	@Column(length=1)
	private String checkStatus;
	
	@ApiParam("内容")
	@Lob
	private String context;
}
