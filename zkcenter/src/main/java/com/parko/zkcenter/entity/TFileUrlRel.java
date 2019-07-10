package com.parko.zkcenter.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.parko.system.entity.Basis;

import io.swagger.annotations.ApiParam;
import lombok.Data;

/**
 * 附件路径关联实体
 * @author Administrator
 *
 */
@Entity
@Table(name = "T_FILE_URL_REL")
@Data
@DynamicInsert
@DynamicUpdate
public class TFileUrlRel extends Basis{
    
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @ApiParam("自增列")
     int id;
	
	@ApiParam("关联对象id")
	private int relId;
	
	@ApiParam("附件路径")
	@Column(length=1000)
	private String fileUrl;
	
	@ApiParam("附件名称")
	@Column(length=200)
	private String fileName;
	
	@ApiParam("附件类型 0 信息资料-指南规范 1 信息资料-特医食品 2 新闻管理 3 轮播图 4 抽检通知 5 评分细则 6 不良事件")
	@Column(length=1)
	private String urlType;
}
