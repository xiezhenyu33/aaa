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
 * 新闻实体类
 * @author 19634
 *
 */
@Entity
@Table(name = "T_NEWS_MANAGE")
@Data
@DynamicInsert
@DynamicUpdate
public class TNewsManage extends BasisEntity {
	

	@Column(length=110,nullable=false)
	@ApiParam("新闻标题")
	private String title;//新闻标题

	@ApiParam("新闻类别")
	@Column(length=20)
	private String newsType;//新闻类别代码 D007

	@ApiParam("发布单位")
	@Column(length=50)
	private String publishingUnit;//发布单位

    @ApiParam("新闻内容-富文本")
	@Lob
	private String newsDesc;//新闻内容-富文本
}