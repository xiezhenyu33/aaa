package com.parko.zkcenter.entity.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.parko.system.entity.BasisEntity;

import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 *质控报表实体
 * @author Administrator
 *
 */
@Entity
@Table(name = "T_QUALITY_CONTROL_REPORT")
@Data
@DynamicInsert
@DynamicUpdate
public class TQualityControlReport extends BasisEntity{
	 
		@ApiParam("报表类型 0 评分标准 1建设管理 2 工作开展调查 3 摸底调查 ")
		@Column(length=1)
		private String reportType;//报表类型 0 评分标准 1建设管理 2 工作开展调查 3 摸底调查
		
		@ApiParam("报表名称 ")
		@Column(length=200)
		private String reportName;//报表名称
		
		@ApiParam("报表编号 ")
		@Column(length=20)
		private String reportNum;//
	
	    @ApiParam("报表状态")
		@Column(length=20)
		private String reportStatus;//报表状态 见码表D006（）

		@Column(length=100,nullable=false)
		@ApiParam("机构名称")
		private String affiliateName;//机构名称
		
		@Column(length=20)
		@ApiParam("机构级别代码")
		private String levelCode;//机构级别代码 见码表 D002
		
		@Column(length=300)
		@ApiParam("医院详细地址")
		private String address;//医院详细地址

		@ApiParam("调表人名称")
		@Column(length=50,nullable=false)
		private String fillFormName;//上报人名称

		@ApiParam("联系电话")
		@Column(length=11,nullable=false)
		private String phone;//联系电话

		@ApiParam("填写时间")
		@Column(length=20,nullable=false)
		private String fillTime;//填写时间
		
		@ApiParam("报表内容json对象字符串")
		@Column(length=5000,nullable=false)
		private String reportContext;//报表内容json对象字符串
		
		@ApiParam("自评总分")
		 @Column(columnDefinition="int default 0")
		private int selfScore;//自评总分
		
		 @Column(columnDefinition="int default 0")
		@ApiParam("复评总分")
		private int repScore;//
		
		
}
